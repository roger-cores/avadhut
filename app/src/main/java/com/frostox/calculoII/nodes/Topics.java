package com.frostox.calculoII.nodes;

/**
 * Created by admin on 14/04/2016.
 */
public class Topics {

    private String name;
    private String subject;

    public Topics() {
        // empty default constructor, necessary for Firebase to be able to deserialize blog posts
    }

    public String getName() {
        return name;
    }

    public String getSubject() {
        return subject;
    }
}
