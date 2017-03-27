package com.companyx.cabservice.service;

import com.companyx.cabservice.resource.DriverLocation;
import redis.clients.jedis.GeoRadiusResponse;
import redis.clients.jedis.GeoUnit;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.geo.GeoRadiusParam;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sr250345 on 3/27/17.
 */
public class LocationService {

    String redisHost = "127.0.0.1";
    Integer redisPort = 6379;
    String key = "DriverLocation2";
    JedisPool pool = null;

    public LocationService()
    {
        pool = new JedisPool(redisHost, redisPort);
    }
    public LocationService(String key)
    {
        pool = new JedisPool(redisHost, redisPort);
        this.key = key;

        initializeTestData();
    }
    public void updateLocation(DriverLocation dl)
    {
        Jedis jedis = null;
        try
        {
            jedis = pool.getResource();
            long response = jedis.geoadd(key, dl.getLongitude(), dl.getLatitude(), dl.getDriverId());


        }catch(Exception e)
        {
            //log

        }
        finally
        {
            jedis.close();
        }
    }

    public List<DriverLocation> getLocations(double longitude, double latitude, double radius)
    {
        List<DriverLocation> result = new ArrayList<>();
        Jedis jedis = null;
        try
        {
            jedis = pool.getResource();
            List<GeoRadiusResponse> members = jedis.georadius(key, longitude, latitude, radius, GeoUnit.M, GeoRadiusParam.geoRadiusParam().withCoord().withDist());

            for(GeoRadiusResponse geoRadiusResponse : members)
            {
                DriverLocation dl = new DriverLocation();
                dl.setDistance(geoRadiusResponse.getDistance());
                dl.setLatitude(geoRadiusResponse.getCoordinate().getLatitude());
                dl.setLongitude(geoRadiusResponse.getCoordinate().getLongitude());
                dl.setDriverId(geoRadiusResponse.getMemberByString());

                result.add(dl);
            }
        }catch(Exception e)
        {
            //log
            e.printStackTrace();
        }
        finally
        {
            jedis.close();
        }
        return result;
    }
    public void cleanUp(String key)
    {
        Jedis jedis = pool.getResource();
        jedis.del(key);
        jedis.close();

    }

    private void initializeTestData()
    {
        DriverLocation dl1 = new DriverLocation();
        dl1.setDriverId("1");
        dl1.setLongitude(77.733197);
        dl1.setLatitude(12.994827);
        this.updateLocation(dl1);

        DriverLocation dl2 = new DriverLocation();
        dl1.setDriverId("2");
        dl1.setLongitude(77.73401);
        dl1.setLatitude(12.996837);
        this.updateLocation(dl1);
    }

}
