package com.frostox.calculoII.view_holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.frostox.calculoII.R;


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