package scratch;

import com.google.common.base.Optional;
import com.google.common.io.ByteStreams;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Snippets {

    private static final Logger LOGGER = LogManager.getLogger();

    public static String trimHorizontalSpace(String str) {
//        String horizontalSpace = "[ \\t\\n\\xA0\\u1680\\u180e\\u2000-\\u200a\\u202f\\u205f\\u3000]"; // pre java 8
        String horizontalSpace = "\\h";
        str = str.replaceAll("(^" + horizontalSpace + "*)|(" + horizontalSpace + "*$)", "");
        return str;
    }

    public static String beanToXMLString(final Object bean) {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final XMLEncoder xmlEncoder = new XMLEncoder(new BufferedOutputStream(byteArrayOutputStream));
        xmlEncoder.writeObject(bean);
        xmlEncoder.close();
        return byteArrayOutputStream.toString();
    }

    public static Object beanFromXMLString(final String xml) {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(xml.getBytes());
        final XMLDecoder xmlDecoder = new XMLDecoder(new BufferedInputStream(byteArrayInputStream));
        Object returnvalue = xmlDecoder.readObject();
        xmlDecoder.close();
        return returnvalue;
    }

    public static URL getClassLocation(Class clazz) {
        URL location = clazz.getResource('/' + clazz.getName().replace('.', '/') + ".class");
        return location;
    }

    public static void runCommandLine(String cmd) {
        CommandLine cmdLine = CommandLine.parse(cmd);
        DefaultExecutor executor = new DefaultExecutor();
        try {
            executor.execute(cmdLine);
        } catch (IOException e) {
        }
    }

    public static String normalizeString(String str) {
        str = str.replace((char) 8216, '\'') // ‘
                .replace((char) 8217, '\'') // ’
                .replace((char) 8211, '-') // –
                .replace((char) 8220, '"') // “
                .replace((char) 8221, '"') // ”
                .replace(Character.toString((char) 8230), "...") // …
                .replace((char) 160, ' ') // nbsp
                .replace((char) 176, ' ') // °
                .replace((char) 186, ' ') // º
                .replace((char) 21, ' ') // nak
                .replace((char) 20, ' ') // dc4
                .replace((char) 19, ' ') // dc3
                .replace((char) 8, ' ') // backspace
                .replace((char) 7, ' ') // bel
                .replace((char) 1, ' ') // nul
        ;
        return str;
    }

    public static Optional<byte[]> downloadFile(String url) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet(url);
        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream instream = entity.getContent();
                try {
                    byte[] bytes = ByteStreams.toByteArray(instream);
                    return Optional.of(bytes);
                } finally {
                    instream.close();
                }
            }
        } finally {
            if (response != null)
                response.close();
        }
        return Optional.absent();
    }

    public static String toBase64(String str) {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encode = encoder.encode(str.getBytes(StandardCharsets.UTF_8));
        return new String(encode, StandardCharsets.US_ASCII);
    }
}
