package com.frostox.calculo.entities;

/**
 * Created by roger on 28/6/16.
 */
public class Device {
    String name;
    String address;

    public Device(String name, String address){
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
