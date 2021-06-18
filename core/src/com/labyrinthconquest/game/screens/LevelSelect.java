package com.labyrinthconquest.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.labyrinthconquest.game.Main;
import com.labyrinthconquest.game.actors.MenuActor;
import com.labyrinthconquest.game.data.Constants;
import com.labyrinthconquest.game.ui.Levels;
import com.labyrinthconquest.game.ui.Localisation;
import com.labyrinthconquest.game.ui.MainUi;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Represents level select screen in the game, handles drawing level select ui.
 */
public class LevelSelect extends GameScreen {
    /**
     * Ui data for this screen
     */
    private Levels ui;

    /**
     * Constructor for level select screen
     * @param batch spritebatch
     * @param game Main game object
     * @param local localisation object
     */
    public LevelSelect(SpriteBatch batch, Main game, Localisation local) {
        super(batch, game, local);
    }

    @Override
    /**
     * code that is run when this screen is shown
     */
    public void show() {
        ui = new Levels(game, local);
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