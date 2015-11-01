package com.example.jens.kitchenconverter;


import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class SpinnerUnitAdapter extends ArrayAdapter<Unit> {
    private List<Unit> mUnits;
    private Context mContext;

    public SpinnerUnitAdapter(Context context, int resource, List<Unit> units) {
        super(context, resource, units);
        this.mContext = context;
        this.mUnits = units;
    }

    public Unit getItem(int position) { return mUnits.get(position); }

    public long getItemId(int position) { return position; }

    // this is for the passive state of the spinner
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // use dynamically created textview, but could reference custom layout
        TextView label = new TextView(mContext);
        label.setTextColor(Color.BLACK);
        label.setTextSize(mContext.getResources().getDimension(R.dimen.list_row_font_size));
        label.setGravity(Gravity.CENTER);
        label.setText(getItem(position).getUnit());

        return label;
    }

    // this is for the chooser dropped down spinner
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView label = (TextView) View.inflate(mContext,R.layout.row_spinner,null);
        label.setText(getItem(position).getUnit());
        return label;
    }
}
