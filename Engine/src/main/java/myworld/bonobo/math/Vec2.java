package myworld.bonobo.math;

import myworld.bonobo.util.Require;

public record Vec2(double x, double y) {

    public static final Vec2 ZERO = new Vec2(0);
    public static final Vec2 IDENTITY = new Vec2(1);

    public static final Vec2 UNIT_X = new Vec2(1,0);
    public static final Vec2 UNIT_Y = new Vec2(0,1);

    public static Vec2 of(double x, double y){
        return new Vec2(x, y);
    }

    public static Vec2 of(double[] data){
        Require.equal(data.length, 2);
        return new Vec2(data);
    }

    public Vec2(double fill){
        this(fill, fill);
    }

    public Vec2(double[] data){
        this(data[0], data[1]);
    }

    public Vec2 add(Vec2 v){
        return Vec2.of(x + v.x, y + v.y);
    }

    public Vec2 subtract(Vec2 v){
        return Vec2.of(x - v.x, y - v.y);
    }

    public Vec2 negate(){
        return multiply(-1);
    }

    public Vec2 multiply(double factor){
        return scale(factor);
    }

    public Vec2 divide(double factor){
        return scale(1.0/factor);
    }

    public Vec2 scale(double factor){
        return Vec2.of(x * factor, y * factor);
    }

    public double length(){
        return Math.sqrt(lengthSquared());
    }

    public double lengthSquared(){
        return x*x + y*y ;
    }

    public Vec2 normalize(){
        return divide(length());
    }

    public double dot(Vec2 v){
        return x*v.x + y*v.y;
    }

}
