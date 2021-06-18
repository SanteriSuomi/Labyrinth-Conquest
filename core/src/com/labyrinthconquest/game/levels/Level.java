package com.labyrinthconquest.game.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.labyrinthconquest.game.collisions.ContactManager;
import com.labyrinthconquest.game.data.Constants;
import com.labyrinthconquest.game.gameobjects.GameObject;
import com.labyrinthconquest.game.gameobjects.Player;
import com.labyrinthconquest.game.pathfinding.Graph;
import com.labyrinthconquest.game.pathfinding.INode;
import com.labyrinthconquest.game.screens.MainGame;
import com.labyrinthconquest.game.ui.Localisation;
import com.labyrinthconquest.game.utils.Utilities;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a level in the game, handles loading, creating and updating level instance
 */
public class Level {
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private World world;
    private MainGame game;
    /**
     * Contact handler for this level
     */
    private ContactManager contactManager;
    private TiledMap tiledMap;
    private TiledMapRenderer tiledMapRenderer;
    /**
     * All level game objects
     */
    private List<GameObject> gameObjects;
    private List<Player> players;
    private String id;
    private Localisation local;

    /**
     * Current level score
     */
    private float score;

    private Deque<Body> toDeleteBodies;

    /**
     * Number of players in goal currently
     */
    private int playersInGoal;

    private Vector3 touchPosition;

    /**
     * Player currently being controlled
     */
    private Player activatedPlayer;

    /**
     * Player touch query callback
     */
    private QueryCallback callback;

    /**
     * Pathfinding graphs separate for each player character
     */
    private Graph graphWater;
    private Graph graphClimb;
    private Graph graphStrong;

    /**
     * Time since last path update, used in stopping players moving indefinitely
     */
    private float timeSinceLastPath = Constants.TIME_SINCE_LAST_PATH_CUTOFF;
    private boolean newPlayerChosen;

    /**
     * Maps buttons to doors
     */
    private Map<String, List<Body>> doorButtonMap;

    /**
     * Set tile map renderer to use given camera
     * @param camera Camera to use
     */
    public void setView(OrthographicCamera camera) {
        tiledMapRenderer.setView(camera);
    }

    /**
     * Render current tilemap
     */
    public void renderTiledMap() {
        tiledMapRenderer.render();
    }

    public int getPlayersInGoal() {
        return playersInGoal;
    }

    public void setPlayersInGoal(int i) {
        playersInGoal = i;
    }

    public TiledMap getTiledMap() {
        return tiledMap;
    }

    /**
     * Add a body to-do delete list. Body will be deleted after next update/draw
     * @param body Body to delete in the next update/draw cycle
     */
    public void addBodyToDeleteList(Body body) {
        toDeleteBodies.add(body);
    }

    /**
     * Return doors used by the given button
     * @param button Button
     * @return List of bodies of doors
     */
    public List<Body> getDoorBodies(String button) {
        return doorButtonMap.get(button);
    }

    public String getId() {
        return id;
    }

    public int getScore() {
        return (int)Math.ceil(score);
    }

