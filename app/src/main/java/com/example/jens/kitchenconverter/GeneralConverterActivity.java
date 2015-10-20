package com.example.jens.kitchenconverter;

import android.database.SQLException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.IOException;
import java.util.List;

public class GeneralConverterActivity extends AppCompatActivity {

    final String[] dimensions = getResources().getStringArray(R.array.dimensions_array);

    private Spinner from_spinner;
    private Spinner to_spinner;
    SpinnerUnitAdapter fUnitAdapter;
    SpinnerUnitAdapter tUnitAdapter;

    private MyRational enterRational;
    private MyRational from_factor;
    private MyRational to_factor;


    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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


        // 1. EditText change listener
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    resultView.setText("");
                } else if (MyRational.validFraction(s.toString())) {

                    enterRational = new MyRational(s.toString());

                    if (enterRational.isSet()) {
                        // get from_factor
                        Unit fUnit = (Unit) from_spinner.getSelectedItem();
                        MyRational from_factor = new MyRational(fUnit.getFactor());

                        // get to_factor
                        Unit tUnit = (Unit) to_spinner.getSelectedItem();
                        MyRational to_factor = new MyRational(tUnit.getFactor());

                        // calculate result
                        MyRational result = enterRational.multiply(from_factor).divide(to_factor);

                        // display depending on fractions/decimal-toggle
                        if (toggle.isChecked()) { // fractions
                            resultView.setText(result.toFractionString());
                        } else { // decimals
                            resultView.setText(result.toDecimalsString());
                        }
                    }
                }
            }
        });

        myDbHelper.close();
    }
}
