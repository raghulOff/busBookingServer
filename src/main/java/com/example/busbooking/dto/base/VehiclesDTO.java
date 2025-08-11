package com.example.busbooking.dto.base;


import jakarta.validation.constraints.NotBlank;

public class VehiclesDTO {
    @NotBlank (message = "vehicleNumber cannot be blank")
    protected String vehicleNumber;
    protected int totalSeats;
    @NotBlank (message = "operatorName cannot be blank")
    protected String operatorName;

    public VehiclesDTO() {}

    public VehiclesDTO( String vehicleNumber, String operatorName ) {
        this.vehicleNumber = vehicleNumber;
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
