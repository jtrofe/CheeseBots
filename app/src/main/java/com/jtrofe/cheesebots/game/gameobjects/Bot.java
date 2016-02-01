package com.jtrofe.cheesebots.game.gameobjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;

import com.jtrofe.cheesebots.GameActivity;
import com.jtrofe.cheesebots.GameApplication;
import com.jtrofe.cheesebots.MainActivity;
import com.jtrofe.cheesebots.game.physics.Vec;

/**
 * Created by MAIN on 1/23/16
 */
public class Bot extends GameObject{
    /**
     * Game properties
     */
    private float mHealthPoints = 100;
    private float mEatingSpeed = 0.1f;

    public float GetEatingSpeed(){
        return mEatingSpeed;
    }

    public void ReduceHealth(float damageAmount){
        mHealthPoints -= damageAmount;
    }

    public boolean IsAlive(){
        return mHealthPoints > 0;
    }

    /**
     * Physics properties
     */
    private float mBoundRadius;

    private Vec mUnitX;
    private Vec mUnitY;

    public Vec GetUnitX(){
        return mUnitX;
    }

    public Vec GetUnitY(){
        return mUnitY;
    }

    public float GetBoundRadius(){
        return mBoundRadius;
    }

    /**
     * Visual properties
     */
    final public static int STATE_WALKING = 0;
    final public static int STATE_EATING = 1;
    final public static int STATE_TURNING_RIGHT = 2;
    final public static int STATE_TURNING_LEFT = 3;

    public int State = STATE_WALKING;

    public Bot(Vec position, Bitmap image, float mass, float eatingSpeed){
        super(position, image, mass);

        this.mEatingSpeed = eatingSpeed;

        this.mType = GameObject.TYPE_BOT;
        this.mBoundRadius = (float) Math.sqrt(mHalfWidth * mHalfWidth + mHalfHeight * mHalfHeight);

        calculateMomentOfInertia();

        this.mAngle = (float) Math.PI/4;
        updateUnitVectors();
    }

    /**
     * Override the normal moment calculation
     * with the formula for a rectangle
     */
    @Override
    protected void calculateMomentOfInertia(){
        int w = mHalfWidth * 2;
        int h = mHalfHeight * 2;

        mMomentOfInertia = (mMass * (w * w + h * h)) / 12;
        mInvMoment = 1 / mMomentOfInertia;
    }

    /**
     * Make sure the unit vectors are up to date after
     * the angle is adjusted
     * @param timeStep How many seconds to simulate
     */
    @Override
    public void UpdateForceAndTorque(float timeStep){
        super.UpdateForceAndTorque(timeStep);

        updateUnitVectors();
    }

    /**
     * Update the half-width unit vectors
     * based on the current angle
     */
    private void updateUnitVectors(){
        float dx1 = (float) Math.sin(mAngle + GameObject.QUARTER_CIRCLE);
        float dy1 = (float) -Math.cos(mAngle + GameObject.QUARTER_CIRCLE);
        mUnitX = new Vec(dx1, dy1);

        float dx2 = (float) Math.sin(mAngle);
        float dy2 = (float) -Math.cos(mAngle);
        mUnitY = new Vec(dx2, dy2);
    }

    /**
     * Calculate where the four corners of the bot
     * @return Corner points, clockwise starting from the top right
     */
    public Vec[] GetVertices(){
        Vec v_x = mUnitX.ScalarMultiply(mHalfWidth);
        Vec v_y = mUnitY.ScalarMultiply(mHalfHeight);
        Vec p = mPosition;

        return new Vec[]{
                p.Add(v_x).Add(v_y),
                p.Add(v_x).Add(v_y.Negate()),
                p.Add(v_x.Negate()).Add(v_y.Negate()),
                p.Add(v_x.Negate()).Add(v_y)
            };
    }


    /**
     * Take a vector in the robot's frame of reference
     * and convert it to a point in the world
     * @param vector Local vector
     * @return Corresponding point in the world
     */
    public Vec LocalVectorToWorldVector(Vec vector){
        Vec vx = mUnitX.ScalarMultiply(vector.x);
        Vec vy = mUnitY.ScalarMultiply(vector.y);

        return mPosition.Add(vx).Add(vy);
    }

    /**
     * Apply torque to the bot to align the
     * unit vector y with some other unit vector
     * @param vector Vector to align with
     */
    public void SteerToAlign(Vec vector){
        float dp_y = vector.Dot(mUnitY);
        float amount_oriented = 2 - ((dp_y * dp_y) + 1);

        float dp_x = vector.Dot(mUnitX); // Used to determine which direction to turn

        int turn_direction = 1;
        if(dp_x < 0) turn_direction = -1;

        float MAX_TURN_TORQUE = 0.6f * turn_direction * mMomentOfInertia;

        ApplyTorque(MAX_TURN_TORQUE * amount_oriented);
    }

    private Rect getFrame(){
        int f = (int) CurrentFrame;

        return new Rect(f * 100, 0, f * 100 + 100, 60);
    }

    @Override
    public void Draw(Canvas canvas){
        Rect src = new Rect(0, 0, 100, 60);
        switch (State){
            case Bot.STATE_EATING:
                if(CurrentFrame < 3 || CurrentFrame >= 5) CurrentFrame = 3;
                src = getFrame();
                CurrentFrame += 0.2;

                break;
            case Bot.STATE_WALKING:
                if(CurrentFrame >= 3) CurrentFrame = 0;

                src = getFrame();
                CurrentFrame += 0.1;

                break;
            default:

        }

        Rect dst = new Rect((int) mPosition.x - mHalfWidth, (int) mPosition.y - mHalfHeight,
                    (int) mPosition.x + mHalfWidth, (int) mPosition.y + mHalfHeight);


        int saveCount = canvas.save();

        canvas.rotate((float) Math.toDegrees(mAngle), mPosition.x, mPosition.y);
        canvas.drawBitmap(GameActivity.SpriteSheets.get(0), src, dst, null);

        canvas.restoreToCount(saveCount);

    }

    public Vec[] GetBounds(){
        Vec[] vertices = GetVertices();

        float minX = GameApplication.min(vertices[0].x, vertices[1].x, vertices[2].x, vertices[3].x);
        float minY = GameApplication.min(vertices[0].y, vertices[1].y, vertices[2].y, vertices[3].y);
        float maxX = GameApplication.max(vertices[0].x, vertices[1].x, vertices[2].x, vertices[3].x);
        float maxY = GameApplication.max(vertices[0].y, vertices[1].y, vertices[2].y, vertices[3].y);

        return new Vec[]{
                new Vec(minX, minY),
                new Vec(maxX, maxY)
        };
    }
}
