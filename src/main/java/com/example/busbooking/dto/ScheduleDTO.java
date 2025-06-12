package com.example.busbooking.dto;

public class ScheduleDTO {
    private int routeId;
    private int busId;
    private String departureTime;
    private String arrivalTime;
    private double price;
    private String journeyDate;
    private int availableSeats;
    private int scheduleId;

    public ScheduleDTO() {
    }

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId( int routeId ) {
        this.routeId = routeId;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats( int availableSeats ) {
        this.availableSeats = availableSeats;
    }

    public int getBusId() {
        return busId;
    }

    public void setBusId( int busId ) {
        this.busId = busId;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime( String departureTime ) {
        this.departureTime = departureTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime( String arrivalTime ) {
        this.arrivalTime = arrivalTime;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice( double price ) {
        this.price = price;
    }

    public String getJourneyDate() {
        return journeyDate;
    }

    public void setJourneyDate( String journeyDate ) {
        this.journeyDate = journeyDate;
    }

    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId( int scheduleId ) {
        this.scheduleId = scheduleId;
    }

    public ScheduleDTO( int routeId, int busId, String departureTime, String arrivalTime, int availableSeats, double price, String journeyDate ) {
        this.routeId = routeId;
        this.busId = busId;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.price = price;
        this.availableSeats = availableSeats;
        this.journeyDate = journeyDate;
    }

    public ScheduleDTO( int scheduleId, int routeId, int busId, String departureTime, String arrivalTime, int availableSeats, double price, String journeyDate ) {
        this.routeId = routeId;
        this.busId = busId;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.price = price;
        this.availableSeats = availableSeats;
        this.journeyDate = journeyDate;
        this.scheduleId = scheduleId;
    }
}
