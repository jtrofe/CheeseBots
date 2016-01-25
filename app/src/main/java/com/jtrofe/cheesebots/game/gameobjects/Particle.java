package com.jtrofe.cheesebots.game.gameobjects;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.jtrofe.cheesebots.game.physics.Vec;

/**
 * Created by MAIN on 1/24/16
 */
public class Particle extends GameObject{


    private Paint mPaint;

    public int LifeSpan;

    public Particle(Vec position, int color, int lifeSpan){
        super(position, null, GameObject.PARTICLE_MASS);

        this.mFriction = 0.97f;

        this.mPaint = new Paint();
        this.mPaint.setColor(color);

        this.LifeSpan = lifeSpan;

        Vec v = Vec.Random(40, 40);
        v.x -= 20;
        v.y -= 20;

        this.mLinearVelocity = v;
    }

    public Particle(Vec position, int color, int lifeSpan, Vec velocity){
        this(position, color, lifeSpan);
        this.mLinearVelocity = velocity;
    }

    @Override
    public void Draw(Canvas canvas){
        canvas.drawCircle(mPosition.x, mPosition.y, GameObject.PARTICLE_RADIUS, mPaint);
    }
}
