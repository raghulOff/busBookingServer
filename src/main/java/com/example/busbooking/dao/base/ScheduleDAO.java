package com.example.busbooking.dao.base;

import com.example.busbooking.dto.base.ScheduleDTO;
import jakarta.ws.rs.core.Response;

import java.util.List;

public interface ScheduleDAO {
    List<ScheduleDTO> getSchedules() throws Exception;
    Response addNewSchedule(ScheduleDTO scheduleDTO) throws Exception;
    Response getScheduleDetails(int scheduleId);

}
