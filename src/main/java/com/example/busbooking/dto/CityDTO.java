package com.example.busbooking.dto;

public class CityDTO {
    private String cityName;
    private int cityId;

    public CityDTO() {
    }

    public CityDTO( String cityName ) {
        this.cityName = cityName;
    }
    public CityDTO( int cityId, String cityName ) {
        this.cityName = cityName;
        this.cityId = cityId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId( int cityId ) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName( String cityName ) {
        this.cityName = cityName;
    }
}
