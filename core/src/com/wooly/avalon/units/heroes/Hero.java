package com.wooly.avalon.units.heroes;

import static com.wooly.avalon.TDGame.SCREEN_BOT_LEFT;
import static com.wooly.avalon.TDGame.SCREEN_BOT_RIGHT;
import static com.wooly.avalon.TDGame.SCREEN_HEIGHT;
import static com.wooly.avalon.TDGame.fetchTexture;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.wooly.avalon.maps.Coordinate;
import com.wooly.avalon.screens.TextBubble;
import com.wooly.avalon.screens.buttons.Button;
import com.wooly.avalon.units.enemies.Enemy;
import com.wooly.avalon.units.AlliedUnit;

import java.util.List;

public abstract class Hero extends AlliedUnit {
    public String name;
    public String description;
    float maxDeathTimer=10f; //the duration for which the hero will be down for if they die
    float deathTimer=0f;
    Texture deadTexture; //texture to be displayed on death
    HeroSelectorButton button;
    protected boolean selected=false;
    boolean moving=false;
    Coordinate goal;
    ShapeRenderer rangeOutline;
    HeroAbility[] abilities;
    HeroAbilityMenu menu;

    /**
     * Heroes are units that can be directly controlled by the player.
     * They can be moved around on the board and they have unique abilities that can be activated using a menu.
     *
     * @param texture
     * @param position
     * @param movementSpeed
     * @param maxHp
     * @param armor
     * @param magicResistance
     * @param damage
     * @param attackDelay
     * @param searchRange
     * @param damageType
     */
    public Hero(Texture texture, Coordinate position,String name, String description, float movementSpeed, int maxHp, int armor, int magicResistance, int damage, float attackDelay, float searchRange, String damageType) {
        super(texture, position, movementSpeed, maxHp, armor, magicResistance, damage, attackDelay, searchRange, damageType);
        this.name=name;
        this.description=description;
        button=new HeroSelectorButton(position);
        turnAround();
        deadTexture=fetchTexture("heroes/dead_hero");

        rangeOutline=new ShapeRenderer();
        rangeOutline.setColor(Color.BLACK);
    }

