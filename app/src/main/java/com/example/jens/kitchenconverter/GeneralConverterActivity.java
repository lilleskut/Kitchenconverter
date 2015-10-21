package com.example.jens.kitchenconverter;

import android.database.SQLException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.IOException;
import java.util.List;

public class GeneralConverterActivity extends AppCompatActivity {


    boolean automaticChanged = false;
    EditText editText;
    TextView resultView;
    ToggleButton toggle;

    private Spinner from_spinner;
    private Spinner to_spinner;
    SpinnerUnitAdapter fUnitAdapter;
    SpinnerUnitAdapter tUnitAdapter;

    private MyRational enterRational = new MyRational();
    private MyRational from_factor = new MyRational();
    private MyRational to_factor = new MyRational();
    private MyRational result = new MyRational();

    private Unit fUnit;
    private Unit tUnit;


    protected void onCreate(Bundle savedInstanceState) {
        String[] dimensions = getResources().getStringArray(R.array.dimensions_array);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_converter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // find views
        from_spinner = (Spinner) findViewById(R.id.from_spinner);
        to_spinner = (Spinner) findViewById(R.id.to_spinner);
        editText = (EditText) findViewById(R.id.enter_value);
        resultView = (TextView) findViewById(R.id.result_value);
        toggle = (ToggleButton) findViewById(R.id.toggle_button);

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

        // populate from/to spinner
        fUnitAdapter = new SpinnerUnitAdapter(this, android.R.layout.simple_spinner_item, list);
        tUnitAdapter = new SpinnerUnitAdapter(this, android.R.layout.simple_spinner_item, list);
        from_spinner.setAdapter(fUnitAdapter);
        to_spinner.setAdapter(tUnitAdapter);

        // initialize from/to_factor
        fUnit = (Unit) from_spinner.getSelectedItem();
        tUnit = (Unit) to_spinner.getSelectedItem();
        from_factor.setRationalFromDouble(fUnit.getFactor());
        to_factor.setRationalFromDouble(tUnit.getFactor());

        // Set listeners
        editText.addTextChangedListener(textWatcher);
        from_spinner.setOnItemSelectedListener(onItemSelectedListenerFrom);
        to_spinner.setOnItemSelectedListener(onItemSelectedListenerTo);
        toggle.setOnCheckedChangeListener(onCheckedChangeListener);

        myDbHelper.close();
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!automaticChanged) {
                if (s.toString().isEmpty() || !MyRational.validFraction(s.toString())) {
                    resultView.setText("");
                    enterRational.unSet();
                    result.unSet();
                } else {
                    enterRational.setRationalFromString(s.toString());
                    if (enterRational.isSet()) {
                        // calculate result
                        result = enterRational.multiply(from_factor).divide(to_factor);
                        // display depending on fractions/decimal-toggle
                        if (toggle.isChecked()) { // fractions
                            resultView.setText(result.toFractionString());
                        } else { // decimals
                            resultView.setText(result.toDecimalsString());
                        }
                    }
                }
                Log.d("swap", "enter= " + enterRational.getNumerator() + "/" + enterRational.getDenominator());
                Log.d("swap", "result= " + result.getNumerator() + "/" + result.getDenominator());
            } else {
                automaticChanged = false;
            }
        }
    };

    CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            automaticChanged = true;
            String s = editText.getText().toString();
            if (!s.isEmpty() && enterRational.isSet() && MyRational.validFraction(s)) {

                if (isChecked) { // fractions;
                    editText.setText(enterRational.toFractionString());
                    resultView.setText(result.toFractionString());

                } else { //decimals
                    editText.setText(String.valueOf(enterRational.toDecimalsString()));
                    resultView.setText(String.valueOf(result.toDecimalsString()));
                }
            }
        }
    };

    AdapterView.OnItemSelectedListener onItemSelectedListenerFrom = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view,
        int position, long id) {
            String s = editText.getText().toString();
            if (!s.isEmpty() && enterRational.isSet() && MyRational.validFraction(s)) {

                fUnit = fUnitAdapter.getItem(position);
                from_factor.setRationalFromDouble(fUnit.getFactor());

                // calculate result
                result = enterRational.multiply(from_factor).divide(to_factor);

                // display depending on fractions/decimal-toggle
                if (toggle.isChecked()) { // fractions
                    resultView.setText(result.toFractionString());
                } else { // decimals
                    resultView.setText(result.toDecimalsString());
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapter) {
        }
    };

    AdapterView.OnItemSelectedListener onItemSelectedListenerTo = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view,
                                   int position, long id) {
            String s = editText.getText().toString();
            if (!s.isEmpty() && enterRational.isSet() && MyRational.validFraction(s)) {

                tUnit = tUnitAdapter.getItem(position);
                to_factor.setRationalFromDouble(tUnit.getFactor());

                // calculate result
                result = enterRational.multiply(from_factor).divide(to_factor);

                // display depending on fractions/decimal-toggle
                if (toggle.isChecked()) { // fractions
                    resultView.setText(result.toFractionString());
                } else { // decimals
                    resultView.setText(result.toDecimalsString());
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapter) {
        }
    };
}
