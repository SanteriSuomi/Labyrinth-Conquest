package com.labyrinthconquest.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.labyrinthconquest.game.Main;
import com.labyrinthconquest.game.ui.HowToPlayUi;
import com.labyrinthconquest.game.ui.Localisation;

/**
 * Represents tutorial screen in the game, handles drawing how to play ui.
 */
public class HowToPlay extends GameScreen {
    /**
     * Ui data for this screen
     */
    private HowToPlayUi ui;

    /**
     * Constructor for tutorial screen
     * @param batch spritebatch
     * @param game Main game object
     * @param local localisation object
     */
    public HowToPlay(SpriteBatch batch, Main game, Localisation local) {
        super(batch, game, local);
    }

    @Override
    /**
     * code that is run when this screen is shown
     */
    public void show() {
        ui = new HowToPlayUi(game, local);
    }

    @Override
    /**
     * Screen renderer
     * @param delta deltatime used for timed events
     */
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        ui.getStage().act(Gdx.graphics.getDeltaTime());
        ui.getStage().draw();
    }

    @Override
    /**
     * Code run after screen resizing
     */
    public void resize(int width, int height) {
        // Empty
    }

    @Override
    /**
     * Code run when pausing screen
     */
    public void pause() {
        // Empty
    }

    @Override
    /**
     * Code run when resuming to screen
     */
    public void resume() {
        // Empty
    }

    @Override
    /**
     * Code run when hiding screen
     */
    public void hide() {
        ui.getStage().dispose();
    }

    @Override
    /**
     * Code run when disposing screen
     */
    public void dispose() {
        // Empty
    }
}
