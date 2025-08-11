package com.example.busbooking.enums;


// ENUM for scheduled seat statuses
public enum ScheduledSeatStatus {
    AVAILABLE(1),
    BOOKED(2),
    BLOCKED(3),
    TRIP_COMPLETED(4);
    private final int id;

    ScheduledSeatStatus(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
