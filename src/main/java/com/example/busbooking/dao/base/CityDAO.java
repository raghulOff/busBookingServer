package com.example.busbooking.dao.base;

import com.example.busbooking.dto.base.CityDTO;
import jakarta.ws.rs.core.Response;

import java.util.List;

public interface CityDAO {
    List<CityDTO> getCities() throws Exception;
    Response addNewCity( CityDTO cityDTO );
}
