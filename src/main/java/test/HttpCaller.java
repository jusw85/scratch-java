package test;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class HttpCaller {

    public static void main(String[] args) throws Exception {
//        System.setProperty("org.apache.commons.logging.Log","org.apache.commons.logging.impl.SimpleLog");
//        System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
//        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "DEBUG");

        HttpCaller kc = new HttpCaller();
//        String shipId = "44937d26-441b-4503-8151-24087a0bdfd2";
        kc.deleteCaller("", "");
//        kc.getCaller("", "");
    }

    public void getCaller(String url, String theId) {
        HttpClient client = HttpClientBuilder.create().build();
        try {
            HttpGet request = new HttpGet("http://192.168.1.151/api/data2/ship-movement-reading");
//            request.addHeader("shipId", theId);
            request.addHeader("apikey", "3a2e140d697447f486095554fbf3cfe0");

            HttpResponse response = client.execute(request);
            int responseCode = response.getStatusLine().getStatusCode();
            if (responseCode == 200) {
                System.out.println("Success");
            } else if (responseCode == 400) {
                System.out.println("Wraps multiple exception conditions");
            } else {
                System.out.println("Other exceptions");
            }
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent())
            );
            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                result.append(line);
            }
            System.out.println(result.toString());
            br.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

//    public void postCaller(String url, JSONObject jsonObj) {
//        HttpClient client = HttpClientBuilder.create().build();
//        try {
//            HttpPost request = new HttpPost(url);
//            StringEntity params = new StringEntity(jsonObj.toString());
//            request.addHeader("Content-Type", "application/json");
//            request.addHeader("Accept", "application/json");
//            request.addHeader("apikey", KlaverConstants.API_KEY);
//            request.setEntity(params);
//            HttpResponse response = client.execute(request);
//            int responseCode = response.getStatusLine().getStatusCode();
//            if (responseCode == 201) {
//                System.out.println("Success");
//            } else if (responseCode == 400) {
//                System.out.println("Wraps multiple exception conditions");
//            } else {
//                System.out.println("Other exceptions");
//            }
//            BufferedReader br = new BufferedReader(
//                    new InputStreamReader(response.getEntity().getContent())
//            );
//            StringBuffer result = new StringBuffer();
//            String line = "";
//            while ((line = br.readLine()) != null) {
//                result.append(line);
//            }
//            System.out.println(result.toString());
//            br.close();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }

//    public void patchCaller(String url, JSONObject jsonObj, String theId) {
//        HttpClient client = HttpClientBuilder.create().build();
//        try {
//            HttpPatch request = new HttpPatch(url);
//            StringEntity params = new StringEntity(jsonObj.toString());
//            request.addHeader("Content-Type", "application/json");
//            request.addHeader("Accept", "application/json");
//            request.addHeader("apikey", KlaverConstants.API_KEY);
//            request.addHeader("shipId", theId);
//            request.setEntity(params);
//            //request.addHeader("body", jsonObj.toString());
//            HttpResponse response = client.execute(request);
//            int responseCode = response.getStatusLine().getStatusCode();
//            if (responseCode == 204) {
//                System.out.println("Success");
//            } else if (responseCode == 400) {
//                System.out.println("Wraps multiple exception conditions");
//            } else {
//                System.out.println("Other exceptions");
//            }
//            BufferedReader br = new BufferedReader(
//                    new InputStreamReader(response.getEntity().getContent())
//            );
//            StringBuffer result = new StringBuffer();
//            String line = "";
//            while ((line = br.readLine()) != null) {
//                result.append(line);
//            }
//            System.out.println(result.toString());
//            br.close();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }

    public void deleteCaller(String url, String theId) throws IOException {
        HttpClient client = HttpClientBuilder.create().build();
        try {
            HttpDelete request = new HttpDelete("http://192.168.1.151/api/data2/ship-movement-reading/c69f838f-4bae-445d-8cbe-75822854ea49");
            request.addHeader("Accept", "application/json");
            request.addHeader("Content-Type", "application/json");
            request.addHeader("apikey", "3a2e140d697447f486095554fbf3cfe0");
            HttpResponse response = client.execute(request);

            int responseCode = response.getStatusLine().getStatusCode();
            if (responseCode == 204) {
                System.out.println("Success");
            } else if (responseCode == 400) {
                System.out.println("Wraps multiple exception conditions");
            } else {
                System.out.println("Other exceptions");
            }
        } finally {

        }
    }

}
