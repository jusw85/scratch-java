package test;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;

public class GenSQLGrid {

    public static void main(String args[]) throws Exception {
//        double lonInterval = 0.01953125;
//        double latInterval = 0.01953125;
//        double lonInterval = 20;
//        double latInterval = 20;
        double interval = 40;
        int zoomlevel = 2;

        String table = "mpa_hackathon_2015.grid";
        File out = new File("grid.sql");
        FileUtils.deleteQuietly(out);
        FileUtils.writeStringToFile(out, "BEGIN;\r\n", StandardCharsets.UTF_8, true);

        for (int i = 0; i < 8; i++, zoomlevel += 1) {
            interval /= 2.0;
            double lonInterval = interval;
            double latInterval = interval;
            for (double lon = -180; lon <= 180
                    - lonInterval; lon += lonInterval) {
                for (double lat = -90; lat <= 90
                        - latInterval; lat += latInterval) {
                    StringBuffer sb = new StringBuffer();
                    String sw = lon + " " + lat;
                    String se = (lon + lonInterval) + " " + lat;
                    String ne = (lon + lonInterval) + " " + (lat + latInterval);
                    String nw = lon + " " + (lat + latInterval);
                    sb.append(" INSERT INTO ").append(table);
                    sb.append(" VALUES (ST_SetSRID(ST_MakePolygon( ");
                    sb.append(" ST_GeomFromText('LINESTRING(");
                    sb.append(sw).append(",");
                    sb.append(nw).append(",");
                    sb.append(ne).append(",");
                    sb.append(se).append(",");
                    sb.append(sw);
                    sb.append(")') ");
                    sb.append(" ),4326), '" + zoomlevel + "'); ");
                    FileUtils.writeStringToFile(out, sb.toString() + "\r\n",
                            StandardCharsets.UTF_8, true);
                }
            }
        }
        FileUtils.writeStringToFile(out, "COMMIT;\r\n", StandardCharsets.UTF_8, true);
    }

}
