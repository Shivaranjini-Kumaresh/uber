package unittest;

import com.companyx.cabservice.resource.DriverLocation;
import com.companyx.cabservice.service.LocationService;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import performance.data.LatLong;
import redis.clients.jedis.*;
import redis.clients.jedis.params.geo.GeoRadiusParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sr250345 on 3/24/17.
 */
public class BasicTest {

    static String key = "testkey";
    static Jedis jedis = null;

    static LocationService locationService;

    @BeforeClass
    public static void init()
    {
        locationService = new LocationService(key);
    }

    @Test
    public void updateAndQueryLocation()
    {
        DriverLocation dl1 = new DriverLocation();
        dl1.setDriverId("1");
        dl1.setLongitude(77.733197);
        dl1.setLatitude(12.994827);
        locationService.updateLocation(dl1);

        DriverLocation dl2 = new DriverLocation();
        dl1.setDriverId("2");
        dl1.setLongitude(77.73401);
        dl1.setLatitude(12.996837);
        locationService.updateLocation(dl1);

        List<DriverLocation> result = locationService.getLocations(77.733197, 12.994827, 1000);
        Assert.assertNotNull(result);
        Assert.assertEquals(2, result.size());
    }

    @AfterClass
    public static void cleanup()
    {
        //locationService.cleanUp(key);
    }
}
