package com.labyrinthconquest.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.labyrinthconquest.game.Main;
import com.labyrinthconquest.game.data.Constants;
import com.labyrinthconquest.game.levels.Level;
import com.labyrinthconquest.game.ui.Localisation;

/**
 * Represents main game screen in the game, handles drawing main game ui and managing game.
 */
public class MainGame extends GameScreen {
    private OrthographicCamera camera;
    private int levelIndex = 1;
    private Level currentLevel;
    public int lvl;
    private Stage stage;
    private Label levelUI;
    private Label characterUI;

    /**
     * Logic for setting level active
     * @param index index for level that is being set
     */
    public void setLevel(int index) {
        currentLevel = getLevelFromIndex(index);
    }

    /**
     * Logic for changing what level you are playing to ui
     * @param text text that says what level you are in
     */
    public void setLevelUIText(CharSequence text) {
        levelUI.setText(text);
    }

    /**
     * Logic for changing what character you are using to ui
     * @param text text that says what character is active
     */
    public void setCharacterUIText(CharSequence text) {
        characterUI.setText(text);
    }

    /**
     * Logic for changing to next level
     */
    public void updateLevel() {
        if (levelIndex >= Constants.LEVELS_COUNT) return;
        currentLevel.dispose();
        currentLevel = getLevelFromIndex(++levelIndex);
        setLevelUIText(currentLevel.getId());
    }

    /**
     * Logic for ending level
     * @param score level complete time
     */
    public void endLevel(int score) {
        game.getLevelEnd().score = score;
        game.getLevelEnd().lvl = lvl;
        game.setScreen(game.getLevelEnd());
    }

    /**
     * Logic for restarting level
     */
    public void restartLevel() {
        currentLevel.dispose();
        currentLevel = getLevelFromIndex(levelIndex);
    }

    /**
     * Getting level data from file
     * @param index level that you are trying to get
     */
    private Level getLevelFromIndex(int index) {
        levelIndex = index;
        Level lvl = new Level(batch, camera, "levels/" + index + ".tmx", Integer.toString(index), 3, this, local);
        setLevelUIText(local.getBundle().get("level")+ ": " + lvl.getId() + " ");
        return lvl;
    }

    /**
     * Constructor for main game screen
     * @param batch spritebatch
     * @param game Main game object
     * @param local localisation object
     */
    public MainGame(SpriteBatch batch, Main game, Localisation local) {
        super(batch, game, local);
    }

    @Override
    /**
     * code that is run when this screen is shown
     */
    public void show() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        setUi();
        lvl++;
        currentLevel = getLevelFromIndex(lvl);
    }

    @Override
    /**
     * Screen renderer
     * @param delta deltatime used for timed events
     */
    public void render(float delta) {
        Gdx.gl.glClearColor(0.3f, 0.6f, 0f, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        currentLevel.update();
        batch.setProjectionMatrix(camera.combined);
        currentLevel.setView(camera);
        camera.update();
        currentLevel.renderTiledMap();
        batch.begin();
        currentLevel.draw();
        batch.end();
        currentLevel.doPhysicsStep();
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    /**
     * Setting all ui element to game screen and managing their input listeners
     */
    public void setUi() {
        stage = new Stage(new StretchViewport(Constants.MENU_VIEWPORT_WIDTH, Constants.MENU_VIEWPORT_HEIGHT));
        Skin skin = new Skin(Gdx.files.internal("LCskin/LabyrinthConquest.json"));
        TextButton back = new TextButton(local.getBundle().get("back"), skin);
        TextButton restart = new TextButton(local.getBundle().get("restart"), skin);
        levelUI = new Label(local.getBundle().get("level") + ":", skin);
        levelUI.setFontScale(1.75f);
        characterUI = new Label(local.getBundle().get("character") + ": ", skin);
        characterUI.setFontScale(1.75f);

        Table table = new Table();
        Table table1 = new Table();
        table.right().top();
        table1.left().top();
        table1.padLeft(-18f);
        table1.setPosition(20f, Constants.MENU_VIEWPORT_HEIGHT - table.getHeight() - 10f);
        table.setPosition(Constants.MENU_VIEWPORT_WIDTH - table.getWidth(), Constants.MENU_VIEWPORT_HEIGHT - table.getHeight());
        table.setTransform(true);
        table1.setTransform(true);
        table.scaleBy(-0.20f);
        table1.scaleBy(-0.17f);
        table1.add(levelUI).pad(3);
        table1.add(characterUI).pad(3);
        table.add(restart).pad(3);
        table.add(back).pad(3);
        stage.addActor(table);
        stage.addActor(table1);

        Gdx.input.setInputProcessor(stage);
        back.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                game.getSounds().playSelect();
                game.getSounds().gamemusic.stop();
                game.getSounds().mainmusic.play();
                game.setScreen(game.getMainMenu());
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        restart.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                game.getSounds().playSelect();
                restartLevel();
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
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
        // Empty
    }

    @Override
    /**
     * Code run when disposing screen
     */
    public void dispose() {
        // Empty
    }
}