package com.example.jens.kitchenconverter;


import android.content.Context;

public class Substance {
    private int id;
    private String name; // name of substance

    private final Context ctx;

    public Substance(Context context) { this.ctx=context; }
    public Substance(String name, Context context) {
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

}
