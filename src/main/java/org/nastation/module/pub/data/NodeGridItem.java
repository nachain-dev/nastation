package org.nastation.module.pub.data;

import java.time.LocalDate;

/**
 * NodeGridItem object data
 */
public class NodeGridItem {

    private LocalDate date;
    private String city;
    private String country;
    private String status;
    private String theme;

    public NodeGridItem() {

    }

    public NodeGridItem(LocalDate date, String city, String country, String status, String theme) {
        this.date = date;
        this.city = city;
        this.country = country;
        this.status = status;
        this.theme = theme;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }
}
