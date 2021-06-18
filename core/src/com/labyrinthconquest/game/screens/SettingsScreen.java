package com.labyrinthconquest.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.labyrinthconquest.game.Main;
import com.labyrinthconquest.game.data.Constants;
import com.labyrinthconquest.game.ui.Localisation;

import java.util.Locale;

/**
 * Represents settings screen in the game, handles drawing settings ui and managing settings saving.
 */
public class SettingsScreen extends GameScreen{
    private Stage stage;
    private Table table;
    private Skin skin;
    private Texture background;
    private Image backgroundImage;
    private TextButton back;
    private TextButton setName;
    private TextArea name;
    private Label volumeInt;
    private Slider volume;
    /**
     * place where settings are saved
     */
    private Preferences prefs;

    /**
     * Constructor for settings screen
     * @param batch spritebatch
     * @param game Main game object
     * @param local localisation object
     */
    public SettingsScreen(SpriteBatch batch, Main game, Localisation local) {
        super(batch, game, local);
    }

    @Override
    /**
     * code that is run when this screen is shown. Ui setup and data fetch are handled here.
     */
    public void show() {
        prefs = Gdx.app.getPreferences(Constants.PREFERENCES_FILE_NAME);
        stage = new Stage(new StretchViewport(Constants.MENU_VIEWPORT_WIDTH, Constants.MENU_VIEWPORT_HEIGHT));
        skin = new Skin(Gdx.files.internal("LCskin/LabyrinthConquest.json"));

        background = new Texture(Gdx.files.internal("ui/background.png"));
        backgroundImage = new Image();
        backgroundImage.setPosition(0, 0);
        backgroundImage.setDrawable(new TextureRegionDrawable(new TextureRegion(background)));
        backgroundImage.setSize(Constants.MENU_VIEWPORT_WIDTH, Constants.MENU_VIEWPORT_HEIGHT);

        String user = prefs.getString(Constants.USERNAME_PREFS_LOCATION);
        if (user == null || user.isEmpty()) {
            user = Constants.USERNAME_PREFS_DEFAULT;
        }
        name = new TextArea(user, skin);
        setName = new TextButton(local.getBundle().get("name"), skin);
        volume = new Slider(0f, 100f, 1f, false, skin);
        int val = (int)(prefs.getFloat(Constants.VOLUME_PREFS_LOCATION) * 100);
        if (val == 0) {
            val = 50;
        }
        volume.setValue(val);
        volumeInt = new Label(local.getBundle().get("volume") + " : " + val, skin, "big");
        back = new TextButton(local.getBundle().get("back"), skin);

        setInputs();

        table = new Table();
        //table.setDebug(true);
        table.setTransform(true);
        table.setPosition(Constants.MENU_VIEWPORT_WIDTH/2f, Constants.MENU_VIEWPORT_HEIGHT/2f);
        table.add(name).width(300).height(70).pad(3);
        table.row();
        table.add(setName).pad(3);
        table.row();
        table.add(volumeInt).pad(3);
        table.row();
        table.add(volume).pad(3);
        table.row();
        table.add(back).pad(3);
        stage.addActor(backgroundImage);
        stage.addActor(table);
    }

    @Override
    /**
     * Screen renderer
     * @param delta deltatime used for timed events
     */
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    /**
     * Setting input listeners
     */
    public void setInputs() {
        Gdx.input.setInputProcessor(stage);
        back.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                game.getSounds().playSelect();
                game.setScreen(game.getMainMenu());
                saveSettings();
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        volume.addListener(new InputListener(){
            @Override
            public boolean handle(Event event) {
                volumeInt.setText(local.getBundle().get("volume") + " : " + (int)volume.getValue());
                game.getSounds().mainmusic.setVolume(volume.getValue() / 100);
                game.getSounds().gamemusic.setVolume(volume.getValue() / 100);
                game.getSounds().val = volume.getValue();
                return false;
            }
        });
        setName.addListener(new InputListener() {
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                game.getSounds().playSelect();
                saveSettings();
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
    }

    /**
     * Settings saving
     */
    private void saveSettings() {
        prefs.putFloat(Constants.VOLUME_PREFS_LOCATION, volume.getValue() / 100);
        prefs.putString(Constants.USERNAME_PREFS_LOCATION, name.getText() != null ? name.getText() : Constants.USERNAME_PREFS_DEFAULT);
        prefs.flush();
    }

    @Override
    /**
     * Code run after screen resizing
     */
    public void resize(int width, int height) {

    }

    @Override
    /**
     * Code run when pausing screen
     */
    public void pause() {
        saveSettings();
    }

    @Override
    /**
     * Code run when resuming to screen
     */
    public void resume() {

    }

    @Override
    /**
     * Code run when hiding screen
     */
    public void hide() {
        saveSettings();
    }

    @Override
    /**
     * Code run when disposing screen
     */
    public void dispose() {

    }
}
