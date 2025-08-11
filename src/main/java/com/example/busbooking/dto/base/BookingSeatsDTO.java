package com.example.busbooking.dto.base;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class BookingSeatsDTO {

    @NotNull(message = "User ID is required.")
    private Integer userId;
    @NotNull(message = "Schedule ID is required.")
    private Integer scheduleId;
    @NotNull(message = "Payable Amount is required.")
    private Integer payableAmount;
    @NotNull(message = "Boarding point ID is required.")
    private Integer boardingPointId;
    @NotNull(message = "Dropping point ID is required.")
    private Integer droppingPointId;
    @NotNull(message = "Passenger details are required.")
    @Valid
    private List<PassengerDetailsDTO> passengerDetails;



    public static class PassengerDetailsDTO {

        @NotNull(message = "Scheduled seat ID required")
        private Integer scheduledSeatId;

        @NotNull(message = "Passenger name is required")
        @NotEmpty(message = "Passenger name shouldn't be empty")
        private String passengerName;

        @NotNull(message = "Passenger age is required")
        @Min(0)
        @Max(100)
        private Integer passengerAge;

        public PassengerDetailsDTO() {
        }

        public Integer getPassengerAge() {
            return passengerAge;
        }

        public void setPassengerAge( Integer passengerAge ) {
            this.passengerAge = passengerAge;
        }

        public String getPassengerName() {
            return passengerName;
        }

        public void setPassengerName( String passengerName ) {
            this.passengerName = passengerName;
        }

        public Integer getScheduledSeatId() {
            return scheduledSeatId;
        }

        public void setScheduledSeatId( Integer scheduledSeatId ) {
            this.scheduledSeatId = scheduledSeatId;
        }


    }

    public BookingSeatsDTO() {
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId( Integer userId ) {
        this.userId = userId;
    }

    public Integer getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId( Integer scheduleId ) {
        this.scheduleId = scheduleId;
    }

    public Integer getPayableAmount() {
        return payableAmount;
    }

    public void setPayableAmount( Integer payableAmount ) {
        this.payableAmount = payableAmount;
    }

    public Integer getBoardingPointId() {
        return boardingPointId;
    }

    public void setBoardingPointId( Integer boardingPointId ) {
        this.boardingPointId = boardingPointId;
    }

    public Integer getDroppingPointId() {
        return droppingPointId;
    }

    public void setDroppingPointId( Integer droppingPointId ) {
        this.droppingPointId = droppingPointId;
    }

    public List<PassengerDetailsDTO> getPassengerDetails() {
        return passengerDetails;
    }

    public void setPassengerDetails( List<PassengerDetailsDTO> passengerDetails ) {
        this.passengerDetails = passengerDetails;
    }


}