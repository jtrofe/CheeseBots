package com.jtrofe.cheesebots.physics.objects;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.jtrofe.cheesebots.game.SpriteHandler;
import com.jtrofe.cheesebots.physics.Vec;

/**
 * A small particle for effects. Just has a color and a short lifespan
 */
public class Particle extends GameObject {

    public int LifeSpan;

    private int mColor;


    public Particle(Vec position, Vec velocity, int color, int lifeSpan){
        super(position);

        Type = GameObject.TYPE_PARTICLE;
        mLinearVelocity = velocity.copy();

        mFriction = 0.97;

        mColor = color;

        LifeSpan = lifeSpan;


        this.calculateMoment();
    }

    @Override
    public void Draw(Canvas canvas){
        SpriteHandler.PAINT.setStyle(Paint.Style.FILL);
        SpriteHandler.PAINT.setColor(mColor);

        canvas.drawCircle(mPosition.xf(), mPosition.yf(), (float) GameObject.PARTICLE_RADIUS, SpriteHandler.PAINT);
    }
}
