package com.wooly.avalon;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.wooly.avalon.maps.MapLoader;

import java.util.Arrays;
import java.util.Objects;

public class Player {
    String dataFileName="playerdata.tsv";
    FileHandle fileHandle;
    public String[] existingHeroes={"Arthur","Mordred"};
    public String[] existingTowers={"archer","barracks"};
    String[] unlockedTowers;
    String[] unlockedHeroes;
    String[] equippedTowers;
    String equippedHero;
    private int stardust; //global currency used to purchase towers and heroes, gained from completing maps

    /**
     * Keeps track of all data related to the player. ex.: unlocked towers.
     */
    public Player() {
        fileHandle=Gdx.files.local(dataFileName);
        try {
            loadData();
        }catch (GdxRuntimeException exception){
            //this happens on first install because the file hasn't been created yet on specific device
            fileHandle.writeString("1000\t0",false); //stardust, difficulty
            fileHandle.writeString("\nArthur\tNone\tNone\t",true); //base unlocked heroes
            fileHandle.writeString("\narcher\tbarracks\twizard\tfarm\t",true); //base unlocked towers
            loadData();
        }
    }
    /**
     * Loads stardust amount, unlocked and equipped towers, heroes into memory.
     */
    public void loadData(){
        if(!Gdx.files.isLocalStorageAvailable()){
            System.out.println("NO LOCAL STORAGE AVAILABLE");
            return;
        }
        String dataPath=Gdx.files.getLocalStoragePath()+"/"+dataFileName;
        System.out.println("LOADING Player data from "+dataPath);

        //loads player info into memory
        String[] datafile =fileHandle.readString().split("\n");
        stardust=Integer.parseInt(datafile[0].split("\t")[0]);
        MapLoader.GAME_DIFFICULTY=Integer.parseInt(datafile[0].split("\t")[1]);
        unlockedHeroes=datafile[1].split("\t");
        unlockedTowers=datafile[2].split("\t");

        //TODO equip the last used ones
        this.equippedHero = unlockedHeroes[0];
        this.equippedTowers = new String[4];
        for (int i = 0; i < 4; i++) {
            equippedTowers[i]=unlockedTowers[i];
        }
    }
    /**
     * Writes to playerdata.txt to save unlocked towers, heroes and stardust.
     */
    public void saveData(){
        fileHandle.writeString(String.valueOf(stardust)+"\t"+String.valueOf(MapLoader.GAME_DIFFICULTY),false); //delete all info
        fileHandle.writeString("\n",true);
        for (String hero:unlockedHeroes) {
            fileHandle.writeString(hero,true);
            fileHandle.writeString("\t",true);
        }
        fileHandle.writeString("\n",true);
        for (String tower:unlockedTowers) {
            fileHandle.writeString(tower,true);
            fileHandle.writeString("\t",true);
        }
    }

    /**
     * Subtracts cost from the Players stardust. Returns false if not enough, true otherwise.
     */
    public boolean spendStardust(int cost){
        if(cost>stardust){
            System.out.println("Not enough stardust");
            return false;
        }
        stardust-=cost;
        return true;
    }
    /**
     * Player unlocks given hero.
     * @param name
     */
    public void unlockHero(String name){
        for (int i = 0; i < unlockedHeroes.length; i++) {
            if(Objects.equals(unlockedHeroes[i], "None")){
                unlockedHeroes[i]=name;
                return;
            }
        }
        //this should never run
        System.out.println("All heroes unlocked");
    }
    /**
     * Player unlocks given tower.
     * @param name
     */
    public void unlockTower(String name){
        for (int i = 0; i < unlockedTowers.length; i++) {
            if(Objects.equals(unlockedTowers[i], "None")) unlockedTowers[i]=name;
            return;
        }
        //this should never run
        System.out.println("All towers unlocked");
    }
    public void equipHero(String name, boolean unequip){
        if(unequip){
            equippedHero="None";
        }else{
            equippedHero=name;
        }
    }

    /**
     * Equips a tower to the first empty slot found and returns true, if no empty slot is found returns false.
     * @param name
     * @param unequip if true instead unequips the tower
     * @return
     */
    public boolean equipTower(String name, boolean unequip){
        if(unequip){
            for (int i = 0; i < equippedTowers.length; i++) {
                if(Objects.equals(equippedTowers[i], name)){
                    equippedTowers[i]="None";
                    return true;
                }
            }
        }else{
            for (int i = 0; i < equippedTowers.length; i++) {
                if(Objects.equals(equippedTowers[i], "None")){
                    equippedTowers[i]=name;
                    return true;
                }
            }
        }
        return false;
    }
    public String[] getEquippedTowers() {
        return equippedTowers;
    }
    public String getEquippedHero() {
        return equippedHero;
    }
    public String[] getUnlockedTowers() {
        return unlockedTowers;
    }
    public String[] getUnlockedHeroes() {
        return unlockedHeroes;
    }
    public int getStardust() {
        return stardust;
    }
    @Override
    public String toString() {
        return "Player{" +
                "stardust="+stardust+
                ", unlockedTowers=" + Arrays.toString(unlockedTowers) +
                ", unlockedHeroes=" + Arrays.toString(unlockedHeroes) +
                ", equippedTowers=" + Arrays.toString(equippedTowers) +
                ", equippedHero='" + equippedHero + '\'' +
                '}';
    }
}
