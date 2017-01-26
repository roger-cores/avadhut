package com.frostox.calculoII.entities.wrappers;

/**
 * Created by roger on 3/5/16.
 */
public class ProductStatus {
    private boolean activated;

    private String startDate;

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
}
