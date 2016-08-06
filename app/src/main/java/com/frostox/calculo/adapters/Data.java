package com.frostox.calculo.adapters;

/**
 * Created by admin on 13/04/2016.
 */
public class Data{
    public String text;

    public Data(String text)
    {
        this.setText(text);
    }

    public String getText(){
        return text;
    }

    public void setText(String text){
        this.text = text;
    }

}