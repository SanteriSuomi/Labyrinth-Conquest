package com.labyrinthconquest.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.labyrinthconquest.game.Main;
import com.labyrinthconquest.game.data.Constants;

import java.util.Locale;

/**
 * Represents main menu ui elements in the game, handles ui inputs.
 */
public class MainUi {
    private Stage stage;
    private Table table;
    private Skin skin;
    private Texture background;
    private Image backgroundImage;
    private Label title;
    private TextButton start;
    private TextButton settings;
    private TextButton language;
    private TextButton exit;
    private Main game;
    /**
     * localisation object
     */
    private Localisation local;

    /**
     * Constructor for main menu Ui. Sets up all ui elements.
     * @param tmp main game object
     * @param tmp2 localisation object
     */
    public MainUi(Main tmp, Localisation tmp2) {
        game = tmp;
        local = tmp2;
        stage = new Stage(new StretchViewport(Constants.MENU_VIEWPORT_WIDTH, Constants.MENU_VIEWPORT_HEIGHT));
        skin = new Skin(Gdx.files.internal("LCskin/LabyrinthConquest.json"));
        background = new Texture(Gdx.files.internal("ui/background.png"));
        backgroundImage = new Image();
        backgroundImage.setPosition(0, 0);
        backgroundImage.setDrawable(new TextureRegionDrawable(new TextureRegion(background)));
        backgroundImage.setSize(Constants.MENU_VIEWPORT_WIDTH, Constants.MENU_VIEWPORT_HEIGHT);

        title = new Label("Labyrinth Conquest", skin, "blackbig");
        title.setFontScale(1f);
        start = new TextButton(local.getBundle().get("play"), skin);
        settings = new TextButton(local.getBundle().get("settings"), skin);
        language = new TextButton(local.getBundle().get("language"), skin);
        exit = new TextButton(local.getBundle().get("exit"), skin);

        setInputs();

        table = new Table();
        table.setTransform(true);
        table.setPosition(Constants.MENU_VIEWPORT_WIDTH/2f, Constants.MENU_VIEWPORT_HEIGHT/2f);
        table.add(title).pad(3);
        table.row();
        table.add(start).pad(3);
        table.row();
        table.add(settings).pad(3);
        table.row();
        table.add(language).pad(3);
        table.row();
        table.add(exit).pad(3);
        stage.addActor(backgroundImage);
        stage.addActor(table);
    }

    /**
     * Get satge for drawing this ui in other files
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * Setting up input listeners for ui elements.
     */
    public void setInputs() {
        Gdx.input.setInputProcessor(stage);
        start.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                game.getSounds().playSelect();
                game.setScreen(game.getLevelSelect());
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });

        settings.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                game.getSounds().playSelect();
                game.setScreen(game.getSettingsScreen());
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });

        language.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                game.getSounds().playSelect();
                local.change();
                game.setScreen(game.getMainMenu());
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {

                return true;
            }
        });

        exit.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.exit();
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
    }
}
