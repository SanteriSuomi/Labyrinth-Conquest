package com.labyrinthconquest.game.collisions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.labyrinthconquest.game.data.Constants;
import com.labyrinthconquest.game.gameobjects.Player;
import com.labyrinthconquest.game.levels.Level;
import com.labyrinthconquest.game.screens.MainGame;
import com.labyrinthconquest.game.utils.Utilities;

import java.util.List;

/**
 * Handles individual level collision events.
 */
public class ContactManager implements ContactListener {
    private MainGame game;
    private Level level;
    /**
     * Number of player needed for the goal reached event to happen
     */
    private int desiredPlayersInGoal;

    public ContactManager(MainGame game, Level level, int desiredPlayersInGoal) {
        this.game = game;
        this.level = level;
        this.desiredPlayersInGoal = desiredPlayersInGoal;
    }

    @Override
    public void beginContact(Contact contact) {
        try {
            startContactEvent(contact);
        } catch (Exception ex) {
            // Ignore collision exceptions
        }
    }

    @Override
    public void endContact(Contact contact) {
        try {
            endContactEvent(contact);
        } catch (Exception ex) {
            // Ignore collision exceptions
        }
    }

    /**
     * Called when end contact has started
     * @param contact Contact of the event
     */
    private void endContactEvent(Contact contact) {
        Body body1 = contact.getFixtureA().getBody();
        Body body2 = contact.getFixtureB().getBody();
        String wall = (String)body1.getUserData();
        Player player = (Player)body2.getUserData();
        if (wall.equals(Constants.MAP_GOAL)) {
            level.setPlayersInGoal(level.getPlayersInGoal() - 1);
        }
        player.setIsInContact(false);
    }

    /**
     * Called when start contact has started
     * @param contact Contact of the event
     */
    private void startContactEvent(Contact contact) {
        Body body1 = contact.getFixtureA().getBody();
        Body body2 = contact.getFixtureB().getBody();
        String name = (String)body1.getUserData();
        Player player = (Player)body2.getUserData();
        if (name.equals(Constants.MAP_WALLS_STRONG) && player.getId().equals(Constants.PLAYER_STRONG)) {
            removeTileAndBody(contact, body1, Constants.MAP_WALLS_TEXTURES);
        } else if (name.equals(Constants.MAP_GOAL)) {
            level.setPlayersInGoal(level.getPlayersInGoal() + 1);
            if (level.getPlayersInGoal() >= desiredPlayersInGoal) {
                onGoalReachedEvent();
            }
        } else if (name.contains(Constants.MAP_BUTTON)) {
            game.getGame().getSounds().playButton();
            removeTilesAndDoors(level.getDoorBodies(Utilities.getColorName(body1.getUserData().toString())), Constants.MAP_WALLS_TEXTURES);
            removeTileAndBody(contact, body1, Constants.MAP_WALLS_TEXTURES);
            player.stopMove();
        }
        player.setIsInContact(true);
    }

    /**
     * Remove a tile and corresponding Box2D body
     * @param contact Contact from which collision position is retrieved from
     * @param body Body to be deleted
     * @param layer Layer in which the tile is removed from
     */
    private void removeTileAndBody(Contact contact, Body body, String layer) {
        TiledMapTileLayer tiledLayer = (TiledMapTileLayer)level.getTiledMap().getLayers().get(layer);
        Vector2 collPos = contact.getFixtureA().getBody().getPosition();
        TiledMapTileLayer.Cell cell = tiledLayer.getCell((int)collPos.x, (int)collPos.y);
        cell.setTile(null);
        level.addBodyToDeleteList(body);
    }

    /**
     * Remove tiles on bodies positions inside layer
     * @param bodies Bodies to be deleted and whose corresponding tiles will be deleted
     * @param layer Layer in which the tiles are removed from
     */
    private void removeTilesAndDoors(List<Body> bodies, String layer) {
        TiledMapTileLayer tiledLayer = (TiledMapTileLayer)level.getTiledMap().getLayers().get(layer);
        for(Body body : bodies) {
            Vector2 pos = body.getPosition();
            TiledMapTileLayer.Cell cell = tiledLayer.getCell((int)pos.x, (int)pos.y);
            cell.setTile(null);
            level.addBodyToDeleteList(body);
        }
    }

    /**
     * Called when all the characters reach the goal
     */
    private void onGoalReachedEvent() {
        game.endLevel(level.getScore());
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        // Empty
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        // Empty
    }
}
