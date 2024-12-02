package com.example.recorrido;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private GLSurfaceView glSurfaceView;
    private GameRenderer renderer;

    private void setupMovementButtons() {
        Button btnMoveForward = findViewById(R.id.btnMoveForward);
        Button btnMoveBackward = findViewById(R.id.btnMoveBackward);
        Button btnMoveLeft = findViewById(R.id.btnMoveLeft);
        Button btnMoveRight = findViewById(R.id.btnMoveRight);

        // Usar OnTouchListener para control más preciso
        btnMoveForward.setOnTouchListener(createMovementTouchListener(
                () -> renderer.setMovingForward(true),
                () -> renderer.setMovingForward(false)
        ));

        btnMoveBackward.setOnTouchListener(createMovementTouchListener(
                () -> renderer.setMovingBackward(true),
                () -> renderer.setMovingBackward(false)
        ));

        btnMoveLeft.setOnTouchListener(createMovementTouchListener(
                () -> renderer.setMovingLeft(true),
                () -> renderer.setMovingLeft(false)
        ));

        btnMoveRight.setOnTouchListener(createMovementTouchListener(
                () -> renderer.setMovingRight(true),
                () -> renderer.setMovingRight(false)
        ));
    }

    // Método para crear un listener de movimiento reutilizable
    private View.OnTouchListener createMovementTouchListener(
            Runnable onPress, Runnable onRelease) {
        return (v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    onPress.run();
                    v.performClick();
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    onRelease.run();
                    return true;
            }
            return false;
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        glSurfaceView = findViewById(R.id.glSurfaceView);
        glSurfaceView.setEGLContextClientVersion(3);
        renderer = new GameRenderer(this);
        glSurfaceView.setRenderer(renderer);

        // Configurar renderizado continuo para actualizaciones de movimiento
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        // Configurar botones de movimiento
        setupMovementButtons();
    }
}