package performance.data;

import redis.clients.jedis.GeoRadiusResponse;
import redis.clients.jedis.GeoUnit;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by sr250345 on 3/26/17.
 */

public class WorkerThread implements Runnable{

    private JedisPool jedisPool;
    private String key;
    double longitude;
    double lattitude;
    String driverId;
    int iterationCount;
    CountDownLatch countDownLatch;

    public WorkerThread(JedisPool jedisPool, String key, double longitude, double lattitude, String driverId, int iterationCount, CountDownLatch countDownLatch)
    {
        this.jedisPool = jedisPool;
        this.key = key;
        this.longitude = longitude;
        this.lattitude = lattitude;
        this.driverId = driverId;
        this.iterationCount = iterationCount;
        this.countDownLatch = countDownLatch;
    }
    @Override
    public void run()
    {
        //update location
        long sTime = System.currentTimeMillis();
        for(int c = 0; c < iterationCount; c++)
        {
            //Update location
            Jedis jedis = jedisPool.getResource();
            jedis.geoadd(key, longitude, lattitude, driverId);
            jedis.close();

            //Query
            jedis = jedisPool.getResource();
            List<GeoRadiusResponse> members = jedis.georadius(key, longitude, lattitude, 500, GeoUnit.M);
            jedis.close();
            //System.out.println("Result size:" + members.size());

        }

        countDownLatch.countDown();

        long eTime = System.currentTimeMillis();
        System.out.println(Thread.currentThread().getName()+ " Number of read/writes " + iterationCount + "\t time taken: " + (eTime-sTime) + "Ms");

    }
}
