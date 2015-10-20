package com.example.jens.kitchenconverter;

import android.database.SQLException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.IOException;
import java.util.List;

public class GeneralConverterActivity extends AppCompatActivity {

    private Spinner from_spinner;
    private Spinner to_spinner;

    SpinnerUnitAdapter fUnitAdapter;
    SpinnerUnitAdapter tUnitAdapter;

    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String[] dimensions = getResources().getStringArray(R.array.dimensions_array);

        setContentView(R.layout.activity_general_converter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        from_spinner = (Spinner) findViewById(R.id.from_spinner);
        to_spinner = (Spinner) findViewById(R.id.to_spinner);

        final EditText editText = (EditText) findViewById(R.id.enter_value);
        final TextView resultView = (TextView) findViewById(R.id.result_value);
        final ToggleButton toggle = (ToggleButton) findViewById(R.id.toggle_button);


        // create or open Database
        DataBaseHelper myDbHelper = new DataBaseHelper(this);
        try {
            myDbHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }
        try {
            myDbHelper.openDataBase();
        } catch (SQLException sqle) {
            throw sqle;
        }
        List<Unit> list = myDbHelper.getAllUnits();

        // populate spinner
        fUnitAdapter = new SpinnerUnitAdapter(this, android.R.layout.simple_spinner_item, list);
        tUnitAdapter = new SpinnerUnitAdapter(this, android.R.layout.simple_spinner_item, list);
        from_spinner.setAdapter(fUnitAdapter);
        to_spinner.setAdapter(tUnitAdapter);

        myDbHelper.close();
    }
}
