package com.education.worder.fragments;

import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.education.worder.R;
import com.education.worder.data.Dictionaries;
import com.education.worder.data.Dictionary;

import java.util.ArrayList;
import java.util.Locale;

public class SettingsFragment extends PreferenceFragmentCompat{

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.app_preferences);
        final String dictKey = getActivity().getResources().getString(R.string.dictionary);
        final ListPreference listDictionary = (ListPreference) findPreference(dictKey);

        setLanguageData(listDictionary);

    }
    private void setLanguageData(ListPreference lp){
        Dictionaries dictionaries = new Dictionaries(getActivity());
        ArrayList<Dictionary> list = dictionaries.load();
        CharSequence[] entries = new CharSequence[list.size()];
        CharSequence[] values = new CharSequence[list.size()];
        for(int i = 0; i<list.size(); i++){
            Dictionary dict = list.get(i);
            entries[i] = dict.getName() +" ("+dict.getLangFrom().getDisplayName(dict.getLangFrom())+"-"+dict.getLangTo().getDisplayName()+")";
            values[i] = String.valueOf(dict.getID());
        }
        lp.setEntries(entries);
        lp.setEntryValues(values);
        lp.setDefaultValue("1");
    }
}
