package com.jtrofe.cheesebots.physics.controllers;

import com.jtrofe.cheesebots.GameApp;
import com.jtrofe.cheesebots.physics.Engine;
import com.jtrofe.cheesebots.physics.Vec;
import com.jtrofe.cheesebots.physics.objects.Bot;
import com.jtrofe.cheesebots.physics.objects.Cheese;
import com.jtrofe.cheesebots.physics.objects.GameObject;
import com.jtrofe.cheesebots.physics.objects.Particle;

import java.util.List;
import java.util.Random;

/**
 * Direct bots towards cheese and make them eat it
 */
public class BotController extends Controller{

    public BotController(Engine engine){
        super(engine);
    }

    @Override
    public void Update(double timeStep){

        List<GameObject> botList = mEngine.GetObjectType(GameObject.TYPE_BOT);

        if(botList.isEmpty()) return;

        List<GameObject> cheeseList = mEngine.GetObjectType(GameObject.TYPE_CHEESE);

        if(cheeseList.isEmpty()){
            // If there are no cheeses on screen, swarm to the center

            Vec center = mEngine.GetWorldSize().ScalarMultiply(0.5);

            for(GameObject botObject:botList){
                Bot b = (Bot) botObject;

                b.State = Bot.STATE_WALKING;

                Vec from_center = center.Subtract(b.GetPosition());

                b.SteerToAlign(from_center.Normalize());

                b.MoveTowardsPoint(center, 0.1, b.GetMass());

                avoidObjects(b, botList);

                checkHealth(b);
            }
        }else{
            for(GameObject botObject:botList){
                Bot b = (Bot) botObject;

                // Find the cheese closest to the bot
                closestCheese cheese = getNearestCheese(b, cheeseList);
                Vec cheese_direction = cheese.cheeseDirection;

                // Align the bot to face its target cheese
                b.SteerToAlign(cheese_direction);

                // If the bot is aligned with the cheese move towards it/eat it
                if(b.GetUnitY().Dot(cheese_direction) > 0.8){
                    attackCheese(b, cheese);
                }

                // Avoid other bots
                avoidObjects(b, botList);

                checkHealth(b);
            }
        }


    }

    private void checkHealth(Bot b){
        if(b.IsAlive()) return;

        long scrap = b.GetScrap();

        mEngine.RemoveBody(b);

        Random rnd = new Random();

        if(mEngine.CountObjectType(GameObject.TYPE_PARTICLE) < 100){

            int particleCount = (int) (b.GetMass() / 3);

            for(int i=0;i<particleCount;i++){
                Vec v = Vec.RandomDir(20);

                int color = rnd.nextBoolean() ? b.GetSecondaryColor() : b.GetTernaryColor();
                if(rnd.nextBoolean()) color = b.GetMainColor();

                Particle p = new Particle(b.GetPosition(), v, color, 40);

                mEngine.AddBody(p);
            }
        }

        GameApp.CurrentGame.OnBotDestroyed(scrap);

        mEngine.JitterController.StartJitter(20);
    }

    private void avoidObjects(Bot b, List<GameObject> objects){
        Vec push_vec = new Vec();

        for(GameObject obj:objects){
            if(obj != b){
                Vec d = obj.GetPosition().Subtract(b.GetPosition());

                if(d.LengthSquared() < 10000){
                    push_vec = push_vec.Subtract(d);
                }
            }

        }

        b.ApplyForceToCenter(push_vec);
    }

    private closestCheese getNearestCheese(Bot b, List<GameObject> cheeseList){
        double closest_distance = Double.MAX_VALUE;
        GameObject closest_cheese = cheeseList.get(0);
        Vec cheese_direction = new Vec(1, 0);
        double goal_length = 10.0;

        double BACK_OFF_DISTANCE = b.GetHalfHeight();

        for(GameObject cheeseObject:cheeseList){
            Vec vector_to_center = cheeseObject.GetPosition().Subtract(b.GetPosition());

            double back_off_distance = ((Cheese) cheeseObject).GetRadius() + BACK_OFF_DISTANCE;

            double cheese_distance = vector_to_center.Length() - back_off_distance;

            if(cheese_distance < closest_distance){
                closest_distance = cheese_distance;
                closest_cheese = cheeseObject;

                cheese_direction = vector_to_center;
                goal_length = cheese_distance;
            }
        }

        return new closestCheese((Cheese) closest_cheese, cheese_direction.Normalize(), goal_length);
    }

    private void attackCheese(Bot b, closestCheese c){
        Vec cheese_vector = c.cheese.GetPosition().Subtract(b.GetPosition());

        if(cheese_vector.Length() < c.cheese.GetRadius() + b.GetHalfHeight() + 10){
            c.cheese.Eat(b.GetEatingSpeed());

            if(b.State != Bot.STATE_EATING) b.State = Bot.STATE_EATING;
        }else{
            if(b.State != Bot.STATE_WALKING) b.State = Bot.STATE_WALKING;
        }

        Vec force_vector = c.cheeseDirection.ScalarMultiply(c.goalLength * 0.3);

        double MAX_FORCE_MAGNITUDE = b.GetMass();

        b.ApplyForceToCenter(force_vector.Clamp(MAX_FORCE_MAGNITUDE));
    }

    private class closestCheese{
        public Cheese cheese;
        public Vec cheeseDirection;
        public double goalLength;

        public closestCheese(Cheese cheese, Vec cheeseDirection, double goalLength){
            this.cheese = cheese;
            this.cheeseDirection = cheeseDirection;
            this.goalLength = goalLength;
        }
    }
}
