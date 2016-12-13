import com.mongodb.*;
import com.mongodb.util.JSON;

import java.net.UnknownHostException;

/**
 * Created by dvm on 07.12.16.
 */
public class MongoManager {

    DBCollection resumeCollection;

    public MongoManager() {

        try {
            MongoClient mongo = new MongoClient( "localhost" , 27017 );
            DB db = mongo.getDB("KTHeadHunter");
            DBCollection table = db.getCollection("resumes");
            resumeCollection = table;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public boolean insertResumeJson(String json){
        DBObject dbObject = (DBObject) JSON.parse(json);
        try{
            WriteResult result = resumeCollection.insert(dbObject);
            CommandResult cmdResult = result.getLastError();

            if (!cmdResult.ok()){
                System.out.println("Error : " + cmdResult.getErrorMessage());
                return false;
            }
        } catch (MongoException e){
            if (e.getCode() == 11000){
                //System.out.println("ResumesCollection already has this resume");
                return false;
            } else {
                e.printStackTrace();
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
