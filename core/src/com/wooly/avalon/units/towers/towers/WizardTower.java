package com.wooly.avalon.units.towers.towers;

import com.wooly.avalon.TDGame;
import com.wooly.avalon.maps.Coordinate;
import com.wooly.avalon.units.towers.RangedTower;
import com.wooly.avalon.units.towers.TowerUpgrade;

public class WizardTower extends RangedTower {
    public WizardTower(Coordinate position,int towerSpawnID) {
        super(TDGame.fetchTexture("towers/towerTextures/archer"),
                position, towerSpawnID, 150f, "wizard_fireball", 20, 1.2f,
                new TowerUpgrade[]{
                    new TowerUpgrade("damage",5,10,40,1.25f),
                    new TowerUpgrade("range", 5, 10, 30, 1.25f)
                }, "magic");
        setName("wizard");
        setDescription("Doesn't shoot very fast, but deals a good amount\n of magic damage in a small area.");
    }
}
