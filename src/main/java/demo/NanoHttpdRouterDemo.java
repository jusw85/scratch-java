package demo;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.router.RouterNanoHTTPD;
import fi.iki.elonen.util.ServerRunner;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class NanoHttpdRouterDemo extends RouterNanoHTTPD {

    public NanoHttpdRouterDemo(int port) {
        super(port);
    }

    public static void main(String[] args) throws IOException {
        NanoHttpdRouterDemo server = new NanoHttpdRouterDemo(8900);
        server.addMappings();
        ServerRunner.executeInstance(server);
    }

    @Override
    public void addMappings() {
        super.addMappings();
        addRoute("/user", UserHandler.class);
        addRoute("/user/:id", UserHandler.class);
        addRoute("/user/help", GeneralHandler.class);
        addRoute("/general/:param1/:param2", GeneralHandler.class);
        addRoute("/photos/:customer_id/:photo_id", null);
        addRoute("/test", String.class);
        addRoute("/interface", UriResponder.class); // this will cause an error when called
        addRoute("/toBeDeleted", String.class);
        removeRoute("/toBeDeleted");
        addRoute("/stream", StreamUrl.class);
        addRoute("/browse/(.)+", StaticPageTestHandler.class, new File("etc").getAbsoluteFile());
    }

    public static class UserHandler extends DefaultHandler {

        @Override
        public String getText() {
            return "not implemented";
        }

        public String getText(Map<String, String> urlParams, NanoHTTPD.IHTTPSession session) {
            String text = "<html><body>User handler. Method: " + session.getMethod().toString() + "<br>";
            text += "<h1>Uri parameters:</h1>";
            for (Map.Entry<String, String> entry : urlParams.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                text += "<div> Param: " + key + "&nbsp;Value: " + value + "</div>";
            }
            text += "<h1>Query parameters:</h1>";
            for (Map.Entry<String, List<String>> entry : session.getParameters().entrySet()) {
                String key = entry.getKey();
                List<String> value = entry.getValue();
                text += "<div> Query Param: " + key + "&nbsp;Value: " + value + "</div>";
            }
            text += "</body></html>";

            return text;
        }

        @Override
        public String getMimeType() {
            return "text/html";
        }

        @Override
        public NanoHTTPD.Response.IStatus getStatus() {
            return NanoHTTPD.Response.Status.OK;
        }

        public NanoHTTPD.Response get(UriResource uriResource, Map<String, String> urlParams, NanoHTTPD.IHTTPSession session) {
            String text = getText(urlParams, session);
            ByteArrayInputStream inp = new ByteArrayInputStream(text.getBytes());
            int size = text.getBytes().length;
            return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), inp, size);
        }

    }

    static public class StreamUrl extends DefaultStreamHandler {

        @Override
        public String getMimeType() {
            return "text/plain";
        }

        @Override
        public Response.IStatus getStatus() {
            return Response.Status.OK;
        }

        @Override
        public InputStream getData() {
            return new ByteArrayInputStream("a stream of data ;-)".getBytes());
        }

    }

    public static class StaticPageTestHandler extends StaticPageHandler {

        @Override
        protected BufferedInputStream fileToInputStream(File fileOrdirectory) throws IOException {
            if ("exception.html".equals(fileOrdirectory.getName())) {
                throw new IOException("trigger something wrong");
            }
            return super.fileToInputStream(fileOrdirectory);
        }
    }

}
