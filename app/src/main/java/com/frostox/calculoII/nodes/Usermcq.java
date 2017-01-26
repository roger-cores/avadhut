package com.frostox.calculoII.nodes;

/**
 * Created by shannonpereira601 on 20/04/16.
 */
public class Usermcq {

    public String getMcqid() {
        return mcqid;
    }

    private String mcqid;
    private String topic;
    private String state;
    private String answer;
    private String question;

    private String yourAnswer;
    private String explanation;
    private String yourOption;

    public String getYourOption() {
        return yourOption;
    }

    public void setYourOption(String yourOption) {
        this.yourOption = yourOption;
    }

    public String getType() {
        return type;
    }

    private String type;

    public Usermcq(String topic, String mcqid, String state, String answer, String question, String type, String yourAnswer, String explanation, String yourOption) {
        this.type = type;
        this.mcqid = mcqid;
        this.topic = topic;
        this.state = state;
        this.answer = answer;
        this.question = question;
        this.yourAnswer = yourAnswer;
        this.explanation = explanation;
        this.yourOption = yourOption;
    }

    public Usermcq() {
    }


    public void setMcqid(String mcqid) {
        this.mcqid = mcqid;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getYourAnswer() {
        return yourAnswer;
    }

    public void setYourAnswer(String yourAnswer) {
        this.yourAnswer = yourAnswer;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public void setType(String type) {
        this.type = type;
    }
}

