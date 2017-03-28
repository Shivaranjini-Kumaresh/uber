package com.companyx.cabservice.service;

import com.companyx.cabservice.resource.DriverLocation;
import com.companyx.cabservice.rest.LocationServiceV1;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.stereotype.Component;
import redis.clients.jedis.*;
import redis.clients.jedis.params.geo.GeoRadiusParam;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by sr250345 on 3/27/17.
 */
@Component
@PropertySource("classpath:app.properties")
public class LocationService {

    //Todo - push into config file
    String redisHost = "127.0.0.1";
    Integer redisPort = 6379;

    /*@Value("${redis.host}")
    String redisHost;
    @Value("${redis.port}")
    int redisPort;*/


    String key = "DriverLocation";
    JedisPool pool = null;

    private static final Logger logger = LoggerFactory.getLogger(LocationService.class);

    public LocationService()
    {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(128);
        pool = new JedisPool(poolConfig, redisHost, redisPort);
        //pool = new JedisPool(redisHost, redisPort);
        logger.info("Connection pool initialized");
    }
    public LocationService(String key)
    {
        pool = new JedisPool(redisHost, redisPort);
        this.key = key;
        logger.info("Connection pool initialized with key " + key);
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
            logger.error(e.getMessage(), e);
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
            logger.info("Result size : " + result.size());
        }catch(Exception e)
        {
            logger.error(e.getMessage(), e);
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
    private void populateSmallTestData()
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
    //Just for test purpose
    public  void populateBigTestData()
    {
        String filePath = "/Users/SR250345/Documents/siddharth/personal/whereismydriver/uk_pastalcode_ locations.xlsx";

        int recordCounter = 0;
        try
        {
            FileInputStream excelFile = new FileInputStream(new File(filePath));
            Workbook workbook = new XSSFWorkbook(excelFile);
            Sheet datatypeSheet = workbook.getSheetAt(0);
            Iterator<Row> iterator = datatypeSheet.iterator();
            while (iterator.hasNext())
            {
                Row currentRow = iterator.next();
                Iterator<Cell> cellIterator = currentRow.iterator();
                int c = 0;
                double latitude = 0;
                double longitude = 0;
                while (cellIterator.hasNext())
                {
                    Cell currentCell = cellIterator.next();
                    if(c == 0)
                    {
                        latitude = currentCell.getNumericCellValue();
                    }
                    else if(c == 1)
                    {
                        longitude = currentCell.getNumericCellValue();
                    }
                    ++c;
                }
                //System.out.println(latitude + "," + longitude);
                DriverLocation dl = new DriverLocation();
                dl.setDriverId("" + recordCounter);
                dl.setLatitude(latitude);
                dl.setLongitude(longitude);
                updateLocation(dl);

                recordCounter++;

            }
        } catch (Exception e)
        {
            logger.error(e.getMessage(), e);
        }
        finally
        {
            System.out.println(recordCounter + " location records created");
        }
    }
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
        return new PropertySourcesPlaceholderConfigurer();
    }

}
