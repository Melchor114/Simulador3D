package com.example.recorrido;

import android.opengl.GLES30;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class GameObject {
    private FloatBuffer vertexBuffer;
    private ShortBuffer indexBuffer;
    private int[] vbo = new int[1];
    private int[] ibo = new int[1];
    private static final int COORDS_PER_VERTEX = 3;

    private float posX, posY, posZ;

    public GameObject(float x, float y, float z) {
        this.posX = x;
        this.posY = y;
        this.posZ = z;

        // Vertices para un cubo completo
        float[] cubeVertices = {
                // Front face
                x, y, z,           // 0
                x + 1, y, z,       // 1
                x + 1, y + 1, z,   // 2
                x, y + 1, z,       // 3

                // Back face
                x, y, z - 1,       // 4
                x + 1, y, z - 1,   // 5
                x + 1, y + 1, z - 1, // 6
                x, y + 1, z - 1    // 7
        };

        // Índices para dibujar el cubo
        short[] cubeIndices = {
                // Front face   
                0, 1, 2, 2, 3, 0,
                // Right face
                1, 5, 6, 6, 2, 1,
                // Back face
                5, 4, 7, 7, 6, 5,
                // Left face
                4, 0, 3, 3, 7, 4,
                // Top face
                3, 2, 6, 6, 7, 3,
                // Bottom face
                4, 5, 1, 1, 0, 4
        };

        // Preparar buffer de vértices
        vertexBuffer = ByteBuffer.allocateDirect(cubeVertices.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexBuffer.put(cubeVertices).position(0);

        // Preparar buffer de índices
        indexBuffer = ByteBuffer.allocateDirect(cubeIndices.length * 2)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer();
        indexBuffer.put(cubeIndices).position(0);

        // Generar y enlazar VBO para vértices
        GLES30.glGenBuffers(1, vbo, 0);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vbo[0]);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER,
                vertexBuffer.capacity() * 4,
                vertexBuffer,
                GLES30.GL_STATIC_DRAW
        );

        // Generar y enlazar IBO para índices
        GLES30.glGenBuffers(1, ibo, 0);
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
        GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER,
                indexBuffer.capacity() * 2,
                indexBuffer,
                GLES30.GL_STATIC_DRAW
        );
    }

    public void draw(float[] projectionMatrix, float[] viewMatrix) {
        // Código de shader simplificado
        String vertexShaderCode =
                "uniform mat4 uMVPMatrix;" +
                        "attribute vec4 vPosition;" +
                        "void main() {" +
                        "  gl_Position = uMVPMatrix * vPosition;" +
                        "}";

        String fragmentShaderCode =
                "precision mediump float;" +
                        "void main() {" +
                        "  gl_FragColor = vec4(0.8, 0.4, 0.2, 1.0);" + // Color naranja para la caja
                        "}";

        int vertexShader = loadShader(GLES30.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentShaderCode);

        int mProgram = GLES30.glCreateProgram();
        GLES30.glAttachShader(mProgram, vertexShader);
        GLES30.glAttachShader(mProgram, fragmentShader);
        GLES30.glLinkProgram(mProgram);

        GLES30.glUseProgram(mProgram);

        int positionHandle = GLES30.glGetAttribLocation(mProgram, "vPosition");
        int matrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");

        float[] mvpMatrix = new float[16];
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        GLES30.glUniformMatrix4fv(matrixHandle, 1, false, mvpMatrix, 0);

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vbo[0]);
        GLES30.glEnableVertexAttribArray(positionHandle);
        GLES30.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX,
                GLES30.GL_FLOAT, false,
                COORDS_PER_VERTEX * 4, 0
        );

        // Usar el buffer de índices para dibujar
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, 36, GLES30.GL_UNSIGNED_SHORT, 0);

        GLES30.glDisableVertexAttribArray(positionHandle);
    }

    private int loadShader(int type, String shaderCode) {
        int shader = GLES30.glCreateShader(type);
        GLES30.glShaderSource(shader, shaderCode);
        GLES30.glCompileShader(shader);
        return shader;
    }

    // Getters para la posición si es necesario
    public float getPosX() { return posX; }
    public float getPosY() { return posY; }
    public float getPosZ() { return posZ; }
}