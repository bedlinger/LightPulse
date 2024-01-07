package at.ac.tgm.hit.medt.bedlinger.lightpulse;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private ImageButton batteryButton;
    private ImageButton lightPatternButton;
    private ImageButton settingsButton;
    private ImageButton powerButton;
    private SeekBar helligkeitSlider;
    private TextView akkuTextView;
    private Button sosButton;
    private CameraManager CameraManager;
    private String cameraId;
    private boolean isTaschenlampeOn = false;
    private BroadcastReceiver akkuReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.init();

        powerButton.setOnClickListener(v -> {
            if (isTaschenlampeOn) {
                taschenlampeOff();
            }
            else {
                taschenlampeOn();
            }
        });

        sosButton.setOnClickListener(v -> sendSOS());
    }

    private void init() {
        // überprüfen ob Taschenlampe verfügbar ist und ggf. Fehlermeldung anzeigen
        boolean isFlashAvailable = getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        if (!isFlashAvailable) {
            showErrorAlert("Error", "Ihr Gerät unterstützt keine Taschenlampe.");
        }
        else {
            CameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
            try {
                cameraId = CameraManager.getCameraIdList()[0];
            } catch (Exception e) {
                showErrorAlert("Error", "Es gab einen Fehler beim Zugriff auf die Taschenlampe.");
            }
        }

        batteryButton = findViewById(R.id.batteryButton);
        lightPatternButton = findViewById(R.id.lightPatternButton);
        settingsButton = findViewById(R.id.settingsButton);
        powerButton = findViewById(R.id.powerButton);
        helligkeitSlider = findViewById(R.id.helligkeitSlider);
        akkuTextView = findViewById(R.id.akkuTextView);
        sosButton = findViewById(R.id.sosButton);
        akkuReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                checkBatteryStatus(intent);
            }
        };
    }

    public void checkBatteryStatus(Intent intent) {
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        int batteryPct = Math.round(level / (float)scale * 100);

        if (batteryPct <= 20) {
            akkuTextView.setTextColor(ContextCompat.getColor(this, R.color.batteryPctLow));
        }
        if (batteryPct > 20 && batteryPct <= 50) {
            akkuTextView.setTextColor(ContextCompat.getColor(this, R.color.batteryPctMedium));
        }
        if (batteryPct > 50) {
            akkuTextView.setTextColor(ContextCompat.getColor(this, R.color.batteryPctHigh));
        }

        akkuTextView.setText(batteryPct + "%");
    }

    private void sendSOS() {
        powerButton.setEnabled(false);
        new Thread(() -> {
            try {
                // SOS in Morse code ist -> ...---...
                int[] pattern = {200, 500, 200, 500, 200, 500, 600, 500, 600, 500, 600, 500, 200, 500, 200, 500, 200, 500};
                for (int time : pattern) {
                    if (isTaschenlampeOn) {
                        taschenlampeOff();
                    } else {
                        taschenlampeOn();
                    }
                    Thread.sleep(time);
                }
                if (isTaschenlampeOn) {
                    taschenlampeOff();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    private void taschenlampeOn() {
        try {
            CameraManager.setTorchMode(cameraId, true);
            isTaschenlampeOn = true;
        } catch (CameraAccessException e) {
            showErrorAlert("Error", "Es gab einen Fehler beim Zugriff auf die Taschenlampe.");
        }
    }

    private void taschenlampeOff() {
        try {
            CameraManager.setTorchMode(cameraId, false);
            isTaschenlampeOn = false;
        } catch (CameraAccessException e) {
            showErrorAlert("Error", "Es gab einen Fehler beim Zugriff auf die Taschenlampe.");
        }
    }

    private void showErrorAlert(String title, String message) {
        if (title == null || title.trim().equals("") || title.isEmpty()) {
            title = "Error";
        }
        if (message == null || message.trim().equals("") || message.isEmpty()) {
            message = "Es gab einen Fehler.";
        }
        AlertDialog alert = new AlertDialog.Builder(MainActivity.this).create();
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setButton(AlertDialog.BUTTON_POSITIVE, "OK", (dialog, which) -> finish());
        alert.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(akkuReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(akkuReceiver);
    }
}