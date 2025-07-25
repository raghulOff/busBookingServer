package com.example.busbooking.model;

public enum ScheduleStatus {
    ACTIVE(1),
    CANCELLED(2),
    DELAYED(3);

    private final int id;

    ScheduleStatus(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
