package com.labyrinthconquest.game.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.labyrinthconquest.game.levels.Level;

/**
 * Represents a base class for game objects
 */
public abstract class GameObject {
    protected Texture texture;
    protected Level level;
    protected World world;
    protected Body body;
    protected float width;
    protected float height;

    public Texture getTexture() {
        return texture;
    }

    public Body getBody() {
        return body;
    }

    public Vector2 getPosition() {
        return body.getPosition();
    }

    public void setPosition(float x, float y) {
        body.setTransform(x, y, 0);
    }

    /**
     * Create a new game object
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
    protected GameObject(String texturePath, World world, Level level, float x, float y, float width, float height, short category, short mask) {
        this.texture = new Texture(Gdx.files.internal(texturePath));
        createBody(world, level, category, mask);
        setPosition(x, y);
        this.width = width;
        this.height = height;
        this.world = world;
        this.level = level;
    }

    /**
     * Create physical body
     * @param world Box2D world
     * @param level Level of the object
     * @param category Collision category of the object
     * @param mask
     */
    public void createBody(World world, Level level, short category, short mask) {
        // Overridable
    }

    /**
     * Update object
     * @param camera Camera used
     */
    public void update(OrthographicCamera camera) {
        // Overridable
    }

    /**
     * Draw object
     * @param batch Batch used in drawing
     */
    public void draw(SpriteBatch batch) {
        batch.draw(getTexture(),
                getPosition().x - width,
                getPosition().y - height,
                width * 2,
                height * 2
        );
    }

    /**
     * Dispose object
     */
    public void dispose() {
        texture.dispose();
    }
}