    /**
     * Create a new instance of a level
     * @param batch Batch to be used for drawing
     * @param camera Camera used for rendering
     * @param tileMapName Name of the tilemap
     * @param id Level identification
     * @param desiredPlayersInGoal Players in this level
     * @param game MainGame instance
     * @param loc Localisation file
     */
    public Level(SpriteBatch batch, OrthographicCamera camera, String tileMapName, String id, final int desiredPlayersInGoal, final MainGame game, Localisation loc) {
        this.game = game;
        this.batch = batch;
        this.camera = camera;
        this.local = loc;
        world = new World(new Vector2(0, 0), true);
        contactManager = new ContactManager(game, this, desiredPlayersInGoal);
        world.setContactListener(contactManager);
        tiledMap = new TmxMapLoader().load(tileMapName);
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, Constants.TILE_MAP_UNIT_SCALE);
        createWalls();
        createPlayersAndGraphs();
        createDoorsAndButtonsMap();
        gameObjects = new ArrayList<>();
        gameObjects.addAll(players);
        this.id = id;
        toDeleteBodies = new ArrayDeque<>();
        /**
         * Create an instance of a query callback, used when querying touch position if there is a player nearby
         */
        callback = new QueryCallback() {
            @Override
            public boolean reportFixture(Fixture fixture) {
                Body hit = fixture.getBody();
                if (fixture.testPoint(touchPosition.x, touchPosition.y) && hit.getUserData() instanceof Player) {
                    activatedPlayer = (Player)hit.getUserData();
                    newPlayerChosen = true;
                    game.setCharacterUIText(local.getBundle().get("character") + ": " + local.getBundle().get(activatedPlayer.getId())  + " ");
                    game.getGame().getSounds().playCaracter();
                    return false;
                }
                return true;
            }
        };
    }

    /**
     * Create level walls
     */
    private void createWalls() {
        transformLayerToBodies(Constants.MAP_WALLS_STATIC, Constants.MAP_WALLS_STATIC, false, Constants.CAT_MAP_WALLS_STATIC);
        transformLayerToBodies(Constants.MAP_WALLS_WATER, Constants.MAP_WALLS_WATER, false, Constants.CAT_MAP_WALLS_WATER);
        transformLayerToBodies(Constants.MAP_WALLS_CLIMB, Constants.MAP_WALLS_CLIMB, false, Constants.CAT_MAP_WALLS_CLIMB);
        transformLayerToBodies(Constants.MAP_WALLS_STRONG, Constants.MAP_WALLS_STRONG, false, Constants.CAT_MAP_WALLS_STRONG);
        transformLayerToBodies(Constants.MAP_GOAL, Constants.MAP_GOAL, true, Constants.CAT_MAP_GOAL);
    }

    /**
     * Transform layer objects to Box2D bodies
     * @param layer Layer to transform
     * @param userData User data to be included in the body
     * @param isSensor Is the body a sensor? (used only for contact events, not collisions)
     * @param category Collision category
     */
    private void transformLayerToBodies(String layer, String userData, boolean isSensor, short category) {
        MapLayer collisionObjectLayer = tiledMap.getLayers().get(layer);
        MapObjects mapObjects = collisionObjectLayer.getObjects();
        Array<RectangleMapObject> rectangleObjects = mapObjects.getByType(RectangleMapObject.class);
        for (RectangleMapObject rectangleObject : rectangleObjects) {
            createBody(scaleRect(rectangleObject.getRectangle(), Constants.TILE_MAP_UNIT_SCALE), userData, isSensor, category);
        }
    }

    /**
     * Create a Box2D body from a map rectangle object
     * @param rect Rectangle to use
     * @param userData User data to be included in the body
     * @param isSensor Is the body a sensor? (used only for contact events, not collisions)
     * @param category Collision category
     * @return Body
     */
    private Body createBody(Rectangle rect, String userData, boolean isSensor, short category) {
        BodyDef myBodyDef = new BodyDef();
        myBodyDef.type = BodyDef.BodyType.StaticBody;
        float x = rect.getX();
        float y = rect.getY();
        float width = rect.getWidth();
        float height = rect.getHeight();
        float centerX = width / 2 + x;
        float centerY = height / 2 + y;
        myBodyDef.position.set(centerX, centerY);
        Body body = world.createBody(myBodyDef);
        body.setUserData(userData);
        PolygonShape groundBox = new PolygonShape();
        groundBox.setAsBox(width / 2 , height / 2 );
        Fixture fixture = body.createFixture(groundBox, 0);
        Filter filter = new Filter();
        filter.categoryBits = category;
        fixture.setFilterData(filter);
        fixture.setSensor(isSensor);
        return body;
    }

    /**
     * Scale tilemap rectangle to fit game size
     * @param rect Map rectangle
     * @param scale Scale to scale by
     * @return Scaled rectangle
     */
    private Rectangle scaleRect(Rectangle rect, float scale) {
        Rectangle rectangle = new Rectangle();
        rectangle.x      = rect.x * scale;
        rectangle.y      = rect.y * scale;
        rectangle.width  = rect.width * scale;
        rectangle.height = rect.height * scale;
        return rectangle;
    }

    /**
     * Create players and their pathfinding graphs
     */
    private void createPlayersAndGraphs() {
        players = new ArrayList<>();
        MapLayer collisionObjectLayer = tiledMap.getLayers().get(Constants.MAP_SPAWNS);
        MapObjects mapObjects = collisionObjectLayer.getObjects();
        Array<RectangleMapObject> spawns = mapObjects.getByType(RectangleMapObject.class);
        Vector2 spawnPointStrong = Utilities.mapRectToGameCoordinates(getSpawn(Constants.MAP_SPAWNPOINT_STRONG, spawns));
        Player strong = new Player(Constants.PLAYER_STRONG_ANIMATION_SHEET, world, this, spawnPointStrong.x, spawnPointStrong.y, Constants.PLAYER_WIDTH,
                Constants.PLAYER_HEIGHT, Constants.PLAYER_STRONG, Constants.CAT_PLAYER_STRONG, Constants.MASK_PLAYER_STRONG);
        Vector2 spawnPointClimber = Utilities.mapRectToGameCoordinates(getSpawn(Constants.MAP_SPAWNPOINT_CLIMBER, spawns));
        Player climb = new Player(Constants.PLAYER_CLIMB_ANIMATION_SHEET, world, this, spawnPointClimber.x, spawnPointClimber.y, Constants.PLAYER_WIDTH,
                Constants.PLAYER_HEIGHT, Constants.PLAYER_CLIMB, Constants.CAT_PLAYER_CLIMB, Constants.MASK_PLAYER_CLIMB);
        Vector2 spawnPointWater = Utilities.mapRectToGameCoordinates(getSpawn(Constants.MAP_SPAWNPOINT_WATER, spawns));
        Player water = new Player(Constants.PLAYER_WATER_ANIMATION_SHEET, world, this, spawnPointWater.x, spawnPointWater.y, Constants.PLAYER_WIDTH,
                Constants.PLAYER_HEIGHT, Constants.PLAYER_WATER, Constants.CAT_PLAYER_WATER, Constants.MASK_PLAYER_WATER);
        players.add(strong);
        players.add(climb);
        players.add(water);
        graphWater = new Graph(tiledMap, Constants.MAP_PATHFINDER_NODES_WATER, world);
        graphClimb = new Graph(tiledMap, Constants.MAP_PATHFINDER_NODES_CLIMB, world);
        graphStrong = new Graph(tiledMap, Constants.MAP_PATHFINDER_NODES_STRONG, world);
    }

    /**
     * Get spawn rectangle from tilemap by name
     * @param name Name of the spawn rectangle
     * @param arr Array of the rectangle objects
     * @return Spawn rectangle
     */
    private Rectangle getSpawn(String name, Array<RectangleMapObject> arr) {
        for(int i = 0; i < arr.size; i++) {
            if (name.equals(arr.get(i).getName())) {
                return arr.get(i).getRectangle();
            }
        }
        return null;
    }

    /**
     * Create a map of the tilemap's doors and buttons so they can be retrieved and manipulated easily
     */
    private void createDoorsAndButtonsMap() {
        doorButtonMap = new HashMap<>();
        MapLayer collisionObjectLayer = tiledMap.getLayers().get(Constants.MAP_DOORS);
        MapObjects mapObjects = collisionObjectLayer.getObjects();
        Array<RectangleMapObject> rectangleObjects = mapObjects.getByType(RectangleMapObject.class);
        for (RectangleMapObject rectangleObject : rectangleObjects) {
            String name = Utilities.getColorName(rectangleObject.getName());
            Body body = createBody(scaleRect(rectangleObject.getRectangle(), Constants.TILE_MAP_UNIT_SCALE), name, false,
                    Constants.CAT_MAP_WALLS_DOORS);
            if (doorButtonMap.containsKey(name)) {
                doorButtonMap.get(name).add(body);
            } else {
                List<Body> newList = new ArrayList<>();
                newList.add(body);
                doorButtonMap.put(name, newList);
            }
        }
        collisionObjectLayer = tiledMap.getLayers().get(Constants.MAP_BUTTONS);
        mapObjects = collisionObjectLayer.getObjects();
        rectangleObjects = mapObjects.getByType(RectangleMapObject.class);
        for (RectangleMapObject rectangleObject : rectangleObjects) {
            createBody(scaleRect(rectangleObject.getRectangle(), Constants.TILE_MAP_UNIT_SCALE), rectangleObject.getName(),
                    false, Constants.CAT_MAP_WALLS_BUTTONS);
        }
    }

    /**
     * Method to update any stuff in this level (before draw)
     */
    public void update() {
        score += Gdx.graphics.getDeltaTime();
        playerTouchInput();
        for(GameObject object : gameObjects) {
            object.update(camera);
        }
    }

    /**
     * Player input
     */
    private void playerTouchInput() {
        if (activatedPlayer != null) {
            timeSinceLastPath += Gdx.graphics.getDeltaTime();
        }
        if(Gdx.input.isTouched() && timeSinceLastPath >= (Constants.TIME_SINCE_LAST_PATH_CUTOFF - 0.01f)) {
            touchPosition = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPosition);
            checkPlayerTouch();
            if (!newPlayerChosen && activatedPlayer != null) {
                timeSinceLastPath = 0;
                setPath();
            }
        }
    }

    /**
     * Get and set path for activated player
     */
    private void setPath() {
        List<INode> path;
        Vector2 touchPosV2 = new Vector2(touchPosition.x, touchPosition.y);
        if (activatedPlayer.getId().equals(Constants.PLAYER_WATER)) {
            path = graphWater.findPath(activatedPlayer.getPosition(), touchPosV2);
        } else if (activatedPlayer.getId().equals(Constants.PLAYER_CLIMB)) {
            path = graphClimb.findPath(activatedPlayer.getPosition(), touchPosV2);
        } else {
            path = graphStrong.findPath(activatedPlayer.getPosition(), touchPosV2);
        }
        if (!path.isEmpty()) {
            activatedPlayer.setMovePath(path);
            game.getGame().getSounds().playRoger();
        }
    }

    /**
     * Check if user touched a player character, in which case set it as the current activated player
     */
    private void checkPlayerTouch() {
        newPlayerChosen = false;
        float offset = Constants.PLAYER_ACTIVATION_OFFSET;
        world.QueryAABB(callback, touchPosition.x - offset, touchPosition.y - offset, touchPosition.x + offset, touchPosition.y + offset);
    }

    /**
     * Draw objects on this level
     */
    public void draw() {
        for(GameObject object : gameObjects) {
            object.draw(batch);
        }
    }

    /**
     * Variable used for smoothing physics
     */
    private double accumulator = 0;

    /**
     * Update level physics timestep
     */
    public void doPhysicsStep() {
        deleteToDeleteBodies();
        float frameTime = Gdx.graphics.getDeltaTime();
        if(frameTime > Constants.MAX_ANIMATION_FRAME_TIME) {
            frameTime = Constants.MAX_ANIMATION_FRAME_TIME;
        }
        accumulator += frameTime;
        while (accumulator >= Constants.COLLISION_TIME_STEP) {
            world.step(Constants.COLLISION_TIME_STEP, 8, 3);
            accumulator -= Constants.COLLISION_TIME_STEP;
        }
    }

    /**
     * Delete all bodies currently marked to be deleted
     */
    private void deleteToDeleteBodies() {
        while (!toDeleteBodies.isEmpty()) {
            world.destroyBody(toDeleteBodies.pollLast());
        }
    }

    /**
     * Dispose current level
     */
    public void dispose() {
        tiledMap.dispose();
        for(GameObject obj : gameObjects) {
            obj.dispose();
        }
    }
}
