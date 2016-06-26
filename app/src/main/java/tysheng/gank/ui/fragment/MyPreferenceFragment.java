package tysheng.gank.ui.fragment;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.v7.app.AppCompatDelegate;

import tysheng.gank.Constant;
import tysheng.gank.R;
import tysheng.gank.ui.SettingActivity;
import tysheng.gank.ui.inter.FragmentCallback;
import tysheng.gank.utils.SPHelper;

/**
 * Created by shengtianyang on 16/3/23.
 */
public class MyPreferenceFragment extends PreferenceFragment {
    private FragmentCallback callback;
    private SPHelper mSPHelper;
    private boolean changed = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        callback = (SettingActivity) getActivity();
        mSPHelper = new SPHelper(getActivity());
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        String key = preference.getKey();
        switch (key) {
            case "night":
                changed = !changed;
                if (((CheckBoxPreference) preference).isChecked())
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                else
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                mSPHelper.setSpBoolean(Constant.IS_SETTING, changed);
                break;
            case "gallery":
                changed = !changed;
                if (((CheckBoxPreference) preference).isChecked())
                    mSPHelper.setSpBoolean(Constant.IS_GALLERY, true);
                else
                    mSPHelper.setSpBoolean(Constant.IS_GALLERY, false);
                mSPHelper.setSpBoolean(Constant.IS_SETTING, changed);
                break;
            default:
                callback.func1(key);
                break;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

}
