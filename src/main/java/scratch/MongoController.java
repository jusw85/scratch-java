package scratch;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.UnknownHostException;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

public class MongoController {

    private static class Driver {
        public static void main(String[] args) {
            String db = "db";
            String collection = "collection";
            populate(db, collection, new File(""));
            wipe(db, collection);
        }

        public static void print(String db, String collection) {
            JSONArray arr = MongoController.getRecords(db, collection);
            for (int i = 0; i < arr.length(); i++)
                System.out.println(arr.get(i));
        }

        public static void wipe(String db, String collection) {
            MongoController.deleteRecords(db, collection);
        }

        public static void populate(String db, String collection, File file) {
            try (Scanner sc = new Scanner(file)) {
                while (sc.hasNextLine()) {
                    String line = sc.nextLine();
                    JSONObject obj = new JSONObject(line);
                    MongoController.addRecord(db, collection, obj);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private static MongoClient mongoClient; // threadsafe and pooled

    public static void init() {
        try {
            mongoClient = new MongoClient("localhost", 27017);
        } catch (UnknownHostException e) {
            // TODO: log error
        }
    }

    public static void destroy() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }

    public static JSONArray getRecords(String dbStr, String collectionStr) {
        if (mongoClient == null)
            init();
        DB db = mongoClient.getDB(dbStr);
        DBCollection collection = db.getCollection(collectionStr);

        DBCursor cursor = collection.find();
        JSONArray arr = new JSONArray();
        try {
            while (cursor.hasNext()) {
                DBObject dbobj = cursor.next();
                JSONObject obj = new JSONObject(dbobj.toString());
                arr.put(obj);
            }
        } finally {
            cursor.close();
        }
        return arr;
    }

    public static void addRecord(String dbStr, String collectionStr,
                                 JSONObject obj) {
        if (mongoClient == null)
            init();
        DB db = mongoClient.getDB(dbStr);
        DBCollection collection = db.getCollection(collectionStr);

        DBObject doc = (DBObject) JSON.parse(obj.toString());
        collection.insert(doc);
    }

    public static void deleteRecords(String dbStr, String collectionStr) {
        if (mongoClient == null)
            init();
        DB db = mongoClient.getDB(dbStr);
        DBCollection collection = db.getCollection(collectionStr);
        collection.drop();
    }

}
