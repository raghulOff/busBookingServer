package com.example.busbooking.dto.bus;

import com.example.busbooking.dto.base.VehiclesDTO;

import java.util.List;


public class BusVehiclesDTO extends VehiclesDTO {

    private String busType;
    private int busId;
    private List<SeatGridCount> seatGridCount;
    private List<SeatDetails> seatDetails;
    private Integer totalColumns;

    public static class SeatDetails {
        private Integer row_number;
        private Integer seat_type_id;
        private Integer col_number;
        private String pos;

        public SeatDetails () {}
        public SeatDetails( int row_number, int seat_type_id, int col_number, String pos ) {
            this.row_number = row_number;
            this.seat_type_id = seat_type_id;
            this.col_number = col_number;
            this.pos = pos;
        }


        public Integer getCol_number() {
            return col_number;
        }

        public void setCol_number( Integer col_number ) {
            this.col_number = col_number;
        }

        public String getPos() {
            return pos;
        }

        public void setPos( String pos ) {
            this.pos = pos;
        }


        public Integer getRow_number() {
            return row_number;
        }

        public void setRow_number( Integer row_number ) {
            this.row_number = row_number;
        }

        public Integer getSeat_type_id() {
            return seat_type_id;
        }

        public void setSeat_type_id( Integer seat_type_id ) {
            this.seat_type_id = seat_type_id;
        }
    }

    public static class SeatGridCount {

        private Integer col_number;
        private Integer total_rows;
        private String pos;

        public SeatGridCount() {}
        public SeatGridCount( int col_number, int total_rows, String pos ) {
            this.col_number = col_number;
            this.total_rows = total_rows;
            this.pos = pos;
        }


        public Integer getCol_number() {
            return col_number;
        }

        public void setCol_number( Integer col_number ) {
            this.col_number = col_number;
        }

        public Integer getTotal_rows() {
            return total_rows;
        }

        public void setTotal_rows( Integer total_rows ) {
            this.total_rows = total_rows;
        }

        public String getPos() {
            return pos;
        }

        public void setPos( String pos ) {
            this.pos = pos;
        }

    }

    public BusVehiclesDTO() {}

    public BusVehiclesDTO( String vehicleNumber, int totalColumns, String operatorName, int busId, String busType ) {
        super(vehicleNumber, operatorName);
        this.totalColumns = totalColumns;
        this.busType = busType;
        this.busId = busId;
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

    public Integer getTotalColumns() {
        return totalColumns;
    }

    public void setTotalColumns( Integer totalColumns ) {
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
