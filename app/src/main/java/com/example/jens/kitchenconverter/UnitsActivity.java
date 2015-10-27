package com.example.jens.kitchenconverter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import java.util.ArrayList;
import java.util.List;

public class UnitsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private static final String TAG = "UnitsActivity";
    private Toolbar toolbar;
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

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DataBaseHelper myDbHelper = new DataBaseHelper(this,getFilesDir().getAbsolutePath());

        // 5. Set this Activity to react to list items being pressed
        mainListView = (ListView) findViewById(R.id.listView);
        mainListView.setOnItemClickListener(this);

        // output as list
        List<Unit> list = myDbHelper.getAllUnits();

        // Create a UnitAdapter for the ListView and Set the ListView to use the UnitAdapter
        mUnitAdapter = new UnitAdapter(this, getLayoutInflater());
        mainListView.setAdapter(mUnitAdapter);

        mUnitAdapter.updateData(list);
        myDbHelper.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add(Menu.NONE, 98,Menu.NONE,R.string.filter).setIcon(R.drawable.ic_filter_list_white_48dp).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(Menu.NONE, 99,Menu.NONE,R.string.add).setIcon(R.drawable.ic_add_white_48dp).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);


        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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
                d.setTitle("Add unit");
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
                        int pos = radioDimensionGroup.indexOfChild(findViewById(checkedId));
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

                                                  Unit addunit = new Unit(unitName, unitDimension, unitFactor, context);
                                                  DataBaseHelper myDbHelper = new DataBaseHelper(context,getFilesDir().getAbsolutePath());

                                                  myDbHelper.addUnit(addunit);
                                                  mUnitAdapter.updateData(myDbHelper.getAllUnits());
                                                  myDbHelper.close();
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


        for(int i=0; i < dimensions.length; i++) {
            RadioButton rb= new RadioButton(context);
            rb.setText(dimensions[i]);
            rb.setId(i);
            if(dimensions[i].equals(savedDimension)) { rb.setChecked(true); }
            radioDimensionGroup.addView(rb,i,layoutParams);
        }


        final EditText editFactor = (EditText) d.findViewById(R.id.editTextFactor);
        editFactor.setText(Double.toString(unit.getFactor()));

        d.show();




        Button deleteBtn = (Button) d.findViewById(R.id.button_delete);
        Button modifyBtn = (Button) d.findViewById(R.id.button_modify);
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
