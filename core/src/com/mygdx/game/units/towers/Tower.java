package com.mygdx.game.units.towers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.maps.Coordinate;
import com.mygdx.game.units.Attacker;
import com.mygdx.game.units.DrawableUnit;
import com.mygdx.game.units.Spawner;
import com.mygdx.game.units.enemies.Enemy;
import com.mygdx.game.units.projectiles.Projectile;
import com.mygdx.game.units.projectiles.ProjectileSpawner;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class Tower extends DrawableUnit implements Attacker {
    float timeSinceLastAttack=0;
    int towerSpawnID; //keeps track of the order towers were built in
    float attackDelay; //defines how much time should pass between attacks
    float range; //defines how far the tower will target
    public TowerUpgrade[] upgrades;

    public Tower(Texture texture, Coordinate position,int towerSpawnID,float attackDelay,TowerUpgrade[] upgrades) {
        super(texture, position);
        this.towerSpawnID=towerSpawnID;
        this.range=200;
        this.attackDelay=attackDelay;
        this.upgrades=upgrades;
    }

    public abstract void attack();
    public abstract void update(List<Enemy> enemies,float timeSinceLastFrame);
    @Override
    public void draw(SpriteBatch batch){
        super.draw(batch);
    }
    /**
     * Increases the level of the specified stat by one.
     */
    public void upgrade(String stat){
        for(TowerUpgrade u:this.upgrades){
            if(Objects.equals(u.stat, stat)){
                u.levelUp();
                applyUpgrade(u);
            }
        }
    }
    /**
     * Returns the cost of the upgrade if it can be upgraded, 0 otherwise.
     * @param stat
     */
    public int costOfUpgrade(String stat){
        //find the upgrade we need to level up
        for(TowerUpgrade u:this.upgrades){
            if(Objects.equals(u.stat, stat)){
                if(!u.isMaxed()){ //if it wasn't maxed yet
                    return u.getCost();
                }
                break;
            }
        }
        return 0;
    }

    /**
     * This increases the stat of the tower, all possible upgrades have to be defined here!
     */
    protected void applyUpgrade(TowerUpgrade u){
        switch (u.stat){
            case "range":
                this.range+=u.getIncrease();
                break;
            case "atkSpeed":
            case "summonSpeed":
                //10 attackspeed = -0.1f delay between attacks
                this.attackDelay-=u.getIncrease()/100f;
                break;
            default:
                System.out.println("No such upgrade");
                break;
        }
    }
    public int getTowerSpawnID() {
        return towerSpawnID;
    }
    public float getAttackDelay() {
        return attackDelay;
    }
    public float getRange() {
        return range;
    }
    @Override
    public String toString() {
        return "Tower{" +
                "towerSpawnID=" + towerSpawnID +
                ", attackDelay=" + attackDelay +
                ", range=" + range +
                ", position=" + position +
                '}';
    }
}