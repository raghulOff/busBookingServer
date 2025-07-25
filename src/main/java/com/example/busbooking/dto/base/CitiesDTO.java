package com.example.busbooking.dto.base;


public class CitiesDTO {
    private String cityName;
    private int cityId;

    public CitiesDTO() {
    }

    public CitiesDTO( String cityName ) {
        this.cityName = cityName;
    }
    public CitiesDTO( int cityId, String cityName ) {
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
