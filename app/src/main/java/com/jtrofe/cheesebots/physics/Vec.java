package com.jtrofe.cheesebots.physics;

import java.util.List;
import java.util.Random;

/**
 * A 2-dimensional vector class
 */
public class Vec {

    public double x;
    public double y;

    public int xi(){
        return (int) x;
    }
    public int yi(){
        return (int) y;
    }

    public float xf(){
        return (float) x;
    }
    public float yf(){
        return (float) y;
    }

    //
    //  Static methods
    //
    public static Vec Random(Vec max){
        Random r = new Random();

        return new Vec(r.nextDouble() * max.x, r.nextDouble() * max.y);
    }

    public static Vec RandomDir(double length){
        Random rnd = new Random();
        double l = length * 0.75 * rnd.nextDouble() + (length * 0.5);
        double angle = Math.PI * 2 * rnd.nextDouble();

        double dx = Math.sin(angle);
        double dy = -Math.cos(angle);

        return new Vec(dx * l, dy * l);
    }

    /**
     * Find the point in a list which is closest to a goal point
     * @param pointList A list of points
     * @param goal The goal point
     * @return Closest point
     */
    public static Vec GetClosestToPoint(List<Vec> pointList, Vec goal){
        double closest_distance = Double.MAX_VALUE;
        Vec closest_point = pointList.get(0);

        for(Vec v:pointList){
            double d = v.Subtract(goal).LengthSquared();

            if(d < closest_distance){
                closest_distance = d;
                closest_point = v.copy();
            }
        }

        return closest_point;
    }

    //
    //  Constructors
    //
    public Vec(){
        this.x = 0;
        this.y = 0;
    }

    public Vec(double x, double y){
        this.x = x;
        this.y = y;
    }

    public Vec copy(){
        return new Vec(x, y);
    }

    //
    //  Math operations
    //
    public Vec Subtract(Vec v){
        return new Vec(x - v.x, y - v.y);
    }

    public Vec Add(Vec v){
        return new Vec(x + v.x, y + v.y);
    }

    public Vec ScalarMultiply(double s){
        return new Vec(x * s, y * s);
    }

    public Vec ScalarDivide(double s){
        return new Vec(x / s, y / s);
    }

    public double Dot(Vec v){
        return x * v.x + y * v.y;
    }

    public Vec Normalize(){
        if(x == 0 && y == 0) return new Vec();

        return this.ScalarDivide(this.Length());
    }

    public Vec Clamp(double maxLength){
        if(this.LengthSquared() > (maxLength * maxLength)){
            return this.Normalize().ScalarMultiply(maxLength);
        }

        return new Vec(x, y);
    }

    public Vec Negate(){
        return new Vec(-x, -y);
    }

    public double LengthSquared(){
        return x * x + y * y;
    }

    public double Length(){
        return Math.sqrt(x * x + y * y);
    }

    @Override
    public String toString(){
        return "{" + x + ", " + y + "}";
    }
}
