package com.example.practicefortest;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.constraint.solver.widgets.Rectangle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Random;

public class GameEngine extends SurfaceView implements Runnable {

    // -----------------------------------
    // ## ANDROID DEBUG VARIABLES
    // -----------------------------------

    // Android debug variables
    final static String TAG = "PONG-GAME";

    // -----------------------------------
    // ## SCREEN & DRAWING SETUP VARIABLES
    // -----------------------------------

    // screen size
    int screenHeight;
    int screenWidth;

    // game state
    boolean gameIsRunning;

    // threading
    Thread gameThread;


    // drawing variables
    SurfaceHolder holder;
    Canvas canvas;
    Paint paintbrush;


    // -----------------------------------
    // ## GAME SPECIFIC VARIABLES
    // -----------------------------------

    // ----------------------------
    // ## SPRITES
    // ----------------------------
    Sprite gun;
    Sprite bg;
    Square bullet;
    int Square_width = 50;
    int score = 0;
    int Lives = 5;
    ArrayList<Square> bullets = new ArrayList<Square>();
    ArrayList<Sprite> enemies = new ArrayList<Sprite>();


    // ----------------------------
    // ## GAME STATS - number of lives, score, etc
    // ----------------------------


    public GameEngine(Context context, int w, int h) {
        super(context);

        this.holder = this.getHolder();
        this.paintbrush = new Paint();

        this.screenWidth = w;
        this.screenHeight = h;

        this.printScreenInfo();

        // @TODO: Add your sprites to this section
        // This is optional. Use it to:
        //  - setup or configure your sprites
        //  - set the initial position of your sprites
        // this.player = new Square(context,100,100, Square_width);
        this.bg = new Sprite(context, 0, 0, R.drawable.bg);

        this.gun = new Sprite(context, 150, 100, R.drawable.shooting);
        this.bullets.add(new Square(context, 150, 100, Square_width, R.drawable.shooting));
//        this.bullets.add(new Square(context, 50, 100, Square_width,R.drawable.shooting));
//        this.bullets.add(new Square(context, -50, 100, Square_width,R.drawable.shooting));
//        this.bullets.add(new Square(context, -150, 100, Square_width,R.drawable.shooting));
        // this.enemy =  new Sprite(context,screenWidth-500,100,R.drawable.bug1);
//        this.enemies.add(new Sprite(context,screenWidth-500,50,R.drawable.bug1));
//        this.enemies.add(new Sprite(context,screenWidth-500,300,R.drawable.bug1));
//        this.enemies.add(new Sprite(context,screenWidth-500,550,R.drawable.bug1));
//        this.enemies.add(new Sprite(context,screenWidth,50,R.drawable.bug2));
//        this.enemies.add(new Sprite(context,screenWidth,300,R.drawable.bug2));
//        this.enemies.add(new Sprite(context,screenWidth,550,R.drawable.bug2));

        //this.enemies.add(new Sprite(context,screenWidth/2,-550,R.drawable.bug1));
        this.enemies.add(new Sprite(context,screenWidth/2,-250,R.drawable.bug1));
        this.enemies.add(new Sprite(context,screenWidth/2,-50,R.drawable.bug1));
        this.enemies.add(new Sprite(context,screenWidth/2,screenHeight-50,R.drawable.bug2));
        this.enemies.add(new Sprite(context, screenWidth / 2, screenHeight - 250, R.drawable.bug2));
//        this.enemies.add(new Sprite(context,screenWidth,550,R.drawable.bug2));


        // @TODO: Any other game setup stuff goes here


    }

    // --------------------------------------------------------------------------------------------
    // HELPER FUNCTIONS
    // ------------------------------

    // This funciton prints the screen height & width to the screen.
    private void printScreenInfo() {

        Log.d(TAG, "Screen (w, h) = " + this.screenWidth + "," + this.screenHeight);
    }


    // ------------------------------
    // GAME STATE FUNCTIONS (run, stop, start)
    // ------------------------------
    @Override
    public void run() {
        while (gameIsRunning == true) {
            this.updatePositions();
            this.redrawSprites();
            this.setFPS();
        }
    }


