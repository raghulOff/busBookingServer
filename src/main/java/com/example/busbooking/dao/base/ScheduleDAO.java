package com.example.busbooking.dao.base;

import com.example.busbooking.dto.base.SchedulesDTO;
import jakarta.ws.rs.core.Response;


/**
 * Interface for defining CRUD operations related to schedules.
 * This abstraction allows for managing schedules of various transportation modes (e.g., bus, train).
 */

public interface ScheduleDAO {

    /**
     * Retrieves all the available schedules from the system.
     *
     * @return A Response containing the list of all schedules or an appropriate error.
     * @throws Exception If there is a database or internal server error.
     */
    Response getSchedules() throws Exception;

    /**
     * Adds a new schedule to the system.
     *
     * @param schedulesDTO A DTO containing schedule details
     * @return A Response indicating the success or failure of the schedule creation.
     * @throws Exception If insertion fails due to invalid input or system errors.
     */
    Response addNewSchedule(SchedulesDTO schedulesDTO) throws Exception;

    /**
     * Retrieves detailed information of a specific schedule based on its ID.
     *
     * @param scheduleId The unique ID of the schedule.
     * @return A Response with the schedule details or an error if not found.
     */
    Response getScheduleDetails(int scheduleId) throws Exception;

    /**
     * Deletes an existing schedule based on the provided schedule ID.
     *
     * @param scheduleId The ID of the schedule to be deleted.
     * @return A Response indicating whether the deletion was successful or if it failed due to constraints.
     */
    Response cancelSchedule(int scheduleId) throws Exception;

    /**
     * Updates an existing schedule with new data.
     *
     * @param schedulesDTO A DTO containing the updated schedule information.
     * @return A Response indicating success or failure of the update operation.
     * @throws Exception If the schedule does not exist or an internal error occurs.
     */
    Response updateSchedule(SchedulesDTO schedulesDTO) throws Exception;
}
