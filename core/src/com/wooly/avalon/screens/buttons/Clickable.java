package com.wooly.avalon.screens.buttons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.wooly.avalon.TDGame;
import com.wooly.avalon.maps.Coordinate;

public abstract class Clickable {
    //
    protected Coordinate position;
    public float  width, height;

    public Clickable(Coordinate position){
        this.position = position;
    }
    public boolean isActive() {
        if (Gdx.input.getX() < position.x() + this.width && Gdx.input.getX() > position.x() &&
                TDGame.SCREEN_HEIGHT - Gdx.input.getY() < position.y() + this.height &&
                TDGame.SCREEN_HEIGHT - Gdx.input.getY() > position.y()) {
            return true;
        }
        return false;
    }

    /**
     * Draws the clickable.
     * If you want to check if it has been clicked use drawCheckClick() instead;
     */
    public abstract void draw(SpriteBatch batch);
    /**
     * Draws the clickable and also checks if it has been clicked.
    */
    public void drawCheckClick(SpriteBatch batch) {
        draw(batch);
        if(isActive() && Gdx.input.justTouched()){
            onClick();
        }
    }
    public abstract void onClick();
    public abstract void dispose();
    public Coordinate getPosition() {
        return position;
    }
    public void setPosition(Coordinate position) {
        this.position = position;
    }
}
