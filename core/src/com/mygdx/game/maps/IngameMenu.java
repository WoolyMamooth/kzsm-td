package com.mygdx.game.maps;

import static com.mygdx.game.TDGame.SCREEN_BOT_LEFT;
import static com.mygdx.game.TDGame.SCREEN_CENTER;
import static com.mygdx.game.TDGame.SCREEN_HEIGHT;
import static com.mygdx.game.TDGame.SCREEN_WIDTH;
import static com.mygdx.game.TDGame.fetchFont;
import static com.mygdx.game.TDGame.fetchTexture;
import static com.mygdx.game.TDGame.place;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.TDGame;
import com.mygdx.game.screens.GameScreen;
import com.mygdx.game.screens.buttons.Clickable;

public class IngameMenu {
    TDGame game;
    GameScreen screen;
    PlayerDataContainer playerData;
    PauseButton pauseButton;
    PauseMenu pauseMenu;
    Texture forBackgrounds=fetchTexture("white_square");
    BitmapFont font;
    float textHeight;
    float textSpacingY=20;
    float backgroundHeight;
    public boolean menuButtonPressed=false;

    /**
     * Displays playerHP, playerGold, also has a PauseButton and Hero ability buttons.
     * @param game Necessary for reloading and exiting.
     * @param screen This screens pause() method will be called when the PauseButton is clicked
     */
    public IngameMenu(TDGame game,GameScreen screen){
        this.game=game;
        this.screen=screen;
        this.playerData=new PlayerDataContainer(place(SCREEN_WIDTH-300,SCREEN_HEIGHT),20);
        this.pauseButton=new PauseButton(place(SCREEN_WIDTH,SCREEN_HEIGHT), TDGame.fetchTexture("buttons/resume_game"),TDGame.fetchTexture("buttons/pause_game"));
        this.pauseMenu=new PauseMenu();

        //create the font
        font=fetchFont(35);

        textHeight=font.getCapHeight();
        backgroundHeight=textHeight*2+textSpacingY*3; //2 lines 1 space top and bot, 1 in between
    }
    public void draw(SpriteBatch batch){
        //if paused, draw the pausemenu on top
        if(screen.isPaused()) pauseMenu.draw(batch);

        //draw background
        batch.setColor(0f,0f,0f,0.7f);
        batch.draw(forBackgrounds,playerData.position.x(),playerData.position.y()-backgroundHeight,300,backgroundHeight);
        batch.setColor(Color.WHITE);

        playerData.draw(batch); //hp and gold
        pauseButton.drawCheckClick(batch); //pause button
    }
    public void dispose() {
        pauseButton.dispose();
        pauseMenu.dispose();
    }

    private class PlayerDataContainer{
        int playerHP;
        int playerGold;
        Coordinate position;
        float offset;

        /**
         * Displays the HP and Gold of the player, uses font from IngameMenu.
         * @param position
         * @param offset Added to the X coordinate of all text.
         */
        public PlayerDataContainer(Coordinate position,float offset) {
            this.position=position;
            this.offset=offset;
        }

        public void draw(SpriteBatch batch) {
            updateData();

            font.setColor(Color.RED);
            font.draw(batch,"HP : "+playerHP, position.x()+offset, position.y()-textSpacingY);

            font.setColor(Color.GOLD);
            font.draw(batch,"Gold: "+playerGold,position.x()+offset, position.y()-textSpacingY*2-textHeight);
        }
        private void updateData(){
            int[] playerData = screen.getPlayerData();
            playerHP=playerData[0];
            playerGold=playerData[1];
        }
    }
    private class PauseButton extends Clickable{
        /**
         * When clicked the screen.pause() will be called.
         * @param position
         * @param activeTexture
         * @param inactiveTexture
         */
        public PauseButton(Coordinate position, Texture activeTexture, Texture inactiveTexture) {
            super(position, activeTexture, inactiveTexture);
            Coordinate offset=new Coordinate(inactiveTexture.getWidth()+20,inactiveTexture.getHeight()+textSpacingY);
            this.position=position.subtract(offset);
        }
        @Override
        public void drawCheckClick(SpriteBatch batch){
            batch.draw((screen.isPaused() ? activeTexture : inactiveTexture), position.x(), position.y(), this.width, this.height);
            if(isActive() && Gdx.input.justTouched()){
                onClick();
            }
        }
        @Override
        public void onClick() {
            screen.pause();
        }
    }
    private class PauseMenu{
        RetryButton retryButton;
        ExitGameButton menuButton;

        /**
         * Includes a full screen background and button that should appear while the game is paused.
         */
        public PauseMenu() {
            Texture sample=fetchTexture("buttons/pick_map_active");
            float centerOffset=-sample.getWidth()/2f;

            retryButton=new RetryButton(SCREEN_CENTER.add(new Coordinate(centerOffset,100)),sample,fetchTexture("buttons/pick_map"));
            menuButton=new ExitGameButton(SCREEN_CENTER.add(new Coordinate(centerOffset,-50)),fetchTexture("buttons/menu_active"),fetchTexture("buttons/menu"));
        }

        public void draw(SpriteBatch batch){
            batch.setColor(0f,0f,0f,0.3f);
            batch.draw(forBackgrounds,SCREEN_BOT_LEFT.x(),SCREEN_BOT_LEFT.y(),SCREEN_WIDTH,SCREEN_HEIGHT);
            batch.setColor(Color.WHITE);

            retryButton.drawCheckClick(batch);
            menuButton.drawCheckClick(batch);
        }
        public void dispose(){
            retryButton.dispose();
            menuButton.dispose();
        }
        private class RetryButton extends Clickable{
            /**
            * When clicked screen.reloadMap() will be called.
            */
            public RetryButton(Coordinate position, Texture activeTexture, Texture inactiveTexture) {
                super(position, activeTexture, inactiveTexture);
            }
            @Override
            public void onClick() {
                screen.reloadMap();
                screen.pause();
            }
        }
        private class ExitGameButton extends Clickable{
            /**
             * When clicked the GameScreen will load a new MainMenu screen.
             * @param position
             * @param activeTexture
             * @param inactiveTexture
             */
            public ExitGameButton(Coordinate position, Texture activeTexture, Texture inactiveTexture) {
                super(position, activeTexture, inactiveTexture);
            }
            @Override
            public void onClick() {
                menuButtonPressed=true;
            }
        }
    }

}
