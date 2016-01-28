package com.jtrofe.cheesebots.game.Levels;

import com.jtrofe.cheesebots.game.physics.Vec;

/**
 * Created by MAIN on 1/26/16
 */
public class Level0 extends GameLevel{

    public Level0(){
        this.MaxBotsOnScreen = 20;

        this.HasTimeLimit = true;
        this.TimeLimit = 30;

        this.CheeseCount = 1;

        this.HasRandomCheeseLocations = false;
        this.CheesePositions = new Vec[]{
                new Vec(0.5f, 0.5f)
            };

        this.HasRandomCheeseSizes = false;
        this.CheeseSizes = new float[]{
                0.1f
            };
    }
}
