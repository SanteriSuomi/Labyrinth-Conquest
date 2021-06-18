package com.labyrinthconquest.game.pathfinding;

/**
 * Interface for making things be able to be part of the pathfinding graph
 */
public interface INode {
    float getX();
    float getY();
    int getIndex();
    void setIndex(int ind);
}
