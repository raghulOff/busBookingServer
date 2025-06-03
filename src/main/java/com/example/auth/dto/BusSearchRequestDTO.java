package com.example.auth.dto;

public class BusSearchRequestDTO {
    private String from;
    private String to;
    private String doj;

    public String getDoj() {
        return doj;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public void setDoj( String doj ) {
        this.doj = doj;
    }

    public void setFrom( String from ) {
        this.from = from;
    }

    public void setTo( String to ) {
        this.to = to;
    }

    @Override
    public String toString() {
        return "from: " + from + " to " + to + " doj " + doj;
    }
}
