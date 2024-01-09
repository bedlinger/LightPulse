package at.ac.tgm.hit.medt.bedlinger.lightpulse;


import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences, rootKey);

            SwitchPreferenceCompat sosShakePreference = findPreference("sos_shake");
            SwitchPreferenceCompat toggleFlashlightShakePreference = findPreference("toggle_flashlight_shake");

            if (sosShakePreference != null) {
                sosShakePreference.setOnPreferenceChangeListener((preference, newValue) -> {
                    if ((Boolean) newValue) {
                        if (toggleFlashlightShakePreference != null) {
                            toggleFlashlightShakePreference.setChecked(false);
                        }
                    } else {
                        if (toggleFlashlightShakePreference != null) {
                            toggleFlashlightShakePreference.setChecked(true);
                        }
                    }
                    return true;
                });
            }

            if (toggleFlashlightShakePreference != null) {
                toggleFlashlightShakePreference.setOnPreferenceChangeListener((preference, newValue) -> {
                    if ((Boolean) newValue) {
                        if (sosShakePreference != null) {
                            sosShakePreference.setChecked(false);
                        }
                    } else {
                        if (sosShakePreference != null) {
                            sosShakePreference.setChecked(true);
                        }
                    }
                    return true;
                });
            }
        }
    }
}