
/**
 * Created by dvm on 08.11.16.
 */
public class HeadHunterExtract {

    //clientID = V1LK39LR46Q43T9P38957539O4DQL3G6GCLJEOIHD0TFETL0UHIKHS0PI5KN9MDA
    //clientSecret = KA385K1OG9F1CGFME0O9B7FJKIUKPUUR51TRP3T022P7UGKCLHVE3GMVU42EIHJQ
    public static void main(String[] args) throws Exception {
        //sendGET("https://api.hh.ru/resumes/083386920000f578380039ed1f494132756439");

        ResumesLoader l = new ResumesLoader();
        l.loadWithURL("https://spb.hh.ru/search/resume?text=java&logic=normal&pos=full_text&exp_period=all_time&order_by=publication_time&specialization=1&clusters=true");
        ///resumes/{resume_id}
        System.out.println("11111");
        //https://spb.hh.ru/search/resume?exp_period=all_time&order_by=relevance&specialization=1&text=&pos=full_text&logic=normal&clusters=true

    }

    class HHExtract {



    }




}
