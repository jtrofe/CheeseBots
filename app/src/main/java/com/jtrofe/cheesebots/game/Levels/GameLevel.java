package com.jtrofe.cheesebots.game.Levels;

import com.jtrofe.cheesebots.game.physics.Vec;

/**
 * Created by MAIN on 1/24/16
 */
public abstract class GameLevel{
    public String InitialMessage = "";

    public int MaxBots = Integer.MAX_VALUE; // Don't set this to have infinite bots
    public int MaxBotsOnScreen;

    public boolean HasTimeLimit;
    public int TimeLimit; // In seconds


    public int CheeseCount;

    public boolean HasRandomCheeseLocations;
    public Vec[] CheesePositions; // In percentage of the screen dimensions

    public boolean HasRandomCheeseSizes;
    public float[] CheeseSizes; // In percentage of the screen height
}
