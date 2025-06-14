package com.example.busbooking.dto.base;

public class VehicleDTO {
    protected String vehicleNumber;
    protected int totalSeats;
    protected String operatorName;

    public VehicleDTO() {}

    public VehicleDTO( String vehicleNumber, int totalSeats, String operatorName ) {
        this.vehicleNumber = vehicleNumber;
        this.totalSeats = totalSeats;
        this.operatorName = operatorName;
    }



    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber( String vehicleNumber ) {
        this.vehicleNumber = vehicleNumber;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats( int totalSeats ) {
        this.totalSeats = totalSeats;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName( String operatorName ) {
        this.operatorName = operatorName;
    }

}
