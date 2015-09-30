package com.example.jens.kitchenconverter;


public class Unit {

    private int id;
    private String unit; // name of unit, e.g. "m", "cm"
    private String dimension; // "length", "mass",...
    private Float factor; // factor relative to base unit

    public Unit(){}

    public Unit(String unit, String dimension, Float factor) {
        super();
        this.unit = unit;
        this.dimension = dimension;
        this.factor = factor;
    }

    public void setId(int i) { this.id = i; }

    public void setUnit(String u) { this.unit = u; }

    public void setDimension(String d) { this.dimension = d; }

    public void setFactor(Float f) { this.factor = f; }

    public int getId() { return id; }

    public String getUnit() { return unit; }

    public String getDimension() { return dimension; }

    public Float getFactor() { return factor; }

    @Override
    public String toString() {
        return "Unit [id=" + id + ", name=" + unit + ", dimension=" + dimension + ", factor=" + factor + "]";
    }


}
