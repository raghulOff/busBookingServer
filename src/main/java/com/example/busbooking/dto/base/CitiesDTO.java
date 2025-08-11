package com.example.busbooking.dto.base;


import jakarta.validation.constraints.NotBlank;

public class CitiesDTO {
    @NotBlank (message = "cityName cannot be blank")
    private String cityName;
    private Integer cityId;

    public CitiesDTO() {
    }

    public CitiesDTO( String cityName ) {
        this.cityName = cityName;
    }
    public CitiesDTO( Integer cityId, String cityName ) {
        this.cityName = cityName;
        this.cityId = cityId;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId( Integer cityId ) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName( String cityName ) {
        this.cityName = cityName;
    }
}
