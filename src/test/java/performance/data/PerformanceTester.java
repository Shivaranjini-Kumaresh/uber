package performance.data;

/**
 * Created by sr250345 on 3/25/17.
 */
import com.companyx.cabservice.resource.DriverLocation;
import com.companyx.cabservice.service.LocationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.geojson.*;
import org.geojson.Feature;
import org.geojson.Geometry;
import redis.clients.jedis.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class PerformanceTester {

    private static String key = "bangalore-locations";
    private static int threadCount = 20;
    private static int iterationCount = 1000;

    static LocationService locationService;

    public static void main(String[] args) throws  Exception{

        locationService = new LocationService(key);
        System.out.println(" Initialization complete");

        System.out.println(" Creating test data");
        initData(locationService);

        Executor executor   = Executors.newFixedThreadPool(threadCount);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        HashMap<String,LatLong> inputDataMap = new HashMap<>();
        inputDataMap.put("1",  new LatLong(77.733197,12.994827));
        inputDataMap.put("2",  new LatLong(77.73401,12.996837));
        inputDataMap.put("3",  new LatLong(77.739136,12.997156));
        inputDataMap.put("4",  new LatLong(77.742475,12.998653));
        inputDataMap.put("5",  new LatLong(77.743061,12.999787));
        inputDataMap.put("6",  new LatLong(77.742617,13.002111));
        inputDataMap.put("7",  new LatLong(77.742762,13.00427));
        inputDataMap.put("8",  new LatLong(77.749299,13.008952));
        inputDataMap.put("9",  new LatLong(77.746156,13.017331));
        inputDataMap.put("10",  new LatLong(77.74815,13.022042));
        inputDataMap.put("11",  new LatLong(77.753561,13.025442));
        inputDataMap.put("12",  new LatLong(77.754374,13.031832));
        inputDataMap.put("13",  new LatLong(77.755762,13.03263));
        inputDataMap.put("14",  new LatLong(77.763378,13.043993));
        inputDataMap.put("15",  new LatLong(77.764424,13.043905));
        inputDataMap.put("16",  new LatLong(77.774969,13.049062));
        inputDataMap.put("17",  new LatLong(77.777512,13.048584));
        inputDataMap.put("18",  new LatLong(77.779738,13.04696));
        inputDataMap.put("19",  new LatLong(77.780463,13.044322));
        inputDataMap.put("20",  new LatLong(77.781389,13.04363));

        for(int c = 1; c <= threadCount; c++)
        {
            Runnable r = new WorkerThread(locationService,  inputDataMap.get(""+c).getLongitude(), inputDataMap.get(""+c).getLatitude(),
                                            ""+c, iterationCount, countDownLatch);
            executor.execute(r);
        }

        countDownLatch.await();

        System.out.println(" Cleaning up");
        locationService.cleanUp(key);
        System.out.println(" Cleanup done");

        System.exit(1);
    }
    private static void initData(LocationService ls) throws Exception
    {
        FileInputStream fis = new FileInputStream(new File("/Users/SR250345/Documents/siddharth/personal/whereismydriver/bangalore-location-data.txt"));
        FeatureCollection featureCollection =
                new ObjectMapper().readValue(fis, FeatureCollection.class);

        Iterator<org.geojson.Feature> iterator = featureCollection.iterator();
        int c = 0;
        while(iterator.hasNext())
        {
            Feature f = iterator.next();
            Object pincode = f.getProperties().get("pincode");
            Geometry geometry = (Geometry)f.getGeometry();
            List coordinatesP = geometry.getCoordinates();
            List<LngLatAlt> coordinates = (List)coordinatesP.get(0);
            for(LngLatAlt lngLatAlt : coordinates)
            {
                ++c;
                DriverLocation dl = new DriverLocation();
                dl.setDriverId(""+c);
                dl.setLongitude(lngLatAlt.getLongitude());
                dl.setLatitude(lngLatAlt.getLatitude());
                ls.updateLocation(dl);
            }
        }

        System.out.println(c + " location records created");
    }
}
