package com.mygdx.game.maps;

import static com.mygdx.game.TDGame.SCREEN_WIDTH;
import static com.mygdx.game.TDGame.fetchTexture;
import static com.mygdx.game.TDGame.player;
import static com.mygdx.game.maps.TDMap.attemptGoldSpend;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.game.TDGame;
import com.mygdx.game.screens.buttons.Button;
import com.mygdx.game.screens.buttons.CustomButton;
import com.mygdx.game.units.enemies.Enemy;
import com.mygdx.game.units.towers.RangedTower;
import com.mygdx.game.units.towers.SummonerTower;
import com.mygdx.game.units.towers.Tower;
import com.mygdx.game.units.towers.TowerUpgrade;

import java.util.List;

public class TowerSpace extends Button {
    private abstract class TowerMenu{
        CustomButton[] buttons; //the buttons contained in the menu
        float buttonOffsetX,buttonOffsetY=128; //offset for the buttons relative to TowerSpace position
        // y is a constant of -128 for now, 64 is the height of a button, so we place 1 above and 1 inline with the towerspace
        int amountOfButtons;
        int buttonFontsize=35;
        float buttonWidth=128,buttonHeight=64;

        public TowerMenu(float buttonOffsetX) {
            this.buttonOffsetX = buttonOffsetX;
            if(position.x()>SCREEN_WIDTH/2f) this.buttonOffsetX *= -2; //flip the menu to left side if on right side of the screen
        }
        public void draw(SpriteBatch batch){
            for (int i = 0; i < amountOfButtons; i++) {
                buttons[i].drawCheckClick(batch);
            }
        }
        public void dispose(){
            for (int i = 0; i < amountOfButtons; i++) {
                buttons[i].dispose();
            }
        }
    }
    private class TowerBuildMenu extends TowerMenu{
        private class TowerBuildButton extends CustomButton {
            public TowerBuildButton(Coordinate position, String towerName) {
                super(position,towerName,buttonFontsize,Color.WHITE, Color.BLACK,buttonWidth,buttonHeight);
            }
            @Override
            public void onClick() {
                build(this.text);
            }
        }
        /**
         * Handles the building of towers on unoccupied TowerSpaces.
         * @param buttonOffsetX
         */
        public TowerBuildMenu(float buttonOffsetX) {
            super(buttonOffsetX);
            this.amountOfButtons=4; //max number of towers that can be brought to a game. should remain 4 but you never know

            buttons=new TowerBuildButton[amountOfButtons]; //buttons that can be clicked to build the chosen tower
            String[] buildableTowerNames = player.getEquippedTowers(); //we show the equipped towers as options
            for (int i = 0; i < amountOfButtons; i++) {
                buttons[i]=new TowerBuildButton(new Coordinate(position.x()+this.buttonOffsetX, position.y()+this.buttonOffsetY),buildableTowerNames[i]);
                buttonOffsetY-=buttons[i].height; //increment vertical offset so we get a list of buttons
            }
        }
    }
    private class TowerUpgradeMenu extends TowerMenu{
        private class TowerUpradeButton extends CustomButton{
            TowerUpgrade u;
            public TowerUpradeButton(Coordinate position, TowerUpgrade upgrade) {
                super(position, upgrade.getIncrease()+" "+upgrade.stat+":"+upgrade.getLevel()+"/"+upgrade.getMaxLevel()+" - "+ upgrade.getCost(),
                        buttonFontsize,Color.WHITE, Color.BLACK,buttonWidth,buttonHeight);
                this.u =upgrade;
            }
            @Override
            public void onClick() {
                System.out.println("Upgrade button clicked: "+ u.stat+" to level "+(u.getLevel()+1));
                if(!u.isMaxed()) {
                    upgrade(u.stat);
                    if (u.isMaxed()) {
                        this.backgroundColor = Color.RED;
                        this.text = u.stat + ":" + u.getLevel() + "/" + u.getMaxLevel();
                    }else this.text = u.getIncrease() + " " + u.stat + ":" + u.getLevel() + "/" + u.getMaxLevel() + " - " + u.getCost();
                }
            }
        }
        /**
         * Handles the upgrading of the tower on different paths.
         * @param buttonOffsetX
         */
        public TowerUpgradeMenu(float buttonOffsetX,TowerUpgrade[] upgrades) {
            super(buttonOffsetX);
            buttonOffsetY=64;
            this.amountOfButtons=upgrades.length; //maximum number of upgrades a tower can have
            buttons=new TowerUpradeButton[amountOfButtons];
            for (int i = 0; i < amountOfButtons; i++) {
                buttons[i]=new TowerUpradeButton(new Coordinate(position.x()+this.buttonOffsetX, position.y()+this.buttonOffsetY),upgrades[i]);
                buttonOffsetY-=buttons[i].height; //increment vertical offset so we get a list of buttons
            }
        }
    }
    TowerMenu menu; //this is a menu that shows the buildable towers
    //appears when TowerSpace is clicked
    Tower tower; //tower that has been built here
    ShapeRenderer rangeOutline; // used to draw a circle around the tower which shows its range
    int towerBuildID =0; //ID of the tower that will be built
    boolean occupied=false; //defines if a tower has been built here or not
    boolean menuVisible=false; //defines if the menu is visible or not, use in onClick and draw
    public TowerSpace(Coordinate position, Texture activeTexture, Texture inactiveTexture) {
        super(position, activeTexture, inactiveTexture);
        this.menu=new TowerBuildMenu(activeTexture.getWidth());

        rangeOutline=new ShapeRenderer();
        rangeOutline.setColor(Color.BLACK);
    }
    @Override
    public void onClick() {
        towerBuildID =TDMap.lastTowerID+1;
        menuVisible=!menuVisible;
    }

