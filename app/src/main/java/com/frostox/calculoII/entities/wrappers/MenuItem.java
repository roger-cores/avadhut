package com.frostox.calculoII.entities.wrappers;

/**
 * Created by roger on 11/14/2016.
 */
public class MenuItem {
    int imageId;

    String menuString;


    public MenuItem(int imageId, String menuString) {
        this.imageId = imageId;
        this.menuString = menuString;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getMenuString() {
        return menuString;
    }

    public void setMenuString(String menuString) {
        this.menuString = menuString;
    }
}
