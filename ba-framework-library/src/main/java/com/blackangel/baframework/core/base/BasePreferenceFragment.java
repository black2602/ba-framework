package com.blackangel.baframework.core.base;

import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.SwitchPreferenceCompat;

/**
 * Created by Finger-kjh on 2017-09-06.
 */

public abstract class BasePreferenceFragment extends PreferenceFragmentCompat {

    private boolean mEnabled = true;

    private PreferenceActionListener mPreferenceActionListener;

    public void setEnabled(boolean enabled) {
        mEnabled = enabled;
    }

    public void setPreferenceActionListener(PreferenceActionListener preferenceActionListener) {
        this.mPreferenceActionListener = preferenceActionListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(getPreferenceXmlResourceId());
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        boolean handled = false;

        if(!mEnabled)
            return true;

        if(preference instanceof SwitchPreferenceCompat) {
            SwitchPreferenceCompat switchPreference = (SwitchPreferenceCompat) preference;
            // 알림설정 ON/OFF
            mPreferenceActionListener.onChangePreferenceSwitchOnOff(this, preference.getOrder(), switchPreference.isChecked());
        } else {
            mPreferenceActionListener.onSelectPreference(this, preference.getOrder(), preference.getTitle().toString());
        }

        return super.onPreferenceTreeClick(preference);
    }

    protected abstract int getPreferenceXmlResourceId();

    public interface PreferenceActionListener {
        void onSelectPreference(BasePreferenceFragment fragment, int position, String menuTitle);
        void onChangePreferenceSwitchOnOff(BasePreferenceFragment fragment, int position, boolean on);
    }
}
