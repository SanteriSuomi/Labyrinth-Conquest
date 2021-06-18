package com.labyrinthconquest.game.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.labyrinthconquest.game.data.Constants;
import com.labyrinthconquest.game.levels.Level;
import com.labyrinthconquest.game.pathfinding.INode;
import com.labyrinthconquest.game.utils.Utilities;

import java.util.List;

/**
 * Represents all the playable characters in the game
 */
public class Player extends DynamicGameObject {
    private String id;

    private List<INode> currentPath;

    /**
     * Current node in the path list
     */
    private int currentNodeIndex;

    /**
     * Whether we're moving
     */
    private boolean moving;

    /**
     * Whether player is in contact
     */
    private boolean isInContact;

    /**
     * Walking animation array
     */
    private Animation<TextureRegion> walkAnimation;

    /**
     * Current animation frame/state time. Used to advance animation frames
     */
    private float animationStateTime;

    /**
     * First frame of the player animation, used for resetting
     */
    private TextureRegion firstFrame;

    /**
     * Current animation frame we're in
     */
    private TextureRegion currentFrame;

    /**
     * Time since we've last moved
     */
    private float timeSinceLastMove;

    public String getId() {
        return id;
    }

    /**
     * Set whether or not this object is in contact of something
     * @param val Value
     */
    public void setIsInContact(boolean val) {
        isInContact = val;
    }

    /**
     * Create a new player object
     * @param texturePath Texture file path
     * @param world Box2D world
     * @param level Level of the object
     * @param x X position
     * @param y Y position
     * @param width Width of the object
     * @param height Height of the object
     * @param id Identification (name) of the player
     * @param category Collision category
     * @param mask Collision mask
     */
    public Player(String texturePath, World world, Level level, float x, float y, float width, float height, String id, short category, short mask) {
        super(texturePath, world, level, x, y, width, height, category, mask);
        this.id = id;
        currentNodeIndex = 0;
        moving = false;

        texture.dispose(); // Dispose inherited texture, it is not needed, as the textures are inside the animation object
        Texture moveSheet = new Texture(Gdx.files.internal(texturePath));
        TextureRegion[][] regions = TextureRegion.split(moveSheet, moveSheet.getWidth() / Constants.PLAYER_ANIMATION_SHEET_COLUMNS,
                moveSheet.getHeight() / Constants.PLAYER_ANIMATION_SHEET_ROWS);
        TextureRegion[] frames = Utilities.transform2DRegionsTo1D(regions, Constants.PLAYER_ANIMATION_SHEET_COLUMNS, Constants.PLAYER_ANIMATION_SHEET_ROWS);
        walkAnimation = new Animation<>(Constants.ANIMATION_FRAME_LENGTH, frames);
        firstFrame = frames[0];
        currentFrame = firstFrame;
    }

    /**
     * Set a new path for the player
     * @param path New path
     */
    public void setMovePath(List<INode> path) {
        moving = true;
        currentPath = path;
        currentNodeIndex = 0;
    }

    /**
     * Cancel current moving & path
     */
    public void stopMove() {
        resetPath();
    }

    /**
     * Update object
     * @param camera Camera used
     */
    @Override
    public void update(OrthographicCamera camera) {
        if (moving) {
            if (checkTimeSinceLastMove()) {
                resetPath();
                return;
            }
            updateAnimationFrame();
            updateMove();
        }
    }

    /**
     * Update and check the time since last node update. If it's higher than the threshold, stop moving
     * @return True threshold reached
     */
    private boolean checkTimeSinceLastMove() {
        timeSinceLastMove += Gdx.graphics.getDeltaTime();
        if (timeSinceLastMove >= Constants.PLAYER_TIME_SINCE_LAST_MOVE_CUT) {
            return true;
        }
        return false;
    }

    /**
     * Update (move forward) the current animation frame time and texture
     */
    private void updateAnimationFrame() {
        animationStateTime += Gdx.graphics.getDeltaTime();
        currentFrame = walkAnimation.getKeyFrame(animationStateTime, true);
        updateFrameDirection();
    }

    /**
     * Update current path and player position along the path
     */
    private void updateMove() {
        INode currentNode = currentPath.get(currentNodeIndex);
        if (Vector2.dst(getPosition().x, getPosition().y, currentNode.getX(), currentNode.getY()) < 0.2f) {
            currentNodeIndex++;
            timeSinceLastMove = 0;
            if (currentNodeIndex >= currentPath.size()) {
                resetPath();
                return;
            }
        }
        Vector2 dir = new Vector2((currentNode.getX() - getPosition().x), (currentNode.getY() - getPosition().y)).nor();
        dir.set(dir.x * Constants.PLAYER_SPEED_MULTIPLIER, dir.y * Constants.PLAYER_SPEED_MULTIPLIER);
        getBody().setLinearVelocity(dir);
    }

    /**
     * Update the current animation's frame direction (where player is moving currently)
     */
    private void updateFrameDirection() {
        if (isInContact) return;
        float horizontalVelocity = getBody().getLinearVelocity().x;
        if (horizontalVelocity > 0 && currentFrame.isFlipX()) {
            currentFrame.flip(true, false);
        } else if (horizontalVelocity < 0 && !currentFrame.isFlipX()) {
            currentFrame.flip(true, false);
        }
    }

    /**
     * Draw object
     * @param batch Batch used in drawing
     */
    @Override
    public void draw(SpriteBatch batch) {
        batch.draw(currentFrame,
                getPosition().x - width,
                getPosition().y - height + Constants.PLAYER_ANIMATION_VERTICAL_DRAW_OFFSET,
                width * 2,
                height * 2
        );
    }

    /**
     * Cancel and reset path variables, and stop player
     */
    public void resetPath() {
        moving = false;
        currentPath = null;
        currentNodeIndex = 0;
        timeSinceLastMove = 0;
        getBody().setAwake(false);
    }

    /**
     * Create Box2D body
     * @param world Box2D world
     * @param level Level of the object
     * @param category Collision category of the object
     * @param mask Collision mask
     */
    @Override
    public void createBody(World world, Level level, short category, short mask) {
        BodyDef myBodyDef = new BodyDef();
        myBodyDef.type = BodyDef.BodyType.DynamicBody;
        Body body = world.createBody(myBodyDef);
        body.setUserData(this);
        FixtureDef playerFixtureDef = new FixtureDef();
        playerFixtureDef.density = 4;
        playerFixtureDef.restitution = 0f;
        playerFixtureDef.friction = 0;
        CircleShape circleshape = new CircleShape();
        circleshape.setRadius(Constants.PLAYER_COLLISION_RADIUS);
        playerFixtureDef.shape = circleshape;
        Fixture fixture = body.createFixture(playerFixtureDef);
        Filter filter = new Filter();
        filter.categoryBits = category;
        filter.maskBits = mask;
        fixture.setFilterData(filter);
        this.body = body;
    }

    /**
     * String representation of player for debug reasons
     * @return String representation of player
     */
    @Override
    public String toString() {
        return "Player{" + "id='" + id + '\'' + '}';
    }
}
