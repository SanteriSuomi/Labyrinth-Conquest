package com.labyrinthconquest.game.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.labyrinthconquest.game.Main;
import com.labyrinthconquest.game.data.Constants;

/**
 * Menu actor represents an object in game menus, that can be clicked and so on
 */
public class MenuActor extends Actor {
    private Sprite sprite;

    /**
     * Create a new menu actor/item.
     * @param texture Texture of the menu item
     * @param actorName Menu item name
     * @param width Width of the item
     * @param height Height of the item
     * @param x X position of the item
     * @param y Y position of the item
     * @param game Main instance
     */
    public MenuActor(Texture texture, final String actorName, float width, float height, int x, int y, final Main game) {
        sprite = new Sprite(texture);
        sprite.setSize(width, height);
        sprite.setOrigin(width / 2, height / 2);
        spritePos(x, y);
        setTouchable(Touchable.enabled);
        setClickEvents(actorName, game);
    }

    /**
     * Set menu click events for current instance
     * @param actorName Name of the actor
     * @param game Main instance
     */
    private void setClickEvents(final String actorName, final Main game) {
        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                switch(actorName) {
                    case "MainMenu":
                        game.setScreen(game.getMainMenu());
                        break;
                    case "StartGame":
                        game.setScreen(game.getLevelSelect());
                        break;
                    case "ExitGame":
                        Gdx.app.exit();
                        break;
                    case "SelectLevel":
                        game.getMainGame().setLevel(1);
                        game.setScreen(game.getMainGame());
                        break;
                    case "Settings":
                        game.setScreen(game.getSettingsScreen());
                        break;
                    case "TextInput":
                        Gdx.input.getTextInput(new MyTextInputListener(), "Nickname input", "DefaultUser", "Nickname");
                        break;
                }
                return true;
            }
        });
    }

    /**
     * Set sprite position and bounds
     * @param x X position
     * @param y Y position
     */
    public void spritePos(float x, float y){
        sprite.setPosition(x, y);
        setBounds(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        sprite.draw(batch);
    }

    /**
     * Input listener for.. debugging? Honestly not sure
     */
    private class MyTextInputListener implements Input.TextInputListener {
        @Override
        public void input(String text) {

        }

        @Override
        public void canceled() {

        }
    }
}