package com.example.busbooking.dto.base;


import java.util.List;
import java.util.Map;


public class BookingsDTO {

    private String journeyDate;
    private double totalAmount;
    private String operatorName;
    private String sourceCity;
    private String destinationCity;
    private String arrivalTime;
    private String departureTime;
    private String boardingLocation;
    private String droppingLocation;
    private List<Map<String, Object>> passengerDetails;

    public BookingsDTO( String journeyDate, double totalAmount, String operatorName, String sourceCity, String destinationCity, String arrivalTime
            , String departureTime, String boardingLocation, String droppingLocation, List<Map<String, Object>> passengerDetails ) {
        this.passengerDetails = passengerDetails;
        this.journeyDate = journeyDate;
        this.totalAmount = totalAmount;
        this.operatorName = operatorName;
        this.sourceCity = sourceCity;
        this.destinationCity = destinationCity;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
        this.boardingLocation = boardingLocation;
        this.droppingLocation = droppingLocation;
    }



    public String getJourneyDate() {
        return journeyDate;
    }

    public void setJourneyDate( String journeyDate ) {
        this.journeyDate = journeyDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount( double totalAmount ) {
        this.totalAmount = totalAmount;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName( String operatorName ) {
        this.operatorName = operatorName;
    }

    public String getSourceCity() {
        return sourceCity;
    }

    public void setSourceCity( String sourceCity ) {
        this.sourceCity = sourceCity;
    }

    public String getDestinationCity() {
        return destinationCity;
    }

    public void setDestinationCity( String destinationCity ) {
        this.destinationCity = destinationCity;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime( String arrivalTime ) {
        this.arrivalTime = arrivalTime;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime( String departureTime ) {
        this.departureTime = departureTime;
    }

    public String getBoardingLocation() {
        return boardingLocation;
    }

    public void setBoardingLocation( String boardingLocation ) {
        this.boardingLocation = boardingLocation;
    }

    public String getDroppingLocation() {
        return droppingLocation;
    }

    public void setDroppingLocation( String droppingLocation ) {
        this.droppingLocation = droppingLocation;
    }

    public List<Map<String, Object>> getPassengerDetails() {
        return passengerDetails;
    }

    public void setPassengerDetails( List<Map<String, Object>> passengerDetails ) {
        this.passengerDetails = passengerDetails;
    }

}
