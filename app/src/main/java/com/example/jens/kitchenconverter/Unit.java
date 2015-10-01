package com.example.jens.kitchenconverter;

import java.util.Arrays;

public class Unit {

    private int id;
    private String unit; // name of unit, e.g. "m", "cm"
    private String dimension; // "length", "mass",...
    private Float factor; // factor relative to base unit

    public Unit(){}

    public Unit(String unit, String dimension, Float factor) {
        super();
        setUnit(unit);
        setDimension(dimension);
        setFactor(factor);
    }

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

        /*
        String[] dimensions_array=null; // need to get from resources

        if(!Arrays.asList(dimensions_array).contains(d)){
            throw new IllegalArgumentException("Dimension is not one of the permittable dimension names");
        }
        */
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
