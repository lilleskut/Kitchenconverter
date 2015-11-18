package com.example.jens.kitchenconverter;


import android.content.Context;

public class Density {

    private int id;
    private String substance; // substance
    private Double density = null; // density in kg/l (water = 1); null means it is not set

    private final Context ctx;

    public Density(Context context) { this.ctx=context; }
    public Density(String substance, Double density, Context context) {
        super();
        this.ctx = context;
        setSubstance(substance);
        setDensity(density);
    }

    // setters
    public void setId(int i) { this.id = i; }
    public void setSubstance(String u) {
        if( u == null ) {
            throw new IllegalArgumentException("Density substance is null");
        }
        this.substance = u;
    }
    public void setDensity(Double f) {
        if( f != null && f < 0 ) {
            throw new IllegalArgumentException("Density is negative");
        }
        this.density = f; }

    // getters
    public int getId() { return id; }
    public String getSubstance() { return substance; }
    public Double getDensity() { return density; }

    @Override
    public String toString() {
        return "Density [id=" + id + ", substance=" + substance + ", density=" + density + "]";
    }
}
