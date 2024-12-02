package com.example.recorrido;

public class Player {
    private float posX = 10f, posY = 1f, posZ = 10f;
    private float rotationX = 0f, rotationY = 0f;

    public void move(float deltaX, float deltaZ) {
        posX += deltaX * Math.cos(rotationY);
        posZ += deltaZ * Math.sin(rotationY);
    }

    public void rotate(float deltaX, float deltaY) {
        rotationX += deltaX;
        rotationY += deltaY;
    }

    // Getters
    public float getPosX() { return posX; }
    public float getPosY() { return posY; }
    public float getPosZ() { return posZ; }
    public float getRotationY() { return rotationY; }
}