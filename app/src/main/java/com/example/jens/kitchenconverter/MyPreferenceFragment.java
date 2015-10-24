package com.example.jens.kitchenconverter;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import java.util.List;

public class MyPreferenceFragment extends PreferenceFragment {
    @Override
    public void onCreate(final Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        if(isAdded()) { //otherwise there can be NPE when using getActivity()
            DataBaseHelper myDbHelper = new DataBaseHelper(getActivity(), getActivity().getFilesDir().getAbsolutePath());

            List<Unit> units = myDbHelper.getAllUnits();
            List<Density> densities =myDbHelper.getAllDensities();
            myDbHelper.close();

            PreferenceScreen units_screen = (PreferenceScreen) findPreference("edit_units");
            PreferenceCategory units_cat = new PreferenceCategory(units_screen.getContext());
            units_cat.setTitle("Edit Units");
            units_screen.addPreference(units_cat);
            for (Unit temp : units) {
                Preference pref = new Preference(units_screen.getContext());
                pref.setTitle(temp.getUnit());
                pref.setSummary(temp.getFactor().toString());
                units_cat.addPreference(pref);
            }


            PreferenceScreen densities_screen = (PreferenceScreen) findPreference("edit_densities");
            PreferenceCategory densities_cat = new PreferenceCategory(densities_screen.getContext());
            densities_cat.setTitle("Edit Densities");
            densities_screen.addPreference(densities_cat);
            for (Density temp : densities) {
                Preference pref = new Preference(densities_screen.getContext());
                pref.setTitle(temp.getSubstance());
                pref.setSummary(temp.getDensity().toString());
                densities_cat.addPreference(pref);
            }
        }
    }
}