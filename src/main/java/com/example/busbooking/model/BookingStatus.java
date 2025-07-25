package com.example.busbooking.model;

public enum BookingStatus {
    BOOKED(1),
    CANCELLED_BY_USER(2),
    CANCELLED_BY_ADMIN(3),
    REFUNDED(4);

    private final int id;

    BookingStatus(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}

