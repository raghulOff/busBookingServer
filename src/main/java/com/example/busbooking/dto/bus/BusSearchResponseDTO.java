package com.example.busbooking.dto.bus;

import java.math.BigDecimal;

public class BusSearchResponseDTO {
    private int scheduleId;
    private String busNumber;
    private String busType;
    private String departureTime;
    private String arrivalTime;
    private BigDecimal price;
    private String operatorName;
    private int distanceKm;
    private String estimateTime;


    public BusSearchResponseDTO( int scheduleId, String busNumber, String busType, String departureTime, String arrivalTime, BigDecimal price, String operatorName, int distanceKm, String estimatedTime ) {
        this.scheduleId = scheduleId;
        this.busNumber = busNumber;
        this.busType = busType;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.price = price;
        this.operatorName = operatorName;
        this.distanceKm = distanceKm;
        this.estimateTime = estimatedTime;
    }

    public int getDistanceKm() {
        return distanceKm;
    }


    public String getEstimateTime() {
        return estimateTime;
    }

    public void setEstimateTime( String estimateTime ) {
        this.estimateTime = estimateTime;
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice( BigDecimal price ) {
        this.price = price;
    }
}
