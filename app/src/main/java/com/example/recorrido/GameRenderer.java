package com.example.recorrido;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GameRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "GameRenderer";

    private Context context;
    private Terrain terrain;
    private Player player;

    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] modelMatrix = new float[16];

    private float moveSpeed = 0.2f;  // Velocidad de movimiento ajustable

    // Controles de movimiento
    private boolean movingForward = false;
    private boolean movingBackward = false;
    private boolean movingLeft = false;
    private boolean movingRight = false;

    private List<GameObject> gameObjects = new ArrayList<>();
    private Random random = new Random();

    public GameRenderer(Context context) {
        this.context = context;
        this.terrain = new Terrain();
        this.player = new Player();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        try {
            // Verificaciones de inicialización
            Log.d(TAG, "OpenGL Version: " + GLES30.glGetString(GLES30.GL_VERSION));
            Log.d(TAG, "OpenGL Vendor: " + GLES30.glGetString(GLES30.GL_VENDOR));
            Log.d(TAG, "OpenGL Renderer: " + GLES30.glGetString(GLES30.GL_RENDERER));

            // Configuraciones básicas

            // Configuración inicial
            GLES30.glClearColor(0.5f, 0.8f, 1.0f, 1.0f);
            GLES30.glEnable(GLES30.GL_DEPTH_TEST);
            GLES30.glDepthFunc(GLES30.GL_LESS);

            // Inicialización
            terrain = new Terrain();
            player = new Player();

        } catch (Exception e) {
            Log.e(TAG, "Error in onSurfaceCreated", e);
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES30.glViewport(0, 0, width, height);

        float ratio = (float) width / height;
        Matrix.perspectiveM(projectionMatrix, 0, 60, ratio, 0.1f, 100f);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        updatePlayerMovement();

        // Limpiar buffers
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);

        // Configuraciones de OpenGL
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
        GLES30.glDepthFunc(GLES30.GL_LESS);
        GLES30.glClearDepthf(1.0f);

        // Matriz de vista
        Matrix.setLookAtM(viewMatrix, 0,
                player.getPosX(), player.getPosY() + 1.5f, player.getPosZ(),
                player.getPosX() + (float)Math.sin(player.getRotationY()),
                player.getPosY() + 1.5f,
                player.getPosZ() - (float)Math.cos(player.getRotationY()),
                0f, 1f, 0f);

        // Dibujar terreno
        terrain.draw(projectionMatrix, viewMatrix);

        // Dibujar objetos
        synchronized(gameObjects) {
            for (GameObject obj : gameObjects) {
                obj.draw(projectionMatrix, viewMatrix);
            }
        }
    }
/*    public void onDrawFrame(GL10 gl) {
        // Actualizar posición del jugador
        updatePlayerMovement();

        // Limpiar buffers
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);

        // Configurar matriz de vista
        Matrix.setLookAtM(viewMatrix, 0,
                player.getPosX(), player.getPosY() + 1.5f, player.getPosZ(),
                player.getPosX() + (float)Math.sin(player.getRotationY()),
                player.getPosY() + 1.5f,
                player.getPosZ() - (float)Math.cos(player.getRotationY()),
                0f, 1f, 0f);

        // Limpiar bindings de buffer antes de dibujar el terreno
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, 0);

        // Dibujar el terreno
        terrain.draw(projectionMatrix, viewMatrix);

        // Dibujar objetos de juego
        synchronized(gameObjects) {
            for (GameObject obj : gameObjects) {
                // Limpiar bindings antes de cada objeto
                GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
                GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, 0);
                obj.draw(projectionMatrix, viewMatrix);
            }
        }

        // Limpiar bindings al final
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, 0);
    }*/
    // Método existente de colocación aleatoria si aún lo quieres
    public void placeRandomObject() {
        float randomX = random.nextFloat() * 20;
        float randomZ = random.nextFloat() * 20;

        GameObject newObject = new GameObject(randomX, 1f, randomZ);
        gameObjects.add(newObject);

        Log.d("GameRenderer", "Objeto aleatorio colocado en: X=" + randomX + ", Z=" + randomZ);
    }

    // Método para agregar un objeto en una posición específica
    public void addGameObject(GameObject object) {
        synchronized(gameObjects) {
            gameObjects.add(object);
            Log.d("GameRenderer", String.format(
                    "Objeto agregado en: X=%.2f, Y=%.2f, Z=%.2f - Total objetos: %d",
                    object.getPosX(), object.getPosY(), object.getPosZ(), gameObjects.size()
            ));
        }
        // Forzar un renderizado inmediato
        if (context instanceof MainActivity) {
            ((MainActivity)context).runOnUiThread(() -> {
                Toast.makeText(context,
                        "Objeto colocado - Total: " + gameObjects.size(),
                        Toast.LENGTH_SHORT).show();
            });
        }
    }
    private void updatePlayerMovement() {
        float deltaX = 0;
        float deltaZ = 0;

        // Calcular movimiento basado en la rotación actual del jugador
        if (movingForward) {
            deltaZ -= moveSpeed * Math.cos(player.getRotationY());
            deltaX -= moveSpeed * Math.sin(player.getRotationY());
        }
        if (movingBackward) {
            deltaZ += moveSpeed * Math.cos(player.getRotationY());
            deltaX += moveSpeed * Math.sin(player.getRotationY());
        }
        if (movingLeft) {
            deltaZ -= moveSpeed * Math.sin(player.getRotationY());
            deltaX += moveSpeed * Math.cos(player.getRotationY());
        }
        if (movingRight) {
            deltaZ += moveSpeed * Math.sin(player.getRotationY());
            deltaX -= moveSpeed * Math.cos(player.getRotationY());
        }

        // Mover el jugador
        player.move(deltaX, deltaZ);
    }
    public float[] screenToWorld(float screenX, float screenY) {
        float playerX = player.getPosX();
        float playerZ = player.getPosZ();
        float playerRotY = player.getRotationY();

        // Colocar objeto frente al jugador
        float distance = 3.0f;
        float objectX = playerX + (float)(Math.sin(playerRotY) * distance);
        float objectZ = playerZ - (float)(Math.cos(playerRotY) * distance);

        // Mantener dentro de los límites del terreno
        objectX = Math.max(1.0f, Math.min(objectX, 19.0f));
        objectZ = Math.max(1.0f, Math.min(objectZ, 19.0f));

        // Elevar el objeto para que sea visible sobre el terreno
        return new float[]{objectX, 1.0f, objectZ};
    }
    public Player getPlayer() {
        return player;
    }

    // Métodos para controlar el movimiento
    public void setMovingForward(boolean moving) {
        this.movingForward = moving;
    }

    public void setMovingBackward(boolean moving) {
        this.movingBackward = moving;
    }

    public void setMovingLeft(boolean moving) {
        this.movingLeft = moving;
    }

    public void setMovingRight(boolean moving) {
        this.movingRight = moving;
    }
}