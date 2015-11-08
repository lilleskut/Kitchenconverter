package com.example.jens.kitchenconverter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.List;

public class PackageDensitiesActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private static final String TAG = "DensitiesActivity";
    private final Context context = this;

    private PackageDensityAdapter mPackageDensityAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_packagedensities);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        if( getSupportActionBar() != null ) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // set header
        TextView header1 = (TextView) findViewById(R.id.column_header1);
        TextView header2 = (TextView) findViewById(R.id.column_header2);
        TextView header3 = (TextView) findViewById(R.id.column_header3);

        header1.setText(R.string.substance);
        header2.setText(R.string.packageName);
        DataBaseHelper myDbHelper = new DataBaseHelper(context,getFilesDir().getAbsolutePath());
        header3.setText(myDbHelper.getBasePackageDensity());


        // display list of densities

        ListView mainListView = (ListView) findViewById(R.id.listView);
        mainListView.setOnItemClickListener(this);

        List<PackageDensity> list = myDbHelper.getAllPackageDensities();
        mPackageDensityAdapter = new PackageDensityAdapter(getLayoutInflater());
        mainListView.setAdapter(mPackageDensityAdapter);

        mPackageDensityAdapter.updateData(list);
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
                Intent i = new Intent(PackageDensitiesActivity.this,SettingsActivity.class);
                startActivity(i);
                break;
            case 99: // add
                // get prompts.xml view

                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.add_packagedensity_prompt, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                // set add_packagedensity_prompt.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final Spinner spinnerSubstance = (Spinner) promptsView.findViewById(R.id.substance_spinner);
                final Spinner spinnerPackage = (Spinner) promptsView.findViewById(R.id.package_spinner);
                final EditText editPackageDensity = (EditText) promptsView.findViewById(R.id.editTextPackageDensity);
                final TextView packageDensityDimension = (TextView) promptsView.findViewById(R.id.packagedensity_dimension);

                final DataBaseHelper myDbHelper = new DataBaseHelper(context,getFilesDir().getAbsolutePath());
                packageDensityDimension.setText(myDbHelper.getBasePackageDensity());



                // get all substances and all package-types from DB

                List<Substance> substanceList = myDbHelper.getAllSubstances();
                List<PackageType> packageTypeList = myDbHelper.getAllPackageTypes();
                myDbHelper.close();
                SpinnerSubstanceAdapter substanceAdapter;
                SpinnerPackageTypeAdapter packageTypeAdapter;

                // populate substances/package-types spinner
                substanceAdapter = new SpinnerSubstanceAdapter(this, android.R.layout.simple_spinner_item, substanceList);
                packageTypeAdapter = new SpinnerPackageTypeAdapter(this, android.R.layout.simple_spinner_item, packageTypeList);
                spinnerSubstance.setAdapter(substanceAdapter);
                spinnerPackage.setAdapter(packageTypeAdapter);


                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setTitle(R.string.addPackageDensity)
                        .setPositiveButton(R.string.add,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        Substance substance = (Substance) spinnerSubstance.getSelectedItem();
                                        PackageType packageType = (PackageType) spinnerPackage.getSelectedItem();

                                        Double packageDensityDensity;
                                        if( !editPackageDensity.getText().toString().trim().equals("") ) {
                                            packageDensityDensity = Double.valueOf(editPackageDensity.getText().toString());


                                            PackageDensity addpackagedensity = new PackageDensity(substance.getName(), packageType.getName(), packageDensityDensity, context);

                                            myDbHelper.addPackageDensity(addpackagedensity);
                                            mPackageDensityAdapter.updateData(myDbHelper.getAllPackageDensities());
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
        final PackageDensity packageDensity = mPackageDensityAdapter.getItem(position);

        Log.d(TAG, "abc");
        // get prompts.xml view

        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.edit_packagedensity_prompt, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set edit_density_prompt.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        /*
        final EditText editSubstance = (EditText) promptsView.findViewById(R.id.editTextSubstance);
        final EditText editDensity = (EditText) promptsView.findViewById(R.id.editTextDensity);
        final TextView densityDimension = (TextView) promptsView.findViewById(R.id.density_dimension);

        editSubstance.setText(density.getSubstance());
        if (density.getDensity() == null ) {
            editDensity.setText("");
        } else {
            editDensity.setText(Double.toString(density.getDensity()));
        }
        */
        final DataBaseHelper myDbHelper = new DataBaseHelper(context,getFilesDir().getAbsolutePath());
        // densityDimension.setText(myDbHelper.getBaseDensity());

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setTitle(R.string.editPackageDensity)
                .setNeutralButton(R.string.delete,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                myDbHelper.deletePackageDensity(packageDensity);
                                mPackageDensityAdapter.updateData(myDbHelper.getAllPackageDensities());
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
