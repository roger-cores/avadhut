package com.frostox.calculoII.nodes;

/**
 * Created by admin on 15/04/2016.
 */
public class MCQs {

    private String ans;
    private String ansA;
    private String ansB;
    private String ansC;
    private String ansD;
    private int difficulty;
    private String explanation;
    private String explanationType;
    private String name;
    private String question;
    private String topic;
    private String type;

    public MCQs() {
        // empty default constructor, necessary for Firebase to be able to deserialize blog posts
    }

    public String getAns() {
        return ans;
    }

    public String getAnsA() {
        return ansA;
    }

    public String getAnsB() {
        return ansB;
    }

    public String getAnsC() {
        return ansC;
    }

    public String getAnsD() {
        return ansD;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public String getExplanation() {
        return explanation;
    }

    public String getExplanationType() {
        return explanationType;
    }

    public String getName() {
        return name;
    }

    public String getQuestion() {
        return question;
    }

    public String getTopic() {
        return topic;
    }

    public String getType() {
        return type;
    }


}
