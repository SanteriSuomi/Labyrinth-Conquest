package com.labyrinthconquest.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.labyrinthconquest.game.screens.HowToPlay;
import com.labyrinthconquest.game.screens.LevelEnd;
import com.labyrinthconquest.game.screens.LevelSelect;
import com.labyrinthconquest.game.screens.MainGame;
import com.labyrinthconquest.game.screens.MainMenu;
import com.labyrinthconquest.game.screens.SettingsScreen;
import com.labyrinthconquest.game.ui.Localisation;
import com.labyrinthconquest.game.utils.Sounds;

/**
 * Game entry point
 */
public class Main extends Game {
	private SpriteBatch batch;
	private MainMenu mainMenu;
	private SettingsScreen settingsScreen;
	private LevelSelect levelSelect;
	private MainGame mainGame;
	private HowToPlay howToPlay;
	private LevelEnd levelEnd;
	/**
	 * localisation object
	 */
	private Localisation local;
	/**
	 * sound handler object
	 */
	private Sounds sounds;

	/**
	 * Main menu getter for setting screen
	 */
	public MainMenu getMainMenu() {
		return mainMenu;
	}

	/**
	 * Settings menu getter for setting screen
	 */
	public SettingsScreen getSettingsScreen() {
		return settingsScreen;
	}

	/**
	 * Level select menu getter for setting screen
	 */
	public LevelSelect getLevelSelect() {
		return levelSelect;
	}

	/**
	 * Main game getter for setting screen
	 */
	public MainGame getMainGame() {
		return mainGame;
	}

	/**
	 * Tutorial menu getter for setting screen
	 */
	public HowToPlay getHowToPlay() {
		return howToPlay;
	}

	/**
	 * Level end menu getter for setting screen
	 */
	public LevelEnd getLevelEnd() {
		return levelEnd;
	}

	/**
	 * Sound object getter for playing sound effects
	 */
	public Sounds getSounds() {return sounds;}

	@Override
	/**
	 * all screen objects are maid here
	 */
	public void create() {
		local = new Localisation();
		batch = new SpriteBatch();
		mainMenu = new MainMenu(batch, this, local);
		settingsScreen = new SettingsScreen(batch, this, local);
		levelSelect = new LevelSelect(batch, this, local);
		mainGame = new MainGame(batch, this, local);
		howToPlay = new HowToPlay(batch, this, local);
		levelEnd = new LevelEnd(batch, this, local);
		sounds = new Sounds();
		setScreen(mainMenu);
	}

	@Override
	/**
	 * Screen renderer
	 */
	public void render() {
		super.render();
	}

	@Override
	/**
	 * Code run when disposing screen
	 */
	public void dispose() {
		super.dispose();
		batch.dispose();
	}
}
