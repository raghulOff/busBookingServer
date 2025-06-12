package com.example.busbooking.dto;

import java.util.List;

public class ScheduleDetailsDTO {

    private String operatorName;
    private String source;
    private String busType;
    private double price;

    private String destination;
    private int scheduleId;
    private String busNumber;
    private String departureTime;
    private String arrivalTime;
    private int availableSeats;
    private List<SeatDTO> seatLayout;


    public static class SeatDTO {
        private String seatNumber;
        private boolean status;
        private int rowNumber;
        private int columnNumber;
        private int seatId;

        public String getSeatNumber() {
            return seatNumber;
        }

        public void setSeatNumber( String seatNumber ) {
            this.seatNumber = seatNumber;
        }

        public boolean isStatus() {
            return status;
        }

        public void setStatus( boolean status ) {
            this.status = status;
        }

        public int getRowNumber() {
            return rowNumber;
        }

        public void setRowNumber( int rowNumber ) {
            this.rowNumber = rowNumber;
        }

        public int getColumnNumber() {
            return columnNumber;
        }

        public void setColumnNumber( int columnNumber ) {
            this.columnNumber = columnNumber;
        }

        public int getSeatId() {
            return seatId;
        }

        public void setSeatId( int seatId ) {
            this.seatId = seatId;
        }

        public SeatDTO( String seatNumber, boolean status, int rowNumber, int columnNumber, int seatId ) {
            this.seatNumber = seatNumber;
            this.status = status;
            this.rowNumber = rowNumber;
            this.columnNumber = columnNumber;
            this.seatId = seatId;
        }
    }

    public ScheduleDetailsDTO( int scheduleId, String busNumber, String departureTime,
                               String arrivalTime, int availableSeats, List<SeatDTO> seatLayout,
                               String source, String destination, String operatorName, String busType, double price ) {
        this.scheduleId = scheduleId;
        this.busNumber = busNumber;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.availableSeats = availableSeats;
        this.seatLayout = seatLayout;
        this.operatorName = operatorName;
        this.source = source;
        this.destination = destination;
        this.busType = busType;
        this.price = price;
    }


    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName( String operatorName ) {
        this.operatorName = operatorName;
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


    public double getPrice() {
        return price;
    }

    public void setPrice( double price ) {
        this.price = price;
    }

    public int getScheduleId() {
        return scheduleId;
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

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime( String departureTime ) {
        this.departureTime = departureTime;
    }

    public String getBusType() {
        return busType;
    }

    public void setBusType( String busType ) {
        this.busType = busType;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime( String arrivalTime ) {
        this.arrivalTime = arrivalTime;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats( int availableSeats ) {
        this.availableSeats = availableSeats;
    }

    public List<SeatDTO> getSeatLayout() {
        return seatLayout;
    }

    public void setSeatLayout( List<SeatDTO> seatLayout ) {
        this.seatLayout = seatLayout;
    }

}