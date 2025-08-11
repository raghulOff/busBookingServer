package com.example.busbooking.dto.bus;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class BusSearchRequestDTO {
    @NotNull (message = "DOJ is required")
    @NotEmpty (message = "DOJ cannot be empty")
    private String doj;

    @NotNull (message = "From city ID is required")
    private Integer fromCityId;

    @NotNull (message = "To city ID is required")
    private Integer toCityId;

    public Integer getFromCityId() {
        return fromCityId;
    }

    public void setFromCityId( Integer fromCityId ) {
        this.fromCityId = fromCityId;
    }

    public Integer getToCityId() {
        return toCityId;
    }

    public void setToCityId( Integer toCityId ) {
        this.toCityId = toCityId;
    }

    public String getDoj() {
        return doj;
    }

    public void setDoj( String doj ) {
        this.doj = doj;
    }

}
