package demo;

import fi.iki.elonen.NanoHTTPD;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class NanoHttpdDemo extends NanoHTTPD {

    public NanoHttpdDemo(int port) {
        super(port);
    }

    public static void main(String[] args) throws IOException {
        NanoHttpdDemo server = new NanoHttpdDemo(0);
//        ServerRunner.executeInstance(server);

        try {
            server.start(NanoHTTPD.SOCKET_READ_TIMEOUT, true);
            System.out.println("Server listening on " + server.getListeningPort());
        } catch (IOException e) {
            System.err.println("Couldn't start server");
            System.err.println(e);
            System.exit(-1);
        }

        System.out.println("Hit Enter to stop.");

        try {
            System.in.read();
        } catch (Throwable ignored) {
        }

        server.stop();
        System.out.println("Server stopped.");
    }

    @Override
    public Response serve(IHTTPSession session) {
        String msg = "<html><body><h1>Hello server</h1>\n";
        Map<String, List<String>> parms = session.getParameters();
        if (parms.get("username") == null) {
            msg += "<form action='?' method='get'>\n  <p>Your name: <input type='text' name='username'></p>\n" + "</form>\n";
        } else {
            msg += "<p>Hello, " + parms.get("username").get(0) + "!</p>";
        }
        return newFixedLengthResponse(msg + "</body></html>\n");
    }

}
