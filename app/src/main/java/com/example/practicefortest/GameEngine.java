package com.example.practicefortest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
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
    final static String TAG="PONG-GAME";

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
       // Sprite enemy;
        int Square_width = 10;
        int score = 0;
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
        this.gun =  new Sprite(context,150,100,R.drawable.shooting);
        this.bullets.add(new Square(context, 150, 100, Square_width,R.drawable.shooting));
        this.bullets.add(new Square(context, 50, 100, Square_width,R.drawable.shooting));
        this.bullets.add(new Square(context, -50, 100, Square_width,R.drawable.shooting));
        this.bullets.add(new Square(context, -150, 100, Square_width,R.drawable.shooting));
       // this.enemy =  new Sprite(context,screenWidth-500,100,R.drawable.bug1);
        this.enemies.add(new Sprite(context,screenWidth-500,100,R.drawable.bug1));
        this.enemies.add(new Sprite(context,screenWidth-500,300,R.drawable.bug1));
        this.enemies.add(new Sprite(context,screenWidth-500,500,R.drawable.bug1));

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
        //--------------------------gun moving----------------------------------------
        if(gunMovingDown == true) {
            this.gun.setyPosition(this.gun.getyPosition() + 10);
            if(this.gun.getyPosition() >= this.screenHeight - 200)
            {
                gunMovingDown = false;
            }
        }
        if(gunMovingDown == false)
        {
            this.gun.setyPosition(this.gun.getyPosition() - 10);
            if(this.gun.getyPosition()<=0)
            {
                gunMovingDown = true;
            }

        }
        //-------------------------enemy moving-------------------------------------------
//        if(enemyMovingDown == true) {
//            this.enemy.setyPosition(this.enemy.getyPosition() + 10);
//            if(this.enemy.getyPosition() >= this.screenHeight - 200)
//            {
//                enemyMovingDown = false;
//            }
//        }
//        if(enemyMovingDown == false)
//        {
//            this.enemy.setyPosition(this.enemy.getyPosition() - 10);
//            if(this.enemy.getyPosition()<=0)
//            {
//                enemyMovingDown = true;
//            }
//
//        }

        // 1. move bullets on the screen

        for (int i = 0; i < this.bullets.size(); i++) {
            Square bullet = this.bullets.get(i);
                bullet.setxPosition(bullet.getxPosition() + 60);
                if(bullet.getxPosition() >= this.screenWidth - 200)
                {
                    bullet.setxPosition(this.gun.getxPosition());
                    bullet.setyPosition(this.gun.getyPosition());
                }

           // 4. update the hitbox position for player ....
            bullet.updateHitbox();

            // 4. update the hitbox position for enemy
           // this.enemy.updateHitbox();

            Log.d(TAG, "----------");

            // @TODO: Collision detection code
            for (int it = 0; it < this.enemies.size(); it++) {
              Sprite  enemy = this.enemies.get(it);
                Log.d(TAG,"Enemy position: " + enemy.getxPosition() + ", " + enemy.getyPosition());

                if(bullet.getHitBox().intersect(enemy.getHitbox()))
                {
                    this.score = this.score + 1;
                    enemy.setxPosition(this.screenWidth);
                    enemy.setyPosition(this.screenWidth);
                    bullet.setxPosition(this.gun.getxPosition()+200);
                    bullet.setyPosition(this.gun.getyPosition());
                }

                if (enemy.getxPosition() <= this.screenWidth / 2) {
                    enemy.setxPosition(this.screenWidth-500);
                    enemy.setyPosition((i+2)*100);
                }
                else
                {
                    enemy.setxPosition(enemy.getxPosition() - 10);
                    enemy.updateHitbox();
                }
            }


        }//for loop end for bullets


    }

    // 2. Tell Android to DRAW the sprites at their positions-----------------------------
    public void redrawSprites() {
        if (this.holder.getSurface().isValid()) {
            this.canvas = this.holder.lockCanvas();

            //----------------
            // Put all your drawing code in this section

            // configure the drawing tools
            this.canvas.drawColor(Color.argb(255,255,255,255));
            paintbrush.setColor(Color.GRAY);

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
                canvas.drawRect(x, y, x+b.getWidth(), y+b.getWidth(), paintbrush);

                                       // 3. draw the bullet's hitbox
                                               paintbrush.setColor(Color.GREEN);
                               paintbrush.setStyle(Paint.Style.STROKE);
                               canvas.drawRect(
                                              b.getHitBox(),
                                               paintbrush
                                               );
                          }
            paintbrush.setColor(Color.MAGENTA);
            paintbrush.setStrokeWidth(10);
            paintbrush.setStyle(Paint.Style.STROKE);
            //draw hit box for enemy
//            canvas.drawRect(this.enemy.getHitbox(),
//                    paintbrush
//            );


            //@TODO: Draw game statistics (lives, score, etc)
            paintbrush.setTextSize(60);
            canvas.drawText("Score :" +score, 20, 100, paintbrush);

            //----------------
            this.holder.unlockCanvasAndPost(canvas);
        }
    }

    // Sets the frame rate of the game----------------
    public void setFPS() {
        try {
            gameThread.sleep(50);
        }
        catch (Exception e) {

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
        }
        else if (userAction == MotionEvent.ACTION_UP) {
            // user lifted their finger
        }
        return true;
    }
}
