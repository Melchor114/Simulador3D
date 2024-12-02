package com.example.recorrido;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private GLSurfaceView glSurfaceView;
    private GameRenderer renderer;

    private TextView coordsText;
    private boolean constructionMode = false;
    private Button btnConstructionMode;
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

    private void setupPlaceObjectButton() {
        Button btnPlaceObject = findViewById(R.id.btnPlaceObject);
        btnPlaceObject.setOnClickListener(v -> showObjectPlacementDialog());
    }

    private void showObjectPlacementDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Colocar Objeto");

        // Crear layout para los campos de entrada
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        // Campos de entrada para coordenadas X, Y, Z
        EditText inputX = new EditText(this);
        inputX.setHint("Coordenada X");
        inputX.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        EditText inputY = new EditText(this);
        inputY.setHint("Coordenada Y");
        inputY.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        EditText inputZ = new EditText(this);
        inputZ.setHint("Coordenada Z");
        inputZ.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        layout.addView(inputX);
        layout.addView(inputY);
        layout.addView(inputZ);

        builder.setView(layout);

        builder.setPositiveButton("Colocar", (dialog, which) -> {
            try {
                float x = Float.parseFloat(inputX.getText().toString());
                float y = Float.parseFloat(inputY.getText().toString());
                float z = Float.parseFloat(inputZ.getText().toString());

                // Crear objeto en las coordenadas especificadas
                GameObject newObject = new GameObject(x, y, z);
                renderer.addGameObject(newObject);

                // Forzar múltiples renderizados
                for (int i = 0; i < 3; i++) {
                    glSurfaceView.requestRender();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Por favor, ingrese coordenadas válidas", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }
    private void setupConstructionMode() {
        btnConstructionMode = findViewById(R.id.btnConstructionMode);
        coordsText = findViewById(R.id.coordsText);

        btnConstructionMode.setOnClickListener(v -> {
            constructionMode = !constructionMode;
            btnConstructionMode.setBackgroundColor(
                    constructionMode ? 0xFFFF0000 : 0xFF888888
            );
            Toast.makeText(this,
                    constructionMode ? "Modo construcción activado" : "Modo construcción desactivado",
                    Toast.LENGTH_SHORT).show();
        });

        glSurfaceView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                float x = event.getX();
                float y = event.getY();

                // Obtener las coordenadas del mundo
                float[] worldPos = renderer.screenToWorld(x, y);
                if (worldPos != null) {
                    // Actualizar el texto de coordenadas
                    String coords = String.format("Toque en: X=%.1f, Z=%.1f", worldPos[0], worldPos[2]);
                    coordsText.setText(coords);

                    // Si estamos en modo construcción, colocar objeto
                    if (constructionMode) {
                        GameObject newObject = new GameObject(worldPos[0], worldPos[1], worldPos[2]);
                        renderer.addGameObject(newObject);

                        // Asegurar que se renderice
                        glSurfaceView.requestRender();
                    }
                }
            }
            return true;
        });

        // Actualizar coordenadas del jugador
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (renderer != null && renderer.getPlayer() != null) {
                    Player player = renderer.getPlayer();
                    String playerCoords = String.format("Jugador en: X=%.1f, Z=%.1f",
                            player.getPosX(), player.getPosZ());
                    coordsText.setText(playerCoords);
                    handler.postDelayed(this, 100);
                }
            }
        });
    }    @Override
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

        // Configurar botón de colocar objeto
        setupPlaceObjectButton();
        setupConstructionMode();

    }
}