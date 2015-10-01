package com.example.jens.kitchenconverter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import java.io.IOException;
import java.util.List;

public class UnitsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private Toolbar toolbar;
    private RadioButton radioButton;
    final Context context = this;

    UnitAdapter mUnitAdapter;
    ListView mainListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_units);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // create or open Database

        DataBaseHelper myDbHelper = new DataBaseHelper(this);

        try {
            myDbHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }

        try {
            myDbHelper.openDataBase();
        }catch(SQLException sqle){
            throw sqle;
        }

        mainListView = (ListView) findViewById(R.id.listView);

        // 5. Set this Activity to react to list items being pressed
        mainListView.setOnItemClickListener(this);

        // output as list

        List<Unit> list = myDbHelper.getAllUnits();

        // Create a UnitAdapter for the ListView
        mUnitAdapter = new UnitAdapter(this, getLayoutInflater());

        // Set the ListView to use the UnitAdapter
        mainListView.setAdapter(mUnitAdapter);

        mUnitAdapter.updateData(list);
        myDbHelper.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // set click listener for "Add Unit" button in activity_units
        Button addUnitBtn = (Button) findViewById(R.id.addBtn);
        addUnitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog d = new Dialog(context);
                d.setContentView(R.layout.add_dialog);
                d.setTitle("Add unit");
                d.setCancelable(true);


                final EditText editUnit = (EditText) d.findViewById(R.id.editTextUnit);
                final RadioGroup radioDimensionGroup= (RadioGroup) d.findViewById(R.id.radio_group);

                // add radio buttons programmatically
                LinearLayout.LayoutParams layoutParams = new RadioGroup.LayoutParams(
                        RadioGroup.LayoutParams.WRAP_CONTENT,
                        RadioGroup.LayoutParams.WRAP_CONTENT);
                String[] dimensions = getResources().getStringArray(R.array.dimensions_array);

                for(int i=0; i < dimensions.length; i++) {
                    RadioButton rb= new RadioButton(context);
                    rb.setText(dimensions[i]);
                    rb.setId(i);
                    radioDimensionGroup.addView(rb,i,layoutParams);
                }

                final EditText editFactor = (EditText) d.findViewById(R.id.editTextFactor);

                Button addBtn = (Button) d.findViewById(R.id.button1);
                // set click listener for add button in add_dialog
                addBtn.setOnClickListener(new View.OnClickListener() {
                                              public void onClick(View v) {

                                                  String unitName = editUnit.getText().toString();

                                                  int rgid = radioDimensionGroup.getCheckedRadioButtonId();
                                                  radioButton = (RadioButton) d.findViewById(rgid);
                                                  String unitDimension = radioButton.getText().toString();

                                                  Float unitFactor = Float.valueOf(editFactor.getText().toString());

                                                  Unit addunit = new Unit(unitName, unitDimension, unitFactor, getApplicationContext());
                                                  DataBaseHelper myDbHelper = new DataBaseHelper(context);
                                                  myDbHelper.addUnit(addunit);
                                                  mUnitAdapter.updateData(myDbHelper.getAllUnits());
                                                  myDbHelper.close();
                                                  d.dismiss();
                                              }
                                          }


                );
                d.show();
            }
        });
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
                Intent i = new Intent(UnitsActivity.this,SettingsActivity.class);
                startActivity(i);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final Unit unit = mUnitAdapter.getItem(position);


        final Dialog d = new Dialog(context);
        d.setContentView(R.layout.edit_dialog);
        d.setTitle("Edit or delete unit");
        d.setCancelable(true);

        // fill form with stored values
        final EditText editUnit = (EditText) d.findViewById(R.id.editTextUnit);
        editUnit.setText(unit.getUnit());

        String savedDimension=unit.getDimension();

        final RadioGroup radioDimensionGroup= (RadioGroup) d.findViewById(R.id.radio_group);

        // add radio buttons programmatically
        LinearLayout.LayoutParams layoutParams = new RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.WRAP_CONTENT,
                RadioGroup.LayoutParams.WRAP_CONTENT);
        String[] dimensions = getResources().getStringArray(R.array.dimensions_array);

        for(int i=0; i < dimensions.length; i++) {
            RadioButton rb= new RadioButton(context);
            rb.setText(dimensions[i]);
            rb.setId(i);
            if(dimensions[i].equals(savedDimension)) { rb.setChecked(true); }
            radioDimensionGroup.addView(rb,i,layoutParams);
        }


        final EditText editFactor = (EditText) d.findViewById(R.id.editTextFactor);
        editFactor.setText(Float.toString(unit.getFactor()));

        d.show();




        Button deleteBtn = (Button) d.findViewById(R.id.button_delete);
        Button modifyBtn = (Button) d.findViewById(R.id.button_modify);
        // set click listener for delete button in modify_dialog
        deleteBtn.setOnClickListener(new View.OnClickListener() {
                                         public void onClick(View v) {

                                             DataBaseHelper myDbHelper = new DataBaseHelper(context);
                                             myDbHelper.deleteUnit(unit);
                                             mUnitAdapter.updateData(myDbHelper.getAllUnits());
                                             myDbHelper.close();
                                             d.dismiss();
                                         }
                                     }
        );

        // set click listener for modify button in modify_dialog
        modifyBtn.setOnClickListener(new View.OnClickListener() {
                                         public void onClick(View v) {
                                             DataBaseHelper myDbHelper = new DataBaseHelper(context);

                                             String unitName = editUnit.getText().toString();
                                             int rgid = radioDimensionGroup.getCheckedRadioButtonId();

                                             radioButton = (RadioButton) d.findViewById(rgid);
                                             String unitDimension = radioButton.getText().toString();

                                             Float unitFactor = Float.valueOf(editFactor.getText().toString());

                                             unit.setUnit(unitName);
                                             unit.setDimension(unitDimension);
                                             unit.setFactor(unitFactor);

                                             myDbHelper.updateUnit(unit);
                                             mUnitAdapter.updateData(myDbHelper.getAllUnits());
                                             myDbHelper.close();
                                             d.dismiss();
                                         }
                                     }
        );


    }
}
