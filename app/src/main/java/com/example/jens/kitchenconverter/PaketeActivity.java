package com.example.jens.kitchenconverter;


import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.List;

public class PaketeActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private static final String TAG = "PaketeActivity";
    private Toolbar toolbar;
    private RadioButton radioButton;
    private RadioButton radioFilterButton;
    final Context context = this;

    PaketAdapter mPaketAdapter;
    ListView mainListView;


    LinearLayout.LayoutParams layoutParams = new RadioGroup.LayoutParams(
            RadioGroup.LayoutParams.WRAP_CONTENT,
            RadioGroup.LayoutParams.WRAP_CONTENT);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pakete);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DataBaseHelper myDbHelper = new DataBaseHelper(this,getFilesDir().getAbsolutePath());

        // 5. Set this Activity to react to list items being pressed
        mainListView = (ListView) findViewById(R.id.listView);
        mainListView.setOnItemClickListener(this);

        // output as list
        List<Paket> list = myDbHelper.getAllPakete();

        // Create a UnitAdapter for the ListView and Set the ListView to use the UnitAdapter
        mPaketAdapter = new PaketAdapter(this, getLayoutInflater());
        mainListView.setAdapter(mPaketAdapter);

        mPaketAdapter.updateData(list);
        myDbHelper.close();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
