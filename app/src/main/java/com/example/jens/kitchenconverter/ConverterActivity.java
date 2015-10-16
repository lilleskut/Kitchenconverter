package com.example.jens.kitchenconverter;

import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;


public class ConverterActivity extends AppCompatActivity {

    private Spinner from_spinner;
    private Spinner to_spinner;
    private static final int startTemp = 100; // start temperature (in Celsius) for temperature converter onCreate
    SpinnerUnitAdapter fUnitAdapter;
    SpinnerUnitAdapter tUnitAdapter;

    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String[] dimensions = getResources().getStringArray(R.array.dimensions_array);
        String dim = getIntent().getStringExtra("dimension");

        if(!Arrays.asList(dimensions).contains(dim)){
            throw new IllegalArgumentException("Dimension is not one of the permittable dimension names");
        }

        if(dim.equals("temperature")) {
            setContentView(R.layout.activity_converter_temperature);

            Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Convert temperature");

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
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Convert " + dim);

            from_spinner = (Spinner) findViewById(R.id.from_spinner);
            to_spinner = (Spinner) findViewById(R.id.to_spinner);

            final EditText enterString = (EditText) findViewById(R.id.enter_value);
            final TextView resultValue = (TextView) findViewById(R.id.result_value);


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

            List<Unit> list = myDbHelper.getUnitsDimension(dim);

            fUnitAdapter = new SpinnerUnitAdapter(this, android.R.layout.simple_spinner_item, list);
            tUnitAdapter = new SpinnerUnitAdapter(this, android.R.layout.simple_spinner_item, list);
            from_spinner.setAdapter(fUnitAdapter);
            to_spinner.setAdapter(tUnitAdapter);


            enterString.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    //float enterValue = Float.valueOf(enterString.getText().toString());
                    if (!s.toString().isEmpty()) {
                        double enterValue = Double.valueOf(s.toString());

                        Unit fUnit = (Unit) from_spinner.getSelectedItem();
                        double from_factor = fUnit.getFactor();

                        Unit tUnit = (Unit) to_spinner.getSelectedItem();
                        double to_factor = tUnit.getFactor();

                        resultValue.setText(prettyPrint(enterValue * from_factor / to_factor));
                    }
                }
            });

            from_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view,
                                           int position, long id) {
                    if (!enterString.getText().toString().isEmpty()) {
                        double enterValue = Double.valueOf(enterString.getText().toString());

                        Unit fUnit = fUnitAdapter.getItem(position);
                        double from_factor = fUnit.getFactor();

                        Unit tUnit = (Unit) to_spinner.getSelectedItem();
                        double to_factor = tUnit.getFactor();

                        resultValue.setText(prettyPrint(enterValue * from_factor / to_factor));
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapter) {
                }
            });

            to_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view,
                                           int position, long id) {
                    if (!enterString.getText().toString().isEmpty()) {
                        double enterValue = Double.valueOf(enterString.getText().toString());

                        Unit fUnit = (Unit) from_spinner.getSelectedItem();
                        double from_factor = fUnit.getFactor();

                        Unit tUnit = tUnitAdapter.getItem(position);
                        double to_factor = tUnit.getFactor();

                        resultValue.setText(prettyPrint(enterValue * from_factor / to_factor));
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapter) {
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

    // print numbers without trailing 0
    private static String prettyPrint(double d) {
        DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        df.setMaximumFractionDigits(340);
        return df.format(d);
    }

    // convert celsius to Fahrenheit
    private static long tempConverter(int celsius) { return (celsius*9/5)+32; }
}
