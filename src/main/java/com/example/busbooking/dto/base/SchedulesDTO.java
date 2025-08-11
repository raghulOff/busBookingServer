package com.example.busbooking.dto.base;


import jakarta.validation.constraints.*;

import java.util.List;

public class SchedulesDTO {

    @NotNull (message = "Route ID is required")
    protected Integer routeId;

    @NotNull (message = "Bus ID is required")
    protected Integer busId;

    @NotBlank(message = "Departure time is required")
    protected String departureTime;

    @NotBlank (message = "Arrival time shouldn't be empty.")
    protected String arrivalTime;

    @NotNull (message = "Price is required")
    @Min(0) @Max(20000)
    protected Double price;

    @NotBlank (message = "Journey date shouldn't be empty.")
    protected String journeyDate;

    protected Integer scheduleId;

    protected List<Integer> boardingPointIds;

    protected List<Integer> droppingPointIds;

    protected Integer statusId;

    public SchedulesDTO() {
    }


    public SchedulesDTO( int scheduleId, int routeId, int busId, String departureTime, String arrivalTime, double price, String journeyDate, Integer status_id ) {
        this.routeId = routeId;
        this.busId = busId;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.price = price;
        this.journeyDate = journeyDate;
        this.scheduleId = scheduleId;
        this.statusId = status_id;
    }
    public SchedulesDTO( int scheduleId, int routeId, int busId, String departureTime, String arrivalTime, double price, String journeyDate) {
        this.routeId = routeId;
        this.busId = busId;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.price = price;
        this.journeyDate = journeyDate;
        this.scheduleId = scheduleId;
    }

    public Integer getRouteId() {
        return routeId;
    }

    public void setRouteId( Integer routeId ) {
        this.routeId = routeId;
    }

    public Integer getBusId() {
        return busId;
    }

    public void setBusId( Integer busId ) {
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

    public Double getPrice() {
        return price;
    }

    public void setPrice( Double price ) {
        this.price = price;
    }

    public String getJourneyDate() {
        return journeyDate;
    }

    public void setJourneyDate( String journeyDate ) {
        this.journeyDate = journeyDate;
    }

    public Integer getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId( Integer scheduleId ) {
        this.scheduleId = scheduleId;
    }

    public List<Integer> getDroppingPointIds() {
        return droppingPointIds;
    }

    public void setDroppingPointIds( List<Integer> droppingPointIds ) {
        this.droppingPointIds = droppingPointIds;
    }

    public List<Integer> getBoardingPointIds() {
        return boardingPointIds;
    }

    public void setBoardingPointIds( List<Integer> boardingPointIds ) {
        this.boardingPointIds = boardingPointIds;
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId( Integer statusId ) {
        this.statusId = statusId;
    }

}
