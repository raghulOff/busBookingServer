package com.example.busbooking.enums;


// ENUM for schedule statuses
public enum ScheduleStatus {
    ACTIVE(1),
    CANCELLED(2),
    DELAYED(3),
    COMPLETED(4);
    private final int id;

    ScheduleStatus(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
