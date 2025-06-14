package com.example.busbooking.dto.bus;

import com.example.busbooking.dto.base.ScheduleDTO;

import java.util.List;

public class BusScheduleDetailsDTO extends ScheduleDTO {

    private String operatorName;
    private String source;
    private String busType;
    private String destination;
    private String busNumber;
    private List<SeatDTO> seatLayout;


//    private int scheduleId;
//    private double price;
//    private String departureTime;
//    private String arrivalTime;
//    private int availableSeats;

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

    public BusScheduleDetailsDTO( int scheduleId, String busNumber, String departureTime,
                                  String arrivalTime, int availableSeats, List<SeatDTO> seatLayout,
                                  String source, String destination, String operatorName, String busType, double price ) {

        super(scheduleId, departureTime, arrivalTime, availableSeats, price);
        this.busNumber = busNumber;
        this.seatLayout = seatLayout;
        this.operatorName = operatorName;
        this.source = source;
        this.destination = destination;
        this.busType = busType;
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

    public List<SeatDTO> getSeatLayout() {
        return seatLayout;
    }

    public void setSeatLayout( List<SeatDTO> seatLayout ) {
        this.seatLayout = seatLayout;
    }

}