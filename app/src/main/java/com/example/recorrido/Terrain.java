package com.example.recorrido;

import android.opengl.GLES30;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Terrain {
    private FloatBuffer vertexBuffer;
    private int mProgram;
    private int[] vbo = new int[1];
    private static final int COORDS_PER_VERTEX = 3;
    private final int vertexCount;
    private final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "void main() {" +
                    "  gl_FragColor = vec4(0.2, 0.7, 0.3, 1.0);" +
                    "}";
    public Terrain() {
        float[] terrainVertices = generateTerrainVertices();
        vertexCount = terrainVertices.length / COORDS_PER_VERTEX;

        // Inicializar buffer de vértices
        vertexBuffer = ByteBuffer.allocateDirect(terrainVertices.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexBuffer.put(terrainVertices).position(0);

        // Crear programa de shader
        int vertexShader = loadShader(GLES30.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentShaderCode);
        mProgram = GLES30.glCreateProgram();
        GLES30.glAttachShader(mProgram, vertexShader);
        GLES30.glAttachShader(mProgram, fragmentShader);
        GLES30.glLinkProgram(mProgram);

        // Generar VBO
        GLES30.glGenBuffers(1, vbo, 0);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vbo[0]);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER,
                vertexBuffer.capacity() * 4,
                vertexBuffer,
                GLES30.GL_STATIC_DRAW);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
    }
    private float[] generateTerrainVertices() {
        int width = 20;
        int depth = 20;
        float[] vertices = new float[width * depth * 6 * 3];
        int index = 0;

        for (int z = 0; z < depth; z++) {
            for (int x = 0; x < width; x++) {
                // Primer triángulo
                vertices[index++] = x;
                vertices[index++] = 0;
                vertices[index++] = z;

                vertices[index++] = x + 1;
                vertices[index++] = 0;
                vertices[index++] = z;

                vertices[index++] = x;
                vertices[index++] = 0;
                vertices[index++] = z + 1;

                // Segundo triángulo
                vertices[index++] = x + 1;
                vertices[index++] = 0;
                vertices[index++] = z;

                vertices[index++] = x + 1;
                vertices[index++] = 0;
                vertices[index++] = z + 1;

                vertices[index++] = x;
                vertices[index++] = 0;
                vertices[index++] = z + 1;
            }
        }
        return vertices;
    }

    public void draw(float[] projectionMatrix, float[] viewMatrix) {
        GLES30.glUseProgram(mProgram);

        int positionHandle = GLES30.glGetAttribLocation(mProgram, "vPosition");
        int mvpMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");

        float[] mvpMatrix = new float[16];
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        GLES30.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vbo[0]);
        GLES30.glEnableVertexAttribArray(positionHandle);
        GLES30.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX,
                GLES30.GL_FLOAT, false,
                COORDS_PER_VERTEX * 4, 0);

        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vertexCount);

        GLES30.glDisableVertexAttribArray(positionHandle);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
        GLES30.glUseProgram(0);
    }

    private int loadShader(int type, String shaderCode) {
        int shader = GLES30.glCreateShader(type);
        GLES30.glShaderSource(shader, shaderCode);
        GLES30.glCompileShader(shader);

        // Verificar estado de compilación
        int[] compiled = new int[1];
        GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) {
            Log.e("Shader Error", "Could not compile shader " + type + ":");
            Log.e("Shader Error", GLES30.glGetShaderInfoLog(shader));
            GLES30.glDeleteShader(shader);
            return 0;
        }
        return shader;
    }

    private void checkGlError(String glOperation) {
        int error;
        while ((error = GLES30.glGetError()) != GLES30.GL_NO_ERROR) {
            Log.e("OpenGL Error", glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }
}