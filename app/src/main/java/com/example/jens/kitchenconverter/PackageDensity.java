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
    public void setSubstance(String u) { this.substance = u; }
    public void setPackageName(String u) {
        this.packageName = u;
    }
    public void setPackageDensity(Double f) { this.packageDensity = f; }

    // getters
    public int getId() { return id; }
    public String getSubstance() { return substance; }
    public String getPackageName() { return packageName; }
    public Double getPackageDensity() { return packageDensity; }
}
