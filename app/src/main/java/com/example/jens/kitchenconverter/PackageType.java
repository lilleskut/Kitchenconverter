package com.example.jens.kitchenconverter;

import android.content.Context;

public class PackageType {
    private int id;
    private String name;

    private final Context ctx;

    public PackageType(Context context) { this.ctx=context; }
    public PackageType(String name, Context context) {
        super();
        this.ctx = context;
        setName(name);
    }

    // setters
    public void setId(int i) { this.id = i; }
    public void setName(String u) {
        this.name = u;
    }


    // getters
    public int getId() { return id; }
    public String getName() { return name; }

    @Override
    public String toString() {
        return "PackageType [id=" + id + ", name=" + name;
    }
}
