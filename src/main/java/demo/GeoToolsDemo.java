package demo;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;
import org.apache.commons.io.FileUtils;
import org.geotools.geojson.geom.GeometryJSON;
import org.json.JSONObject;

import java.io.File;
import java.nio.charset.StandardCharsets;

public class GeoToolsDemo {

    public static void main(String[] args) throws Exception {
        String geoJson = FileUtils.readFileToString(new File("etc/geojson.json"), StandardCharsets.UTF_8);
        JSONObject geometryObj = new JSONObject(geoJson);
        JSONObject polygonObj = geometryObj.getJSONObject("geometry");

        GeometryJSON geometryJSON = new GeometryJSON();
        Geometry geometry = geometryJSON.read(polygonObj.toString());

        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        double lon = 0.5;
        double lat = 0.5;
        Point point = geometryFactory.createPoint(new Coordinate(lon, lat));

        System.out.println(geometry);
        System.out.println(point);
        System.out.println(geometry.contains(point));
    }

}
