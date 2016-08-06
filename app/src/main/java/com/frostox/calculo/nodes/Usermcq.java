package com.frostox.calculo.nodes;

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

    public String getType() {
        return type;
    }

    private String type;

    public Usermcq(String topic, String mcqid, String state, String answer, String question, String type) {
        this.type = type;
        this.mcqid = mcqid;
        this.topic = topic;
        this.state = state;
        this.answer = answer;
        this.question = question;
    }

    public Usermcq() {
    }

    public String getAnswer() {
        return answer;
    }

    public String getQuestion() {
        return question;
    }

    public String getTopic() {
        return topic;
    }

    public String getState() {
        return state;
    }

}

