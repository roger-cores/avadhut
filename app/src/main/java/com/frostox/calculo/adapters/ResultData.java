package com.frostox.calculo.adapters;

/**
 * Created by admin on 17/04/2016.
 */
public class ResultData {

    public String imgqnurl;
    public String expurl;
    public String qno;
    public String qn;
    public String ans;
    public String exp;
    public int ct;
    public String imgansurl;
    public String id;



    public ResultData(String id, String imgqnurl,String imgansurl, String expurl, String qno,String qn,String ans,String exp, int ct)
    {
        this.id = id;
        this.setCt(ct);
        this.setExpurl(expurl);
        this.setImgqnurl(imgqnurl);
        this.setImgansurl(imgansurl);
        this.setQno(qno);
        this.setQn(qn);
        this.setAns(ans);
        this.setExp(exp);
    }


    public int getCt() {
        return ct;
    }

    public void setCt(int ct) {
        this.ct = ct;
    }


    public String getImgansurl() {
        return imgansurl;
    }

    public void setImgansurl(String imgansurl) {
        this.imgansurl = imgansurl;
    }

    public String getImgqnurl() {
        return imgqnurl;
    }

    public void setImgqnurl(String imgqnurl) {
        this.imgqnurl = imgqnurl;
    }

    public String getExpurl() {
        return expurl;
    }

    public void setExpurl(String expurl) {
        this.expurl = expurl;
    }

    public String getQno() {
        return qno;
    }

    public void setQno(String qno) {
        this.qno = qno;
    }

    public String getQn() {
        return qn;
    }

    public void setQn(String qn) {
        this.qn = qn;
    }

    public String getAns() {
        return ans;
    }

    public void setAns(String ans) {
        this.ans = ans;
    }

    public String getExp() {
        return exp;
    }

    public void setExp(String exp) {
        this.exp = exp;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}