package com.labyrinthconquest.game.ui;

import com.badlogic.gdx.Gdx;
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
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.labyrinthconquest.game.Main;
import com.labyrinthconquest.game.data.Constants;

/**
 * Represents Tutorial ui elements in the game, handles ui inputs.
 */
public class HowToPlayUi {
    private Stage stage;
    private Table table;
    private Skin skin;
    private Texture background;
    private Image backgroundImage;
    private Label label;
    private Label first;
    private Label second;
    private Label third;
    private Label fourth;
    private Label fifth;
    private Label sixth;
    private Label seventh;
    private Label eight;
    private TextButton back;
    private Main game;
    /**
     * localisation object
     */
    private Localisation local;

    /**
     * Constructor for tutorial Ui. Sets up all ui elements.
     * @param tmp main game object
     * @param tmp2 localisation object
     */
    public HowToPlayUi(Main tmp, Localisation tmp2) {
        game = tmp;
        local = tmp2;
        stage = new Stage(new StretchViewport(Constants.MENU_VIEWPORT_WIDTH, Constants.MENU_VIEWPORT_HEIGHT));
        skin = new Skin(Gdx.files.internal("LCskin/LabyrinthConquest.json"));
        background = new Texture(Gdx.files.internal("ui/background.png"));
        backgroundImage = new Image();
        backgroundImage.setPosition(0, 0);
        backgroundImage.setDrawable(new TextureRegionDrawable(new TextureRegion(background)));
        backgroundImage.setSize(Constants.MENU_VIEWPORT_WIDTH, Constants.MENU_VIEWPORT_HEIGHT);

        label = new Label(local.getBundle().get("tutorial"), skin, "blackbig");
        first = new Label(local.getBundle().get("first"), skin);
        second = new Label(local.getBundle().get("second"), skin);
        third = new Label(local.getBundle().get("third"), skin);
        fourth = new Label(local.getBundle().get("fourth"), skin);
        fifth = new Label(local.getBundle().get("fifth"), skin);
        sixth = new Label(local.getBundle().get("sixth"), skin);
        seventh = new Label(local.getBundle().get("seventh"), skin);
        eight = new Label(local.getBundle().get("eight"), skin);
        back = new TextButton(local.getBundle().get("back"), skin);

        setInputs();

        table = new Table();
        //table.setDebug(true);
        table.setTransform(true);
        table.setPosition(Constants.MENU_VIEWPORT_WIDTH/2f, Constants.MENU_VIEWPORT_HEIGHT/2f);
        table.row();
        table.add(label).pad(3);
        table.row();
        table.add(first).pad(3);
        table.row();
        table.add(second).pad(3);
        table.row();
        table.add(third).pad(3);
        table.row();
        table.add(fourth).pad(3);
        table.row();
        table.add(fifth).pad(3);
        table.row();
        table.add(sixth).pad(3);
        table.row();
        table.add(seventh).pad(3);
        table.row();
        table.add(eight).pad(3);
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

        back.addListener(new InputListener(){
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
    }
}
