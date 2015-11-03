package com.example.jens.kitchenconverter;

import android.content.Context;

import java.util.Arrays;

public class Unit {

    private int id;
    private String unit; // name of unit, e.g. "m", "cm"
    private String dimension; // "length", "mass",...
    private Double factor; // factor relative to base unit
    private boolean base; // unit is base unit of its dimension


    private final Context ctx;

    public Unit(Context context){ this.ctx = context; }

    public Unit(String unit, String dimension, Double factor, Boolean base, Context context) {
        super();
        this.ctx = context;
        setUnit(unit);
        setDimension(dimension);
        setFactor(factor);
        setBase(base);
    }

    public void setId(int i) { this.id = i; }

    public void setUnit(String u) {
        if( u == null ) {
            throw new IllegalArgumentException("Unit name is null");
        }
        this.unit = u;
    }

    public void setDimension(String d) {
        if( d == null ) {
            throw new IllegalArgumentException("Dimension is null");
        }

        String[] dimensions = ctx.getResources().getStringArray(R.array.dimensions_array);

        if(!Arrays.asList(dimensions).contains(d)){
            throw new IllegalArgumentException("Dimension is not one of the permittable dimension names");
        }

            this.dimension = d;
    }

    public void setFactor(Double f) {
        if( f == null ) {
            throw new IllegalArgumentException("Factor is null");
        }
        if ( f < 0 ) {
            throw new IllegalArgumentException("Factor is negative");
        }

        this.factor = f; }

    public void setBase(Boolean b) {
        this.base = b;
    }

    public int getId() { return id; }

    public String getUnit() { return unit; }

    public String getDimension() { return dimension; }

    public Double getFactor() { return factor; }

    public Boolean getBase() { return base; }



    @Override
    public String toString() {
        return "Unit [id=" + id + ", name=" + unit + ", dimension=" + dimension + ", factor=" + factor + "]";
    }


}
