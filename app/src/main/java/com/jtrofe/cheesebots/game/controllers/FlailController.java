package com.jtrofe.cheesebots.game.controllers;

import android.graphics.Color;
import android.graphics.Paint;

import com.jtrofe.cheesebots.GameApplication;
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
    public void Update(float timeStep){
        if(mEngine.LevelComplete) return;
        List<GameObject> flailList = mEngine.GetType(GameObject.TYPE_FLAIL);
        List<GameObject> botList = mEngine.GetType(GameObject.TYPE_BOT);

        // Validate the touch points on the flails
        for(GameObject f:flailList){
            Flail flail = (Flail) f;

            if(flail.TouchPointId == -1){
                // Find an available touch point

                mEngine.SetAvailableTouchPoint(flail);
            }else{
                // Check if the point still exists. If not, find an available one

                if(mEngine.GetTouchPointById(flail.TouchPointId) == null){
                    flail.TouchPointId = -1;
                    mEngine.SetAvailableTouchPoint(flail);
                }
            }
        }



        for(GameObject f:flailList){
            Flail flail = (Flail) f;

            // If the user is dragging across the screen, move the
            //   flail to the touch point
            if(flail.TouchPointId != -1){
                // Hooke's law:
                //  F = kX

                float k = ((Flail) f).GetK();

                Engine.TouchPoint p = mEngine.GetTouchPointById(flail.TouchPointId);

                if(((Flail) f).HandleNode != null && p != null){


                    Vec T = mEngine.GetTouchPointById(flail.TouchPointId).Point;

                    GameObject n = ((Flail) f).HandleNode;
                    n.SetPosition(T);
                    n.ClearForce();

                    Paint pa = new Paint();
                    pa.setColor(Color.YELLOW);
                    n.GetPosition().Circle(mEngine.debugCanvas, pa);

                }else if(p != null) {
                    float r = ((Flail) f).GetRadius();
                    Vec attachPoint = f.LocalVectorToWorldVector(new Vec(-r, r));

                    Vec T = mEngine.GetTouchPointById(flail.TouchPointId).Point;
                    Vec X = T.Subtract(attachPoint);

                    Vec F = X.ScalarMultiply(k);

                    F = F.Clamp(300); // Max force that will be applied

                    //f.ApplyForce(F, f.GetPosition());
                    f.ApplyForce(F, attachPoint);
                    flail.HandlePoint = p.Point.Clone();
                }else{
                    flail.HandlePoint = null;
                }
            }else{
                flail.HandlePoint = null;
            }

            List<Manifold> possible_collisions = broadPhase(flail, botList);

            narrowPhase(possible_collisions);
        }
    }

    private List<Manifold> broadPhase2(Flail flail, List<GameObject> botList, float timeStep){
        List<Manifold> possible_collisions = new ArrayList<>();

        float r = flail.GetRadius();
        Vec adj_velocity = flail.GetLinearVelocity().ScalarMultiply(timeStep);
        Vec old_pos = flail.GetPosition();
        Vec new_pos = old_pos.Add(adj_velocity);

        Vec p0 = old_pos.Add(new Vec(r, 0));
        Vec p1 = old_pos.Add(new Vec(-r, 0));
        Vec p2 = old_pos.Add(new Vec(0, r));
        Vec p3 = old_pos.Add(new Vec(0, -r));

        Vec p4 = new_pos.Add(new Vec(r, 0));
        Vec p5 = new_pos.Add(new Vec(-r, 0));
        Vec p6 = new_pos.Add(new Vec(0, r));
        Vec p7 = new_pos.Add(new Vec(0, -r));

        float minX = GameApplication.min(p0.x, p1.x, p2.x, p3.x, p4.x, p5.x, p6.x, p7.x);
        float minY = GameApplication.min(p0.y, p1.y, p2.y, p3.y, p4.y, p5.y, p6.y, p7.y);
        float maxX = GameApplication.max(p0.x, p1.x, p2.x, p3.x, p4.x, p5.x, p6.x, p7.x);
        float maxY = GameApplication.max(p0.y, p1.y, p2.y, p3.y, p4.y, p5.y, p6.y, p7.y);

        float flail_width = maxX - minX;
        float flail_height = maxY - minY;
        Paint p = new Paint();
        p.setColor(Color.BLUE);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(4);


        for(GameObject bot_object:botList){
            Vec[] bot_bounds = ((Bot) bot_object).GetBounds();

            float w = bot_bounds[1].x - bot_bounds[0].x;
            float h = bot_bounds[1].y - bot_bounds[0].y;

            if(Math.abs(bot_bounds[0].x - minX) * 2 < (w + flail_width) &&
                    Math.abs(bot_bounds[0].y - minY) * 2 < (h + flail_height)){
                possible_collisions.add(new Manifold(flail, bot_object));
            }
        }

        return possible_collisions;
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

    private void narrowPhase2(List<Manifold> possible_collisions, float timeStep){

        Paint p = new Paint();
        p.setStrokeWidth(3);
        p.setStyle(Paint.Style.STROKE);
        p.setColor(Color.RED);

        for(Manifold m:possible_collisions){
            float flail_radius = m.flail.GetRadius();
            Vec flail_vec = m.flail.GetLinearVelocity().ScalarMultiply(timeStep);
            Vec flail_position = m.flail.GetPosition();
            Vec bot_position = m.bot.GetPosition();
            Vec[] bot_vertices = m.bot.GetVertices();

            Vec bot_unit_x = m.bot.GetUnitX();
            Vec bot_unit_y = m.bot.GetUnitY();

            int w = m.bot.GetHalfWidth();
            int h = m.bot.GetHalfHeight();


            Vec[] bot_points = new Vec[]{
                    bot_vertices[3].Add(bot_unit_y.ScalarMultiply(flail_radius)),
                    bot_vertices[0].Add(bot_unit_x.ScalarMultiply(flail_radius)),
                    bot_vertices[1].Add(bot_unit_y.ScalarMultiply(-flail_radius)),
                    bot_vertices[2].Add(bot_unit_x.ScalarMultiply(-flail_radius))
            };

            Vec top = bot_unit_x.ScalarMultiply(w * 2);
            Vec right = bot_unit_y.ScalarMultiply(h * 2);

            top.Draw(mEngine.debugCanvas, bot_points[0], p);
            right.Negate().Draw(mEngine.debugCanvas, bot_points[1], p);
            top.Negate().Draw(mEngine.debugCanvas, bot_points[2], p);
            right.Draw(mEngine.debugCanvas, bot_points[3], p);

            flail_vec.Draw(mEngine.debugCanvas, flail_position, p);

            float t0 = getIntersection(flail_position, flail_vec, bot_points[0], top);
            float t1 = getIntersection(flail_position, flail_vec, bot_points[1], right.Negate());
            float t2 = getIntersection(flail_position, flail_vec, bot_points[2], top.Negate());
            float t3 = getIntersection(flail_position, flail_vec, bot_points[3], right);

            Vec v0 = flail_vec.ScalarMultiply(t0);
            Vec v1 = flail_vec.ScalarMultiply(t1);
            Vec v2 = flail_vec.ScalarMultiply(t2);
            Vec v3 = flail_vec.ScalarMultiply(t3);

            v0.Add(flail_position).Circle(mEngine.debugCanvas, p);
            v1.Add(flail_position).Circle(mEngine.debugCanvas, p);
            v2.Add(flail_position).Circle(mEngine.debugCanvas, p);
            v3.Add(flail_position).Circle(mEngine.debugCanvas, p);

            if(t0 < 0) t0 = Float.MAX_VALUE;
            if(t1 < 0) t1 = Float.MAX_VALUE;
            if(t2 < 0) t2 = Float.MAX_VALUE;
            if(t3 < 0) t3 = Float.MAX_VALUE;

            if(t0 <= 1 || t1 <= 1 || t2 <= 1 || t3 <= 1){
                float t = GameApplication.min(t0, t1, t2, t3);

                if(t < Float.MAX_VALUE){
                    Vec v = flail_vec.ScalarMultiply(t);
                    m.flail.SetPosition(flail_position.Add(v));
                    m.flail.SetLinearVelocity(new Vec());
                    m.flail.ClearForce();
                    m.flail.SetLinearVelocity(flail_vec.ScalarMultiply(-5));
                }
            }
        }
    }

    /**
     * Calculate how far along vector 1 it intersects with vector 2
     * @param p1 The starting point of vector 1
     * @param v1 Vector 1
     * @param p2 The starting point of vector 2
     * @param v2 Vector 2
     * @return The percentage of v1's length where the intersection is
     */
    private float getIntersection(Vec p1, Vec v1, Vec p2, Vec v2){
        Vec v3 = p2.Subtract(p1);

        float perp1 = v3.x * v2.y - v3.y * v2.x;

        float perp2 = v1.x * v2.y - v1.y * v2.x;

        if(perp2 == 0) return Float.MAX_VALUE;

        return perp1 / perp2;
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


            //TODO note: removing force applied to flail makes the game easier
            if(!m.flail.PlowThrough){
                m.flail.ApplyForce(v.ScalarMultiply(40), point_of_collision);
            }
            m.bot.ApplyForce(v.ScalarMultiply(-40), point_of_collision);

            // Calculate amount to damage the bot
            float l = m.flail.GetLinearVelocity().Length();
            float momentum = l * m.flail.GetMass();
            float damage = momentum / 30;

            float dp = v.Normalize().Dot(new Vec(1, 0));
            m.flail.ApplyTorque(dp * l * 0.5f);

            if(damage > 0.5f)
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

        float bot_min = GameApplication.min(p0, p1, p2, p3);
        float bot_max = GameApplication.max(p0, p1, p2, p3);

        boolean overlapping = Math.max(flail_min, bot_min) <= Math.min(flail_max,bot_max);

        if(!overlapping){
            return new float[]{0.0f};
        }else{
            float penetration_depth = Math.min(bot_max, flail_max) - Math.max(bot_min, flail_min);

            return new float[]{1.0f, penetration_depth, axis.x, axis.y};
        }
    }
}
