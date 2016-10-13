package demo;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.json.JsonOrgJsonProvider;
import com.jayway.jsonpath.spi.mapper.JsonOrgMappingProvider;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class JsonPathDemo {

    public static void main(String[] args) throws Exception {
        String json = FileUtils.readFileToString(new File("etc/jsonpath.json"), StandardCharsets.UTF_8);

        List<String> authors = JsonPath.read(json, "$.store.book[*].author");
        System.out.println(StringUtils.join(authors, ", "));

        Configuration configuration = Configuration.builder()
                .jsonProvider(new JsonOrgJsonProvider())
                .mappingProvider(new JsonOrgMappingProvider())
                .build();

        DocumentContext document = JsonPath.using(configuration).parse(json);

        JsonPath path = JsonPath.compile("$.store.book[0].author");
        String author0 = document.read(path);
        System.out.println(author0);

        JSONArray prices = document.read("$..price");
        System.out.println(StringUtils.join(prices, ", "));

        prices = document.read("$.store.book..price");
        System.out.println(StringUtils.join(prices, ", "));

        JSONArray array = document.read("$..book.length()");
        System.out.println(array.get(0));
    }

}
