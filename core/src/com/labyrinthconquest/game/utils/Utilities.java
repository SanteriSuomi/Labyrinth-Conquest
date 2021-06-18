package com.labyrinthconquest.game.utils;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.labyrinthconquest.game.data.Constants;

/**
 * Contains miscellaneous utility methods
 */
public class Utilities {
    private Utilities() {}

    /**
     * Transform a tilemap rectangle object to game coordinates
     * @param rect Rectangle from which to transform
     * @return Coordinates in-game
     */
    public static Vector2 mapRectToGameCoordinates(Rectangle rect) {
        return new Vector2(rect.x / Constants.TILE_WIDTH, rect.y / Constants.TILE_HEIGHT);
    }

    /**
     * Helper function to quickly get the color value of a door/button
     * @param s Name of door/button
     * @return Color of door/button (e.g blue_door returns blue)
     */
    public static String getColorName(String s) {
        int divIndex = s.indexOf('_');
        return s.substring(0, divIndex);
    }

    /**
     * Transform a 2D texture region array into a 1D texture region array
     * @param regions 2D array
     * @return 1D array
     */
    public static TextureRegion[] transform2DRegionsTo1D(TextureRegion[][] regions, int columns, int rows) {
        TextureRegion[] frames = new TextureRegion[columns * rows];
        int ind = 0;
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < columns; j++) {
                frames[ind++] = regions[i][j];
            }
        }
        return frames;
    }
}
