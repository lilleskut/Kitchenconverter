package com.example.jens.kitchenconverter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
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
                    builder.setMessage("Are you sure to make " + unit.getName() + " your new base unit for dimension " + unit.getDimension() + "?");

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
        final String[] dimensions = getResources().getStringArray(R.array.dimensions_array);
        switch(id) {
            case R.id.action_settings:
                Intent i = new Intent(UnitsActivity.this,SettingsActivity.class);
                startActivity(i);
                break;
             case 98: // filter

                 final List<String> filterList = new ArrayList<>(Arrays.asList(dimensions));
                 filterList.add("All");

                 AlertDialog.Builder filterDialogBuilder = new AlertDialog.Builder(
                         context);

                 filterDialogBuilder.setTitle(R.string.filter);
                 filterDialogBuilder.setSingleChoiceItems(filterList.toArray(new String[filterList.size()]), -1, new DialogInterface.OnClickListener() {
                     public void onClick(DialogInterface dialog, int item) {

                         String filterString = filterList.get(item);
                         if (filterString.equalsIgnoreCase("all")) {
                             mUnitAdapter.getFilter().filter("");
                         } else {
                             mUnitAdapter.getFilter().filter(filterString);
                         }
                     }

                 });

                 AlertDialog filterDialog = filterDialogBuilder.create();
                 filterDialog.show();

                break;

            case 99: // add

                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.add_unit_prompt, null);

                AlertDialog.Builder addDialogBuilder = new AlertDialog.Builder(
                        context);

                // set add_density_prompt.xml to alertdialog builder
                addDialogBuilder.setView(promptsView);

                final EditText editUnit = (EditText) promptsView.findViewById(R.id.editTextUnit);
                final EditText editFactor = (EditText) promptsView.findViewById(R.id.editTextFactor);
                final Spinner unitSpinner = (Spinner) promptsView.findViewById(R.id.unit_spinner);


                final DataBaseHelper myDbHelper = new DataBaseHelper(context,getFilesDir().getAbsolutePath());

                ArrayList<List<Unit>> unitListArray = new ArrayList<>(); // collection of unit lists; each array elemtn corresponds to one dimension
                final ArrayList<SpinnerUnitAdapter> unitAdapterArray = new ArrayList<>();

                for(int j=0; j < dimensions.length; j++) {
                    List<Unit> list = myDbHelper.getUnitsDimension(dimensions[j]);
                    SpinnerUnitAdapter sUnitAdapter = new SpinnerUnitAdapter(this, android.R.layout.simple_spinner_item, list);
                    unitListArray.add(j,list);
                    unitAdapterArray.add(sUnitAdapter);
                }

                myDbHelper.close();


                // set dialog message
                addDialogBuilder
                        .setCancelable(false)
                        .setTitle(R.string.addUnit)
                        .setSingleChoiceItems(dimensions, -1, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                unitSpinner.setAdapter(unitAdapterArray.get(item));
                            }})
                        .setPositiveButton(R.string.add,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        String unitName = editUnit.getText().toString();

                                        int selectedPosition = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
                                        String unitDimension = dimensions[selectedPosition];

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

                                }
                            })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    dialog.cancel();
                                }

                        });

                // create alert dialog
                AlertDialog addDialog = addDialogBuilder.create();
                addDialog.show();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final Unit unit = mUnitAdapter.getItem(position);
        final String[] dimensions = getResources().getStringArray(R.array.dimensions_array);

        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.add_unit_prompt, null);

        AlertDialog.Builder editDialogBuilder = new AlertDialog.Builder(
                context);

        editDialogBuilder.setView(promptsView);

        final EditText editUnit = (EditText) promptsView.findViewById(R.id.editTextUnit);
        editUnit.setText(unit.getName());

        String savedDimension = unit.getDimension();

        final EditText editFactor = (EditText) promptsView.findViewById(R.id.editTextFactor);
        editFactor.setText(Double.toString(unit.getFactor()));

        final Spinner unitSpinner = (Spinner) promptsView.findViewById(R.id.unit_spinner);

        final DataBaseHelper myDbHelper = new DataBaseHelper(context, getFilesDir().getAbsolutePath());

        ArrayList<List<Unit>> unitListArray = new ArrayList<>(); // collection of unit lists; each array elemtn corresponds to one dimension
        final ArrayList<SpinnerUnitAdapter> unitAdapterArray = new ArrayList<>();

        int savedDimensionId = -1;
        for (int j = 0; j < dimensions.length; j++) {
            List<Unit> list = myDbHelper.getUnitsDimension(dimensions[j]);
            SpinnerUnitAdapter sUnitAdapter = new SpinnerUnitAdapter(this, android.R.layout.simple_spinner_item, list);
            unitListArray.add(j, list);
            unitAdapterArray.add(sUnitAdapter);
            if (dimensions[j].equals(savedDimension)) {
                unitSpinner.setAdapter(sUnitAdapter);
                savedDimensionId = j;
            }

        }

        myDbHelper.close();


        // set dialog message
        editDialogBuilder
                .setCancelable(false)
                .setTitle(R.string.editUnit)
                .setSingleChoiceItems(dimensions, savedDimensionId, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        unitSpinner.setAdapter(unitAdapterArray.get(item));
                    }
                })
                .setPositiveButton(R.string.modify,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                DataBaseHelper myDbHelper = new DataBaseHelper(context, getFilesDir().getAbsolutePath());

                                String unitName = editUnit.getText().toString();

                                int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                                String unitDimension = dimensions[selectedPosition];

                                Double unitFactor = Double.valueOf(editFactor.getText().toString());
                                if (unitFactor > zeroThreshold) { // don't set units with factor 0
                                    Unit selected_unit = (Unit) unitSpinner.getSelectedItem();
                                    Double spinner_factor = selected_unit.getFactor();

                                    unit.setName(unitName);
                                    unit.setDimension(unitDimension);
                                    unit.setFactor(unitFactor * spinner_factor);

                                    myDbHelper.updateUnit(unit);
                                    mUnitAdapter.updateData(myDbHelper.getAllUnits());
                                    myDbHelper.close();
                                }
                            }
                        })
                .setNeutralButton(R.string.delete,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                DataBaseHelper myDbHelper = new DataBaseHelper(context, getFilesDir().getAbsolutePath());
                                myDbHelper.deleteUnit(unit);
                                mUnitAdapter.updateData(myDbHelper.getAllUnits());
                                myDbHelper.close();
                            }
                        })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }

                });

        // create alert dialog
        AlertDialog editDialog = editDialogBuilder.create();
        editDialog.show();



        if( unit.getBase() ) { // don't show delete button if base unit
            editDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setEnabled(false);
        }



    }


}
