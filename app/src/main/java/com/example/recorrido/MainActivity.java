package com.example.recorrido;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private WorldView worldView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        worldView = findViewById(R.id.worldView);

        // Find buttons
        Button btnLeft = findViewById(R.id.btnLeft);
        Button btnRight = findViewById(R.id.btnRight);
        Button btnUp = findViewById(R.id.btnUp);
        Button btnDown = findViewById(R.id.btnDown);
        Button btnReset = findViewById(R.id.btnReset);

        // Set button click listeners
        btnLeft.setOnClickListener(v -> worldView.moveView(-100, 0));
        btnRight.setOnClickListener(v -> worldView.moveView(100, 0));
        btnUp.setOnClickListener(v -> worldView.moveView(0, -100));
        btnDown.setOnClickListener(v -> worldView.moveView(0, 100));
        btnReset.setOnClickListener(v -> worldView.resetView());
    }
}