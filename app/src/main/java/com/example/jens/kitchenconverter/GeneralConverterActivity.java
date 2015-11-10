package com.example.jens.kitchenconverter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
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
    private MyRational from_packagedensity_factor = new MyRational();
    private MyRational to_packagedensity_factor = new MyRational();
    private MyRational result = new MyRational();

    private Unit fUnit;
    private Unit tUnit;
    private Density density;
    private PackageDensity fDensity;
    private PackageDensity tDensity;


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

            if (inputValid() && result.isSet() ) {
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

            fUnit = fUnitAdapter.getItem(position);
            from_factor.setRationalFromDouble(fUnit.getFactor());

            if ( fUnit.getDimension().equals("pack") ) {
                DataBaseHelper myDbHelper = new DataBaseHelper(getApplicationContext(),getFilesDir().getAbsolutePath());
                List<PackageDensity> packageDensities = myDbHelper.getDensitiesSubstance(density.getSubstance());
                if ( packageDensities.size() > 0 ) {
                    from_packagedensity_factor.setRationalFromDouble(packageDensities.get(0).getPackageDensity()); // for >1 need to add select dialog
                }
                myDbHelper.close();
            }
            if (inputValid()) {
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



                tUnit = tUnitAdapter.getItem(position);
                to_factor.setRationalFromDouble(tUnit.getFactor());

                if ( tUnit.getDimension().equals("pack") ) {
                    DataBaseHelper myDbHelper = new DataBaseHelper(getApplicationContext(),getFilesDir().getAbsolutePath());
                    List<PackageDensity> packageDensities = myDbHelper.getDensitiesSubstance(density.getSubstance());
                    if ( packageDensities.size() > 0 ) {
                        to_packagedensity_factor.setRationalFromDouble(packageDensities.get(0).getPackageDensity()); // for >1 need to add select dialog
                    }
                    myDbHelper.close();
                }

            if (inputValid()) {
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


                density = densityAdapter.getItem(position);
                density_factor.setRationalFromDouble(density.getDensity());
                if ( tUnit.getDimension().equals("pack") ) {
                    DataBaseHelper myDbHelper = new DataBaseHelper(getApplicationContext(),getFilesDir().getAbsolutePath());
                    List<PackageDensity> packageDensities = myDbHelper.getDensitiesSubstance(density.getSubstance());
                    if ( packageDensities.size() > 0 ) {
                        to_packagedensity_factor.setRationalFromDouble(packageDensities.get(0).getPackageDensity()); // for >1 need to add select dialog
                    }
                    myDbHelper.close();
                }
                if ( fUnit.getDimension().equals("pack") ) {
                    DataBaseHelper myDbHelper = new DataBaseHelper(getApplicationContext(),getFilesDir().getAbsolutePath());
                    List<PackageDensity> packageDensities = myDbHelper.getDensitiesSubstance(density.getSubstance());
                    if ( packageDensities.size() > 0 ) {
                        from_packagedensity_factor.setRationalFromDouble(packageDensities.get(0).getPackageDensity()); // for >1 need to add select dialog
                    }
                    myDbHelper.close();
                }
            if (inputValid()) {
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

    private static boolean isMassPack(Unit a, Unit b) {
        String aDim = a.getDimension();
        String bDim = b.getDimension();
        return (aDim.equals("mass") && bDim.equals("pack")) || (bDim.equals("mass") && aDim.equals("pack"));
    }

    private static boolean isVolumePack(Unit a, Unit b) {
        String aDim = a.getDimension();
        String bDim = b.getDimension();
        return (aDim.equals("volume") && bDim.equals("pack")) || (bDim.equals("volume") && aDim.equals("pack"));
    }

    private static boolean hasSameDimension(Unit a, Unit b) {
        return a.getDimension().equals(b.getDimension());
    }

    private void cannotConvert() {
        result.unSet();
        Toast cannotConvert = Toast.makeText(getApplicationContext(),"Cannot convert these",Toast.LENGTH_SHORT);
        cannotConvert.show();
    }

    private void calculateDisplayResult() {

        /* cases:
            1. same dimension
            1.1 not "pack"
            1.2 pack -> pack

            2. different dimension
            2.1 mass <-> volume (convert via densities table)
            2.2 mass <-> package (convert via packageDensities table)
            2.3 volume <-> package (convert via densities and packageDensities table)
            2.4 not convertable combinations such as mass<->length (inform user)
        */

        if(hasSameDimension(fUnit,tUnit) && !fUnit.getDimension().equals("pack") ) {

            if( !fUnit.getDimension().equals("pack") ) {// case 1.1
                Log.d("TAG","case 1.1");
                result = enterRational.multiply(from_factor).divide(to_factor);
            } else { // case 1.2
                Log.d("TAG","case 1.2");
                if ( from_packagedensity_factor.isSet() && to_packagedensity_factor.isSet() ) {
                    result = enterRational.multiply(from_packagedensity_factor).divide(to_packagedensity_factor);
                } else {
                    cannotConvert();
                }
            }

        }  else { // case 2
            if(isMassVolume(fUnit,tUnit)) { // case 2.1
                Log.d("TAG","case 2.1");
                if (density_factor.isSet()) {
                    if (fUnit.getDimension().equals("mass")) { // mass -> volume
                        result = enterRational.multiply(from_factor).divide(to_factor).divide(density_factor);
                    } else { // volume -> mass
                        result = enterRational.multiply(from_factor).divide(to_factor).multiply(density_factor);
                    }
                } else { // no density defined for this substance
                    cannotConvert();
                }
            } else if ( isMassPack(fUnit,tUnit) ) {// case 2.2
                Log.d("TAG","case 2.2");
                if ( fUnit.getDimension().equals("mass") && to_packagedensity_factor.isSet() ) { // mass -> pack
                    result = enterRational.multiply(from_factor).divide(to_packagedensity_factor);
                } else if ( tUnit.getDimension().equals("mass") && from_packagedensity_factor.isSet() ) { // pack -> mass
                    result = enterRational.multiply(from_packagedensity_factor).divide(to_factor);
                } else {
                    cannotConvert();
                }
            } else if ( isVolumePack(fUnit, tUnit) ) { // case 2.3
                Log.d("TAG","case 2.3");
                if ( fUnit.getDimension().equals("volume") && to_packagedensity_factor.isSet() && density_factor.isSet() ) { // volume -> pack
                    result = enterRational.multiply(from_factor).divide(to_packagedensity_factor).multiply(density_factor);
                } else if ( fUnit.getDimension().equals("volume") && to_packagedensity_factor.isSet() && density_factor.isSet() ) { // pack -> volume
                    result = enterRational.multiply(from_factor).divide(to_packagedensity_factor).divide(density_factor);
                } else {
                    cannotConvert();
                }
            } else { // case 2.4
                Log.d("TAG","case 2.4");
                cannotConvert();
            }
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
