package com.example.auth.dto;

public class RouteDTO {
    private String source;
    private String destination;
    private int distanceKm;
    private String estimatedTime;
    private int routeId;

    public RouteDTO() {
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

    public int getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm( int distanceKm ) {
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

    public RouteDTO( String source, String destination, int distanceKm, String estimatedTime ) {
        this.source = source;
        this.destination = destination;
        this.distanceKm = distanceKm;
        this.estimatedTime = estimatedTime;
    }

    public RouteDTO( int routeId, String source, String destination, int distanceKm, String estimatedTime ) {
        this.source = source;
        this.destination = destination;
        this.distanceKm = distanceKm;
        this.estimatedTime = estimatedTime;
        this.routeId = routeId;
    }
}
