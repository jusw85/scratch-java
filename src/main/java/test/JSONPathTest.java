package test;

import org.json.JSONObject;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

public class JSONPathTest {

    public static void main(String[] args) throws Exception {
        JsonPath path = JsonPath.compile("$.b");
        String json = "{\"a\" : 4}";
//        int x = path.read(json);
//        JSONObject xx = new JSONObject(json);
        DocumentContext dc = JsonPath.parse(json);
        
        
        int x = dc.read(path);
//        int x = path.read(xx);
//        int x = JsonPath.read("{\"a\" : 4}", "$.a");
        System.out.println(x);
    }

}
