package com.example.jens.kitchenconverter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

public class DensityAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<Density> mDensities;

    public DensityAdapter(LayoutInflater inflater) {
        this.mInflater = inflater;
        mDensities = new LinkedList<>();
    }

    public void updateData(List<Density> densities) {
        mDensities = densities;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mDensities.size();
    }

    @Override
    public Density getItem(int position) {
        return mDensities.get(position);
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
            convertView = mInflater.inflate(R.layout.row_density, null);

            // create a new "Holder" with subviews
            holder = new ViewHolder();
            holder.substanceTextView = (TextView) convertView.findViewById(R.id.text_substance);
            holder.densityTextView = (TextView) convertView.findViewById(R.id.text_density);

            // hang onto this holder for future recyclage
            convertView.setTag(holder);
        } else {

            // skip all the expensive inflation/findViewById
            // and just get the holder you already made
            holder = (ViewHolder) convertView.getTag();
        }

        Density density = getItem(position);

        String substanceTitle;
        String densityTitle;

        substanceTitle = density.getSubstance();

        if ( density.getDensity() == null  ) {
            densityTitle = "";
        } else {
            densityTitle = density.getDensity().toString();
        }

        // Send these Strings to the TextViews for display
        holder.substanceTextView.setText(substanceTitle);
        holder.densityTextView.setText(densityTitle);

        return convertView;
    }


    private static class ViewHolder {
        public TextView substanceTextView;
        public TextView densityTextView;
    }
}
