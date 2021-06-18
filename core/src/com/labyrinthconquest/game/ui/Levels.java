package com.labyrinthconquest.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.labyrinthconquest.game.Main;
import com.labyrinthconquest.game.data.Constants;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Represents level select ui elements in the game, handles ui inputs.
 */
public class Levels {
    private Stage stage;
    private Table table;
    private Texture background;
    private Image backgroundImage;
    private Skin skin;
    private TextButton tutorial;
    private ArrayList<TextButton> levels;
    private TextButton back;
    private Main game;
    /**
     * localisation object
     */
    private Localisation local;

    /**
     * Constructor for level select Ui. Sets up all ui elements.
     * @param tmp main game object
     * @param tmp2 localisation object
     */
    public Levels(Main tmp, Localisation tmp2) {
        game = tmp;
        local = tmp2;
        levels = new ArrayList<>();
        stage = new Stage(new StretchViewport(Constants.MENU_VIEWPORT_WIDTH, Constants.MENU_VIEWPORT_HEIGHT));
        skin = new Skin(Gdx.files.internal("LCskin/LabyrinthConquest.json"));
        background = new Texture(Gdx.files.internal("ui/background.png"));
        backgroundImage = new Image();
        backgroundImage.setPosition(0, 0);
        backgroundImage.setDrawable(new TextureRegionDrawable(new TextureRegion(background)));
        backgroundImage.setSize(Constants.MENU_VIEWPORT_WIDTH, Constants.MENU_VIEWPORT_HEIGHT);

        tutorial = new TextButton(local.getBundle().get("tutorial"), skin);

        for(int i = 1; i <= Constants.LEVELS_COUNT; i++) {
            levels.add(new TextButton(local.getBundle().get("level") + " " + i, skin));
        }

        back = new TextButton(local.getBundle().get("back"), skin);

        setInputs();

        table = new Table();
        //table.setDebug(true);
        table.setTransform(true);
        table.setPosition(Constants.MENU_VIEWPORT_WIDTH/2f, Constants.MENU_VIEWPORT_HEIGHT/2f);
        table.add(tutorial).pad(3);
        table.add(back).pad(3);
        table.row();
        for(int i = 0; i < levels.size(); i++) {
            table.add(levels.get(i)).pad(3);
            if(i % 2 == 1) {
                table.row();
            }
        }
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
        for(int i = 0; i < Constants.LEVELS_COUNT; i++) {
            final int finalI = i;
            levels.get(i).addListener(new InputListener(){
                @Override
                public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                    game.getSounds().playSelect();
                    game.getSounds().mainmusic.stop();
                    game.getSounds().gamemusic.play();
                    game.getMainGame().lvl = finalI;
                    game.setScreen(game.getMainGame());
                }
                @Override
                public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                    return true;
                }
            });
        }

        tutorial.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                game.getSounds().playSelect();
                game.setScreen(game.getHowToPlay());
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });

        back.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                game.getSounds().playSelect();
                game.setScreen(game.getMainMenu());
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
    }
}
