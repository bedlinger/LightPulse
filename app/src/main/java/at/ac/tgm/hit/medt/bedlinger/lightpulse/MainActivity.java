package at.ac.tgm.hit.medt.bedlinger.lightpulse;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final int SHAKE_THRESHOLD_SOS = 80;
    private static final int SHAKE_THRESHOLD_POWER_OFF = 10;

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
    private boolean hatHelligkeitsregelung = false;
    private int helligkeit = 100;
    private BroadcastReceiver akkuReceiver;
    private boolean hasAccelerometer;
    private final boolean checkForSOS = false;
    private final boolean powerOnShake = true;
    private SensorManager sensorManager;
    private long lastUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.init();

        try {
            if (CameraManager.getCameraCharacteristics(cameraId).get(CameraCharacteristics.FLASH_INFO_STRENGTH_MAXIMUM_LEVEL) > 1) {
                hatHelligkeitsregelung = true;
                helligkeitSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int helligkeit, boolean fromUser) {
                        MainActivity.this.helligkeit = helligkeit;
                        if (isTaschenlampeOn) {
                            taschenlampeOn(MainActivity.this.helligkeit);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });
            } else {
                helligkeitSlider.setEnabled(false);
                AlertDialog alert = new AlertDialog.Builder(MainActivity.this).create();
                alert.setTitle("Hinweis");
                alert.setMessage("Ihr Gerät unterstützt keine Helligkeitsregelung der Taschenlampe.");
                alert.setButton(AlertDialog.BUTTON_POSITIVE, "OK", (dialog, which) -> dialog.dismiss());
                alert.show();
            }
        } catch (CameraAccessException e) {
            showErrorAlert("Error", "Es gab einen Fehler beim Zugriff auf die Taschenlampe.");
        }

        powerButton.setOnClickListener(v -> {
            if (isTaschenlampeOn) {
                taschenlampeOff();
            } else {
                taschenlampeOn(helligkeit);
            }
        });

        sosButton.setOnClickListener(v -> sendSOS());
    }

    private void init() {
        // überprüfen ob Taschenlampe verfügbar ist und ggf. Fehlermeldung anzeigen
        boolean isFlashAvailable = getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        if (!isFlashAvailable) {
            showErrorAlert("Error", "Ihr Gerät unterstützt keine Taschenlampe.");
        } else {
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
        helligkeitSlider.setProgress(helligkeit);
        helligkeitSlider.setMin(1);
        akkuTextView = findViewById(R.id.akkuTextView);
        sosButton = findViewById(R.id.sosButton);
        akkuReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                checkBatteryStatus(intent);
            }
        };
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        hasAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null;
    }

    public void checkBatteryStatus(Intent intent) {
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        int batteryPct = Math.round(level / (float) scale * 100);

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
        sosButton.setEnabled(false);
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
            runOnUiThread(() -> powerButton.setEnabled(true));
            runOnUiThread(() -> sosButton.setEnabled(true));
        }).start();
    }

    private void taschenlampeOn(int helligkeit) {
        try {
            if (hatHelligkeitsregelung) {
                int maxHelligkeit = CameraManager.getCameraCharacteristics(cameraId).get(CameraCharacteristics.FLASH_INFO_STRENGTH_MAXIMUM_LEVEL);
                int mappedHelligkeit = (helligkeit * maxHelligkeit) / 100;
                CameraManager.turnOnTorchWithStrengthLevel(cameraId, mappedHelligkeit);
            } else {
                taschenlampeOn();
            }
        } catch (CameraAccessException e) {
            showErrorAlert("Error", "Es gab einen Fehler beim Zugriff auf die Taschenlampe.");
        }
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

    private void checkOnShake(float x, float y, float z) {
        long curTime = System.currentTimeMillis();
        if ((curTime - lastUpdate) > 100) {
            lastUpdate = curTime;

            double acceleration = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2)) - SensorManager.GRAVITY_EARTH;

            if (checkForSOS) {
                if (acceleration > SHAKE_THRESHOLD_SOS) {
                    sendSOS();
                }
            }
            if (powerOnShake) {
                if (acceleration > SHAKE_THRESHOLD_POWER_OFF) {
                    if (isTaschenlampeOn) {
                        taschenlampeOff();
                    } else {
                        taschenlampeOn(helligkeit);
                    }
                }
            }
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
        if (hasAccelerometer) {
            sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(akkuReceiver);
        if (hasAccelerometer) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (hasAccelerometer) {
            checkOnShake(event.values[0], event.values[1], event.values[2]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}