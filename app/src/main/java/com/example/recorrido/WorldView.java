package com.example.recorrido;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class WorldView extends View {
    private Paint paintLandscape;
    private Paint paintSky;
    private Paint paintSun;
    private Paint paintTrees;
    private Paint paintMountains;

    private float offsetX = 0; // Desplazamiento horizontal
    private float offsetY = 0; // Desplazamiento vertical
    private GestureDetector gestureDetector;

    private float initialOffsetX = 0;
    private float initialOffsetY = 0;

    public WorldView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inicializarPinturas();

        // Gesture detector configuration
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                offsetX -= distanceX; // Update displacement
                offsetY -= distanceY;
                invalidate(); // Redraw view
                return true;
            }
        });
    }

    // New method to move view by specific amount
    public void moveView(float deltaX, float deltaY) {
        offsetX += deltaX;
        offsetY += deltaY;

        // Limit the displacement
        offsetX = Math.max(-getWidth(), Math.min(offsetX, getWidth()));
        offsetY = Math.max(-getHeight(), Math.min(offsetY, getHeight()));

        invalidate();
    }

    // New method to reset view to initial position
    public void resetView() {
        offsetX = initialOffsetX;
        offsetY = initialOffsetY;
        invalidate();
    }


    private void inicializarPinturas() {
        paintSky = new Paint();
        paintSky.setColor(Color.rgb(135, 206, 235)); // Azul cielo

        paintLandscape = new Paint();
        paintLandscape.setColor(Color.rgb(34, 139, 34)); // Verde paisaje

        paintSun = new Paint();
        paintSun.setColor(Color.YELLOW);
        paintSun.setShadowLayer(10, 0, 0, Color.YELLOW);

        paintTrees = new Paint();
        paintTrees.setColor(Color.rgb(0, 100, 0)); // Verde bosque

        paintMountains = new Paint();
        paintMountains.setColor(Color.rgb(96, 96, 96)); // Gris montaña
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Transladar el canvas según el desplazamiento
        canvas.save();
        canvas.translate(offsetX, offsetY);

        int width = getWidth();
        int height = getHeight();

        // Cielo
        canvas.drawRect(0, 0, width, height * 2 / 3, paintSky);

        // Sol
        canvas.drawCircle(width * 4 / 5, height / 4, 80, paintSun);

        // Montañas
        Path mountainPath1 = new Path();
        mountainPath1.moveTo(0, height * 2 / 3);
        mountainPath1.lineTo(width / 3, height / 3);
        mountainPath1.lineTo(width * 2 / 3, height * 2 / 3);
        mountainPath1.close();
        canvas.drawPath(mountainPath1, paintMountains);

        Path mountainPath2 = new Path();
        mountainPath2.moveTo(width / 2, height * 2 / 3);
        mountainPath2.lineTo(width, height / 2);
        mountainPath2.lineTo(width, height * 2 / 3);
        mountainPath2.close();
        canvas.drawPath(mountainPath2, paintMountains);

        // Paisaje
        canvas.drawRect(0, height * 2 / 3, width, height, paintLandscape);

        // Árboles
        Paint treeTrunk = new Paint();
        treeTrunk.setColor(Color.rgb(139, 69, 19)); // Marrón tronco

        // Árbol 1
        canvas.drawRect(width / 6 - 20, height * 5 / 6, width / 6 + 20, height * 2 / 3 + 100, treeTrunk);
        Path treeTop1 = new Path();
        treeTop1.moveTo(width / 6 - 50, height * 2 / 3 + 100);
        treeTop1.lineTo(width / 6, height / 2);
        treeTop1.lineTo(width / 6 + 50, height * 2 / 3 + 100);
        treeTop1.close();
        canvas.drawPath(treeTop1, paintTrees);

        // Árbol 2
        canvas.drawRect(width * 5 / 6 - 20, height * 5 / 6, width * 5 / 6 + 20, height * 2 / 3 + 100, treeTrunk);
        Path treeTop2 = new Path();
        treeTop2.moveTo(width * 5 / 6 - 50, height * 2 / 3 + 100);
        treeTop2.lineTo(width * 5 / 6, height / 2 + 50);
        treeTop2.lineTo(width * 5 / 6 + 50, height * 2 / 3 + 100);
        treeTop2.close();
        canvas.drawPath(treeTop2, paintTrees);

        canvas.restore(); // Restaurar la posición original del canvas
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = gestureDetector.onTouchEvent(event);

        // Limitar el desplazamiento (opcional)
        offsetX = Math.max(-getWidth(), Math.min(offsetX, getWidth()));
        offsetY = Math.max(-getHeight(), Math.min(offsetY, getHeight()));

        return result;
    }
}
