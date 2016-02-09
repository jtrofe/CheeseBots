package com.jtrofe.cheesebots.game.physics.constraints;


import com.jtrofe.cheesebots.game.gameobjects.GameObject;
import com.jtrofe.cheesebots.game.physics.Vec;

/**
 * Enforce a distance between two game objects
 */
public class DistanceConstraint extends Constraint{

    protected Vec mLocalPointA = new Vec();
    protected Vec mLocalPointB = new Vec();
    protected float mDistance;

    public void SetLocalPointA(Vec a){
        mLocalPointA = a.Clone();
    }

    public void SetLocalPointB(Vec b){
        mLocalPointA = b.Clone();
    }

    public DistanceConstraint(GameObject bodyA, GameObject bodyB, float distance){
        super(bodyA, bodyB);

        this.mDistance = distance;
    }

    @Override
    public void Solve(float timeStep){
        final Vec pA = mBodyA.LocalVectorToWorldVector(mLocalPointA);
        final Vec pB = mBodyB.LocalVectorToWorldVector(mLocalPointB);

        final Vec axis = pB.Subtract(pA);
        final Vec unitAxis = axis.Normalize();
        final float currentDistance = axis.Length();

        final float relVel = mBodyB.GetLinearVelocity().Subtract(mBodyA.GetLinearVelocity()).Dot(unitAxis);

        float relDist = currentDistance - mDistance;

        float remove = relVel + relDist / timeStep;
        float impulse = remove / (mBodyA.GetInvMass() + mBodyB.GetInvMass());

        Vec I = unitAxis.ScalarMultiply(impulse);

        mBodyA.ApplyImpulse(I, pA);
        mBodyB.ApplyImpulse(I.Negate(), pB);
    }
}