    @Override
    public void update(List<Enemy> enemies, float timeSinceLastFrame) {
        if(!isDead()) {
            button.update(position);
            updateCooldowns(timeSinceLastFrame);
            if (shouldBeDead()) {
                die(); //TODO heroes will respawn, but for now they just get deleted
                return;
            }
            if (moving) { //TODO should only move when map is clicked, not menu
                if (target != null) {
                    target.setTarget(null);
                    target = null;
                }
                move(goal);
                if (atCoordinate(goal)) {
                    moving = false;
                }
                checkMovement();
            } else {
                searchCenterPosition = position;
                checkMovement();
                super.update(enemies, timeSinceLastFrame);
            }
        }else{
            //the hero is dead, decrease death timer
            deathTimer-=timeSinceLastFrame;
            if (deathTimer<=0){
                //if the timer is up, resurrect
                currentHp=maxHp;
            }
        }
    }
    protected void updateCooldowns(float timeSinceLastFrame){
        for (HeroAbility ability:abilities) {
            ability.updateCooldown(timeSinceLastFrame);
        }
    }
    @Override
    public void die() {
        target=null;
        deathTimer=maxDeathTimer;
    }
    @Override
    public void draw(SpriteBatch batch) {
        if(!isDead()) {
            super.draw(batch);
            button.drawCheckClick(batch);
            menu.draw(batch);
            if (selected) {
                batch.end();
                drawRange(searchRange,true);
                drawRange(searchRange,false);
                batch.begin();
            }
        }else{
            batch.draw(deadTexture,position.x(),position.y());
        }
    }
    private boolean isDead(){
        return deathTimer>0;
    }
    /**
     * Draws a circle around the hero at the specified range.
     * @param range
     * @param filled if true it will be filled with an almost transparent grey color
     *               else it will be a black line
     */
    protected void drawRange(float range,boolean filled){
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        if(filled) {
            rangeOutline.begin(ShapeRenderer.ShapeType.Filled);
            rangeOutline.setColor(0, 0, 0, 0.1f);
            rangeOutline.circle(position.x() + width / 2f, position.y() + width / 2f, range);
            rangeOutline.end();
        }else {
            rangeOutline.begin(ShapeRenderer.ShapeType.Line);
            rangeOutline.setColor(0, 0, 0, 0.7f);
            rangeOutline.circle(position.x() + width / 2f, position.y() + width / 2f, range);
            rangeOutline.end();
        }
    }
    private void checkMovement(){
        if (selected && Gdx.input.justTouched()) {
            selected=false;
            int x = Gdx.input.getX();
            int y = Gdx.input.getY();
            goal = new Coordinate(x, SCREEN_HEIGHT - y);
            moving=true;
        }
    }
    public HeroAbility[] getAbilities() {
        return abilities;
    }
    protected class HeroSelectorButton extends Button {
        private boolean onHero=true;
        /**
         * Used for selecting the hero, floats on top of them and is only visible when clicked.
         * @param position
         */
        public HeroSelectorButton(Coordinate position) {
            super(position, fetchTexture("white_square"), fetchTexture("white_square"));
        }
        public HeroSelectorButton(Coordinate position,Texture texture) {
            super(position, fetchTexture("white_square"), texture);
            onHero=false;
        }
        public void update(Coordinate position){
            this.position=position;
        }
        @Override
        public void draw(SpriteBatch batch) {
            if(!onHero) {
                batch.draw(inactiveTexture,position.x(),position.y(),width,height);
            }
            if (isActive()) {
                batch.setColor(1, 1, 1, 0.2f);
                batch.draw(activeTexture, position.x(), position.y(), width, height);
                batch.setColor(Color.WHITE);
            }
        }
        @Override
        public void onClick() {
            selected=!selected;
        }
    }
    protected class HeroAbilityMenu{
        protected class HeroAbilityButton extends Button {
            HeroAbility ability;
            /**
             * Used for activating the abilities of heroes.
             * @param position
             * @param ability
             */
            public HeroAbilityButton(Coordinate position,HeroAbility ability) {
                super(position, fetchTexture("white_square"), ability.icon);
                this.ability=ability;
            }
            @Override
            public void onClick() {
                if(!ability.isPassive && !ability.onCooldown()) ability.activate();
            }
        }
        protected class HeroAbilityInfo extends Button{
            boolean toggled=false;
            TextBubble textBubble;
            /**
             * When clicked a description of the ability will be displayed.
             * @param position
             */
            public HeroAbilityInfo(Coordinate position, String text) {
                super(position, fetchTexture("white_square"), fetchTexture("enemies/red_square"));
                textBubble=new TextBubble(SCREEN_BOT_LEFT,text,20,Color.WHITE,600,new Color(0,0,0,0.5f));
            }
            @Override
            public void draw(SpriteBatch batch) {
                super.draw(batch);
                if (toggled) textBubble.draw(batch);
            }
            @Override
            public void onClick() {
                toggled=!toggled;
            }
        }
        HeroSelectorButton selectorButton;
        HeroAbilityButton[] abilityButtons;
        HeroAbilityInfo[] abilityInfos;
        int abilityNum;

        /**
         * Contains buttons for every ability the hero has plus the left-most button can be used to select the hero
         * as if the hero itself was been clicked.
         * @param abilities
         */
        public HeroAbilityMenu(HeroAbility[] abilities){
            abilityNum=abilities.length;

            int iconWidth=abilities[0].icon.getWidth();
            int iconHeight=abilities[0].icon.getHeight();

            abilityButtons=new HeroAbilityButton[abilityNum];
            abilityInfos=new HeroAbilityInfo[abilityNum];

            Coordinate buttonPosition=SCREEN_BOT_RIGHT.subtract(new Coordinate(iconWidth*(abilityNum+1)+10,-10));

            selectorButton=new HeroSelectorButton(buttonPosition,texture);
            buttonPosition=buttonPosition.add(new Coordinate(iconWidth,0));

            for (int i = 0; i < abilityNum; i++) {
                abilityButtons[i]=new HeroAbilityButton(buttonPosition,abilities[i]);
                abilityInfos[i]=new HeroAbilityInfo(buttonPosition.add(new Coordinate(0,iconHeight)),abilities[i].description);

                buttonPosition=buttonPosition.add(new Coordinate(iconWidth,0));
            }
        }
        public void draw(SpriteBatch batch){
            selectorButton.drawCheckClick(batch);
            for (int i = 0; i < abilityNum; i++) {
                abilityButtons[i].drawCheckClick(batch);
                abilityInfos[i].drawCheckClick(batch);
            }
        }
    }
}
