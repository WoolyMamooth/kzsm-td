package com.wooly.avalon.units;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.wooly.avalon.maps.Coordinate;

public class MovableUnit extends DrawableUnit{
    float movementSpeed;

    /**
     * Adds movement logic to DrawableUnit, used for anything that has a texture and moves.
     * @param texture
     * @param position
     * @param movementSpeed
     */
    public MovableUnit(Texture texture, Coordinate position,float movementSpeed) {
        super(texture, position);
        this.movementSpeed=movementSpeed;
    }

    protected void move(Coordinate goal){
        if(goal==null) {
            movementSpeed=0;
            super.dispose();
            return;
        }

        //turn the unit around if needed
        if(!facingLeft && goal.x()<position.x()) turnAround();
        if(facingLeft && goal.x()> position.x()) turnAround();

        Coordinate movementDirection=goal.subtract(position).normalize(); //get a unit vector pointing to goal
        position=position.add(movementDirection.multiplyByScalar(movementSpeed* Gdx.graphics.getDeltaTime())); //move
    }

    //checks if the unit has reached a given coordinate
    protected boolean atCoordinate(Coordinate coordinate){
        if(coordinate==null || position==null) return true;
        if(
                position.x()>coordinate.x()-width/2f &&
                        position.y()>coordinate.y()-height/2f &&
                        position.x()<coordinate.x()+width/2f &&
                        position.y()<coordinate.y()+height/2f
        ) return true;
        return false;
    }
    public float getMovementSpeed() {
        return movementSpeed;
    }
}