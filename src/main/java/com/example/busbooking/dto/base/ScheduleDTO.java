package com.example.busbooking.dto.base;

public class ScheduleDTO {
    protected int routeId;
    protected int busId;
    protected String departureTime;
    protected String arrivalTime;
    protected double price;
    protected String journeyDate;
    protected int scheduleId;

    public ScheduleDTO() {
    }

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId( int routeId ) {
        this.routeId = routeId;
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

    public ScheduleDTO( int scheduleId, String departureTime, String arrivalTime, double price ) {
        this.scheduleId = scheduleId;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.price = price;
//        this.availableSeats = availableSeats;
    }

    public ScheduleDTO( int scheduleId, int routeId, int busId, String departureTime, String arrivalTime, double price, String journeyDate ) {
        this.routeId = routeId;
        this.busId = busId;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.price = price;
//        this.availableSeats = availableSeats;
        this.journeyDate = journeyDate;
        this.scheduleId = scheduleId;
    }
}
