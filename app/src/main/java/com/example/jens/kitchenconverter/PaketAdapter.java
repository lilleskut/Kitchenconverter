package com.example.jens.kitchenconverter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

public class PaketAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<Paket> mPakete;

    public PaketAdapter(Context context, LayoutInflater inflater) {
        this.mContext = context;
        this.mInflater = inflater;
        mPakete = new LinkedList<>();
    }

    public void updateData(List<Paket> pakete) {
        //update the adapter's dataset
        mPakete = pakete;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mPakete.size();
    }

    @Override
    public Paket getItem(int position) {
        return mPakete.get(position);
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
            convertView = mInflater.inflate(R.layout.row_paket, null);

            // create a new "Holder" with subviews
            holder = new ViewHolder();
            holder.substanceTextView = (TextView) convertView.findViewById(R.id.text_substance);
            holder.dimensionTextView = (TextView) convertView.findViewById(R.id.text_dimension);
            holder.valueTextView = (TextView) convertView.findViewById(R.id.text_value);

            // hang onto this holder for future recyclage
            convertView.setTag(holder);
        } else {

            // skip all the expensive inflation/findViewById
            // and just get the holder you already made
            holder = (ViewHolder) convertView.getTag();
        }

        Paket paket = getItem(position);

        String substanceTitle;
        String dimensionTitle;
        String valueTitle;

        substanceTitle = paket.getSubstance();
        dimensionTitle = paket.getDimension();
        valueTitle = paket.getValue().toString();

        // Send these Strings to the TextViews for display
        holder.substanceTextView.setText(substanceTitle);
        holder.dimensionTextView.setText(dimensionTitle);
        holder.valueTextView.setText(valueTitle);

        return convertView;
    }

    private static class ViewHolder {
        public TextView substanceTextView;
        public TextView dimensionTextView;
        public TextView valueTextView;
    }
}
