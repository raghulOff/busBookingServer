package com.example.busbooking.dao.base;
import com.example.busbooking.dto.bus.BusVehicleDTO;
import jakarta.ws.rs.core.Response;

import java.util.List;

public interface VehicleDAO {
    List<BusVehicleDTO> getAll() throws Exception;
    Response delete(int id) throws Exception;
    Response update( BusVehicleDTO busVehicleDTO ) throws Exception;
    Response addNew( BusVehicleDTO busVehicleDTO ) throws Exception;
}
