package com.example.busbooking.dto.bus;

import com.example.busbooking.dto.base.VehicleDTO;

public class BusVehicleDTO extends VehicleDTO {
    private String busType;
    private int busId;

    public BusVehicleDTO() {}

    public BusVehicleDTO( String vehicleNumber, int totalSeats, String operatorName, int busId, String busType ) {
        super(vehicleNumber, totalSeats, operatorName);
        this.busType = busType;
        this.busId = busId;
    }


    public int getBusId() {
        return busId;
    }

    public void setBusId( int busId ) {
        this.busId = busId;
    }

    public String getBusType() {
        return busType;
    }

    public void setBusType( String busType ) {
        this.busType = busType;
    }
}

//package com.example.busbooking.dto;
//
//public class BusDTO {
//    private String busNo;
//    private String busType;
//    private int totalSeats;
//    private String operatorName;
//    private int busId;
//    public BusDTO() {}
//
//    public BusDTO( String busNo, String busType, int totalSeats, String operatorName ) {
//        this.busNo = busNo;
//        this.busType = busType;
//        this.totalSeats = totalSeats;
//        this.operatorName = operatorName;
//    }
//
//    public BusDTO( int busId, String busNo, String busType, int totalSeats, String operatorName ) {
//        this.busNo = busNo;
//        this.busId = busId;
//        this.busType = busType;
//        this.totalSeats = totalSeats;
//        this.operatorName = operatorName;
//    }
//
//    public int getBusId() {
//        return busId;
//    }
//
//    public void setBusId( int busId ) {
//        this.busId = busId;
//    }
//
//    public String getBusNo() {
//        return busNo;
//    }
//
//    public void setBusNo( String busNo ) {
//        this.busNo = busNo;
//    }
//
//    public String getBusType() {
//        return busType;
//    }
//
//    public void setBusType( String busType ) {
//        this.busType = busType;
//    }
//
//    public int getTotalSeats() {
//        return totalSeats;
//    }
//
//    public void setTotalSeats( int totalSeats ) {
//        this.totalSeats = totalSeats;
//    }
//
//    public String getOperatorName() {
//        return operatorName;
//    }
//
//    public void setOperatorName( String operatorName ) {
//        this.operatorName = operatorName;
//    }
//}
