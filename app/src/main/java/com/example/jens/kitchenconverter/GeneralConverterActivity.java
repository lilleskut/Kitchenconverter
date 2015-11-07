package com.example.jens.kitchenconverter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.List;

public class GeneralConverterActivity extends AppCompatActivity {

    private final static String TAG = "GeneralConverter";

    private boolean automaticChanged = false;
    private EditText editText;
    private TextView resultView;
    private ToggleButton toggle;


    private SpinnerUnitAdapter fUnitAdapter;
    private SpinnerUnitAdapter tUnitAdapter;
    private SpinnerDensityAdapter densityAdapter;

    private MyRational enterRational = new MyRational();
    private MyRational from_factor = new MyRational();
    private MyRational to_factor = new MyRational();
    private MyRational density_factor = new MyRational();
    private MyRational result = new MyRational();

    private Unit fUnit;
    private Unit tUnit;
    private Density density;


    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_converter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        if( getSupportActionBar() != null ) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        // find views
        Spinner from_spinner = (Spinner) findViewById(R.id.from_spinner);
        Spinner to_spinner = (Spinner) findViewById(R.id.to_spinner);
        editText = (EditText) findViewById(R.id.enter_value);
        resultView = (TextView) findViewById(R.id.result_value);
        toggle = (ToggleButton) findViewById(R.id.toggle_button);
        Spinner density_spinner = (Spinner) findViewById(R.id.density_spinner);
        final Button clear_button = (Button) findViewById(R.id.clear_button);

        // get all units and all densities from DB
        DataBaseHelper myDbHelper = new DataBaseHelper(this,getFilesDir().getAbsolutePath());
        List<Unit> list = myDbHelper.getAllUnits();
        List<Density> densities = myDbHelper.getAllDensities();
        myDbHelper.close();

        // populate from/to/density spinner
        fUnitAdapter = new SpinnerUnitAdapter(this, android.R.layout.simple_spinner_item, list);
        tUnitAdapter = new SpinnerUnitAdapter(this, android.R.layout.simple_spinner_item, list);
        densityAdapter = new SpinnerDensityAdapter(this, android.R.layout.simple_spinner_item, densities);
        from_spinner.setAdapter(fUnitAdapter);
        to_spinner.setAdapter(tUnitAdapter);
        density_spinner.setAdapter(densityAdapter);

        // initialize from/to/density_factor
        fUnit = (Unit) from_spinner.getSelectedItem();
        tUnit = (Unit) to_spinner.getSelectedItem();
        density = (Density) density_spinner.getSelectedItem();
        from_factor.setRationalFromDouble(fUnit.getFactor());
        to_factor.setRationalFromDouble(tUnit.getFactor());
        density_factor.setRationalFromDouble(density.getDensity());

        // Set listeners
        editText.addTextChangedListener(textWatcher);
        from_spinner.setOnItemSelectedListener(onItemSelectedListenerFrom);
        to_spinner.setOnItemSelectedListener(onItemSelectedListenerTo);
        density_spinner.setOnItemSelectedListener(onItemSelectedListenerDensity);
        toggle.setOnCheckedChangeListener(toggleListener);
        clear_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                editText.setText("");
                resultView.setText("");
                enterRational.unSet();
                result.unSet();
            }
        });
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
                if (inputValid()) {
                    enterRational.setRationalFromString(s.toString());
                    if (enterRational.isSet()) {
                        calculateDisplayResult();
                    }
                } else {
                    resultView.setText("");
                    enterRational.unSet();
                    result.unSet();
                }
            } else {
                automaticChanged = false;
            }
        }
    };

    CompoundButton.OnCheckedChangeListener toggleListener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            if (inputValid()) {
                automaticChanged = true;
                String editString;
                if (isChecked) { // fractions;
                    editString = enterRational.toFractionString();
                    resultView.setText(result.toFractionString());

                } else { //decimals
                    editString = String.valueOf(enterRational.toDecimalsString());
                    resultView.setText(String.valueOf(result.toDecimalsString()));
                }
                editText.setText(editString);
                editText.setSelection(editString.length());
            }
        }
    };

    AdapterView.OnItemSelectedListener onItemSelectedListenerFrom = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view,
        int position, long id) {

            if (inputValid()) {

                fUnit = fUnitAdapter.getItem(position);
                from_factor.setRationalFromDouble(fUnit.getFactor());

                calculateDisplayResult();
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

            if (inputValid()) {

                tUnit = tUnitAdapter.getItem(position);
                to_factor.setRationalFromDouble(tUnit.getFactor());

                calculateDisplayResult();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapter) {
        }
    };

    AdapterView.OnItemSelectedListener onItemSelectedListenerDensity = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view,
                                   int position, long id) {
            if (inputValid()) {

                density = densityAdapter.getItem(position);
                density_factor.setRationalFromDouble(density.getDensity());

                calculateDisplayResult();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapter) {
        }
    };


    private boolean inputValid() { // check whether entered value has proper format
        String s = editText.getText().toString();

        // return (!s.isEmpty() && enterRational.isSet() && MyRational.validFraction(s));
        if (s.isEmpty() || !MyRational.validFraction(s) ) {
            return false;
        } else {
            return true;
        }
    }


    private static boolean isMassVolume(Unit a, Unit b) {
        String aDim = a.getDimension();
        String bDim = b.getDimension();
        return (aDim.equals("mass") && bDim.equals("volume")) || (bDim.equals("mass") && aDim.equals("volume"));
    }

    private static boolean hasSameDimension(Unit a, Unit b) {
        return a.getDimension().equals(b.getDimension());
    }

    private void calculateDisplayResult() {
        if(hasSameDimension(fUnit,tUnit)) { // calculate and display result
            result = enterRational.multiply(from_factor).divide(to_factor);

        }  else if(isMassVolume(fUnit,tUnit)) { // mass-volume conversion

            if(fUnit.getDimension().equals("mass")) {
                result = enterRational.multiply(from_factor).divide(to_factor).divide(density_factor);
            } else {
                result = enterRational.multiply(from_factor).divide(to_factor).multiply(density_factor);
            }

        } else { // not convertable
            result.unSet();

            Toast cannotConvert = Toast.makeText(getApplicationContext(),"Cannot convert these",Toast.LENGTH_SHORT);
            cannotConvert.show();
        }
        if (result.isSet()) {
            if (toggle.isChecked()) { // fractions
                resultView.setText(result.toFractionString());
            } else { // decimals
                resultView.setText(result.toDecimalsString());
            }
        } else {
            resultView.setText("");
        }
    }
}
