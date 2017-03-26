package com.companyx.cabservice;

import redis.clients.jedis.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sr250345 on 3/24/17.
 */
public class Test {

    public static void main(String[] args)
    {
        String redisHost = "127.0.0.1";
        Integer redisPort = 6379;

        //the jedis connection pool..
        JedisPool pool = new JedisPool(redisHost, redisPort);
        Jedis jedis = pool.getResource();

        Map<String, GeoCoordinate> coordinateMap = new HashMap<String, GeoCoordinate>();
        coordinateMap.put("Palermo", new GeoCoordinate(13.361389, 38.115556));
        coordinateMap.put("Catania", new GeoCoordinate(15.087269, 37.502669));
        jedis.geoadd("Sicily", coordinateMap);

        List<GeoRadiusResponse> members = jedis.georadius("Sicily", 15, 37, 200, GeoUnit.KM);

        System.out.println(members);

        cleanUp(jedis, "Sicily");
    }

    private static void cleanUp(Jedis jedis, String key)
    {
        jedis.del(key);
    }

}
