package at.ac.tgm.hit.medt.bedlinger.lightpulse;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LightshowFragment extends Fragment {

    private static final String ARG_SHOW_NUMBER = "showNumber";
    private ImageButton b1, b2, b3, b4, b5, b6, b7, b8, returnButton;
    private Button playButton;
    private SeekBar dauerSlider, intensitaetSlider;
    private boolean hasIntensityControl;
    private CameraManager cameraManager;
    private String cameraId;
    private boolean isTaschenlampeOn = false;
    private int dauer, intensitaet;

    private Map<String, ArrayList<Integer>> lichtmuster;

    public static LightshowFragment newInstance(int showNumber, boolean hasIntensityControl) {
        LightshowFragment fragment = new LightshowFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SHOW_NUMBER, showNumber);
        args.putBoolean("hasIntensityControl", hasIntensityControl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lightshow, container, false);

        init(view);
        if (!hasIntensityControl) {
            intensitaetSlider.setEnabled(false);
        }

        dauerSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                dauer = (int) (progress * 1000 / 100.0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        if (hasIntensityControl) {
            intensitaetSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    try {
                        int maxBrightness = cameraManager.getCameraCharacteristics(cameraId).get(CameraCharacteristics.FLASH_INFO_STRENGTH_MAXIMUM_LEVEL);
                        intensitaet = (int) (progress * maxBrightness / 100.0);
                    } catch (CameraAccessException e) {
                        showErrorAlert("Es gab einen Fehler beim Zugriff auf die Taschenlampe.");
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });
        }

        // now each buttons represents a step in the lightshow and the user can set the light pattern
        // by clicking on the buttons he can set the duration of the light and the intensity

        b1.setOnClickListener(v -> {
            ArrayList<Integer> lichtmuster1 = new ArrayList<>();
            lichtmuster1.add(dauer);
            lichtmuster1.add(intensitaet);
            lichtmuster.put("b1", lichtmuster1);
            dauerSlider.setProgress(0);
            intensitaetSlider.setProgress(0);
        });

        b2.setOnClickListener(v -> {
            ArrayList<Integer> lichtmuster2 = new ArrayList<>();
            lichtmuster2.add(dauer);
            lichtmuster2.add(intensitaet);
            lichtmuster.put("b2", lichtmuster2);
            dauerSlider.setProgress(0);
            intensitaetSlider.setProgress(0);
        });

        b3.setOnClickListener(v -> {
            ArrayList<Integer> lichtmuster3 = new ArrayList<>();
            lichtmuster3.add(dauer);
            lichtmuster3.add(intensitaet);
            lichtmuster.put("b3", lichtmuster3);
            dauerSlider.setProgress(0);
            intensitaetSlider.setProgress(0);
        });

        b4.setOnClickListener(v -> {
            ArrayList<Integer> lichtmuster4 = new ArrayList<>();
            lichtmuster4.add(dauer);
            lichtmuster4.add(intensitaet);
            lichtmuster.put("b4", lichtmuster4);
            dauerSlider.setProgress(0);
            intensitaetSlider.setProgress(0);
        });

        b5.setOnClickListener(v -> {
            ArrayList<Integer> lichtmuster5 = new ArrayList<>();
            lichtmuster5.add(dauer);
            lichtmuster5.add(intensitaet);
            lichtmuster.put("b5", lichtmuster5);
            dauerSlider.setProgress(0);
            intensitaetSlider.setProgress(0);
        });

        b6.setOnClickListener(v -> {
            ArrayList<Integer> lichtmuster6 = new ArrayList<>();
            lichtmuster6.add(dauer);
            lichtmuster6.add(intensitaet);
            lichtmuster.put("b6", lichtmuster6);
            dauerSlider.setProgress(0);
            intensitaetSlider.setProgress(0);
        });

        b7.setOnClickListener(v -> {
            ArrayList<Integer> lichtmuster7 = new ArrayList<>();
            lichtmuster7.add(dauer);
            lichtmuster7.add(intensitaet);
            lichtmuster.put("b7", lichtmuster7);
            dauerSlider.setProgress(0);
            intensitaetSlider.setProgress(0);
        });

        b8.setOnClickListener(v -> {
            ArrayList<Integer> lichtmuster8 = new ArrayList<>();
            lichtmuster8.add(dauer);
            lichtmuster8.add(intensitaet);
            lichtmuster.put("b8", lichtmuster8);
            dauerSlider.setProgress(0);
            intensitaetSlider.setProgress(0);
        });

        playButton.setOnClickListener(v -> {
            if (lichtmuster.size() < 8) {
                showHintAlert("Es wurde kein vollständiges Lichtmuster eingegeben.");
            } else {
                lichtmusterAbspielen();
            }
        });

        returnButton.setOnClickListener(v -> {
            if (isTaschenlampeOn) {
                taschenlampeOff();
            }
            requireActivity().finish();
        });


        return view;
    }

    public void init(View view) {
        b1 = view.findViewById(R.id.button1);
        b2 = view.findViewById(R.id.button2);
        b3 = view.findViewById(R.id.button3);
        b4 = view.findViewById(R.id.button4);
        b5 = view.findViewById(R.id.button5);
        b6 = view.findViewById(R.id.button6);
        b7 = view.findViewById(R.id.button7);
        b8 = view.findViewById(R.id.button8);
        playButton = view.findViewById(R.id.playButton);
        returnButton = view.findViewById(R.id.returnButton);
        dauerSlider = view.findViewById(R.id.dauerSlider);
        intensitaetSlider = view.findViewById(R.id.intensitaetSlider);
        assert getArguments() != null;
        hasIntensityControl = getArguments().getBoolean("hasIntensityControl");
        cameraManager = (CameraManager) requireActivity().getSystemService(Context.CAMERA_SERVICE);
        try {
            cameraId = cameraManager.getCameraIdList()[0];
        } catch (Exception e) {
            showErrorAlert("Es gab einen Fehler beim Zugriff auf die Taschenlampe.");
        }
        lichtmuster = new HashMap<>();
    }

    public void lichtmusterAbspielen() {
        if (isTaschenlampeOn) {
            taschenlampeOff();
        }
        for (Map.Entry<String, ArrayList<Integer>> entry : lichtmuster.entrySet()) {
            // Atmungseffekt: langsam heller werden
            for (int i = 0; i <= entry.getValue().get(1); i++) {
                taschenlampeOn(i);
                try {
                    Thread.sleep(entry.getValue().get(0));
                } catch (InterruptedException e) {
                    showErrorAlert("Es gab einen Fehler beim Abspielen der Lightshow.");
                }
            }
            // Atmungseffekt: langsam dunkler werden
            for (int i = entry.getValue().get(1); i >= 0; i--) {
                taschenlampeOn(i);
                try {
                    Thread.sleep(entry.getValue().get(0));
                } catch (InterruptedException e) {
                    showErrorAlert("Es gab einen Fehler beim Abspielen der Lightshow.");
                }
            }
            taschenlampeOff();
            try {
                Thread.sleep(900);
            } catch (InterruptedException e) {
                showErrorAlert("Es gab einen Fehler beim Abspielen der Lightshow.");
            }
        }
    }

    private void taschenlampeOn(int helligkeit) {
        try {
            if (hasIntensityControl) {
                int maxBrightness = cameraManager.getCameraCharacteristics(cameraId).get(CameraCharacteristics.FLASH_INFO_STRENGTH_MAXIMUM_LEVEL);
                int mappedBrightness = (helligkeit * maxBrightness) / 100;
                cameraManager.turnOnTorchWithStrengthLevel(cameraId, mappedBrightness);
            } else {
                cameraManager.setTorchMode(cameraId, true);
            }
            isTaschenlampeOn = true;
        } catch (CameraAccessException e) {
            showErrorAlert("Es gab einen Fehler beim Zugriff auf die Taschenlampe.");
        }
    }

    private void taschenlampeOff() {
        try {
            cameraManager.setTorchMode(cameraId, false);
            isTaschenlampeOn = false;
        } catch (CameraAccessException e) {
            showErrorAlert("Es gab einen Fehler beim Zugriff auf die Taschenlampe.");
        }
    }

    private void showErrorAlert(String message) {
        String title = "Error";
        if (message == null || message.trim().equals("") || message.isEmpty()) {
            message = "Es gab einen Fehler.";
        }
        AlertDialog alert = new AlertDialog.Builder(requireActivity()).create();
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setButton(AlertDialog.BUTTON_POSITIVE, "OK", (dialog, which) -> requireActivity().finish());
        alert.show();
    }

    private void showHintAlert(String message) {
        String title = "Hinweis";
        if (message == null || message.trim().equals("") || message.isEmpty()) {
            message = "Es gab einen Fehler.";
        }
        AlertDialog alert = new AlertDialog.Builder(requireActivity()).create();
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setButton(AlertDialog.BUTTON_POSITIVE, "OK", (dialog, which) -> dialog.dismiss());
        alert.show();
    }
}