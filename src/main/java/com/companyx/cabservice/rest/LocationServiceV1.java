package com.companyx.cabservice.rest;

import com.companyx.cabservice.resource.DriverLocation;
import com.companyx.cabservice.resource.Location;
import com.companyx.cabservice.service.LocationService;
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

    @RequestMapping(method= RequestMethod.GET, value="/", produces= MediaType.APPLICATION_JSON_VALUE)
    public String helloworld()
    {
        return "Hello world";
    }
    @RequestMapping(method= RequestMethod.POST, value="/drivers/{id}/location", consumes= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateDriverLocation(@PathVariable("id") String driverId, @RequestBody Location location)
    {
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
        List<DriverLocation> result = locationService.getLocations(longitude, latitude, radius);
        return new ResponseEntity<List<DriverLocation>>(result, HttpStatus.OK);
    }


}
