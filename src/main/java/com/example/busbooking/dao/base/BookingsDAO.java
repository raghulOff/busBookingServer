package com.example.busbooking.dao.base;

import com.example.busbooking.dto.base.BookSeatDTO;
import jakarta.ws.rs.core.Response;

public interface BookingsDAO {
    Response getAllBookings( int userId );
    Response bookSeat( BookSeatDTO bookSeatDTO );
    Response cancelTicket( int seatId );
}
