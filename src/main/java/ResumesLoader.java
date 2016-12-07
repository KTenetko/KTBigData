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

    String[] specializations = {"1.232","1.296","1.536","1.30","1.3",
    "1.359","1.113","1.50","1.395","1.246","1.274","1.161","1.475","1.277",
    "1.400","1.474","1.221","1.273","1.82","1.89","1.270","1.327","1.295","1.211",
            "1.10","1.172","1.203","1.117","1.110","1.225","1.9","1.25","1.420","1.137",
            "1.116","1.400"};

    String[] expirience = {"noExperience","between1And3","between3And6","experience=moreThan6"};

    MongoManager mongoManager;
    private List<String> idsList = new ArrayList<String>();

    boolean noNextPage;

    public void loadWithURL(String url) throws IOException {

        mongoManager = new MongoManager();
        String currentURL;

        for (int area = 1; area < 13; area++){
            String areaString = String.valueOf(area);
            for (int specialization = 0; specialization < specializations.length;specialization++){
                String sp = specializations[specialization];
                for (int exp = 0; exp < expirience.length; exp++){
                    String expString = expirience[exp];
                    for (int age = 17; age < 70; ) {
                        String age_from = String.valueOf(age);
                        String age_to = String.valueOf(age + 2);

                        Formatter f = new Formatter();
                        f.format("https://spb.hh.ru/search/resume?exp_period=all_time&order_by=publication_time&specialization=%s&area=%s&text=&pos=full_text&experience=%s&label=only_with_age&logic=normal&clusters=true&age_to=%s&age_from=%s&from=cluster_age",
                                sp, areaString,expString,age_to,age_from);
                        currentURL = f.toString();
                        System.out.println(currentURL);
                        loadAllSearchPagesWithURL(currentURL);
                        age += 2;
                    }
                }
            }
        }
    }

    private void loadAllSearchPagesWithURL(String url){

        idsList.clear();
        noNextPage = false;

        try {
            sendGET(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i=1;i< 250;i++){
            if (noNextPage){
                break;
            }
            String pageUrl = url + "&page="+i;
            try {
                sendGET(pageUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Count resumes: " + idsList.size());
        Iterator<String> crunchifyIterator = idsList.iterator();
        while (crunchifyIterator.hasNext()) {
            String resume = "https://api.hh.ru/resumes/"+ crunchifyIterator.next();
            System.out.println(resume);
            try {
                getResume(resume);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void sendGET(String url) throws IOException {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();
        System.out.println("GET Response Code :: " + responseCode);
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
                    System.out.println(id);
                    idsList.add(id);
                }
            }

            System.out.println("Response length: " + response.toString().length());
            System.out.println(response.toString());

            if (idsListCount == idsList.size()){
                noNextPage = true;
            }
        } else {
            System.out.println("GET request not worked");
        }
    }

    private void getResume(String url) throws IOException {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();
        System.out.println("GET Response Code :: " + responseCode);
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

            mongoManager.insertResumeJson(response.toString());

            System.out.println("Response length: " + response.toString().length());
            System.out.println(response.toString());
            //writeToFile(response.toString());
        } else {
            System.out.println("GET request not worked");
        }
    }

    private void writeToFile(String data){
        try{
            File file =new File("resume.txt");

            //if file doesnt exists, then create it
            if(!file.exists()){
                file.createNewFile();
            }

            //true = append file
            FileWriter fileWritter = new FileWriter(file.getName(),true);
            BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
            bufferWritter.write(data);
            bufferWritter.write("\n");
            bufferWritter.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

}