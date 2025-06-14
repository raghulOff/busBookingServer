package com.example.busbooking.dao.base;

import com.example.busbooking.dto.base.RouteDTO;
import jakarta.ws.rs.core.Response;

import java.util.List;

public interface RouteDAO {
    List<RouteDTO> getRoutes() throws Exception;
    Response addNewRoute( RouteDTO routeDTO ) throws Exception;
    Response deleteRoute( int routeId ) throws Exception;
    Response updateRoute( RouteDTO routeDTO ) throws Exception;
}
