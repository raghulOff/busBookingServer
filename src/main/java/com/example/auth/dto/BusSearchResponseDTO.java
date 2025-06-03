package com.example.auth.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class BusSearchResponseDTO {
    private int scheduleId;
    private String busNumber;
    private String busType;
    private String departureTime;
    private String arrivalTime;
    private int availableSeats;
    private BigDecimal price;
    private String operatorName;
    private int distanceKm;


    public BusSearchResponseDTO( int scheduleId, String busNumber, String busType, String departureTime, String arrivalTime, int availableSeats, BigDecimal price, String operatorName, int distanceKm ) {
        this.scheduleId = scheduleId;
        this.busNumber = busNumber;
        this.busType = busType;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.availableSeats = availableSeats;
        this.price = price;
        this.operatorName = operatorName;
        this.distanceKm = distanceKm;
    }

    public int getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm( int distanceKm ) {
        this.distanceKm = distanceKm;
    }

    public int getScheduleId() {
        return scheduleId;
    }

    public void setOperatorName( String operatorName ) {
        this.operatorName = operatorName;
    }

    public void setScheduleId( int scheduleId ) {
        this.scheduleId = scheduleId;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public void setBusNumber( String busNumber ) {
        this.busNumber = busNumber;
    }

    public String getBusType() {
        return busType;
    }

    public void setBusType( String busType ) {
        this.busType = busType;
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

    public String getOperatorName() {
        return operatorName;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats( int availableSeats ) {
        this.availableSeats = availableSeats;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice( BigDecimal price ) {
        this.price = price;
    }
}
