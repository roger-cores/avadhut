package com.frostox.calculo.view_holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import calculo.frostox.com.calculo.R;

/**
 * Created by roger on 5/5/16.
 */
public class MyViewHolder extends RecyclerView.ViewHolder {

    public ImageView imgquestion;
    public ImageView imganswer;
    public ImageView ct;
    public TextView questionnumber;
    public TextView answer;
    public TextView question;

    public MyViewHolder(View v) {
        super(v);
        ct = (ImageView) v.findViewById(R.id.ct);
        imganswer = (ImageView) v.findViewById(R.id.imganswer);
        imgquestion = (ImageView) v.findViewById(R.id.imgquestion);
        questionnumber = (TextView) v.findViewById(R.id.questionnumber);
        question = (TextView) v.findViewById(R.id.question);
        answer = (TextView) v.findViewById(R.id.answer);
    }

}
