package com.labyrinthconquest.game.pathfinding;

import com.badlogic.gdx.math.Vector2;

/**
 * Heuristic function for pathfinding
 */
public class GraphHeuristic implements com.badlogic.gdx.ai.pfa.Heuristic<INode> {
    @Override
    public float estimate(INode current, INode goal) {
        return Vector2.dst(current.getX(), current.getY(), goal.getX(), goal.getY());
    }
}
