package performance.data;

/**
 * Created by sr250345 on 3/26/17.
 */
public class LatLong
{
    double longitude;
    double latitude;

    public LatLong(double longitude, double latitude)
    {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
