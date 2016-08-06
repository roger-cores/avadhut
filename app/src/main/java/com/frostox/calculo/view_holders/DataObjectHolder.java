package com.frostox.calculo.view_holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import calculo.frostox.com.calculo.R;

/**
 * Created by roger on 5/5/16.
 */
public class DataObjectHolder extends RecyclerView.ViewHolder {
    public TextView label;
    public DataObjectHolder(View itemView) {
        super(itemView);
        label = (TextView) itemView.findViewById(R.id.recycler_view_item_text);
    }

}