package com.example.jens.kitchenconverter;

import android.content.Context;

import java.util.Arrays;

public class Unit {

    private int id;
    private String name; // name of unit, e.g. "m", "cm"
    private String dimension; // "length", "mass",...
    private Double factor; // factor relative to base name
    private boolean base; // name is base name of its dimension


    private final Context ctx;

    public Unit(Context context){ this.ctx = context; }
    public Unit(String name, String dimension, Double factor, Boolean base, Context context) {
        super();
        this.ctx = context;
        setName(name);
        setDimension(dimension);
        setFactor(factor);
        setBase(base);
    }

    // setters
    public void setId(int i) { this.id = i; }
    public void setName(String u) {
        if( u == null ) {
            throw new IllegalArgumentException("Unit name is null");
        }
        this.name = u;
    }
    public void setDimension(String d) {
        if( d == null ) {
            throw new IllegalArgumentException("Dimension is null");
        }

        String[] dimensions = ctx.getResources().getStringArray(R.array.dimensions_array);

        if(!Arrays.asList(dimensions).contains(d) && !d.equals("pack")){
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

        this.factor = f;
    }
    public void setBase(Boolean b) {
        this.base = b;
    }

    // getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDimension() { return dimension; }
    public Double getFactor() { return factor; }
    public Boolean getBase() { return base; }

    public boolean isPack() {
        return dimension.equals("pack");
    }

    @Override
    public String toString() {
        return "Unit [id=" + id + ", name=" + name + ", dimension=" + dimension + ", factor=" + factor + "]";
    }


}