    /**
     * The buildable towers are defined in this method
     * TODO implement gold subtraction for building etc
     * @param towerName what to build
     */
    private void build(String towerName){
        Texture texture=TDGame.fetchTexture("towers/towerTextures/"+towerName);
        TowerUpgrade[] upgrades;
        switch (towerName){
            case "archer":
                upgrades=new TowerUpgrade[]{
                        new TowerUpgrade("range",3,10,10,1.25f),
                        new TowerUpgrade("atkSpeed",3,10,20,1.5f),
                        new TowerUpgrade("damage",3,25,5,2f)
                };
                tower=new RangedTower(texture,position, towerBuildID,"arrow",10,0.5f,upgrades);
                break;
            case "barracks":
                upgrades=new TowerUpgrade[]{
                        new TowerUpgrade("minions",3,1,10,1f)
                };
                tower=new SummonerTower(texture,position,towerBuildID,"guard",5f,1,upgrades);
                break;
            case "None":
            default:
                upgrades = new TowerUpgrade[]{};
                tower=new RangedTower(texture,position, towerBuildID,"arrow",0,1f,upgrades);
                System.out.println("Warning tower "+ towerBuildID +" is set to default");
                break;
        }
        occupied=true;
        TDMap.lastTowerID++; //increment tower IDs to help keep track of them
        activeTexture=fetchTexture("white_square");
        menuVisible=false;

        this.menu=new TowerUpgradeMenu(tower.getTexture().getWidth(),upgrades); //change the menu
    }
    private void upgrade(String upgradeName){
        System.out.println("Upgrading");
        int cost=tower.costOfUpgrade(upgradeName);
        if(attemptGoldSpend(cost)){
            tower.upgrade(upgradeName);
        }
        menuVisible=false;
    }
    @Override
    public void draw(SpriteBatch batch) {
        if(occupied) {
            if(menuVisible) { //if it is selected draw range
                batch.end(); //for some reason batch and ShapeRenderer caused conflicts so we temporarily have to end the batch
                drawTowerRange();
                batch.begin();

                menu.draw(batch);
            }else if(isActive()){ //draw a small outline for the tower while hovering over it
                batch.setColor(1,1,1,0.2f);
                batch.draw(activeTexture,position.x(),position.y(),64,64);
                batch.setColor(Color.WHITE);
            }
            tower.draw(batch);
        }else{
            super.draw(batch);
            if (menuVisible) {
                menu.draw(batch);
            }
        }
        if (isActive() && Gdx.input.justTouched()) {
            onClick();
        }
    }
    private void drawTowerRange(){
        //enables alpha channel for colors
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        rangeOutline.begin(ShapeRenderer.ShapeType.Filled);
        rangeOutline.setColor(0,0,0,0.1f);
        rangeOutline.circle(position.x() + width / 2f, position.y() + width / 2f,tower.getRange());
        rangeOutline.end();

        rangeOutline.begin(ShapeRenderer.ShapeType.Line);
        rangeOutline.setColor(0,0,0,0.7f);
        rangeOutline.circle(position.x() + width / 2f, position.y() + width / 2f, tower.getRange()); //draw range of tower
        rangeOutline.end();
    }

    /**
     * Used for setting the targeting of towers
     * @param enemies
     */
    public void update(List<Enemy> enemies,float timeSinceLastFrame){
        if(tower==null) return;
        tower.update(enemies,timeSinceLastFrame);
    }
    @Override
    public void dispose(){
        super.dispose();
        menu.dispose();
    }
}
