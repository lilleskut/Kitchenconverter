package com.example.jens.kitchenconverter;


import android.content.Context;

public class PackageDensity {
    private int id;
    private String substance;
    private String packageName;
    private Double packageDensity;

    private final Context ctx;

    public PackageDensity(Context context) { this.ctx=context; }
    public PackageDensity(String substance, String packageName, Double packageDensity, Context context) {
        super();
        this.ctx = context;
        setSubstance(substance);
        setPackageName(packageName);
        setPackageDensity(packageDensity);
    }

    // setters
    public void setId(int i) { this.id = i; }
    public void setSubstance(String u) {
        if( u == null ) {
            throw new IllegalArgumentException("PackageDensity substance is null");
        }
        this.substance = u;
    }
    public void setPackageName(String u) {
        if( u == null ) {
            throw new IllegalArgumentException("PackageDensity package type is null");
        }
        this.packageName = u;
    }
    public void setPackageDensity(Double f) {
        if( f != null && f < 0 ) {
            throw new IllegalArgumentException("Package density is negative");
        }
        this.packageDensity = f;
    }

    // getters
    public int getId() { return id; }
    public String getSubstance() { return substance; }
    public String getPackageName() { return packageName; }
    public Double getPackageDensity() { return packageDensity; }

    @Override
    public String toString() {
        return  substance + " | " + packageName + " | " + packageDensity;
    }
}
