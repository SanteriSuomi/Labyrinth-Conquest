package com.labyrinthconquest.game.gameobjects;

import com.badlogic.gdx.physics.box2d.World;
import com.labyrinthconquest.game.levels.Level;

/**
 * Represents a dynamic game object (object that primarily moves)
 */
public class DynamicGameObject extends GameObject {
    /**
     * Create a new dynamic game object
     * @param texturePath Texture file path
     * @param world Box2D world
     * @param level Level of the object
     * @param x X position
     * @param y Y position
     * @param width Width of the object
     * @param height Height of the object
     * @param category Collision category
     * @param mask Collision mask
     */
    public DynamicGameObject(String texturePath, World world, Level level, float x, float y, float width, float height, short category, short mask) {
        super(texturePath, world, level, x, y, width, height, category, mask);
    }
}