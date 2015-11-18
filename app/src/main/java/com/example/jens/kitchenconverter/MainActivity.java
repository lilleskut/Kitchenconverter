package com.example.jens.kitchenconverter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        final String[] dimensions = getResources().getStringArray(R.array.dimensions_array);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        // prepare Database

        DataBaseHelper myDbHelper = new DataBaseHelper(this,getFilesDir().getAbsolutePath());
        myDbHelper.prepareDataBase();
        myDbHelper.close();

        final GridLayout gridLayout = (GridLayout) findViewById(R.id.grid_layout);
        for(int j=0; j < dimensions.length; j++) {
            final String dim = dimensions[j];
            Button b= new Button(getApplicationContext());
            b.setText(dim);
            b.setId(j);

            b.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {
                    Intent i = new Intent(MainActivity.this, ConverterActivity.class);
                    i.putExtra("dimension",dim);
                    startActivity(i);
                }
            });

            gridLayout.addView(b,j);
        }

        // add temperature converter button
        Button tempBtn = new Button(getApplicationContext());
        tempBtn.setText("temperature");
        tempBtn.setId(dimensions.length);

        tempBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent i = new Intent(MainActivity.this, ConverterActivity.class);
                i.putExtra("dimension","temperature");
                startActivity(i);
            }
        });
        gridLayout.addView(tempBtn,dimensions.length);
        

        Button d = (Button) findViewById(R.id.general_converter);
        d.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent i = new Intent(MainActivity.this, GeneralConverterActivity.class);
                startActivity(i);
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
                Intent i = new Intent(MainActivity.this,SettingsActivity.class);
                startActivity(i);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
