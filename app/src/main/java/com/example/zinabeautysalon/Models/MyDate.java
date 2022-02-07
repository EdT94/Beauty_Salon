package com.example.zinabeautysalon.Models;


public class MyDate implements Comparable<MyDate> {
    private String year;
    private String month;
    private String day;

    public MyDate(String year, String month, String day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public MyDate() {

    }

    public String getYear() {
        return year;
    }

    public MyDate setYear(String year) {
        this.year = year;
        return this;
    }

    public String getMonth() {
        return month;
    }

    public MyDate setMonth(String month) {
        this.month = month;
        return this;
    }

    public String getDay() {
        return day;
    }

    public MyDate setDay(String day) {
        this.day = day;
        return this;
    }

    @Override
    public String toString() {
        return day + "/" + month + "/" + year;
    }


    @Override
    public int compareTo(MyDate other) {
        if (Integer.parseInt(this.getYear()) == Integer.parseInt(other.getYear()))
            if (Integer.parseInt(this.getMonth()) == Integer.parseInt(other.getMonth()))
                if (Integer.parseInt(this.getDay()) == Integer.parseInt(other.getDay()))
                    return 0;


        if (Integer.parseInt(this.getYear()) > Integer.parseInt(other.getYear()))
            return 1;
        else if (Integer.parseInt(this.getYear()) < Integer.parseInt(other.getYear()))
            return -1;

        if (Integer.parseInt(this.getMonth()) > Integer.parseInt(other.getMonth()))
            return 1;
        else if (Integer.parseInt(this.getMonth()) < Integer.parseInt(other.getMonth()))
            return -1;

        if (Integer.parseInt(this.getDay()) > Integer.parseInt(other.getDay()))
            return 1;

        return -1;

    }
}
