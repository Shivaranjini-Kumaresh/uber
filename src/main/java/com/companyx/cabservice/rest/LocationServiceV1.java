package com.companyx.cabservice.rest;

import com.companyx.cabservice.resource.DriverLocation;
import com.companyx.cabservice.resource.Location;
import com.companyx.cabservice.service.LocationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by sr250345 on 3/27/17.
 */

@RestController
@RequestMapping("/api/v1")
public class LocationServiceV1 {

    @Autowired
    private LocationService locationService;

    private static final Logger logger = LoggerFactory.getLogger(LocationServiceV1.class);

    @RequestMapping(method= RequestMethod.GET, value="/", produces= MediaType.APPLICATION_JSON_VALUE)
    public String helloworld()
    {
        logger.info("helloworld()");
        return "Hello world";
    }
    @RequestMapping(method= RequestMethod.POST, value="/drivers/{id}/location", consumes= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateDriverLocation(@PathVariable("id") String driverId, @RequestBody Location location)
    {
        logger.info("updateDriverLocation()" + "driverId: " + driverId + ", Latitude: " +
                        location.getLatitude()+ ", Longitude: " + location.getLongitude());
        //check valid driver
        int dId = Integer.parseInt(driverId);
        if(dId < 1 || dId > 50000)
        {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        //check valid latitude & longitude
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        //latitude should be between -90 to 90
        //longitude should be between -180 to 180
        if(latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180)
        {
            logger.error("Invalid latitude/longitude");
            return new ResponseEntity(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        DriverLocation dl = new DriverLocation();
        dl.setDriverId(driverId);
        dl.setLongitude(location.getLongitude());
        dl.setLatitude(location.getLatitude());
        locationService.updateLocation(dl);
        return new ResponseEntity(HttpStatus.OK);
    }
    @RequestMapping(method= RequestMethod.GET, value="/drivers", produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DriverLocation>> getNearestDrivers(@RequestParam("longitude") double longitude,
                                                                @RequestParam("latitude") double latitude, @RequestParam("radius") double radius)
    {
        logger.info("getNearestDrivers()" + ", radius: " + radius + ", Latitude: " +
                latitude + ", Longitude: " + longitude);

        if(latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180)
        {
            logger.error("Invalid latitude/longitude");
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        List<DriverLocation> result = locationService.getLocations(longitude, latitude, radius);
        return new ResponseEntity<List<DriverLocation>>(result, HttpStatus.OK);
    }

    //use to create 50K test records
    @RequestMapping(method= RequestMethod.POST, value="/populate", produces= MediaType.APPLICATION_JSON_VALUE)
    public String populate50KRecords()
    {
        logger.info("populate50Krecords()");
        locationService.populateBigTestData();
        return "populated 50K test records";
    }



}