    public void pauseGame() {
        gameIsRunning = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            // Error
        }
    }

    public void startGame() {
        gameIsRunning = true;
        gameThread = new Thread(this);
        gameThread.start();
    }


    // -------------------------------------------------------------------------------------------
    // GAME ENGINE FUNCTIONS
    // - update, draw, setFPS
    // ------------------------------

    // 1. Tell Android the (x,y) positions of your sprites
    boolean gunMovingDown = true;
    boolean enemyMovingDown = true;

    public void updatePositions() {
        // @TODO: Update the position of the sprites
        //Log.d(TAG,"player position: " + this.player.getxPosition() + ", " + this.player.getyPosition());
        //  Log.d(TAG,"Enemy position: " + this.enemy.getxPosition() + ", " + this.enemy.getyPosition());


        // 1. move bullets on the screen
        bulletsMoveFun();

        Log.d(TAG, "----------");


        // 2. move the enemy
        for (int it = 0; it < this.enemies.size(); it++) {
            Sprite enemy = this.enemies.get(it);
            // Log.d(TAG,"Enemy position: " + enemy.getxPosition() + ", " + enemy.getyPosition());

            if (enemy.getxPosition() < this.screenWidth - 300 || enemy.getyPosition() < 0) {
                enemy.setxPosition(enemy.getxPosition() + 10);
                enemy.setyPosition(enemy.getyPosition() + 2);
            } else {
                enemy.setxPosition(enemy.getxPosition() + 10);
                enemy.setyPosition(enemy.getyPosition() - 2);
            }
            if (enemy.getxPosition() >= this.screenWidth - 300) {
                enemy.setxPosition(this.screenWidth - 200);
                enemy.setyPosition(it * 200);

            }

            enemy.updateHitbox();

        }

    }//for loop end for enemies


    /* This function handles bullet movement & collision detection */
    public void bulletsMoveFun() {

        for (int i = 0; i < this.bullets.size(); i++) {
            // Get bullet from array
            bullet = this.bullets.get(i);

            // Move bullet to the new position
            bullet.setxPosition(bullet.getxPosition() + 200);
            if (bullet.getxPosition() >= this.screenWidth - 200) {
                bullet.setxPosition(this.gun.getxPosition());
                bullet.setyPosition(this.gun.getyPosition());
            }
            // Update the bullet hitbox position
            bullet.updateHitbox();

            // Check if bullet hits enemy
            for (int j = 0; j < this.enemies.size(); j++) {
                Sprite e = this.enemies.get(j);

                if (bullet.getHitBox().intersect(e.getHitbox())) {
                    Log.d(TAG, "bullet hits the box ");

                    this.score = this.score + 1;
                    e.setxPosition(e.getInitialX());
                    e.setyPosition(e.getInitialY());
                }
            } // end check

        } // ends for loop
    }


    // 2. Tell Android to DRAW the sprites at their positions-----------------------------
    public void redrawSprites() {
        if (this.holder.getSurface().isValid()) {
            this.canvas = this.holder.lockCanvas();

            //----------------
            // Put all your drawing code in this section

            // configure the drawing tools
            this.canvas.drawColor(Color.argb(255, 255, 255, 255));
            paintbrush.setColor(Color.GRAY);
            //draw bg
            bg.setWidth(this.screenWidth - 500);
            bg.setHeight(this.screenHeight - 800);
            canvas.drawBitmap(this.bg.getImage(), this.bg.getxPosition(), this.bg.getyPosition(), paintbrush);


            //draw gun
            canvas.drawBitmap(this.gun.getImage(), this.gun.getxPosition(), this.gun.getyPosition(), paintbrush);

            //draw enemy image
            // canvas.drawBitmap(this.enemy.getImage(), this.enemy.getxPosition(), this.enemy.getyPosition(), paintbrush);
            for (int i = 0; i < this.enemies.size(); i++) {
                // 1. get the (x,y) of the bullet
                Sprite e = this.enemies.get(i);
                int x = e.getxPosition();
                int y = e.getyPosition();
                // 2. draw the bullet
                paintbrush.setColor(Color.BLACK);
                paintbrush.setStyle(Paint.Style.FILL);
                canvas.drawBitmap(e.getImage(), e.getxPosition(), e.getyPosition(), paintbrush);

                // 3. draw the bullet's hitbox
                paintbrush.setColor(Color.GREEN);
                paintbrush.setStyle(Paint.Style.STROKE);
                canvas.drawRect(
                        e.getHitbox(),
                        paintbrush
                );
            }

            //@TODO: Draw the sprites (rectangle, circle, etc)
            //draw players
            for (int i = 0; i < this.bullets.size(); i++) {
                // 1. get the (x,y) of the bullet
                Square b = this.bullets.get(i);
                int x = b.getxPosition();
                int y = b.getyPosition();
                // 2. draw the bullet
                paintbrush.setColor(Color.BLACK);
                paintbrush.setStyle(Paint.Style.FILL);
                canvas.drawRect(x, y, x + b.getWidth(), y + b.getWidth(), paintbrush);

                // 3. draw the bullet's hitbox
                paintbrush.setColor(Color.GREEN);
                paintbrush.setStyle(Paint.Style.STROKE);
                paintbrush.setStrokeWidth(5);
                canvas.drawRect(
                        b.getHitBox(),
                        paintbrush
                );
            }
            paintbrush.setColor(Color.MAGENTA);
            paintbrush.setStrokeWidth(10);
            paintbrush.setStyle(Paint.Style.STROKE);

            //@TODO: Draw game statistics (lives, score, etc)
            paintbrush.setTextSize(80);
            canvas.drawText("Score :" + score, 20, 100, paintbrush);
            canvas.drawText("Lives :" + Lives, this.screenWidth - 400, 100, paintbrush);


            //----------------
            this.holder.unlockCanvasAndPost(canvas);
        }
    }

    // Sets the frame rate of the game----------------
    public void setFPS() {
        try {
            gameThread.sleep(50);
        } catch (Exception e) {

        }
    }

    // ------------------------------
    // USER INPUT FUNCTIONS
    // ------------------------------

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int userAction = event.getActionMasked();
        //@TODO: What should happen when person touches the screen?
        if (userAction == MotionEvent.ACTION_DOWN) {
            // user pushed down on screen
            this.gun.setyPosition((int) event.getY());
            gun.updateHitbox();
        } else if (userAction == MotionEvent.ACTION_UP) {
            // user lifted their finger

        }
        return true;
    }

}
