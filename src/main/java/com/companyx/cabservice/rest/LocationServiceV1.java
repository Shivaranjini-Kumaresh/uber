package com.companyx.cabservice.rest;

import com.companyx.cabservice.resource.Location;
import com.companyx.cabservice.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by sr250345 on 3/27/17.
 */

@RestController
@RequestMapping("/api/v1")
public class LocationServiceV1 {

    @Autowired
    private LocationService locationService;

    @RequestMapping(method= RequestMethod.POST, value="/drivers/{id}/location", consumes= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateDriverLocation(Location location)
    {

        return null;
    }


}
