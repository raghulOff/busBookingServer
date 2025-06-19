package com.example.busbooking.dto.bus;

import com.example.busbooking.dto.base.ScheduleDTO;

import java.util.List;

public class BusScheduleDetailsDTO extends ScheduleDTO {
    private int totalColumns;
    private String operatorName;
    private String source;
    private String busType;
    private String destination;
    private String busNumber;
    private List<SeatDTO> seatLayout;




    public static class SeatDTO {
        private String seatTypeName;

        private String seatNumber;
        private String status;
        private int rowNumber;
        private int columnNumber;
        private int seatId;
        private String pos;
        private int seatTypeId;


        public String getSeatTypeName() {
            return seatTypeName;
        }

        public void setSeatTypeName( String seatTypeName ) {
            this.seatTypeName = seatTypeName;
        }

        public String getStatus() {
            return status;
        }

        public int getSeatTypeId() {
            return seatTypeId;
        }

        public void setSeatTypeId( int seatTypeId ) {
            this.seatTypeId = seatTypeId;
        }

        public String getPos() {
            return pos;
        }

        public void setPos( String pos ) {
            this.pos = pos;
        }

        public String getSeatNumber() {
            return seatNumber;
        }

        public void setSeatNumber( String seatNumber ) {
            this.seatNumber = seatNumber;
        }

        public String  isStatus() {
            return status;
        }

        public void setStatus( String status ) {
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

        public SeatDTO( String seatTypeName, int seatTypeId, String pos, String seatNumber, String status, int rowNumber, int columnNumber, int seatId ) {
            this.seatTypeId = seatTypeId;
            this.seatNumber = seatNumber;
            this.status = status;
            this.rowNumber = rowNumber;
            this.columnNumber = columnNumber;
            this.seatId = seatId;
            this.pos = pos;
            this.seatTypeName = seatTypeName;
        }
    }

    public BusScheduleDetailsDTO( int routeId, int busId, int scheduleId, String busNumber, String departureTime,
                                  String arrivalTime, List<SeatDTO> seatLayout,
                                  String source, String destination, String operatorName,
                                  String busType, double price, String journeyDate,
                                  int totalColumns) {

        super(scheduleId, routeId, busId, departureTime, arrivalTime, price, journeyDate);
        this.busNumber = busNumber;
        this.seatLayout = seatLayout;
        this.operatorName = operatorName;
        this.source = source;
        this.destination = destination;
        this.busType = busType;
        this.totalColumns = totalColumns;

    }

    public int getTotalColumns() {
        return totalColumns;
    }

    public void setTotalColumns( int totalColumns ) {
        this.totalColumns = totalColumns;
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