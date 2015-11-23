package com.example.jens.kitchenconverter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.Arrays;
import java.util.List;


public class ConverterActivity extends AppCompatActivity {

    private Spinner from_spinner;
    private Spinner to_spinner;

    private MyRational enterRational;

    private static final String TAG = "ConverterActivity";
    private static final int startTemp = 100; // start temperature (in Celsius) for temperature converter onCreate
    private SpinnerUnitAdapter fUnitAdapter;
    private SpinnerUnitAdapter tUnitAdapter;

    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String[] dimensions = getResources().getStringArray(R.array.dimensions_array);
        String dim = getIntent().getStringExtra("dimension");

        if(!Arrays.asList(dimensions).contains(dim) && !dim.contains("temperature")){
            throw new IllegalArgumentException("Dimension is not one of the permitted dimension names");
        }

        if(dim.equals("temperature")) {
            setContentView(R.layout.activity_converter_temperature);

            Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
            setSupportActionBar(toolbar);
            if( getSupportActionBar() != null ) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle(R.string.temperature_converter);
            }

            final TextView celsius = (TextView) findViewById(R.id.celsius);
            final TextView fahrenheit = (TextView) findViewById(R.id.fahrenheit);
            final TextView kelvin = (TextView) findViewById(R.id.kelvin);
            SeekBar mSeekBar = (SeekBar) findViewById(R.id.seek_bar);

            String celsiusString = String.valueOf(startTemp)+" \u2103";
            String fahrenheitString = String.valueOf(tempConverter(startTemp))+" \u2109";
            String kelvinString = String.valueOf(startTemp-273)+" K";

            celsius.setText(celsiusString);
            fahrenheit.setText(fahrenheitString);
            kelvin.setText(kelvinString);

            mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    String cString = String.valueOf(progress-273)+" \u2103";
                    String fString = String.valueOf(tempConverter(progress-273))+" \u2109";
                    String kString = String.valueOf(progress)+" K";

                    celsius.setText(cString);
                    fahrenheit.setText(fString);
                    kelvin.setText(kString);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        } else {


            setContentView(R.layout.activity_converter);

            Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
            setSupportActionBar(toolbar);
            if( getSupportActionBar() != null ) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("Convert " + dim);
            }

            from_spinner = (Spinner) findViewById(R.id.from_spinner);
            to_spinner = (Spinner) findViewById(R.id.to_spinner);

            final EditText editText = (EditText) findViewById(R.id.enter_value);
            final TextView resultView = (TextView) findViewById(R.id.result_value);
            final ToggleButton toggle = (ToggleButton) findViewById(R.id.toggle_button);


            // create or open Database

            DataBaseHelper myDbHelper = new DataBaseHelper(this,getFilesDir().getAbsolutePath());

            List<Unit> list = myDbHelper.getUnitsDimension(dim);

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
                    if (!s.toString().isEmpty() && MyRational.validFraction(s.toString())) {

                        enterRational = new MyRational(s.toString());

                        if(enterRational.isSet()) {
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

            //2. From spinner listener
            from_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view,
                                           int position, long id) {
                    String s = editText.getText().toString();
                    if (!s.isEmpty() && enterRational.isSet()) {

                        Unit fUnit = fUnitAdapter.getItem(position);
                        MyRational from_factor = new MyRational(fUnit.getFactor());

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

                @Override
                public void onNothingSelected(AdapterView<?> adapter) {
                }
            });

            //3. To spinner listener
            to_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view,
                                           int position, long id) {
                    String s = editText.getText().toString();
                    if (!s.isEmpty() && enterRational.isSet()) {

                        Unit fUnit = (Unit) from_spinner.getSelectedItem();
                        MyRational from_factor = new MyRational(fUnit.getFactor());

                        Unit tUnit = tUnitAdapter.getItem(position);
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

                @Override
                public void onNothingSelected(AdapterView<?> adapter) {
                }
            });

            //4. Toggle button listener
            toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    String s = editText.getText().toString();
                    //if (!s.isEmpty() && MyRational.validFraction(s)) {
                    if (!s.isEmpty() && enterRational.isSet()) {

                        Unit fUnit = (Unit) from_spinner.getSelectedItem();
                        MyRational from_factor = new MyRational(fUnit.getFactor());

                        Unit tUnit = (Unit) to_spinner.getSelectedItem();
                        MyRational to_factor = new MyRational(tUnit.getFactor());

                        // calculate result
                        MyRational result = enterRational.multiply(from_factor).divide(to_factor);

                        if (isChecked) { // fractions;
                            editText.setText(enterRational.toFractionString());
                            resultView.setText(result.toFractionString());

                        } else { //decimals
                            editText.setText(String.valueOf(enterRational.toDecimalsString()));
                            resultView.setText(String.valueOf(result.toDecimalsString()));
                        }
                    }
                }
            });

            myDbHelper.close();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id) {
            case R.id.action_settings:
                Intent i = new Intent(ConverterActivity.this,SettingsActivity.class);
                startActivity(i);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    // convert celsius to Fahrenheit
    private static long tempConverter(int celsius) { return (celsius*9/5)+32; }
}
