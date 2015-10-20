package com.example.jens.kitchenconverter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

/**
 * Created by jens on 10/20/15.
 */
public class GeneralConverterActivity extends AppCompatActivity {

    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String[] dimensions = getResources().getStringArray(R.array.dimensions_array);

        setContentView(R.layout.activity_general_converter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
