import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dvm on 12.11.16.
 */
public class ResumesLoader {

    static final String headHunterSearchResumeBaseURL = "https://api.hh.ru/resumes/";

    String[] specializations = {"1.221","1.273","1.82","1.89","1.270","1.327","1.295","1.211",
            "1.10","1.172","1.203","1.117","1.110","1.225","1.9","1.25","1.420","1.137",
            "1.116","1.232","1.296","1.536","1.30","1.3",
    "1.359","1.113","1.50","1.395","1.246","1.274","1.161","1.475","1.277",
    "1.400","1.474"};

    String[] expirience = {"noExperience","between1And3","between3And6","experience=moreThan6"};

    MongoManager mongoManager;
    private List<String> idsList = new ArrayList<String>();
    private int addedResumes = 0;
    private int totalLoaded = 0;


    boolean noNextPage;

    public void loadResumes() throws IOException {

        mongoManager = new MongoManager();
        String currentURL;

        for (int area = 2; area < 13; area++){
            String areaString = String.valueOf(area);
            int specialization = area == 2 ? 13 : 0;
            for (; specialization < specializations.length;specialization++){
                String sp = specializations[specialization];
                int exp = specialization < 5 && area == 2 ? 7 : 0;
                for (; exp < expirience.length; exp++){
                    String expString = expirience[exp];
                    int age = specialization < 5 && area == 13 && exp == 3 ? 26 : 17;
                    for (; age < 70; ) {
                        String age_from = String.valueOf(age);
                        String age_to = String.valueOf(age + 2);
                        System.out.println("Area:"+area+" Speciazization:"+specialization+"/"+specializations.length+"("+sp+")"+" Exp:"+expString+" Age:"+
                        age_from+"-"+age_to);

                        Formatter f = new Formatter();
                        f.format("https://spb.hh.ru/search/resume?exp_period=all_time&order_by=publication_time&specialization=%s&area=%s&text=&pos=full_text&experience=%s&label=only_with_age&logic=normal&clusters=true&age_to=%s&age_from=%s&from=cluster_age",
                                sp, areaString,expString,age_to,age_from);
                        currentURL = f.toString();
                        System.out.println(currentURL);
                        loadAllSearchPagesWithURL(currentURL);
                        age += 3;
                        totalLoaded += addedResumes;
                        System.out.println("Total load from start:" + totalLoaded);
                        System.out.println("-------------------------------------");
                    }
                }
            }
        }
    }

    // area = 2
    // spec = 5
    // exp = 3
    // age = 23-25
    private void loadAllSearchPagesWithURL(String url){

        idsList.clear();
        addedResumes = 0;
        noNextPage = false;

        try {
            sendGET(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i=1;i< 250;i++){
            if (noNextPage){
                System.out.println("Load pages before noNext:" + i);
                break;
            }
            String pageUrl = url + "&page="+i;
            try {
                sendGET(pageUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (i == 249){
                System.out.println("Load all pages:" + i);
            }
        }
        Iterator<String> iterator = idsList.iterator();
        while (iterator.hasNext()) {
            String resume = "https://api.hh.ru/resumes/"+ iterator.next();
            //System.out.println(resume);
            try {
                getResume(resume);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Iterations stats:" + addedResumes + "/" +idsList.size());

    }


    private void sendGET(String url) throws IOException {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();
        //System.out.println("GET Response Code :: " + responseCode);
        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            Pattern p = Pattern.compile("href=\"\\/resume\\/?[0-9a-z]*");
            Matcher m = p.matcher(response);

            int idsListCount = idsList.size();
            while (m.find()) {
                String href = m.group();
                Pattern idPattern = Pattern.compile("\\w{38}");
                Matcher idMatcher = idPattern.matcher(href);
                while (idMatcher.find()) {
                    String id = idMatcher.group();
                    idsList.add(id);
                }
            }

            //System.out.println("Response length: " + response.toString().length());
            //System.out.println(response.toString());

            if (idsListCount == idsList.size()){
                noNextPage = true;
            }
        } else {
            System.out.println("Request not worked: "+ url);
        }
    }

    private void getResume(String url) throws IOException {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();
        //System.out.println("GET Response Code :: " + responseCode);
        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result

            boolean result = mongoManager.insertResumeJson(response.toString());
            if (result == true){
                addedResumes++;
            }
        } else {
            System.out.println("GET request not worked");
        }
    }
}