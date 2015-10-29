package com.example.jens.kitchenconverter;


import android.content.Context;

public class Density {

    private int id;
    private String substance; // name of substance
    private Double density; // density in kg/l (water = 1)

    private Context ctx;

    public Density(Context context) { this.ctx=context; }

    public Density(String substance, Double density, Context context) {
        super();
        this.ctx = context;
        setSubstance(substance);
        setDensity(density);
    }

    public void setId(int i) { this.id = i; }

    public void setSubstance(String u) {
        if( u == null ) {
            throw new IllegalArgumentException("Substance name is null");
        }
        this.substance = u;
    }

    public void setDensity(Double f) {
        if( f == null ) {
            throw new IllegalArgumentException("Density is null");
        }
        this.density = f; }

    public int getId() { return id; }
    public String getSubstance() { return substance; }

    public Double getDensity() { return density; }
}
