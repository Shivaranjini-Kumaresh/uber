# Building a Uber/Lyft like service
## Overview
There are huge number of taxis who go around the city looking for rides. Typically, cabs are evenly distributed across the city. There are customers all over the place trying to find the taxis. To facilitate this, we need to keep track of taxi's current location and provide an ability to search for cabs in a given area.
## Exposed services
Taxi Location
Driver app should be able to send their current location every 60 seconds. They’ll call following API to update their location   Request:
PUT /drivers/{id}/location
{
"latitude": 12.97161923, "longitude": 77.59463452, "accuracy": 0.7 }
Response:
- 200 OK on successful update Body: {}
- 404 Not Found if the driver ID is invalid (valid driver ids - 1 to 50000) Body: {}
- 422 Un-processable Entity - with appropriate message. For example: {"errors": ["Latitude should be between +/- 90"]}

Customer applications will use following API to find drivers around a given location Request: GET /drivers
Parameters:
"latitude" - mandatory
"longitude" - mandatory
"radius" - optional defaults to 500 meters "limit" - optional defaults to 10
Response: - 200 OK
[
{id: 42, latitude: 12.97161923, longitude: 77.59463452, distance: 123}, {id: 84, latitude: 12.97161923, longitude: 77.59463452, distance: 123} ]
- 400 Bad Request - If the parameters are wrong {"errors": ["Latitude should be between +/- 90"]}
 Distance in the response is a straight line distance between driver's location and location in the query
