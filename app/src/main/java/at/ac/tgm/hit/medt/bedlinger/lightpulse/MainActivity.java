package at.ac.tgm.hit.medt.bedlinger.lightpulse;

import androidx.appcompat.app.AppCompatActivity;

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
    private TextView akkulaufzeitTextView;
    private Button sosButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.init();
    }

    private void init() {
        this.batteryButton = findViewById(R.id.batteryButton);
        this.lightPatternButton = findViewById(R.id.lightPatternButton);
        this.settingsButton = findViewById(R.id.settingsButton);
        this.powerButton = findViewById(R.id.powerButton);
        this.helligkeitSlider = findViewById(R.id.helligkeitSlider);
        this.akkulaufzeitTextView = findViewById(R.id.akkulaufzeitTextView);
        this.sosButton = findViewById(R.id.sosButton);
    }
}