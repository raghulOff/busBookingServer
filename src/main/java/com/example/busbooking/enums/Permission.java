package com.example.busbooking.enums;

/**
 * Enum representing all possible permissions used in the bus booking system.
 * These permissions are used for role-based access control (RBAC) to restrict or allow access to specific actions or endpoints.
 */

public enum Permission {

    USER_LOGOUT,                    // Logout the currently logged-in user
    ADD_USER,                       // Add a new user
    VIEW_HOME_PAGE,                 // View general user home page
    VIEW_BOOKING_PAGE,             // View the seat booking page
    VIEW_ADMIN_HOME_PAGE,          // View the admin dashboard
    VIEW_DEVELOPER_HOME_PAGE,      // View the developer dashboard
    VIEW_BOOKINGS_HISTORY,         // View booking history
    GET_ROLES,                // Fetch all roles
    BOOK_SEATS,                    // Book seats for a bus schedule
    GET_USER_BOOKING_HISTORY,      // Get booking history for the current user
    CANCEL_PASSENGER_TICKET,       // Cancel a booked passenger ticket
    SEARCH_BUSES,                  // Search available buses based on city/date
    GET_ALL_BUSES,                 // Retrieve all buses in the system
    DELETE_BUS,                    // Delete a bus
    UPDATE_BUS,                    // Update bus details
    ADD_NEW_BUS,                   // Add a new bus
    GET_SEAT_TYPES,                // Get all seat types (e.g., sleeper, seater)
    GET_ALL_CITIES,                // Fetch all available cities
    DELETE_CITY,                   // Delete a city from system
    ADD_NEW_CITY,                  // Add a new city
    GET_SCHEDULE_STOP_POINTS,      // Fetch stop points of a bus schedule
    GET_CITY_LOCATIONS,            // Get all locations within a city
    ADD_NEW_LOCATION,              // Add a new location
    GET_BUS_ROUTES,                // Fetch all bus routes
    ADD_BUS_ROUTE,                 // Add a new route
    DELETE_BUS_ROUTE,              // Delete a route
    UPDATE_BUS_ROUTE,              // Update route details
    GET_BUS_SCHEDULES,             // Get all bus schedules
    ADD_BUS_SCHEDULE,              // Add a new schedule
    GET_BUS_SCHEDULE_DETAILS,      // Get full details of a specific schedule
    CANCEL_BUS_SCHEDULE,          // Cancel an existing schedule
    UPDATE_BUS_SCHEDULE           // Update details of a schedule
}
