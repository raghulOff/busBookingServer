package com.example.busbooking.dto.bus;

import com.example.busbooking.dto.base.VehicleDTO;

import java.util.List;
import java.util.Map;

public class BusVehicleDTO extends VehicleDTO {

    private String busType;
    private int busId;
    private List<SeatGridCount> seatGridCount;
    private List<SeatDetails> seatDetails;
    private int totalColumns;

    public static class SeatDetails {
        private int row_number;
        private int seat_type_id;
        private int col_number;
        private String pos;

        public SeatDetails () {}
        public SeatDetails( int row_number, int seat_type_id, int col_number, String pos ) {
            this.row_number = row_number;
            this.seat_type_id = seat_type_id;
            this.col_number = col_number;
            this.pos = pos;
        }


        public int getCol_number() {
            return col_number;
        }

        public void setCol_number( int col_number ) {
            this.col_number = col_number;
        }

        public String getPos() {
            return pos;
        }

        public void setPos( String pos ) {
            this.pos = pos;
        }


        public int getRow_number() {
            return row_number;
        }

        public void setRow_number( int row_number ) {
            this.row_number = row_number;
        }

        public int getSeat_type_id() {
            return seat_type_id;
        }

        public void setSeat_type_id( int seat_type_id ) {
            this.seat_type_id = seat_type_id;
        }
    }

    public static class SeatGridCount {

        private int col_number;
        private int total_rows;
        private String pos;

        public SeatGridCount() {}
        public SeatGridCount( int col_number, int total_rows, String pos ) {
            this.col_number = col_number;
            this.total_rows = total_rows;
            this.pos = pos;
        }


        public int getCol_number() {
            return col_number;
        }

        public void setCol_number( int col_number ) {
            this.col_number = col_number;
        }

        public int getTotal_rows() {
            return total_rows;
        }

        public void setTotal_rows( int total_rows ) {
            this.total_rows = total_rows;
        }

        public String getPos() {
            return pos;
        }

        public void setPos( String pos ) {
            this.pos = pos;
        }

    }

    public BusVehicleDTO() {}

    public BusVehicleDTO( String vehicleNumber, int totalColumns, String operatorName, int busId, String busType ) {
        super(vehicleNumber, operatorName);
        this.totalColumns = totalColumns;
        this.busType = busType;
        this.busId = busId;
    }

    public BusVehicleDTO( String busType, int busId, List<SeatGridCount> seatGridCount, List<SeatDetails> seatDetails, int totalColumns ) {
        this.busType = busType;
        this.busId = busId;
        this.seatGridCount = seatGridCount;
        this.seatDetails = seatDetails;
        this.totalColumns = totalColumns;
    }



    public List<SeatGridCount> getSeatGridCount() {
        return seatGridCount;
    }

    public void setSeatGridCount( List<SeatGridCount> seatGridCount ) {
        this.seatGridCount = seatGridCount;
    }

    public List<SeatDetails> getSeatDetails() {
        return seatDetails;
    }

    public void setSeatDetails( List<SeatDetails> seatDetails ) {
        this.seatDetails = seatDetails;
    }

    public int getTotalColumns() {
        return totalColumns;
    }

    public void setTotalColumns( int totalColumns ) {
        this.totalColumns = totalColumns;
    }

    public int getBusId() {
        return busId;
    }

    public void setBusId( int busId ) {
        this.busId = busId;
    }

    public String getBusType() {
        return busType;
    }

    public void setBusType( String busType ) {
        this.busType = busType;
    }
}
