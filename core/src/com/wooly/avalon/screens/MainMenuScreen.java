package com.wooly.avalon.screens;

import static com.wooly.avalon.TDGame.place;

import com.badlogic.gdx.graphics.Color;
import com.wooly.avalon.TDGame;
import com.wooly.avalon.maps.Coordinate;
import com.wooly.avalon.screens.buttons.Clickable;
import com.wooly.avalon.screens.buttons.DifficultyButton;
import com.wooly.avalon.screens.buttons.ExitButton;
import com.wooly.avalon.screens.buttons.LoadScreenButton;
import com.wooly.avalon.screens.other.HeroMenuScaleSlider;
import com.wooly.avalon.screens.other.TextBubble;
import com.wooly.avalon.screens.other.VolumeSlider;

public class MainMenuScreen extends MenuScreen {
    Clickable playButton,exitButton,shopButton,settingsButton;
    public MainMenuScreen(TDGame game){
        super(game);
        System.out.println("LOADING MainMenuScreen");
        Coordinate pos=centerButton(1);
        this.playButton=new LoadScreenButton(this.game,TDGame.fetchTexture("buttons/play_active"),
                TDGame.fetchTexture("buttons/play"),
                place(pos.x(), pos.y()),
                "chooseMap");

        pos=centerButton(2);
        this.shopButton=new LoadScreenButton(this.game,TDGame.fetchTexture("buttons/store_active"),
                TDGame.fetchTexture("buttons/store"),
                place(pos.x(), pos.y()),
                "shop");

        pos=centerButton(3);
        this.settingsButton=new LoadScreenButton(this.game,TDGame.fetchTexture("buttons/settings_active"),
                TDGame.fetchTexture("buttons/settings"),
                place(pos.x(), pos.y()),
                "settings");

        pos=centerButton(4);
        this.exitButton=new ExitButton(TDGame.fetchTexture("buttons/exit_active"),
                TDGame.fetchTexture("buttons/exit"),
                place(pos.x(), pos.y()));

        TDGame.musicHandler.playMusic("menu1");
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        game.batch.begin();
        renderButton(playButton);
        renderButton(exitButton);
        renderButton(shopButton);
        renderButton(settingsButton);
        game.batch.end();
    }

    @Override
    public void dispose() {
        playButton.dispose();
        exitButton.dispose();
        shopButton.dispose();
    }
}
