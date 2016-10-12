package test;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class JsonPathDemo {

    public static void main(String[] args) throws Exception {
        String json = FileUtils.readFileToString(new File("etc/jsonpath.json"), StandardCharsets.UTF_8);
        List<String> authors = JsonPath.read(json, "$.store.book[*].author");
        System.out.println(StringUtils.join(authors, ","));

//        Object document = Configuration.defaultConfiguration().jsonProvider().parse(json);
//
//        JsonPath path = JsonPath.compile("$.a");
////        String json = "{\"a\" : 4}";
////        int x = path.read(json);
////        JSONObject xx = new JSONObject(json);
//        DocumentContext dc = JsonPath.parse(json);
//
//
//        int x = dc.read(path);
////        int x = path.read(xx);
////        int x = JsonPath.read("{\"a\" : 4}", "$.a");
//        System.out.println(x);
    }

}
