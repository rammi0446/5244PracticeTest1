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

        Square enemy;
        int Square_width = 50;
        int score = 0;
    ArrayList<Square> players = new ArrayList<Square>();
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
        this.players.add(new Square(context, 150, 100, Square_width));
        this.players.add(new Square(context, 50, 100, Square_width));
        this.players.add(new Square(context, -50, 100, Square_width));
        this.players.add(new Square(context, -150, 100, Square_width));
        this.enemy =  new Square(context,screenWidth-500,100,Square_width);

        // @TODO: Any other game setup stuff goes here


    }

    // ------------------------------
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


    // ------------------------------
    // GAME ENGINE FUNCTIONS
    // - update, draw, setFPS
    // ------------------------------

    // 1. Tell Android the (x,y) positions of your sprites
    boolean enemyMovingDown = true;
    public void updatePositions() {
        // @TODO: Update the position of the sprites
       //Log.d(TAG,"player position: " + this.player.getxPosition() + ", " + this.player.getyPosition());
        Log.d(TAG,"Enemy position: " + this.enemy.getxPosition() + ", " + this.enemy.getyPosition());

        if(enemyMovingDown == true) {
            this.enemy.setyPosition(this.enemy.getyPosition() + 10);
            if(this.enemy.getyPosition() >= this.screenHeight - 200)
            {
                enemyMovingDown = false;
            }
        }
        if(enemyMovingDown == false)
        {
            this.enemy.setyPosition(this.enemy.getyPosition() - 10);
            if(this.enemy.getyPosition()<=0)
            {
                enemyMovingDown = true;
            }

        }

        // 1. calculate distance between bullet and enemy
        for (int i = 0; i < this.players.size(); i++) {
            Square player = this.players.get(i);
            double a = this.enemy.getxPosition() - player.getxPosition();
            double b = this.enemy.getyPosition() - player.getyPosition();


            double d = Math.sqrt((a * a) + (b * b));

            Log.d(TAG, "Distance to enemy: " + d);

            // 2. calculate xn and yn constants
            // (amount of x to move, amount of y to move)
            double xn = (a / d);
            double yn = (b / d);

            // 3. calculate new (x,y) coordinates
            int newX = player.getxPosition() + (int) (xn * 30);
            int newY = player.getyPosition() + (int) (yn * 30);
            player.setxPosition(newX);
            player.setyPosition(newY);


            // 4. update the hitbox position for player ....
            player.updateHitbox();

            // 4. update the hitbox position for enemy
            this.enemy.updateHitbox();

            Log.d(TAG, "----------");

            // @TODO: Collision detection code

        if(player.getHitBox().intersect(this.enemy.getHitBox()))
        {
            this.score = this.score + 1;
            player.setxPosition(player.getInitialX());
            player.setyPosition(player.getInitialY());
        }
        }
    }

    // 2. Tell Android to DRAW the sprites at their positions
    public void redrawSprites() {
        if (this.holder.getSurface().isValid()) {
            this.canvas = this.holder.lockCanvas();

            //----------------
            // Put all your drawing code in this section

            // configure the drawing tools
            this.canvas.drawColor(Color.argb(255,255,255,255));
            paintbrush.setColor(Color.GRAY);


            //@TODO: Draw the sprites (rectangle, circle, etc)
            //draw players
            for (int i = 0; i < this.players.size(); i++) {
                               // 1. get the (x,y) of the bullet
                                       Square b = this.players.get(i);
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

            //draw enemy
            paintbrush.setColor(Color.BLACK);
            paintbrush.setStyle(Paint.Style.FILL);
            canvas.drawRect(this.enemy.getxPosition(),
                    this.enemy.getyPosition(),
                    this.enemy.getxPosition() + this.enemy.getWidth(),
                    this.enemy.getyPosition() +this.enemy.getWidth(),
                    paintbrush
            );
            paintbrush.setColor(Color.MAGENTA);
            paintbrush.setStrokeWidth(10);
            paintbrush.setStyle(Paint.Style.STROKE);
            //draw hit box for player
//            canvas.drawRect(this.player.getHitBox(),
//
//                    paintbrush
//            );
            //draw hit box for enemy
            canvas.drawRect(this.enemy.getHitBox(),

                    paintbrush
            );

            //@TODO: Draw game statistics (lives, score, etc)
            paintbrush.setTextSize(60);
            canvas.drawText("Score :" +score, 20, 100, paintbrush);

            //----------------
            this.holder.unlockCanvasAndPost(canvas);
        }
    }

    // Sets the frame rate of the game
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
