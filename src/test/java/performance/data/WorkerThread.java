package performance.data;

import com.companyx.cabservice.resource.DriverLocation;
import com.companyx.cabservice.service.LocationService;
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

    LocationService ls;
    double longitude;
    double lattitude;
    String driverId;
    int iterationCount;
    CountDownLatch countDownLatch;

    public WorkerThread(LocationService ls, double longitude, double lattitude, String driverId, int iterationCount, CountDownLatch countDownLatch)
    {
        this.ls = ls;
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
            DriverLocation dl = new DriverLocation();
            dl.setDriverId(""+c);
            dl.setLongitude(longitude);
            dl.setLatitude(lattitude);
            ls.updateLocation(dl);

            List<DriverLocation> result = ls.getLocations(longitude, lattitude, 500);
            //System.out.println("Result size:" + result.size());
        }

        countDownLatch.countDown();

        long eTime = System.currentTimeMillis();
        System.out.println(Thread.currentThread().getName()+ " Number of read/writes " + iterationCount + "\t time taken: " + (eTime-sTime) + "Ms");

    }
}
