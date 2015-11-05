package com.example.jens.kitchenconverter;


import android.content.Context;

public class Density {

    private int id;
    private int substanceId; // id of substance
    private Double density; // density in kg/l (water = 1)

    private final Context ctx;

    public Density(Context context) { this.ctx=context; }
    public Density(int substanceId, Double density, Context context) {
        super();
        this.ctx = context;
        setSubstanceId(substanceId);
        setDensity(density);
    }

    // setters
    public void setId(int i) { this.id = i; }
    public void setSubstanceId(int u) {
        this.substanceId = u;
    }
    public void setDensity(Double f) {
        if( f == null ) {
            throw new IllegalArgumentException("Density is null");
        }
        this.density = f; }

    // getters
    public int getId() { return id; }
    public int getSubstanceId() { return substanceId; }
    public Double getDensity() { return density; }
}
