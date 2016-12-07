
/**
 * Created by dvm on 08.11.16.
 */
public class HeadHunterExtract {

    //clientID = V1LK39LR46Q43T9P38957539O4DQL3G6GCLJEOIHD0TFETL0UHIKHS0PI5KN9MDA
    //clientSecret = KA385K1OG9F1CGFME0O9B7FJKIUKPUUR51TRP3T022P7UGKCLHVE3GMVU42EIHJQ
    public static void main(String[] args) throws Exception {

        ResumesLoader l = new ResumesLoader();
        l.loadWithURL("https://spb.hh.ru/search/resume?age_to=50&order_by=publication_time&specialization=1.221&area=2&text=&pos=full_text&experience=moreThan6&label=only_with_age&label=only_with_salary&exp_period=all_time&logic=normal&clusters=true&age_from=20&salary_from=85000&salary_to=130000&from=cluster_salary");
        //https://spb.hh.ru/search/resume?exp_period=all_time&order_by=relevance&specialization=1&text=&pos=full_text&logic=normal&clusters=true

    }
}
