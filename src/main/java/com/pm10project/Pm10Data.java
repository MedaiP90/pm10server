package com.pm10project;

@SuppressWarnings("WeakerAccess")
public class Pm10Data {

    private Long id, timestamp;
    private Double latitude, longitude, pm10, pm2_5;

    Pm10Data(
            Long id,
            Double la,
            Double lo,
            Double p1,
            Double p2,
            Long ts
    ) {
        this.id = id;
        this.latitude = la;
        this.longitude = lo;
        this.pm10 = p1;
        this.pm2_5 = p2;
        this.timestamp = ts;
    }

    public Long getId() {
        return id;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getPm10() {
        return pm10;
    }

    public Double getPm2_5() {
        return pm2_5;
    }
}
