package com.example.busbooking.dao.base;

import jakarta.ws.rs.core.Response;

public interface LocationDAO {
    Response getScheduleLocations(int scheduleId, int type);
}
