package com.example.jens.kitchenconverter;

import android.database.SQLException;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;

import android.preference.PreferenceScreen;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;



public class SettingsActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }

    public static class MyPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);


/*
            DataBaseHelper myDbHelper = new DataBaseHelper(getActivity());

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



            List<Unit> list = myDbHelper.getAllUnits();
            myDbHelper.close();
*/

            PreferenceScreen screen = this.getPreferenceScreen();
            PreferenceCategory cat = new PreferenceCategory(screen.getContext());
            cat.setTitle("Units");
            screen.addPreference(cat);
 /*           for(Unit temp : list) {
                Preference pref = new Preference(screen.getContext());
                pref.setTitle(temp.getUnit());
                pref.setSummary(temp.getFactor().toString());
                cat.addPreference(pref);
            }
*/


        }
    }
}
