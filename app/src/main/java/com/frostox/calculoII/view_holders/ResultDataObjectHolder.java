package com.frostox.calculoII.view_holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.frostox.calculoII.R;


public class ResultDataObjectHolder extends RecyclerView.ViewHolder {
    public TextView label;
    public  TextView difficulty;
    public TextView date;
    public LinearLayout ll;
    public ResultDataObjectHolder(View itemView) {
        super(itemView);
        label = (TextView) itemView.findViewById(R.id.recycler_view_item_text);
        difficulty = (TextView)itemView.findViewById(R.id.difficulty);
        date = (TextView)itemView.findViewById(R.id.date);
        ll = (LinearLayout)itemView.findViewById(R.id.ll);
    }

}