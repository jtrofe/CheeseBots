package com.jtrofe.cheesebots.physics.controllers;

import com.jtrofe.cheesebots.GameApp;
import com.jtrofe.cheesebots.physics.Engine;
import com.jtrofe.cheesebots.physics.Vec;
import com.jtrofe.cheesebots.physics.objects.Bot;
import com.jtrofe.cheesebots.physics.objects.Flail;
import com.jtrofe.cheesebots.physics.objects.GameObject;
import com.jtrofe.cheesebots.physics.objects.Particle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by MAIN on 3/12/16
 */
public class FlailController extends Controller{

    public FlailController(Engine engine){
        super(engine);
    }

    @Override
    public void Update(double timeStep){

        List<GameObject> flailList = mEngine.GetObjectType(GameObject.TYPE_FLAIL);
        if(flailList.isEmpty()) return;

        Flail flail = (Flail) flailList.get(0);
        flail.HandlePoint = mEngine.GetGame().TouchPoint.copy();

        Vec handle_point = flail.HandlePoint.copy();

        if(handle_point.x != -1 || handle_point.y != -1){
            double radius = flail.GetRadius();
            Vec attach_point = flail.LocalVectorToWorldVector(new Vec(-radius, radius));
            Vec X = handle_point.Subtract(attach_point);
            double MAX_FORCE = flail.GetMass() * 20.0;
            Vec force = (X.ScalarMultiply(flail.GetK())).Clamp(MAX_FORCE);

            flail.ApplyForce(force, attach_point);
        }

        List<GameObject> botList = mEngine.GetObjectType(GameObject.TYPE_BOT);

        List<manifold> possibleCollisions = broadPhase(flail, botList);

        narrowPhase(possibleCollisions);
    }

    /**
     * Perform broad-phase collision detection between
     * the flail and the bots by comparing the overlap
     * between the flail and the bots' bounding circles
     */
    private List<manifold> broadPhase(Flail flail, List<GameObject> botList){
        List<manifold> possible_collisions = new ArrayList<>();

        for(GameObject botObject:botList){
            double bot_radius = ((Bot) botObject).GetBoundRadius();
            double min_distance = bot_radius + flail.GetRadius();

            Vec vector_to_bot_center = botObject.GetPosition().Subtract(flail.GetPosition());

            if(vector_to_bot_center.LengthSquared() < min_distance * min_distance) {
                possible_collisions.add(new manifold(flail, (Bot) botObject));
            }
        }

        return possible_collisions;
    }

    private void narrowPhase(List<manifold> manifolds){

        mainLoop:
        for(manifold m:manifolds){
            Flail flail = m.flail;
            Vec flail_position = flail.GetPosition();
            List<Vec> bot_vertices = m.bot.GetVertices();

            // Get closest vertex to flail center
            Vec closest_vertex = Vec.GetClosestToPoint(bot_vertices, flail_position);

            // Get axes
            List<Vec> separating_axes = Arrays.asList(
                    m.bot.GetUnitX().Negate(),
                    m.bot.GetUnitY().Negate(),
                    flail_position.Subtract(closest_vertex).Normalize().Negate()
                );

            // Get vectors from the center of the flail to each vertex on the bot
            List<Vec> vectors_to_corners = new ArrayList<>();
            for(Vec v:bot_vertices){
                vectors_to_corners.add(v.Subtract(flail_position));
            }

            satResult result = new satResult(false);

            // For each axis, test for overlap. If there is overlap,
            // see if this is the axis of least penetration
            for(Vec axis:separating_axes){
                satResult r = SAT(flail, vectors_to_corners, axis);
                if(!r.penetrating) continue mainLoop;

                if(Math.abs(r.overlap) < Math.abs(result.overlap)) result = r;
            }

            // This is the axis of least penetration
            Vec axis = result.axis;

            // If the axis is pointing away from the center of the bot, negate it
            Vec to_bot_center = m.bot.GetPosition().Subtract(flail_position);
            if(to_bot_center.Dot(axis) < 0) axis = axis.Negate();

            Vec point_of_collision = flail_position.Add(axis.ScalarMultiply(flail.GetRadius()));
            axis = axis.ScalarMultiply(-result.overlap);

            double RESOLVE_FORCE = flail.GetMass();

            double FLAIL_FORCE = RESOLVE_FORCE * (m.bot.GetMass() / (flail.GetMass() + m.bot.GetMass()));
            double BOT_FORCE = RESOLVE_FORCE * (flail.GetMass() / (flail.GetMass() + m.bot.GetMass()));

            if(!flail.IsPlow)
                flail.ApplyImpulse(axis.ScalarMultiply(FLAIL_FORCE), point_of_collision);

            m.bot.ApplyImpulse(axis.ScalarMultiply(-BOT_FORCE), point_of_collision);

            // Damage the bot
            double DAMAGE_MULTIPLIER = 1.0 / 100.0;

            double speed = flail.GetLinearVelocity().Length();
            double momentum = flail.GetMass() * speed;
            double damage = momentum * DAMAGE_MULTIPLIER;

            double dp = axis.Normalize().Dot(new Vec(1, 0));
            flail.ApplyTorque(dp * speed * 0.5);

            if(damage > 0.5 && speed > 10){
                m.bot.ApplyDamage(damage);
            }

            if(speed > 10){
                Vec v = Vec.RandomDir(20);
                Particle p = new Particle(point_of_collision, v, m.bot.GetMainColor(), 50);
                mEngine.AddBody(p);

                v = Vec.RandomDir(20);
                p = new Particle(point_of_collision, v, m.bot.GetSecondaryColor(), 50);
                mEngine.AddBody(p);

                v = Vec.RandomDir(20);
                p = new Particle(point_of_collision, v, m.bot.GetTernaryColor(), 50);
                mEngine.AddBody(p);
            }
        }
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
    private satResult SAT(Flail flail, List<Vec> vectors_to_corners, Vec axis){
        double flail_min = -flail.GetRadius();
        double flail_max = -flail_min;

        double p0 = vectors_to_corners.get(0).Dot(axis);
        double p1 = vectors_to_corners.get(1).Dot(axis);
        double p2 = vectors_to_corners.get(2).Dot(axis);
        double p3 = vectors_to_corners.get(3).Dot(axis);

        double bot_min = GameApp.min(p0, p1, p2, p3);
        double bot_max = GameApp.max(p0, p1, p2, p3);

        boolean overlapping = Math.max(flail_min, bot_min) <= Math.min(flail_max, bot_max);

        if(!overlapping){
            return new satResult(false);
        }else{
            double penetration_depth = Math.min(bot_max, flail_max) - Math.max(bot_min, flail_min);

            return new satResult(true, penetration_depth, axis.copy());
        }
    }

    private class satResult{

        public boolean penetrating = false;
        public double overlap = Double.MAX_VALUE;
        public Vec axis = new Vec();

        public satResult(boolean penetrating){
            this.penetrating = penetrating;
        }

        public satResult(boolean penetrating, double overlap, Vec axis){
            this.penetrating = penetrating;
            this.overlap = overlap;
            this.axis = axis.copy();
        }
    }
    private class manifold{

        public Flail flail;
        public Bot bot;

        public manifold(Flail flail, Bot bot){
            this.flail = flail;
            this.bot = bot;
        }
    }
}
