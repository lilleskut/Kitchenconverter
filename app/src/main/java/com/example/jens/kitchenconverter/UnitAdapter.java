package com.example.jens.kitchenconverter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by jens on 9/19/15.
 * used for display as listview
 */
public class UnitAdapter extends BaseAdapter {

    Context mContext;
    LayoutInflater mInflater;
    List<Unit> mUnits;

    public UnitAdapter(Context context, LayoutInflater inflater) {
        mContext = context;
        mInflater = inflater;
        mUnits = new LinkedList<>();
    }

    public void updateData(List<Unit> units) {
        //update the adapter's dataset
        mUnits = units;
        notifyDataSetChanged();
    }



    @Override
    public int getCount() {
        return mUnits.size();
    }

    @Override
    // public Object getItem(int position) {
    public Unit getItem(int position) {
        return mUnits.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        // check if the view already exists
        // if so, no need to inflate and findViewById again!
        if (convertView == null) {

            // Inflate the custom row layout from your XML.
            convertView = mInflater.inflate(R.layout.row_unit, null);

            // create a new "Holder" with subviews
            holder = new ViewHolder();
            holder.unitTextView = (TextView) convertView.findViewById(R.id.text_unit);
            holder.dimensionTextView = (TextView) convertView.findViewById(R.id.text_dimension);
            holder.factorTextView = (TextView) convertView.findViewById(R.id.text_factor);

            // hang onto this holder for future recyclage
            convertView.setTag(holder);
        } else {

            // skip all the expensive inflation/findViewById
            // and just get the holder you already made
            holder = (ViewHolder) convertView.getTag();
        }

        Unit unit = getItem(position);

        String unitTitle;
        String dimensionTitle;
        String factorTitle;

        unitTitle = unit.getUnit();
        dimensionTitle = unit.getDimension();
        factorTitle = unit.getFactor().toString();

        // Send these Strings to the TextViews for display
        holder.unitTextView.setText(unitTitle);
        holder.dimensionTextView.setText(dimensionTitle);
        holder.factorTextView.setText(factorTitle);

        return convertView;
    }

    private static class ViewHolder {
        public TextView unitTextView;
        public TextView dimensionTextView;
        public TextView factorTextView;
    }
}
