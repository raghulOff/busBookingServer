package com.example.busbooking.dto.base;


public class RoutesDTO {
    private String source;
    private String destination;
    private Integer distanceKm;
    private String estimatedTime;
    private int routeId;
    private int sourceCityId;
    private int destinationCityId;

    public RoutesDTO() {
    }

    public int getSourceCityId() {
        return sourceCityId;
    }

    public void setSourceCityId( int sourceCityId ) {
        this.sourceCityId = sourceCityId;
    }

    public int getDestinationCityId() {
        return destinationCityId;
    }

    public void setDestinationCityId( int destinationCityId ) {
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

    public RoutesDTO( String source, String destination, int distanceKm, String estimatedTime ) {
        this.source = source;
        this.destination = destination;
        this.distanceKm = distanceKm;
        this.estimatedTime = estimatedTime;
    }

    public RoutesDTO( int routeId, String source, String destination, int distanceKm, String estimatedTime, int sourceCityId, int destinationCityId ) {
        this.source = source;
        this.destination = destination;
        this.distanceKm = distanceKm;
        this.estimatedTime = estimatedTime;
        this.routeId = routeId;
        this.sourceCityId = sourceCityId;
        this.destinationCityId = destinationCityId;

    }
}
