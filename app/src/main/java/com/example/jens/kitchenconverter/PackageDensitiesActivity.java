package com.example.jens.kitchenconverter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class PackageDensitiesActivity extends AppCompatActivity {
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

        List<PackageDensity> list = myDbHelper.getAllPackageDensities();
        mPackageDensityAdapter = new PackageDensityAdapter(getLayoutInflater());
        mainListView.setAdapter(mPackageDensityAdapter);

        mPackageDensityAdapter.updateData(list);
        myDbHelper.close();
    }

}
