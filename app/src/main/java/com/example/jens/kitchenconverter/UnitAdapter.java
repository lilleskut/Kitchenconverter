package com.example.jens.kitchenconverter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

class UnitAdapter extends BaseAdapter implements Filterable {

    private final LayoutInflater mInflater;
    private  List<Unit> oUnits; // original data
    private List<Unit> fUnits; // filtered data
    private final UnitFilter mFilter = new UnitFilter();

    public UnitAdapter(LayoutInflater inflater) {
        this.mInflater = inflater;
        oUnits = new LinkedList<>();
        fUnits = new LinkedList<>();
    }

    public void updateData(List<Unit> units) {
        //update the adapter's data set
        oUnits = units;
        fUnits = units;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return fUnits.size();
    }

    @Override
    // public Object getItem(int position) {
    public Unit getItem(int position) {
        return fUnits.get(position);
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
            convertView = mInflater.inflate(R.layout.row_unit, parent, false);

            // create a new "Holder" with subviews
            holder = new ViewHolder();
            holder.nameTextView = (TextView) convertView.findViewById(R.id.text_name);
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

        unitTitle = unit.getName();
        dimensionTitle = unit.getDimension();
        factorTitle = unit.getFactor().toString();

        // Send these Strings to the TextViews for display
        holder.nameTextView.setText(unitTitle);
        holder.dimensionTextView.setText(dimensionTitle);
        holder.factorTextView.setText(factorTitle);

        if(unit.getBase()) {
            convertView.setBackgroundColor(Color.LTGRAY);
        } else {
            convertView.setBackgroundColor(Color.WHITE);
        }
        return convertView;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    private class UnitFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final List<Unit> list = oUnits;
            int count = list.size();

            final ArrayList<Unit> nlist = new ArrayList<>(count);

            String filterableString;

            for(int i=0; i < count; i++) {
                filterableString = list.get(i).getDimension();
                if(filterableString.toLowerCase().contains(filterString)) {
                    nlist.add(list.get(i));
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            fUnits = (ArrayList<Unit>) results.values;
            notifyDataSetChanged();
        }
    }

    private static class ViewHolder {
        public TextView nameTextView;
        public TextView dimensionTextView;
        public TextView factorTextView;
    }
}
