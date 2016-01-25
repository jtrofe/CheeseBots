package com.jtrofe.cheesebots.game.physics;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

import java.util.Random;

/**
 * Created by MAIN on 1/20/16
 */
public class Vec{

    public float x;
    public float y;

    public static Vec Random(float maxX, float maxY){
        Random rnd = new Random();
        float dx = rnd.nextFloat() * maxX;
        float dy = rnd.nextFloat() * maxY;

        return new Vec(dx, dy);
    }

    public Vec(){
        this.x = 0;
        this.y = 0;
    }

    public Vec(float x, float y){
        this.x = x;
        this.y = y;
    }

    public Vec(PointF point){
        this.x = point.x;
        this.y = point.y;
    }

    public Vec Clone(){
        return new Vec(x, y);
    }

    @Override
    public String toString(){
        return "(" + Math.round((x * 1000)/1000) + ", " +
                    Math.round((y * 1000)/1000) + ")";
    }

    /**
     * Math functions
     */
    public Vec Add(Vec v2){
        return new Vec(x + v2.x, y + v2.y);
    }

    public Vec Subtract(Vec v2){
        return new Vec(x - v2.x, y - v2.y);
    }

    public Vec ScalarDivide(float d){
        return new Vec(x / d, y / d);
    }

    public Vec ScalarMultiply(float d){
        return new Vec(x * d, y * d);
    }

    public float Length(){
        return (float) Math.sqrt(x * x + y * y);
    }

    public float LengthSquared(){
        return x * x + y * y;
    }

    public float Dot(Vec v2){
        return x * v2.x + y * v2.y;
    }

    public Vec Normalize(){
        float length = this.Length();

        return new Vec(x / length, y / length);
    }

    public Vec Negate(){
        return new Vec(-x, -y);
    }


    public void Circle(Canvas canvas, Paint p){
        canvas.drawCircle(x, y, 20, p);
    }

    public void Draw(Canvas canvas, Vec point, Paint p){
        canvas.drawLine(point.x, point.y, point.x + x, point.y + y, p);
    }


    public Vec Clamp(float maxLength){
        if(x * x + y * y > maxLength * maxLength){
            return Normalize().ScalarMultiply(maxLength);
        }

        return new Vec(x, y);
    }


    // Random helpful functions

    /**
     * Find the point in a list which is closest to a goal point
     * @param pointList A list of points
     * @param goal The goal point
     * @return Closest point
     */
    public static Vec GetClosestToPoint(Vec[] pointList, Vec goal){
        float closest_distance = Float.MAX_VALUE;
        Vec closest_point = pointList[0];

        for(Vec v:pointList){
            float d = v.Subtract(goal).LengthSquared();

            if(d < closest_distance){
                closest_distance = d;
                closest_point = v;
            }
        }

        return closest_point;
    }


    public static Vec[] GetVectorsFromPoint(Vec[] pointList, Vec start){
        Vec[] returnList = new Vec[pointList.length];

        for(int i=0;i<pointList.length;i++){
            returnList[i] = pointList[i].Subtract(start);
        }

        return returnList;
    }
}
