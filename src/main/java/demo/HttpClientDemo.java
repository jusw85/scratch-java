package demo;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class HttpClientDemo {

    public static void main(String[] args) throws Exception {
//        https://requestb.in/
        testGet("http://requestb.in/1jz1s0g1");
        testPost("http://requestb.in/1jz1s0g1");
    }

    public static void testGet(String url) throws Exception {
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        request.addHeader("Custom-Header", "foobar");

        HttpResponse response = client.execute(request);
        int responseCode = response.getStatusLine().getStatusCode();
        System.out.println("Response code: " + responseCode);

        BufferedReader br = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent())
        );
        StringBuffer result = new StringBuffer();
        String line;
        while ((line = br.readLine()) != null) {
            result.append(line).append(System.lineSeparator());
        }
        br.close();

        System.out.println("Response: ");
        System.out.println(result.toString());

    }

    public static void testPost(String url) throws Exception {
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(url);
        StringEntity params = new StringEntity("Hello World!");
        request.setEntity(params);

        HttpResponse response = client.execute(request);
        int responseCode = response.getStatusLine().getStatusCode();
        System.out.println("Response code: " + responseCode);

        String responseString = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

        System.out.println("Response: ");
        System.out.println(responseString);
    }

}
