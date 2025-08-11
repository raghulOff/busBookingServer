package com.example.busbooking.dto.base;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class RoutesDTO {

    private String source;
    private String destination;

    @Min(1)
    @Max(10000)
    @NotNull (message = "distanceKm cannot be null")
    private Integer distanceKm;

    @NotNull(message = "Estimated Time is required")
    @NotEmpty(message = "Estimated Time shouldn't be empty")
    private String estimatedTime;

    private int routeId;

    @NotNull(message = "Source city ID is required")
    private Integer sourceCityId;

    @NotNull(message = "Destination city ID is required")
    private Integer destinationCityId;

    public RoutesDTO() {
    }

    public RoutesDTO( int routeId, String source, String destination, Integer distanceKm, String estimatedTime, Integer sourceCityId, Integer destinationCityId ) {
        this.source = source;
        this.destination = destination;
        this.distanceKm = distanceKm;
        this.estimatedTime = estimatedTime;
        this.routeId = routeId;
        this.sourceCityId = sourceCityId;
        this.destinationCityId = destinationCityId;

    }

    public Integer getSourceCityId() {
        return sourceCityId;
    }

    public void setSourceCityId( Integer sourceCityId ) {
        this.sourceCityId = sourceCityId;
    }

    public Integer getDestinationCityId() {
        return destinationCityId;
    }

    public void setDestinationCityId( Integer destinationCityId ) {
        this.destinationCityId = destinationCityId;
    }

    public String getSource() {
        return source;
    }

    public void setSource( String source ) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination( String destination ) {
        this.destination = destination;
    }

    public Integer getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm( Integer distanceKm ) {
        this.distanceKm = distanceKm;
    }

    public String getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime( String estimatedTime ) {
        this.estimatedTime = estimatedTime;
    }

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId( int routeId ) {
        this.routeId = routeId;
    }
}
