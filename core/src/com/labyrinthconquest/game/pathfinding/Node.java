package com.labyrinthconquest.game.pathfinding;

/**
 * A single in the pathfinding graph
 */
public class Node implements INode {
    private float x;
    private float y;
    /**
     * Index of the node in the graph, used in the algorithm for performance
     */
    private int index;

    public Node(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public void setIndex(int ind) {
        index = ind;
    }
}
