package com.example.pethotel;

public class Reservation {
    public String fromDate;
    public String toDate;
    public String petStaying;

    public Reservation(){

    }

    public Reservation(String fromDate, String toDate, String petName){
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.petStaying = petName;
    }
}
