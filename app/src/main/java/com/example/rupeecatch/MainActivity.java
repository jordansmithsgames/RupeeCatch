package com.example.rupeecatch;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MainActivity extends AppCompatActivity implements SensorEventListener, SurfaceHolder.Callback {

    Bitmap link, redRupee, blueRupee, greenRupee;
    int linkX, linkY, redRupeeX, redRupeeY, blueRupeeX, blueRupeeY, greenRupeeX, greenRupeeY;
    int linkW, linkH, rupeeW, rupeeH;
    SurfaceHolder holder = null;
    Animator anim;
    boolean initialized;
    float accX, accY;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bitmap linkBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.linkreaching);
        Bitmap redRupeeBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.redrupee);
        Bitmap blueRupeeBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bluerupee);
        Bitmap greenRupeeBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.greenrupee);

        int scaleFactor = 5;
        linkW = linkBitmap.getWidth() * scaleFactor;
        linkH = linkBitmap.getHeight() * scaleFactor;
        link = Bitmap.createScaledBitmap(linkBitmap, linkW, linkH, false);
        rupeeW = redRupeeBitmap.getWidth() * scaleFactor;
        rupeeH = redRupeeBitmap.getHeight() * scaleFactor;
        redRupee = Bitmap.createScaledBitmap(redRupeeBitmap, rupeeW, rupeeH, false);
        blueRupee = Bitmap.createScaledBitmap(blueRupeeBitmap, rupeeW, rupeeH, false);
        greenRupee = Bitmap.createScaledBitmap(greenRupeeBitmap, rupeeW, rupeeH, false);

        /*          0, 0
         *           |                       |
         *           |                       |
         *    110px  |   Link           80px |   Rupee
         *           |                       |
         *           |__________             |__________
         *               80px                     50px
         * */

        SensorManager manager=(SensorManager)getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometer=manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if(accelerometer!=null){
            manager.registerListener(this,accelerometer,SensorManager.SENSOR_DELAY_NORMAL,SensorManager.SENSOR_DELAY_UI);
        }

        SurfaceView my_surface=findViewById(R.id.surfaceView);
        my_surface.getHolder().addCallback(this);

        anim = new Animator(this);
        anim.start();
    }

    int speed = 10;
    int score = 0;

    public void update(int width, int height) {
        if (!initialized) {
            /* Set initial positions for Link and the colored Rupees, like so:
             *                           R========
             *                           ====B====
             *                           ========G
             *                           =========
             *                           L========
             * */
            linkX = 0;
            linkY = height - linkH;

            redRupeeX = 0;
            redRupeeY = 0;

            blueRupeeX = width / 2;
            blueRupeeY = height / 5;

            greenRupeeX = width - rupeeW;
            greenRupeeY = (height / 5) * 2;

            initialized = true;
        }

        linkX -= accX * 2;
        redRupeeY += speed;
        blueRupeeY += speed * 2;
        greenRupeeY += speed * 5;

        if (linkX < 0) linkX = 0;
        else if (linkX > width - linkW) linkX  = width - linkW;

        if (Math.abs(linkX - redRupeeX) < linkW && Math.abs(linkY - redRupeeY) < linkH) {
            score += 20;
            redRupeeY = 0;
            redRupeeX = (int)(Math.random() * (width - rupeeW));
        }
        if (Math.abs(linkX - blueRupeeX) < linkW && Math.abs(linkY - blueRupeeY) < linkH) {
            score += 5;
            blueRupeeY = 0;
            blueRupeeX = (int)(Math.random() * (width - rupeeW));
        }
        if (Math.abs(linkX - greenRupeeX) < linkW && Math.abs(linkY - greenRupeeY) < linkH) {
            score += 1;
            greenRupeeY = 0;
            greenRupeeX = (int)(Math.random() * (width - rupeeW));
        }

        if (redRupeeY > height) {
            redRupeeY = 0;
            redRupeeX = (int)(Math.random() * (width - rupeeW));
        }
        if (blueRupeeY > height) {
            blueRupeeY = 0;
            blueRupeeX = (int)(Math.random() * (width - rupeeW));
        }
        if (greenRupeeY > height) {
            greenRupeeY = 0;
            greenRupeeX = (int)(Math.random() * (width - rupeeW));
        }

    }

    public void draw() {
        if (holder == null) return;

        Canvas c = holder.lockCanvas();
        update(c.getWidth(), c.getHeight());

        c.drawColor(Color.rgb(79,199,119));
        c.drawBitmap(link, linkX, linkY, null);
        c.drawBitmap(redRupee, redRupeeX, redRupeeY, null);
        c.drawBitmap(blueRupee, blueRupeeX, blueRupeeY, null);
        c.drawBitmap(greenRupee, greenRupeeX, greenRupeeY, null);

        Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(100);
        c.drawText("Score: " + score, 15, 100, textPaint);

        holder.unlockCanvasAndPost(c);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        accX = sensorEvent.values[0];
        accY = sensorEvent.values[1];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        holder = surfaceHolder;
        draw();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        holder = surfaceHolder;
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        holder = null;
    }

    @Override
    public void onDestroy() {
        anim.finish();
        SensorManager manager=(SensorManager) getSystemService(Context.SENSOR_SERVICE);
        manager.unregisterListener(this,manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
        super.onDestroy();
    }
}