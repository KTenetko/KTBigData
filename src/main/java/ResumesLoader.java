import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dvm on 12.11.16.
 */
public class ResumesLoader {

    private List<String> idsList = new ArrayList<String>();

    public void loadWithURL(String url) throws IOException {

        sendGET(url);
        for (int i=1;i< 10;i++){
            String pageUrl = url + "&page="+i;
            sendGET(url);
        }
        System.out.println("Count resumes: " + idsList.size());
        Iterator<String> crunchifyIterator = idsList.iterator();
        while (crunchifyIterator.hasNext()) {
            String resume = "https://api.hh.ru/resumes/"+ crunchifyIterator.next();
            System.out.println(resume);
            getResume(resume);
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
            System.out.println("Response length: " + response.toString().length());
            System.out.println(response.toString());
            writeToFile(response.toString());
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