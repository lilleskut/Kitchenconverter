package com.example.jens.kitchenconverter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

class PackageDensityAdapter extends BaseAdapter {
    private final LayoutInflater mInflater;
    private List<PackageDensity> mPackageDensities;

    public PackageDensityAdapter(LayoutInflater inflater) {
        this.mInflater = inflater;
        mPackageDensities = new LinkedList<>();
    }

    public void updateData(List<PackageDensity> packageDensities) {
        mPackageDensities = packageDensities;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mPackageDensities.size();
    }

    @Override
    public PackageDensity getItem(int position) {
        return mPackageDensities.get(position);
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
            convertView = mInflater.inflate(R.layout.row_packagedensity, parent, false);

            // create a new "Holder" with subviews
            holder = new ViewHolder();
            holder.substanceTextView = (TextView) convertView.findViewById(R.id.text_substance);
            holder.packageTextView = (TextView) convertView.findViewById(R.id.text_package);
            holder.packageDensityTextView = (TextView) convertView.findViewById(R.id.text_package_density);

            // hang onto this holder for future recyclage
            convertView.setTag(holder);
        } else {

            // skip all the expensive inflation/findViewById
            // and just get the holder you already made
            holder = (ViewHolder) convertView.getTag();
        }

        PackageDensity packageDensity = getItem(position);

        String substanceTitle;
        String packageTitle;
        String packageDensityTitle;

        substanceTitle = packageDensity.getSubstance();
        packageTitle=packageDensity.getPackageName();
        packageDensityTitle = packageDensity.getPackageDensity().toString();


        // Send these Strings to the TextViews for display
        holder.substanceTextView.setText(substanceTitle);
        holder.packageTextView.setText(packageTitle);
        holder.packageDensityTextView.setText(packageDensityTitle);

        return convertView;
    }


    private static class ViewHolder {
        public TextView substanceTextView;
        public TextView packageTextView;
        public TextView packageDensityTextView;
    }
}
