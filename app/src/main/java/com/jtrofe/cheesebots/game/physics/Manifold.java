package com.jtrofe.cheesebots.game.physics;

import com.jtrofe.cheesebots.game.gameobjects.Bot;
import com.jtrofe.cheesebots.game.gameobjects.Flail;
import com.jtrofe.cheesebots.game.gameobjects.GameObject;

/**
 * Just a simple data structure
 */
public class Manifold {

    public Flail flail;
    public Bot bot;

    public Manifold(Flail flail, GameObject bot){
        this.flail = flail;
        this.bot = (Bot) bot;
    }
}
