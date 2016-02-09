package com.jtrofe.cheesebots.game.controllers;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import com.jtrofe.cheesebots.game.gameobjects.Bot;
import com.jtrofe.cheesebots.game.gameobjects.Cheese;
import com.jtrofe.cheesebots.game.gameobjects.GameObject;
import com.jtrofe.cheesebots.game.gameobjects.Particle;
import com.jtrofe.cheesebots.game.physics.Engine;
import com.jtrofe.cheesebots.game.physics.Vec;

import java.util.List;
import java.util.Random;

/**
 * Created by MAIN on 1/23/16
 */
public class BotController extends Controller{

    public BotController(Engine engine){
        super(engine);
    }

    @Override
    public void Update(float timeStep){
        List<GameObject> botList = mEngine.GetType(GameObject.TYPE_BOT);
        List<GameObject> cheeseList = mEngine.GetType(GameObject.TYPE_CHEESE);
        List<GameObject> flailList = mEngine.GetType(GameObject.TYPE_FLAIL);

        if(mEngine.LevelComplete && cheeseList.size() != 0){
            for (GameObject botObject : botList) {
                Bot b = (Bot) botObject;
                b.ReduceHealth(Float.MAX_VALUE);
                checkHealth(b);
            }
            return;
        }
        if(cheeseList.size() == 0){
            Vec center_of_mass = new Vec();

            for(GameObject b:botList){
                center_of_mass = center_of_mass.Add(b.GetPosition());
            }

            center_of_mass = center_of_mass.ScalarDivide(botList.size());

            for(GameObject b:botList){
                ((Bot) b).State = Bot.STATE_WALKING;
                // Move towards center of mass
                b.MoveTowardsPoint(center_of_mass, 0.1f, 50f);

                // Move bot away from nearby bots
                avoidBots((Bot) b, botList);

                avoidBots((Bot) b, flailList);

                checkHealth((Bot) b);
            }

        }else{
            for(GameObject botObject:botList){
                Bot b = (Bot) botObject;

                // Find the cheese the bot is closest to and get
                //   some data about how to get to it
                closestCheese c = getNearestCheese(b, cheeseList);
                Vec cheese_direction = c.cheeseDirection;

                // Align the bot to face its desired cheese
                b.SteerToAlign(cheese_direction);

                // If the bot is aligned with the cheese move towards it
                if(b.GetUnitY().Dot(cheese_direction) > 0.8){
                    targetCheese(b, c);
                }

                // Move bot away from nearby bots
                avoidBots(b, botList);


                avoidBots(b, flailList);

                checkHealth(b);
            }
        }
    }

    private void checkHealth(Bot b){
        if(b.IsAlive()) return;

        //TODO add explosion or something

        mEngine.AddBotDestroyed(b.ScrapMetal);

        if(mEngine.GetType(GameObject.TYPE_PARTICLE).size() < 100) {
            for (int i = 0; i < 20; i++) {
                Particle particle = new Particle(b.GetPosition(), Color.YELLOW, 40);
                mEngine.AddBody(particle);
            }
        }



        mEngine.RemoveBody(b);

        // Add another bot if the limit hasn't been reached
        if(mEngine.GetBotsDestroyed() < mEngine.MaxBots - mEngine.MaxBotsOnScreen + 1 && !mEngine.LevelComplete)
            AddBot();

        mEngine.mJitterControl.StartJitter(20);
    }

    private void AddBot(){
        Random rnd = new Random();
        float x;
        float y;
        if(rnd.nextFloat() > 0.5){
            x = rnd.nextFloat() * (mEngine.GetWorldWidth() + 200) - 100;

            if(rnd.nextFloat() > 0.5){
                y = mEngine.GetWorldHeight() + 100;
            }else{
                y = -100;
            }
        }else{
            y = rnd.nextFloat() * (mEngine.GetWorldHeight() + 200) - 100;

            if(rnd.nextFloat() > 0.5){
                x = mEngine.GetWorldWidth() + 100;
            }else{
                x = -100;
            }
        }

        Bitmap ic = Bitmap.createBitmap(100, 60, Bitmap.Config.ARGB_8888);


        Bot b = new Bot(new Vec(x, y), ic, 30, 0.02f);

        mEngine.AddBody(b);
    }


    //
    // Movement functions
    //
    /**
     * Boids function, prevent bots from getting too close to
     * each other by pushing them away from nearby bots
     * @param b The bot to push
     * @param botList List of other bots
     */
    private void avoidBots(Bot b, List<GameObject> botList){
        Vec push_vec = new Vec();

        for(GameObject other_bot:botList){
            if(other_bot == b) continue;

            Vec d = other_bot.GetPosition().Subtract(b.GetPosition());
            float dist = d.LengthSquared();

            if(dist < 10000){
                push_vec.x -= d.x;
                push_vec.y -= d.y;
            }
        }

        float FORCE_MULTIPLIER = 1.0f;

        b.ApplyForce(push_vec.ScalarMultiply(FORCE_MULTIPLIER), b.GetPosition());
    }

    private closestCheese getNearestCheese(Bot b, List<GameObject> cheeseList){
        float closest_distance = Float.MAX_VALUE;
        GameObject closest_cheese_object = cheeseList.get(0);
        Vec cheese_direction = new Vec(1, 0);
        float goal_length = 10;

        // The bot will try to get to the edge of the cheese, not the center of it
        float BACK_OFF_DISTANCE = b.GetBoundRadius() * 0.75f;

        for(GameObject cheese_object:cheeseList){
            Vec vector_to_center = cheese_object.GetPosition().Subtract(b.GetPosition());

            float back_off_distance = ((Cheese) cheese_object).GetRadius() + BACK_OFF_DISTANCE;

            float cheese_distance = vector_to_center.Length() - back_off_distance;

            if(cheese_distance < closest_distance){
                closest_distance = cheese_distance;
                closest_cheese_object = cheese_object;

                cheese_direction = vector_to_center.Normalize();
                goal_length = cheese_distance;
            }
        }

        return new closestCheese((Cheese) closest_cheese_object, cheese_direction, goal_length);
    }

    /**
     * Find the closest cheese to the bot and move towards it
     * @param b Bot to move
     * @param c The target cheese
     */
    private void targetCheese(Bot b, closestCheese c){
        // If we are close to the cheese, eat it
        float EATING_DISTANCE = 20;
        if(c.goalLength < EATING_DISTANCE){
            c.cheese.Eat(b.GetEatingSpeed());

            if(b.State != Bot.STATE_EATING) {
                b.CurrentFrame = 4;
                b.State = Bot.STATE_EATING;
            }
        }else{
            if(b.State != Bot.STATE_WALKING) {
                b.CurrentFrame = 0;
                b.State = Bot.STATE_WALKING;
            }
        }

        Vec force_vector = c.cheeseDirection.ScalarMultiply(c.goalLength * 0.3f);

        // Clamp force
        float MAX_FORCE_MAGNITUDE = 50;

        force_vector = force_vector.Clamp(MAX_FORCE_MAGNITUDE);

        b.ApplyForce(force_vector, b.GetPosition());
    }

    private class closestCheese{

        public Cheese cheese;
        public Vec cheeseDirection;
        public float goalLength;

        public closestCheese(Cheese cheese, Vec cheeseDirection, float goalLength){
            this.cheese = cheese;
            this.cheeseDirection = cheeseDirection;
            this.goalLength = goalLength;
        }
    }


}
