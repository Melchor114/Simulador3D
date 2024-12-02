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
    private float previousX = 0;
    private float previousY = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configuración específica para OpenGL ES 3
        glSurfaceView = findViewById(R.id.glSurfaceView);

        // IMPORTANTE: Configurar el contexto de cliente antes de establecer el renderizador
        glSurfaceView.setEGLContextClientVersion(3);

        // Configurar un renderizador con manejo de errores
        renderer = new GameRenderer(this);

        // Establecer el modo de renderizado
        glSurfaceView.setPreserveEGLContextOnPause(true);
        glSurfaceView.setRenderer(renderer);

        // Renderizar solo cuando hay cambios
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupMovementButtons() {
        Button btnMoveForward = findViewById(R.id.btnMoveForward);
        Button btnMoveBackward = findViewById(R.id.btnMoveBackward);
        Button btnMoveLeft = findViewById(R.id.btnMoveLeft);
        Button btnMoveRight = findViewById(R.id.btnMoveRight);

        btnMoveForward.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    renderer.setMovingForward(true);
                    return true;
                case MotionEvent.ACTION_UP:
                    renderer.setMovingForward(false);
                    return true;
            }
            return false;
        });

        btnMoveBackward.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    renderer.setMovingBackward(true);
                    return true;
                case MotionEvent.ACTION_UP:
                    renderer.setMovingBackward(false);
                    return true;
            }
            return false;
        });

        btnMoveLeft.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    renderer.setMovingLeft(true);
                    return true;
                case MotionEvent.ACTION_UP:
                    renderer.setMovingLeft(false);
                    return true;
            }
            return false;
        });

        btnMoveRight.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    renderer.setMovingRight(true);
                    return true;
                case MotionEvent.ACTION_UP:
                    renderer.setMovingRight(false);
                    return true;
            }
            return false;
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float deltaX = x - previousX;
                float deltaY = y - previousY;

                // Rotar la vista con menor sensibilidad
                renderer.getPlayer().rotate(-deltaX * 0.005f, -deltaY * 0.005f);
                break;
        }

        previousX = x;
        previousY = y;
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        glSurfaceView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        glSurfaceView.onResume();
    }
}