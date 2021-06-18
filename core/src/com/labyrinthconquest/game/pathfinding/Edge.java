package com.labyrinthconquest.game.pathfinding;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.math.Vector2;

/**
 * Edge in a graph, used in the pathfinding graph
 */
public class Edge implements Connection<INode> {
    private INode from;
    private INode to;
    /**
     * Cost of moving from from node to to node. Here it is just the euclidean distance
     */
    private float cost;

    public Edge(INode from, INode to) {
        this.from = from;
        this.to = to;
        cost = Vector2.dst(from.getX(), from.getY(), to.getX(), to.getY());
    }

    @Override
    public float getCost() {
        return cost;
    }

    @Override
    public INode getFromNode() {
        return from;
    }

    @Override
    public INode getToNode() {
        return to;
    }
}
