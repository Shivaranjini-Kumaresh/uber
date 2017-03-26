package performance.data;

/**
 * Created by sr250345 on 3/25/17.
 */
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
    private static int iterationCount = 10000;

    public static void main(String[] args) throws  Exception{


        JedisPool pool = init();
        System.out.println(" Initialization complete");

        System.out.println(" Creating test data");
        initData(pool.getResource(), key);

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
            Runnable r = new WorkerThread(pool, key,  inputDataMap.get(""+c).getLongitude(), inputDataMap.get(""+c).getLatitude(),
                                            ""+c, iterationCount, countDownLatch);
            executor.execute(r);
        }

        countDownLatch.await();

        //queryData(jedis, key);

        System.out.println(" Cleaning up");
        cleanUp(pool.getResource(), key);
        System.out.println(" Cleanup done");

        System.exit(1);
    }
    private static JedisPool init()
    {
        String redisHost = "127.0.0.1";
        Integer redisPort = 6379;

        //the jedis connection pool..
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(100);
        JedisPool pool = new JedisPool(redisHost, redisPort);
        return pool;

    }
    private static void initData(Jedis jedis, String key) throws Exception
    {
        FileInputStream fis = new FileInputStream(new File("/Users/SR250345/Documents/siddharth/personal/whereismydriver/bangalore-location-data.txt"));
        FeatureCollection featureCollection =
                new ObjectMapper().readValue(fis, FeatureCollection.class);

        Iterator<org.geojson.Feature> iterator = featureCollection.iterator();
        //Map<String, GeoCoordinate> coordinateMap = new HashMap<String, GeoCoordinate>();
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
                //System.out.println("Adding: " + lngLatAlt.getLongitude() + "," + lngLatAlt.getLatitude());
                jedis.geoadd(key, lngLatAlt.getLongitude(), lngLatAlt.getLatitude(), "" + c);
            }
        }

        System.out.println(c + " location records created");
    }
    private static void queryData(Jedis jedis, String key)
    {
        long startTime = System.currentTimeMillis();
        List<GeoRadiusResponse> members = jedis.georadius(key, 77.55105,12.840131, 10, GeoUnit.KM);
        long endTime = System.currentTimeMillis();
        System.out.println("Querying...");
        System.out.println("Result size:" + members.size() + " time taken: " + (endTime-startTime) + "Ms");
    }
    private static void cleanUp(Jedis jedis, String key)
    {
        jedis.del(key);
    }


}
