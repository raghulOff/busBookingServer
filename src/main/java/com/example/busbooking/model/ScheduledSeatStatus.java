package com.example.busbooking.model;

public enum ScheduledSeatStatus {
    AVAILABLE(1),
    BOOKED(2),
    BLOCKED(3);

    private final int id;

    ScheduledSeatStatus(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
