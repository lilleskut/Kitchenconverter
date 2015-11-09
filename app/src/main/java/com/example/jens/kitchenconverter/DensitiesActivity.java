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
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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
                // get prompts.xml view

                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.edit_density_prompt, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                // set edit_density_prompt.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText editSubstance = (EditText) promptsView.findViewById(R.id.editTextSubstance);
                final EditText editDensity = (EditText) promptsView.findViewById(R.id.editTextDensity);
                final TextView densityDimension = (TextView) promptsView.findViewById(R.id.density_dimension);

                final DataBaseHelper myDbHelper = new DataBaseHelper(context,getFilesDir().getAbsolutePath());
                densityDimension.setText(myDbHelper.getBaseDensity());

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setTitle(R.string.addDensity)
                        .setPositiveButton(R.string.add,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                            String densitySubstance = editSubstance.getText().toString();
                                            Double densityDensity;
                                        if( editDensity.getText().toString().trim().equals("") ) {
                                            densityDensity = null;
                                        } else {
                                            densityDensity = Double.valueOf(editDensity.getText().toString());
                                        }
                                        if (!myDbHelper.substanceExists(densitySubstance)) {
                                            Density adddensity = new Density(densitySubstance, densityDensity, context);
                                            myDbHelper.addDensity(adddensity);
                                            mDensityAdapter.updateData(myDbHelper.getAllDensities());
                                        } else {
                                            Toast.makeText(getApplicationContext(), "This substance already exists", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                myDbHelper.close();
                alertDialog.show();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final Density density = mDensityAdapter.getItem(position);

        // get prompts.xml view


        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.edit_density_prompt, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set edit_density_prompt.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText editSubstance = (EditText) promptsView.findViewById(R.id.editTextSubstance);
        final EditText editDensity = (EditText) promptsView.findViewById(R.id.editTextDensity);
        final TextView densityDimension = (TextView) promptsView.findViewById(R.id.density_dimension);

        editSubstance.setText(density.getSubstance());
        if (density.getDensity() == null ) {
            editDensity.setText("");
        } else {
            editDensity.setText(Double.toString(density.getDensity()));
        }

        final DataBaseHelper myDbHelper = new DataBaseHelper(context,getFilesDir().getAbsolutePath());
        densityDimension.setText(myDbHelper.getBaseDensity());

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setTitle(R.string.editDensity)
                .setPositiveButton(R.string.modify,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                String densitySubstance = editSubstance.getText().toString();
                                Double densityDensity;
                                if( editDensity.getText().toString().trim().equals("") ) {
                                    densityDensity = null;
                                } else {
                                    densityDensity = Double.valueOf(editDensity.getText().toString());
                                }

                                density.setSubstance(densitySubstance);
                                density.setDensity(densityDensity);

                                myDbHelper.updateDensity(density);
                                mDensityAdapter.updateData(myDbHelper.getAllDensities());
                                myDbHelper.close();
                            }
                        })
                .setNeutralButton(R.string.delete,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                myDbHelper.deleteDensity(density);
                                mDensityAdapter.updateData(myDbHelper.getAllDensities());
                                myDbHelper.close();
                            }
                        })
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        myDbHelper.close();
        alertDialog.show();

    }
}
