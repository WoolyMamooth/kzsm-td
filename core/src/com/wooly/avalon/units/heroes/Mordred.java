package com.wooly.avalon.units.heroes;

import static com.wooly.avalon.TDGame.fetchTexture;

import com.wooly.avalon.maps.Coordinate;
import com.wooly.avalon.units.UnitBuff;
import com.wooly.avalon.units.enemies.Enemy;

import java.util.List;

public class Mordred extends RangedHero{
    private int baseDamage; //necessary for AllConsumingFlames
    /**
     * Mordred is a ranged fire mage that gains more power the more enemies she defeats.
     * @param position
     */
    public Mordred(Coordinate position) {
        super(fetchTexture("heroes/mordred/mordred"),
                position,"Mordred","description here",
                150, 70, 0, 5, 5, 3, 200,
                "arrow",300, "magic");
        baseDamage=damage;
        setAbilities(new HeroAbility[]{
            new AllConsumingFlames(),
            new Lightspeed()
        });
    }
    @Override
    public void update(List<Enemy> enemies, float timeSinceLastFrame) {
        updateExistingProjectiles();
        projectileSpawner.spawnLocation=position;
        if(target!=null && target.shouldBeDead()) abilities[0].activate(); //if the target died get the damage buff from the passive
        super.update(enemies, timeSinceLastFrame);
    }

    @Override
    public void die() {
        super.die();
        damage=baseDamage;
    }

    private class AllConsumingFlames extends HeroAbility{
        int damageIncreasePerStack;
        public AllConsumingFlames(){
            super("All Consuming Flames",fetchTexture("enemies/red_square"));
            damageIncreasePerStack=1;
            setDescription(" Mordred gains power from the enemies she fells.\n After every unit killed she gets "
                    +damageIncreasePerStack+" damage.\n Stacks infinitely, but resets on death.");
        }
        @Override
        public void activate() {
            damage+=damageIncreasePerStack;
        }
    }
    private class Lightspeed extends HeroAbility{
        float buffDuration =10f;
        int movementSpeedModifier=200;
        int attackSpeedModifier=250;
        public Lightspeed(){
            super("Lightspeed",fetchTexture("enemies/red_square"),20);
            setDescription(" Mordred channels the power of fire to accelerate herself,\n for "+buffDuration+" seconds she gains "+
                    movementSpeedModifier+" movement speed\n and "+attackSpeedModifier+" attack speed.");
        }
        @Override
        public void activate() {
            addBuff(new UnitBuff("movementSpeed",movementSpeedModifier, buffDuration));
            addBuff(new UnitBuff("attackSpeed",attackSpeedModifier,buffDuration));
            super.activate();
        }
    }
}
