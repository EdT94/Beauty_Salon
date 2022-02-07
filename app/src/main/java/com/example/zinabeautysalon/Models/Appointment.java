package com.example.zinabeautysalon.Models;


public class Appointment {
    private MyDate date;
    private MyTime time;
    private String accountEmail;
    private String treatment;
    private Boolean approved = false;


    public Appointment(MyDate date, MyTime time, String accountEmail, String treatment, Boolean approved) {
        this.date = date;
        this.time = time;
        this.accountEmail = accountEmail;
        this.treatment = treatment;
        this.approved = approved;
    }


    public Appointment() {

    }

    public MyDate getDate() {
        return date;
    }

    public Appointment setDate(MyDate date) {
        this.date = date;
        return this;
    }

    public MyTime getTime() {
        return time;
    }

    public Appointment setTime(MyTime time) {
        this.time = time;
        return this;
    }

    public String getTreatment() {
        return treatment;
    }

    public Appointment setTreatment(String treatment) {
        this.treatment = treatment;
        return this;
    }

    public String getAccountEmail() {
        return accountEmail;
    }

    public Appointment setAccount(String accountEmail) {
        this.accountEmail = accountEmail;
        return this;
    }

    public Boolean getApproved() {
        return approved;
    }

    public Appointment setApproved(Boolean approved) {
        this.approved = approved;
        return this;
    }

    @Override
    public String toString() {
        return this.getTreatment() + " ב" + this.getDate() + ", " + this.getTime();
    }


    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        Appointment app = (Appointment) other;
        if (this.getDate().compareTo(((Appointment) other).getDate()) == 0)
            if (this.getTime().compareTo(((Appointment) other).getTime()) == 0)
                return true;
        return false;
    }
}



