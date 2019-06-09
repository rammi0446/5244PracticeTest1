package com.example.practicefortest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

public class Square {
    private int xPosition;
    private int yPosition;
    private int width;
    private int initialX;
    private int initialY;
    // image




    public int getInitialX() {
        return initialX;
    }

    public void setInitialX(int initialX) {
        this.initialX = initialX;
    }

    public int getInitialY() {
        return initialY;
    }

    public void setInitialY(int initialY) {
        this.initialY = initialY;
    }


    Rect hitBox;

    public Rect getHitBox() {
        return hitBox;
    }

    public void setHitBox(Rect hitBox) {
        this.hitBox = hitBox;
    }
    public void updateHitbox() {
                this.hitBox.left = this.xPosition;
                this.hitBox.top = this.yPosition;
                this.hitBox.right = this.xPosition + this.width;
               this.hitBox.bottom = this.yPosition + this.width;
           }

    public Square(Context context, int x, int y, int width, int imageName)
    {
        this.xPosition = x;
        this.yPosition = y;
        this.width = width;
        initialX = x;
        initialY = y;


        this.hitBox = new Rect(
                this.xPosition,
                this.yPosition,
                this.xPosition + this.width,
                this.yPosition + this.width
        );
    }

    public void setxPosition(int xPosition) {
        this.xPosition = xPosition;
    }

    public void setyPosition(int yPosition) {
        this.yPosition = yPosition;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getxPosition() {
        return xPosition;
    }

    public int getyPosition() {
        return yPosition;
    }

    public int getWidth() {
        return width;
    }
}
