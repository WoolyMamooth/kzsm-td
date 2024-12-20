package com.wooly.avalon.units.towers;

import com.badlogic.gdx.graphics.Texture;
import com.wooly.avalon.TDGame;
import com.wooly.avalon.maps.Coordinate;
import com.wooly.avalon.units.AlliedUnit;
import com.wooly.avalon.units.enemies.Enemy;

import java.util.List;

public class Summon extends AlliedUnit {
    /**
     * Summons are spawned by SummonerTowers.
     * @param texture
     * @param position
     * @param movementSpeed
     * @param maxHp
     * @param armor
     * @param magicResistance
     * @param damage
     * @param attackDelay
     */
    public Summon(Texture texture, Coordinate position, Coordinate spawnOffset,float movementSpeed, int maxHp, int armor, int magicResistance, int damage, float attackDelay, float searchRange, String damageType) {
        super(texture, position, movementSpeed, maxHp, armor, magicResistance, damage, attackDelay, searchRange, damageType);
        this.position=this.position.add(spawnOffset); //we add a random offset so they don't just spawn on each other
        if(TDGame.CustomRandom.nextBoolean()){
            turnAround();
        }
    }
    /**
     * @param enemies should be the list of enemies in the towers range.
     * @param timeSinceLastFrame
     */
    public void update(List<Enemy> enemies, float timeSinceLastFrame){
        super.update(enemies,timeSinceLastFrame);
    }
}
