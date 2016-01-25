package com.jtrofe.cheesebots.game.controllers;

import android.graphics.Color;
import android.graphics.Paint;

import com.jtrofe.cheesebots.MainActivity;
import com.jtrofe.cheesebots.game.gameobjects.Bot;
import com.jtrofe.cheesebots.game.gameobjects.Flail;
import com.jtrofe.cheesebots.game.gameobjects.GameObject;
import com.jtrofe.cheesebots.game.gameobjects.Particle;
import com.jtrofe.cheesebots.game.physics.Engine;
import com.jtrofe.cheesebots.game.physics.Vec;
import com.jtrofe.cheesebots.game.physics.Manifold;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MAIN on 1/23/16
 */
public class FlailController extends Controller{

    public FlailController(Engine engine){
        super(engine);
    }

    @Override
    public void Update(){
        List<GameObject> flailList = mEngine.GetType(GameObject.TYPE_FLAIL);
        List<GameObject> botList = mEngine.GetType(GameObject.TYPE_BOT);

        for(GameObject f:flailList){
            Flail flail = (Flail) f;

            // If the user is dragging across the screen, move the
            //   flail to the touch point
            if(mEngine.Dragging){
                // Hooke's law:
                //  F = kX

                float k = ((Flail) f).GetK();
                Vec X = mEngine.TouchPoint.Subtract(f.GetPosition());

                Vec F = X.ScalarMultiply(k);

                F = F.Clamp(300); // Max force that will be applied

                f.ApplyForce(F, f.GetPosition());
            }

            List<Manifold> possible_collisions = broadPhase(flail, botList);

            narrowPhase(possible_collisions);
        }
    }

    /**
     * Create a list of possible collisions by comparing the distance
     * between bots and the flail with the flail radius and the circle
     * which fits around the bot
     * @param flail The flail
     * @param botList All bots on screen
     * @return List of manifolds
     */
    private List<Manifold> broadPhase(Flail flail, List<GameObject> botList){
        List<Manifold> possible_collisions = new ArrayList<>();

        Paint p = new Paint();
        p.setColor(Color.BLUE);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(4);

        for(GameObject bot_object:botList){
            float bot_radius = ((Bot) bot_object).GetBoundRadius();
            float min_distance = bot_radius + flail.GetRadius();

            Vec vector_to_bot_center = bot_object.GetPosition().Subtract(flail.GetPosition());


            if(vector_to_bot_center.LengthSquared() < min_distance * min_distance){
                possible_collisions.add(new Manifold(flail, bot_object));
            }
        }

        return possible_collisions;
    }

