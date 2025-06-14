package com.example.busbooking.dto.base;

import java.util.List;

public class BookSeatDTO {
    private int userId;
    private int scheduleId;
    private int payableAmount;
    private int boardingPointId;
    private int droppingPointId;
    private List<PassengerDetailsDTO> passengerDetails;

    public BookSeatDTO() {}


    public static class PassengerDetailsDTO {
        private int seatId;
        private String passengerName;
        private int passengerAge;
        public PassengerDetailsDTO (String passengerName, int passengerAge, int seatId) {
            this.passengerAge = passengerAge;
            this.passengerName = passengerName;
            this.seatId = seatId;
        }

        public PassengerDetailsDTO () {}
        public int getSeatId() {
            return seatId;
        }

        public void setSeatId( int seatId ) {
            this.seatId = seatId;
        }

        public String getPassengerName() {
            return passengerName;
        }

        public void setPassengerName( String passengerName ) {
            this.passengerName = passengerName;
        }

        public int getPassengerAge() {
            return passengerAge;
        }

        public void setPassengerAge( int passengerAge ) {
            this.passengerAge = passengerAge;
        }

    }
    public BookSeatDTO( int userId, int scheduleId, int payableAmount, int boardingPointId, int droppingPointId, List<PassengerDetailsDTO> passengerDetails ) {
        this.userId = userId;
        this.scheduleId = scheduleId;
        this.payableAmount = payableAmount;
        this.boardingPointId = boardingPointId;
        this.droppingPointId = droppingPointId;
        this.passengerDetails = passengerDetails;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId( int userId ) {
        this.userId = userId;
    }

    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId( int scheduleId ) {
        this.scheduleId = scheduleId;
    }

    public int getPayableAmount() {
        return payableAmount;
    }

    public void setPayableAmount( int payableAmount ) {
        this.payableAmount = payableAmount;
    }

    public int getBoardingPointId() {
        return boardingPointId;
    }

    public void setBoardingPointId( int boardingPointId ) {
        this.boardingPointId = boardingPointId;
    }

    public int getDroppingPointId() {
        return droppingPointId;
    }

    public void setDroppingPointId( int droppingPointId ) {
        this.droppingPointId = droppingPointId;
    }

    public List<PassengerDetailsDTO> getPassengerDetails() {
        return passengerDetails;
    }

    public void setPassengerDetails( List<PassengerDetailsDTO> passengerDetails ) {
        this.passengerDetails = passengerDetails;
    }

}