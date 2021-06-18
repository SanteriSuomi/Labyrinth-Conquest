package com.labyrinthconquest.game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.labyrinthconquest.game.data.Constants;

/**
 * Represents Sound object for the game, handles playing music and sound effects.
 */
public class Sounds {
    public float val;
    public Music mainmusic;
    public Music gamemusic;
    public Sound select;
    public Sound character;
    public Sound roger;
    public Sound button;
    private Preferences prefs;

    /**
     * Constructor for Sounds object. Sets up both songs and sound effects. Also fetches sound volume.
     */
    public Sounds() {
        prefs = Gdx.app.getPreferences(Constants.PREFERENCES_FILE_NAME);
        mainmusic = Gdx.audio.newMusic(Gdx.files.internal("music/menu-music.mp3"));
        gamemusic = Gdx.audio.newMusic(Gdx.files.internal("music/game-music.mp3"));
        select = Gdx.audio.newSound(Gdx.files.internal("music/select.mp3"));
        character = Gdx.audio.newSound(Gdx.files.internal("music/character.mp3"));
        roger = Gdx.audio.newSound(Gdx.files.internal("music/roger.mp3"));
        button = Gdx.audio.newSound(Gdx.files.internal("music/button.mp3"));
        val = (prefs.getFloat(Constants.VOLUME_PREFS_LOCATION) * 100);
        if (val == 0) {
            val = 50;
        }
        mainmusic.setVolume(val / 100);
        gamemusic.setVolume(val / 100);
        mainmusic.setLooping(true);
        gamemusic.setLooping(true);
    }

    /**
     * Handles playing menu button press sound
     */
    public void playSelect() {
        select.play(val / 50);
    }

    /**
     * Handles playing character change sound
     */
    public void playCaracter() {
        character.play(val / 50);
    }

    /**
     * Handles playing character move sound
     */
    public void playRoger() {
        roger.play(val / 50);
    }

    /**
     * Handles playing in-game button press sound
     */
    public void playButton() {
        button.play(val / 25);
    }
}
