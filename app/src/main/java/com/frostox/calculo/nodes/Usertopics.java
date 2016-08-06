package com.frostox.calculo.nodes;

/**
 * Created by shannonpereira601 on 20/04/16.
 */
public class Usertopics{

    private String name;
    private String topicid;
    public String timestamp;

    public String getDifficulty() {
        return difficulty;
    }

    public String difficulty;

    public Usertopics() {
    }

    public Usertopics(String name, String topicid, String timestamp, String difficulty) {
        this.difficulty = difficulty;
        this.name = name;
        this.topicid = topicid;
        this.timestamp = timestamp;
    }


    public String getTimestamp() {
        return timestamp;
    }

    public String getTopicid() {
        return topicid;
    }

    public String getName() {
        return name;
    }

}

