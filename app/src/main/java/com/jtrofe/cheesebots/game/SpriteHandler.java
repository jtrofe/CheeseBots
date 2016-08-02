package com.jtrofe.cheesebots.game;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;

import com.jtrofe.cheesebots.GameApp;
import com.jtrofe.cheesebots.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Static class to handle loading sprite sheets and making game objects aware of animations
 */
public class SpriteHandler {

    public static final int SHEET_SMALL_BOT     = 0;
    public static final int SHEET_MEDIUM_BOT    = 1;
    public static final int SHEET_LARGE_BOT     = 2;
    public static final int SHEET_GIANT_BOT     = 3;
    public static final int SHEET_FLAIL         = 4;
    public static final int SHEET_CHEESE        = 5;

    public static final Paint PAINT = new Paint();

    //
    // Animation frame arrays
    //
    public static final List<int[]> WALK_FRAMES = Arrays.asList(
            new int[]{1, 2, 3, 2},  // Small bot
            new int[]{1, 2},        // Medium bot
            new int[]{1, 2, 3, 2},  // Large bot
            new int[]{1, 2, 3, 2}   // Giant bot
        );

    public static final List<int[]> EAT_FRAMES = Arrays.asList(
            new int[]{0, 4, 5, 4},      // Small bot
            new int[]{0, 3, 4, 3},      // Medium bot
            new int[]{4, 5, 6, 7, 8},   // Large bot
            new int[]{4, 5, 6, 7, 8, 9} // Giant bot
        );

    public static final List<int[]> FLAIL_FRAMES = Arrays.asList(
            new int[]{0},               // Bowling ball
            new int[]{1},               // Spiky ball
            new int[]{2, 3, 4, 5, 4, 3} // Plasma ball
        );

    /**
     * Load all the necessary sprite sheets
     * @return List of sprite sheet bitmaps, in the order of the static int constants
     */
    public static List<Bitmap> GetSpriteSheets(){
        final Resources resources = GameApp.App.getResources();

        List<Bitmap> sheetList = new ArrayList<>();

        sheetList.add(fetchSpriteSheet(resources, R.raw.bot_frames_small, 600, 180));

        sheetList.add(fetchSpriteSheet(resources, R.raw.bot_frames_medium, 425, 300));

        sheetList.add(fetchSpriteSheet(resources, R.raw.bot_frames_large, 1116, 660));

        sheetList.add(fetchSpriteSheet(resources, R.raw.bot_frames_giant, 3200, 975));

        sheetList.add(fetchSpriteSheet(resources, R.raw.flail_frames, 600, 100));

        sheetList.add(fetchSpriteSheet(resources, R.raw.cheese_frames, 600, 300));

        return sheetList;
    }

    /**
     * Create a scaled bitmap from a sprite sheet resource
     * @param resources Resource object from a context
     * @param res Sprite sheet resource ID
     * @param width Scaled width
     * @param height Scaled height
     * @return Sprite sheet bitmap
     */
    private static Bitmap fetchSpriteSheet(Resources resources, int res, int width, int height){
        return Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, res),
                                    width, height, false);
    }
}
