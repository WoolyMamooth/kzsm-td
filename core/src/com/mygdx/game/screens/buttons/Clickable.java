package com.mygdx.game.screens.buttons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.TDGame;
import com.mygdx.game.maps.Coordinate;
import com.mygdx.game.screens.MenuScreen;

public abstract class Clickable {
    protected TDGame game;
    //
    protected Coordinate position;
    public float  width, height;
    protected Texture activeTexture, inactiveTexture;

    public Clickable(TDGame game, Coordinate position, Texture activeTexture, Texture inactiveTexture) {
        this.game = game;
        this.position = position;
        this.width = activeTexture.getWidth() * MenuScreen.MENU_SCALE;
        this.height = activeTexture.getHeight() * MenuScreen.MENU_SCALE;
        this.activeTexture = activeTexture;
        this.inactiveTexture = inactiveTexture;
    }

    public boolean isActive() {
        if (Gdx.input.getX() < position.x() + this.width && Gdx.input.getX() > position.x() &&
                TDGame.SCREEN_HEIGHT - Gdx.input.getY() < position.y() + this.height &&
                TDGame.SCREEN_HEIGHT - Gdx.input.getY() > position.y()) {
            return true;
        }
        return false;
    }

    public void draw() {
        game.batch.draw((isActive() ? activeTexture : inactiveTexture), position.x(), position.y(), this.width, this.height);
    }
    public abstract void onClick();

    public Texture getTexture() {
        if (this.isActive()) {
            return this.activeTexture;
        }
        return this.inactiveTexture;
    }

    public void dispose(){
        activeTexture.dispose();
        inactiveTexture.dispose();
    }

    public Coordinate getPosition() {
        return position;
    }
}