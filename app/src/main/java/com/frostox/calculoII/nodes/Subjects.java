package com.frostox.calculoII.nodes;

/**
 * Created by admin on 14/04/2016.
 */
public class Subjects {

    private String name;
    private String course;

    public Subjects() {
        // empty default constructor, necessary for Firebase to be able to deserialize blog posts
    }

    public String getName() {
        return name;
    }

    public String getCourse() {
        return course;
    }
}