    /**
     * Use the separating axis theorem to test a manifold
     * and determine if the flail and bot are actually colliding
     * @param possible_collisions Object that just stores the flail and bot objects
     * @return List of actually colliding pairs of objects
     */
    private List<Manifold> narrowPhase(List<Manifold> possible_collisions){
        List<Manifold> actual_collisions = new ArrayList<>();

        Paint p = new Paint();
        p.setStrokeWidth(3);
        p.setStyle(Paint.Style.STROKE);
        p.setColor(Color.RED);

        for(Manifold m:possible_collisions){
            Vec flail_position = m.flail.GetPosition();
            Vec[] bot_vertices = m.bot.GetVertices();

            // Get closest vertex to flail center
            Vec closest_vertex = Vec.GetClosestToPoint(bot_vertices, flail_position);

            // Get axes
            Vec[] separating_axes = new Vec[]{
                    m.bot.GetUnitX().Negate(),
                    m.bot.GetUnitY().Negate(),
                    m.flail.GetPosition().Subtract(closest_vertex).Normalize().Negate()
                };

            // Get vectors from the flail center to each corner of the bot
            Vec[] vectors_to_corners = Vec.GetVectorsFromPoint(bot_vertices, flail_position);

            // Perform SAT calculations
            float[] SAT_results;

            float[] SAT_results_0 = SAT(m.flail, vectors_to_corners, separating_axes[0]);
            if(SAT_results_0[0] == 0.0f) continue;

            SAT_results = SAT_results_0;

            float[] SAT_results_1 = SAT(m.flail, vectors_to_corners, separating_axes[1]);
            if(SAT_results_1[0] == 0.0f) continue;

            if(Math.abs(SAT_results_1[1]) < Math.abs(SAT_results[1])) SAT_results = SAT_results_1;


            float[] SAT_results_2 = SAT(m.flail, vectors_to_corners, separating_axes[2]);
            if(SAT_results_2[0] == 0.0f) continue;

            if(Math.abs(SAT_results_2[1]) < Math.abs(SAT_results[1])) SAT_results = SAT_results_2;

            // This is the axis of least penetration
            Vec v = new Vec(SAT_results[2], SAT_results[3]);

            // If the axis is pointing away from the center of the bot, negate it
            Vec to_bot_center = m.bot.GetPosition().Subtract(flail_position);
            if(to_bot_center.Dot(v) < 0){
                v = v.Negate();
            }

            Vec point_of_collision = flail_position.Add(v.ScalarMultiply(m.flail.GetRadius()));
            v = v.ScalarMultiply(SAT_results[1]).Negate();

            v.Draw(mEngine.debugCanvas, point_of_collision, p);

            m.flail.ApplyForce(v.ScalarMultiply(40), point_of_collision);
            m.bot.ApplyForce(v.ScalarMultiply(-40), point_of_collision);

            // Calculate amount to damage the bot
            float momentum = m.flail.GetLinearVelocity().Length() * m.flail.GetMass();
            float damage = momentum / 100;
            m.bot.ReduceHealth(damage);


            if(m.flail.GetLinearVelocity().LengthSquared() > 100) {
                Particle particle = new Particle(point_of_collision, Color.YELLOW, 50);
                mEngine.AddBody(particle);

                particle = new Particle(point_of_collision, Color.RED, 50);
                mEngine.AddBody(particle);

                particle = new Particle(point_of_collision, Color.YELLOW, 50);
                mEngine.AddBody(particle);
            }

        }

        return actual_collisions;
    }

    /**
     * Project a flail and bot onto an axis and determine if
     * their shadows overlap
     * @param flail The flail
     * @param vectors_to_corners Vectors pointing from the center of the flail to each vertex of the bot
     * @param axis The axis to project onto
     * @return 4 floats. The first float is 0 if they are not overlapping and 1 if they are. The
     *      second is the amount of overlap, if any. The last two are the axis coordinates
     */
    private float[] SAT(Flail flail, Vec[] vectors_to_corners, Vec axis){
        //Flail projection is always -radius to +radius
        //Get projection for each vector onto the axis

        float flail_min = -flail.GetRadius();
        float flail_max = -flail_min;

        float p0 = vectors_to_corners[0].Dot(axis);
        float p1 = vectors_to_corners[1].Dot(axis);
        float p2 = vectors_to_corners[2].Dot(axis);
        float p3 = vectors_to_corners[3].Dot(axis);

        float bot_min = MainActivity.min(p0, p1, p2, p3);
        float bot_max = MainActivity.max(p0, p1, p2, p3);

        boolean overlapping = Math.max(flail_min, bot_min) <= Math.min(flail_max,bot_max);

        if(!overlapping){
            return new float[]{0.0f};
        }else{
            //TODO replace 0 with calculated overlap
            float penetration_depth = penetration_depth = Math.min(bot_max, flail_max) - Math.max(bot_min, flail_min);

            return new float[]{1.0f, penetration_depth, axis.x, axis.y};
        }


        /*Vec start = axis.ScalarMultiply(bot_min).Add(offset);
        Vec end = axis.ScalarMultiply(bot_max).Add(offset);

        Paint p = new Paint();
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(3);
        p.setColor(Color.YELLOW);

        mEngine.debugCanvas.drawLine(start.x, start.y, end.x, end.y, p);*/

    }
}
