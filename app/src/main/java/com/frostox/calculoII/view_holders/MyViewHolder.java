package com.frostox.calculoII.view_holders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.frostox.calculoII.R;


/**
 * Created by roger on 5/5/16.
 */
public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    protected ImageView imgquestion;
    protected ImageView imgexplanation;
    protected ImageView imganswer;
    protected ImageView ct;
    protected TextView questionnumber;
    protected TextView question;
    protected TextView answer;
    protected TextView explanation;

    protected TextView yourAnswer;
    protected ImageView yourAnswerImage;

    private Context context;


    public MyViewHolder(View v) {
        super(v);
        v.setOnClickListener(this);
        ct = (ImageView)v.findViewById(R.id.ct);
        imgexplanation = (ImageView) v.findViewById(R.id.hintimg);
        imganswer= (ImageView) v.findViewById(R.id.imganswer);
        imgquestion = (ImageView) v.findViewById(R.id.imgquestion);
        questionnumber = (TextView) v.findViewById(R.id.questionnumber);
        question = (TextView) v.findViewById(R.id.question);
        answer = (TextView) v.findViewById(R.id.answer);
        explanation = (TextView) v.findViewById(R.id.hint);

        yourAnswer = (TextView) v.findViewById(R.id.yourans);
        yourAnswerImage = (ImageView) v.findViewById(R.id.youransimg);
    }

    @Override
    public void onClick(View v) {
        context = v.getContext();

        Toast.makeText(context, "Clicked here " + getAdapterPosition(), Toast.LENGTH_SHORT).show();

    }

    public ImageView getImgquestion() {
        return imgquestion;
    }

    public void setImgquestion(ImageView imgquestion) {
        this.imgquestion = imgquestion;
    }

    public ImageView getImgexplanation() {
        return imgexplanation;
    }

    public void setImgexplanation(ImageView imgexplanation) {
        this.imgexplanation = imgexplanation;
    }

    public ImageView getImganswer() {
        return imganswer;
    }

    public void setImganswer(ImageView imganswer) {
        this.imganswer = imganswer;
    }

    public ImageView getCt() {
        return ct;
    }

    public void setCt(ImageView ct) {
        this.ct = ct;
    }

    public TextView getQuestionnumber() {
        return questionnumber;
    }

    public void setQuestionnumber(TextView questionnumber) {
        this.questionnumber = questionnumber;
    }

    public TextView getQuestion() {
        return question;
    }

    public void setQuestion(TextView question) {
        this.question = question;
    }

    public TextView getAnswer() {
        return answer;
    }

    public void setAnswer(TextView answer) {
        this.answer = answer;
    }

    public TextView getExplanation() {
        return explanation;
    }

    public void setExplanation(TextView explanation) {
        this.explanation = explanation;
    }

    public TextView getYourAnswer() {
        return yourAnswer;
    }

    public void setYourAnswer(TextView yourAnswer) {
        this.yourAnswer = yourAnswer;
    }

    public ImageView getYourAnswerImage() {
        return yourAnswerImage;
    }

    public void setYourAnswerImage(ImageView yourAnswerImage) {
        this.yourAnswerImage = yourAnswerImage;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}