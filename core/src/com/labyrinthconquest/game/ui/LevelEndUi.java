package com.labyrinthconquest.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.labyrinthconquest.game.Main;
import com.labyrinthconquest.game.data.Constants;

/**
 * Represents Level end ui elements in the game, handles ui inputs.
 */
public class LevelEndUi {
    private Stage stage;
    private Table table;
    private Skin skin;
    private Texture background;
    private Image backgroundImage;
    private Label label;
    private Label time;
    private TextButton next;
    private TextButton back;
    private Main game;
    /**
     * localisation object
     */
    private Localisation local;
    public int lvl;
    public int score;

    /**
     * Constructor for level end Ui. Sets up all ui elements.
     * @param tmp main game object
     * @param tmp2 localisation object
     * @param tmp3 level index
     * @param tmp4 level complete time
     */
    public LevelEndUi(Main tmp, Localisation tmp2, int tmp3, int tmp4) {
        game = tmp;
        local = tmp2;
        lvl = tmp3;
        score = tmp4;
        stage = new Stage(new StretchViewport(Constants.MENU_VIEWPORT_WIDTH, Constants.MENU_VIEWPORT_HEIGHT));
        skin = new Skin(Gdx.files.internal("LCskin/LabyrinthConquest.json"));
        background = new Texture(Gdx.files.internal("ui/background.png"));
        backgroundImage = new Image();
        backgroundImage.setPosition(0, 0);
        backgroundImage.setDrawable(new TextureRegionDrawable(new TextureRegion(background)));
        backgroundImage.setSize(Constants.MENU_VIEWPORT_WIDTH, Constants.MENU_VIEWPORT_HEIGHT);

        label = new Label(local.getBundle().get("level") + " " + lvl + " " + local.getBundle().get("complete"), skin, "blackbig");
        time = new Label( local.getBundle().get("score")+ " : " + score, skin, "blackbig");
        next = new TextButton(local.getBundle().get("next"), skin);
        back = new TextButton(local.getBundle().get("back"), skin);

        setInputs();

        if(lvl > Constants.LEVELS_COUNT - 1) {
            next.setTouchable(Touchable.disabled);
            next.setText(local.getBundle().get("final"));
        }

        table = new Table();
        //table.setDebug(true);
        table.setTransform(true);
        table.setPosition(Constants.MENU_VIEWPORT_WIDTH/2f, Constants.MENU_VIEWPORT_HEIGHT/2f);
        table.row();
        table.add(label).pad(3);
        table.row();
        table.add(time).pad(3);
        table.row();
        table.add(next).pad(3);
        table.row();
        table.add(back).pad(3);
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

        next.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                game.getSounds().playSelect();
                game.getMainGame().updateLevel();
                game.setScreen(game.getMainGame());
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
                game.getSounds().gamemusic.stop();
                game.getSounds().mainmusic.play();
                game.setScreen(game.getLevelSelect());
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
    }
}
