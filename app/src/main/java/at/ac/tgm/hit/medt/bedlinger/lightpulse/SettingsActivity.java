package at.ac.tgm.hit.medt.bedlinger.lightpulse;


import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        MaterialToolbar actionbar = findViewById(R.id.actionbar);
        actionbar.setTitle("Einstellungen");
        this.setSupportActionBar(actionbar);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();

        ImageButton returnButton = findViewById(R.id.returnButton2);
        returnButton.setOnClickListener(v -> finish());
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences, rootKey);

            SwitchPreferenceCompat sosShakePreference = findPreference("sos_shake");
            SwitchPreferenceCompat toggleFlashlightShakePreference = findPreference("toggle_flashlight_shake");

            Preference.OnPreferenceChangeListener listener = (preference, newValue) -> {
                if (preference.getKey().equals("sos_shake")) {
                    toggleFlashlightShakePreference.setChecked(!(Boolean) newValue);
                    sosShakePreference.setChecked((Boolean) newValue);
                } else if (preference.getKey().equals("toggle_flashlight_shake")) {
                    sosShakePreference.setChecked(!(Boolean) newValue);
                    toggleFlashlightShakePreference.setChecked((Boolean) newValue);
                }
                return true;
            };

            sosShakePreference.setOnPreferenceChangeListener(listener);
            toggleFlashlightShakePreference.setOnPreferenceChangeListener(listener);
        }
    }
}