package com.example.jens.kitchenconverter;

import android.content.Context;

import java.util.Arrays;

public class Unit {

    private int id;
    private String unit; // name of unit, e.g. "m", "cm"
    private String dimension; // "length", "mass",...
    private Float factor; // factor relative to base unit

    private Context ctx;


    public Unit(Context context){ this.ctx = context; }

    public Unit(String unit, String dimension, Float factor, Context context) {
        super();
        this.ctx = context;
        setUnit(unit);
        setDimension(dimension);
        setFactor(factor);


    }

    public void setCtx(Context context) { this.ctx = context; }

    public void setId(int i) { this.id = i; }

    public void setUnit(String u) {
        if( u == null ) {
            throw new IllegalArgumentException("Unit name is null");
        }
        this.unit = u; }

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

    public void setFactor(Float f) {
        if( f == null ) {
            throw new IllegalArgumentException("Factor is null");
        }
        this.factor = f; }

    public int getId() { return id; }

    public String getUnit() { return unit; }

    public String getDimension() { return dimension; }

    public Float getFactor() { return factor; }

    @Override
    public String toString() {
        return "Unit [id=" + id + ", name=" + unit + ", dimension=" + dimension + ", factor=" + factor + "]";
    }


}
