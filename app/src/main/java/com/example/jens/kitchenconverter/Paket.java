package com.example.jens.kitchenconverter;


import android.content.Context;

import java.util.Arrays;

public class Paket {
    private int id;
    private String substance; // type, e.g. "yeast", "baking powder",...
    private String dimension; // "length", "mass",...
    private Double value; // value relative to base unit

    private Context ctx;


    public Paket(Context context){ this.ctx = context; }
    public Paket(String substance, String dimension, Double value, Context context) {
        super();
        this.ctx = context;
        setSubstance(substance);
        setDimension(dimension);
        setValue(value);
    }

    // setters
    public void setId(int i) { this.id = i; }
    public void setSubstance(String u) {
        if( u == null ) {
            throw new IllegalArgumentException("Substance name is null");
        }
        this.substance = u;
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
    public void setValue(Double f) {
        if( f == null ) {
            throw new IllegalArgumentException("Factor is null");
        }
        this.value = f; }

    //getters
    public int getId() { return id; }
    public String getSubstance() { return substance; }
    public String getDimension() { return dimension; }
    public Double getValue() { return value; }

    @Override
    public String toString() {
        return "Package [id=" + id + ", substance=" + substance + ", dimension=" + dimension + ", value=" + value + "]";
    }
}
