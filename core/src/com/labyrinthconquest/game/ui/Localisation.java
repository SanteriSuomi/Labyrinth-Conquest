package com.labyrinthconquest.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.I18NBundle;
import com.labyrinthconquest.game.data.Constants;

/**
 * Represents localisation for the game, handles reading localisation data from localisation files.
 */
public class Localisation {
    /**
     * uk localisation
     */
    private java.util.Locale locale;
    /**
     * fin localisation
     */
    private java.util.Locale localedefault;
    private I18NBundle bundle;
    private Preferences prefs;
    private boolean english;

    /**
     * Constructor for Localisation object. Sets up both languages and search for saved localisation preference.
     */
    public Localisation() {
        prefs = Gdx.app.getPreferences(Constants.PREFERENCES_FILE_NAME);
        locale = new java.util.Locale("en", "UK");
        localedefault = java.util.Locale.getDefault();
        prefs = Gdx.app.getPreferences(Constants.PREFERENCES_FILE_NAME);
        try {
            english = prefs.getBoolean(Constants.LOCALISATION_PREFS_LOCATION);
        }catch (java.lang.NullPointerException yes) {
            english = false;
        }

        if(english) {
            bundle = I18NBundle.createBundle(Gdx.files.internal("locales/labyrinthconquest"), locale);
        }
        else {
            bundle = I18NBundle.createBundle(Gdx.files.internal("locales/labyrinthconquest"), localedefault);
        }
    }

    /**
     * Bundle returner for data fetching
     */
    public I18NBundle getBundle() {
        return bundle;
    }

    /**
     * language changer.
     */
    public void change() {
        english = prefs.getBoolean(Constants.LOCALISATION_PREFS_LOCATION);
        if(english) {
            bundle = I18NBundle.createBundle(Gdx.files.internal("locales/labyrinthconquest"), localedefault);
            english = false;
        }
        else {
            bundle = I18NBundle.createBundle(Gdx.files.internal("locales/labyrinthconquest"), locale);
            english = true;
        }
        saveSettings();
    }

    /**
     * language preference saver
     */
    private void saveSettings() {
        prefs.putBoolean(Constants.LOCALISATION_PREFS_LOCATION, english);
        prefs.flush();
    }
}
