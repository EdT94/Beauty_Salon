package com.example.zinabeautysalon.Models;


public class MyTime implements Comparable<MyTime> {
    private String hours;
    private String minutes;

    public MyTime(String hours, String minutes) {
        this.hours = hours;
        this.minutes = minutes;
    }

    public MyTime() {

    }

    public String getHours() {
        return hours;
    }

    public MyTime setHours(String hours) {
        this.hours = hours;
        return this;
    }

    public String getMinutes() {
        return minutes;
    }

    public MyTime setMinutes(String minutes) {
        this.minutes = minutes;
        return this;
    }

    @Override
    public String toString() {
        return hours + ":" + minutes;
    }


    @Override
    public int compareTo(MyTime other) {
        if (Integer.parseInt(this.getHours()) == Integer.parseInt(other.getHours()))
            if (Integer.parseInt(this.getMinutes()) == Integer.parseInt(other.getMinutes()))
                return 0;


        if (Integer.parseInt(this.getHours()) > Integer.parseInt(other.getHours()))
            return 1;
        else if (Integer.parseInt(this.getHours()) < Integer.parseInt(other.getHours()))
            return -1;

        if (Integer.parseInt(this.getMinutes()) > Integer.parseInt(other.getMinutes()))
            return 1;
        else
            return -1;
    }

    @Override
    public boolean equals(Object o) {
        if (this.compareTo((MyTime) o) == 0)
            return true;
        return false;
    }


}
