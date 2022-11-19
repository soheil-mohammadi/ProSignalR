package org.soheil.supersignalr.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by soheilmohammadi on 9/9/18.
 */

public class PlansModel {

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("price")
    private double price;


    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "PlansModel is " +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price + ".";
    }
}
