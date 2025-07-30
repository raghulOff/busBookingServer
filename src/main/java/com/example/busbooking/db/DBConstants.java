package com.example.busbooking.db;

public class DBConstants {
    // List of users
    public static final String USERS = "users";
    // User roles (admin, manager, etc.)
    public static final String ROLES = "roles";
    // Junction table: Users assigned to roles
    public static final String USER_ROLES = "user_roles";
    // List of cities
    public static final String CITIES = "cities";
    //    Bus information
    public static final String BUSES = "buses";
    //    Types of seats (e.g., sleeper, seater)
    public static final String SEAT_TYPE = "seat_type";
    //    Columns (e.g., vertical sections of seats)
    public static final String SEAT_GRID_COLUMNS = "seat_grid_columns";
    //    Physical seat units
    public static final String SEATS = "seats";
    //    Routes between cities
    public static final String ROUTES = "routes";
    //    Bus schedules
    public static final String SCHEDULES = "schedules";
    //    Passenger personal data
    public static final String PASSENGER_DETAILS = "passenger_details";
    //    Locations (boarding/dropping points)
    public static final String LOCATIONS = "locations";
    //    Ticket bookings
    public static final String BOOKINGS = "bookings";
    //    Seats linked to a schedule
    public static final String SCHEDULED_SEATS = "scheduled_seats";
    //    Passenger-seat bookings per schedule
    public static final String BOOKING_SEATS = "booking_seats";
    //    Mapping cities to stop locations
    public static final String CITY_LOCATIONS = "city_locations";
    //    Stop time and type per schedule
    public static final String STOPS = "scheduled_stops";
    //    Booking status of a passenger
    public static final String BOOKING_STATUSES = "booking_statuses";
    //    Schedule status of schedules
    public static final String SCHEDULE_STATUSES = "schedule_statuses";
    //    Stop type for stops (e.g. BOARDING, DROPPING, HALT etc).
    public static final String STOP_TYPE = "stop_type";
    //    Schedule seat status values (e.g. AVAILABLE, BOOKED, BLOCKED)
    public static final String SCHEDULED_SEAT_STATUSES = "scheduled_seat_statuses";


    // Unique constraint violation
    public static final String UNIQUE_VIOLATION = "23505";

    // Foreign key violation (e.g., trying to delete a referenced row)
    public static final String FOREIGN_KEY_VIOLATION = "23503";

    private DBConstants() {
    }
}

