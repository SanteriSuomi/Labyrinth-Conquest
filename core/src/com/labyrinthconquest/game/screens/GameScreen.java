package com.labyrinthconquest.game.screens;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Screen;
import com.labyrinthconquest.game.Main;
import com.labyrinthconquest.game.ui.Localisation;

/**
 * Base class for all the different screens in the game
 */
public abstract class GameScreen implements Screen {
    protected SpriteBatch batch;
    protected Main game;
    protected Localisation local;

    public Main getGame() {
        return game;
    }

    /**
     * Create a new game screen
     * @param batch Batch used for drawing
     * @param game Main instance
     * @param local Localisation file
     */
    protected GameScreen(SpriteBatch batch, Main game, Localisation local) {
        this.batch = batch;
        this.game = game;
        this.local = local;
    }
}