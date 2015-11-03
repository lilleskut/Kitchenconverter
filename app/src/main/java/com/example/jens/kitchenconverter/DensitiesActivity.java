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
import android.widget.RadioGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

public class DensitiesActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private static final String TAG = "DensitiesActivity";
    private final Context context = this;

    private DensityAdapter mDensityAdapter;


    LinearLayout.LayoutParams layoutParams = new RadioGroup.LayoutParams(
            RadioGroup.LayoutParams.WRAP_CONTENT,
            RadioGroup.LayoutParams.WRAP_CONTENT);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_densities);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        if( getSupportActionBar() != null ) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // set header
        TextView header1 = (TextView) findViewById(R.id.column_header1);
        TextView header2 = (TextView) findViewById(R.id.column_header2);

        header1.setText(R.string.substance);
        DataBaseHelper myDbHelper = new DataBaseHelper(context,getFilesDir().getAbsolutePath());
        header2.setText(myDbHelper.getBaseDensity());


        // display list of densities

        ListView mainListView = (ListView) findViewById(R.id.listView);
        mainListView.setOnItemClickListener(this);

        List<Density> list = myDbHelper.getAllDensities();
        mDensityAdapter = new DensityAdapter(getLayoutInflater());
        mainListView.setAdapter(mDensityAdapter);

        mDensityAdapter.updateData(list);
        myDbHelper.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add(Menu.NONE, 99,Menu.NONE,R.string.add).setIcon(R.drawable.ic_add_white_48dp).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.action_settings:
                Intent i = new Intent(DensitiesActivity.this,SettingsActivity.class);
                startActivity(i);
                break;
            case 99: // add
                final Dialog d = new Dialog(context);
                d.setContentView(R.layout.add_density_dialog);
                d.setTitle("Add density");
                d.setCancelable(true);
                final EditText editSubstance = (EditText) d.findViewById(R.id.editTextSubstance);
                final EditText editDensity = (EditText) d.findViewById(R.id.editTextDensity);
                final TextView densityDimension = (TextView) d.findViewById(R.id.density_dimension);

                final DataBaseHelper myDbHelper = new DataBaseHelper(context,getFilesDir().getAbsolutePath());
                densityDimension.setText(myDbHelper.getBaseDensity());

                Button addBtn = (Button) d.findViewById(R.id.button1);
                // set click listener for add button in add_unit_dialog
                addBtn.setOnClickListener(new View.OnClickListener() {
                                              public void onClick(View v) {

                                                  String densitySubstance = editSubstance.getText().toString();
                                                  Double densityDensity = Double.valueOf(editDensity.getText().toString());

                                                  Density adddensity = new Density(densitySubstance, densityDensity, context);

                                                  myDbHelper.addDensity(adddensity);
                                                  mDensityAdapter.updateData(myDbHelper.getAllDensities());
                                                  d.dismiss();
                                              }
                                          }


                );
                myDbHelper.close();
                d.show();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final Density density = mDensityAdapter.getItem(position);

        final Dialog d = new Dialog(context);
        d.setContentView(R.layout.edit_density_dialog);
        d.setTitle("Edit or delete density");
        d.setCancelable(true);

        // fill form with stored values
        final EditText editSubstance = (EditText) d.findViewById(R.id.editTextSubstance);
        editSubstance.setText(density.getSubstance());

        final EditText editDensity = (EditText) d.findViewById(R.id.editTextDensity);
        editDensity.setText(Double.toString(density.getDensity()));
        final TextView densityDimension = (TextView) d.findViewById(R.id.density_dimension);

        final DataBaseHelper myDbHelper = new DataBaseHelper(context,getFilesDir().getAbsolutePath());
        densityDimension.setText(myDbHelper.getBaseDensity());

        d.show();




        Button deleteBtn = (Button) d.findViewById(R.id.button_delete);
        Button modifyBtn = (Button) d.findViewById(R.id.button_modify);
        // set click listener for delete button in modify_dialog
        deleteBtn.setOnClickListener(new View.OnClickListener() {
                                         public void onClick(View v) {

                                             myDbHelper.deleteDensity(density);
                                             mDensityAdapter.updateData(myDbHelper.getAllDensities());
                                             myDbHelper.close();
                                             d.dismiss();
                                         }
                                     }
        );


        // set click listener for modify button in modify_dialog
        modifyBtn.setOnClickListener(new View.OnClickListener() {
                                         public void onClick(View v) {

                                             String densitySubstance = editSubstance.getText().toString();
                                             Double densityDensity = Double.valueOf(editDensity.getText().toString());
                                             density.setSubstance(densitySubstance);
                                             density.setDensity(densityDensity);

                                             myDbHelper.updateDensity(density);
                                             mDensityAdapter.updateData(myDbHelper.getAllDensities());
                                             myDbHelper.close();
                                             d.dismiss();
                                         }
                                     }
        );

        myDbHelper.close();
    }
}
