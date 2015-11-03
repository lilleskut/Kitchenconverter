package com.example.jens.kitchenconverter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class UnitsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private static final String TAG = "UnitsActivity";
    private static final Double zeroThreshold = 0.000000001;
    private RadioButton radioButton;
    private RadioButton radioFilterButton;
    final Context context = this;

    UnitAdapter mUnitAdapter;
    ListView mainListView;


    LinearLayout.LayoutParams layoutParams = new RadioGroup.LayoutParams(
            RadioGroup.LayoutParams.WRAP_CONTENT,
            RadioGroup.LayoutParams.WRAP_CONTENT);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_units);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        if( getSupportActionBar() != null ) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        // set header
        TextView header1 = (TextView) findViewById(R.id.column_header1);
        TextView header2 = (TextView) findViewById(R.id.column_header2);
        TextView header3 = (TextView) findViewById(R.id.column_header3);

        header1.setText(R.string.unit);
        header2.setText(R.string.dimension);
        header3.setText(R.string.factor);

        final DataBaseHelper myDbHelper = new DataBaseHelper(this,getFilesDir().getAbsolutePath());

        mainListView = (ListView) findViewById(R.id.listView);
        mainListView.setOnItemClickListener(this);

        mainListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final Unit unit = mUnitAdapter.getItem(position);

                if( !unit.getBase() ) { //only do something if long clicked on not base unit

                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

                    builder.setTitle("Confirm new base unit");
                    builder.setMessage("Are you sure to make " + unit.getUnit() + " your new base unit for dimension " + unit.getDimension() + "?");

                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            myDbHelper.updateBaseUnit(unit);
                            mUnitAdapter.updateData(myDbHelper.getAllUnits());
                            dialog.dismiss();
                        }
                    });

                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.show();
                }
                return true;
            }
        });

        List<Unit> list = myDbHelper.getAllUnits();

        mUnitAdapter = new UnitAdapter(getLayoutInflater());
        mainListView.setAdapter(mUnitAdapter);

        mUnitAdapter.updateData(list);
        myDbHelper.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add(Menu.NONE, 98,Menu.NONE,R.string.filter).setIcon(R.drawable.ic_filter_list_white_48dp).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add(Menu.NONE, 99,Menu.NONE,R.string.add).setIcon(R.drawable.ic_add_white_48dp).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);


        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        String[] dimensions = getResources().getStringArray(R.array.dimensions_array);
        switch(id) {
            case R.id.action_settings:
                Intent i = new Intent(UnitsActivity.this,SettingsActivity.class);
                startActivity(i);
                break;
             case 98: // filter
                final Dialog fd = new Dialog(context);
                fd.setContentView(R.layout.filter_units_dialog);
                fd.setTitle("Filter");
                fd.setCancelable(true);

                final RadioGroup radioFilterGroup= (RadioGroup) fd.findViewById(R.id.radio_group);
                for(int j=0; j < dimensions.length; j++) {
                     RadioButton rb= new RadioButton(context);
                     rb.setText(dimensions[j]);
                     rb.setId(j);
                     radioFilterGroup.addView(rb,j,layoutParams);
                }

                radioFilterGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                     @Override
                     public void onCheckedChanged(RadioGroup group, int checkedId) {

                         int rfid = radioFilterGroup.getCheckedRadioButtonId();
                         radioFilterButton = (RadioButton) fd.findViewById(rfid);
                         String filterString = radioFilterButton.getText().toString();
                         if(filterString.equalsIgnoreCase("all")) {
                             mUnitAdapter.getFilter().filter("");
                         } else {
                             mUnitAdapter.getFilter().filter(filterString);
                         }
                         fd.dismiss();
                  }
                 });
                 fd.show();
                break;

            case 99: // add
                final Dialog d = new Dialog(context);
                d.setContentView(R.layout.add_unit_dialog);
                d.setTitle(R.string.addUnit);
                d.setCancelable(true);

                final EditText editUnit = (EditText) d.findViewById(R.id.editTextUnit);
                final RadioGroup radioDimensionGroup= (RadioGroup) d.findViewById(R.id.radio_group);

                DataBaseHelper myDbHelper = new DataBaseHelper(context,getFilesDir().getAbsolutePath());

                ArrayList<List<Unit>> unitListArray = new ArrayList<>(); // collection of unit lists; each array elemtn corresponds to one dimension
                final ArrayList<SpinnerUnitAdapter> unitAdapterArray = new ArrayList<>();

                for(int j=0; j < dimensions.length; j++) {
                    RadioButton rb = new RadioButton(context);
                    rb.setText(dimensions[j]);
                    rb.setId(j);
                    radioDimensionGroup.addView(rb, j, layoutParams);

                    List<Unit> list = myDbHelper.getUnitsDimension(dimensions[j]);
                    SpinnerUnitAdapter sUnitAdapter = new SpinnerUnitAdapter(this, android.R.layout.simple_spinner_item, list);
                    unitListArray.add(j,list);
                    unitAdapterArray.add(sUnitAdapter);
                }

                myDbHelper.close();

                final EditText editFactor = (EditText) d.findViewById(R.id.editTextFactor);
                final Spinner unitSpinner = (Spinner) d.findViewById(R.id.unit_spinner);




                radioDimensionGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        unitSpinner.setAdapter(unitAdapterArray.get(checkedId));
                    }
                });



                Button addBtn = (Button) d.findViewById(R.id.button1);


                // set click listener for add button in add_unit_dialog
                addBtn.setOnClickListener(new View.OnClickListener() {
                                              public void onClick(View v) {
                                                  String unitName = editUnit.getText().toString();

                                                  int rgid = radioDimensionGroup.getCheckedRadioButtonId();
                                                  radioButton = (RadioButton) d.findViewById(rgid);
                                                  String unitDimension = radioButton.getText().toString();

                                                  Double unitFactor = Double.valueOf(editFactor.getText().toString());

                                                  if (unitFactor > zeroThreshold ) { // don't set units with factor 0
                                                      Unit selected_unit = (Unit) unitSpinner.getSelectedItem();
                                                      Double spinner_factor = selected_unit.getFactor();

                                                      Unit addunit = new Unit(unitName, unitDimension, unitFactor * spinner_factor, false, context);
                                                      DataBaseHelper myDbHelper = new DataBaseHelper(context, getFilesDir().getAbsolutePath());

                                                      myDbHelper.addUnit(addunit);
                                                      mUnitAdapter.updateData(myDbHelper.getAllUnits());
                                                      myDbHelper.close();
                                                  }
                                                  d.dismiss();
                                              }
                                          }
                );
                d.show();
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
        d.setContentView(R.layout.edit_unit_dialog);
        d.setTitle("Edit or delete unit");
        d.setCancelable(true);

        // fill form with stored values
        final EditText editUnit = (EditText) d.findViewById(R.id.editTextUnit);
        editUnit.setText(unit.getUnit());

        String savedDimension=unit.getDimension();

        final RadioGroup radioDimensionGroup= (RadioGroup) d.findViewById(R.id.radio_group);
        String[] dimensions = getResources().getStringArray(R.array.dimensions_array);
        // add radio buttons programmatically
        LinearLayout.LayoutParams layoutParams = new RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.WRAP_CONTENT,
                RadioGroup.LayoutParams.WRAP_CONTENT);
        final Spinner unitSpinner = (Spinner) d.findViewById(R.id.unit_spinner);

        DataBaseHelper myDbHelper = new DataBaseHelper(context,getFilesDir().getAbsolutePath());
        ArrayList<List<Unit>> unitListArray = new ArrayList<>(); // collection of unit lists; each array elemtn corresponds to one dimension
        final ArrayList<SpinnerUnitAdapter> unitAdapterArray = new ArrayList<>();

        for(int i=0; i < dimensions.length; i++) {
            RadioButton rb= new RadioButton(context);
            rb.setText(dimensions[i]);
            rb.setId(i);
            if(dimensions[i].equals(savedDimension)) { rb.setChecked(true); }
            radioDimensionGroup.addView(rb,i,layoutParams);

            List<Unit> list = myDbHelper.getUnitsDimension(dimensions[i]);
            SpinnerUnitAdapter sUnitAdapter = new SpinnerUnitAdapter(this, android.R.layout.simple_spinner_item, list);
            unitListArray.add(i, list);
            unitAdapterArray.add(sUnitAdapter);
            if(dimensions[i].equals(savedDimension)) { unitSpinner.setAdapter(sUnitAdapter); }
        }

        myDbHelper.close();

        final EditText editFactor = (EditText) d.findViewById(R.id.editTextFactor);
        editFactor.setText(Double.toString(unit.getFactor()));

        Button deleteBtn = (Button) d.findViewById(R.id.button_delete);
        Button modifyBtn = (Button) d.findViewById(R.id.button_modify);

        if( unit.getBase() ) { // don't show delete button if base unit
            deleteBtn.setVisibility(View.INVISIBLE);
        }

        d.show();





        radioDimensionGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                unitSpinner.setAdapter(unitAdapterArray.get(checkedId));
            }
        });

        // set click listener for delete button in modify_dialog
        deleteBtn.setOnClickListener(new View.OnClickListener() {
                                         public void onClick(View v) {

                                             DataBaseHelper myDbHelper = new DataBaseHelper(context,getFilesDir().getAbsolutePath());
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
                                             DataBaseHelper myDbHelper = new DataBaseHelper(context,getFilesDir().getAbsolutePath());

                                             String unitName = editUnit.getText().toString();
                                             int rgid = radioDimensionGroup.getCheckedRadioButtonId();

                                             radioButton = (RadioButton) d.findViewById(rgid);
                                             String unitDimension = radioButton.getText().toString();

                                             Double unitFactor = Double.valueOf(editFactor.getText().toString());

                                             Unit selected_unit = (Unit) unitSpinner.getSelectedItem();
                                             Double spinner_factor = selected_unit.getFactor();

                                             unit.setUnit(unitName);
                                             unit.setDimension(unitDimension);
                                             unit.setFactor(unitFactor*spinner_factor);

                                             myDbHelper.updateUnit(unit);
                                             mUnitAdapter.updateData(myDbHelper.getAllUnits());
                                             myDbHelper.close();
                                             d.dismiss();
                                         }
                                     }
        );


    }


}
