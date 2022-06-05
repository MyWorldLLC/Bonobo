/*
 * Copyright 2022 MyWorld, LLC
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package myworld.bonobo.math;

import myworld.bonobo.util.Require;

public record Vec3(double x, double y, double z) {

    public static final Vec3 ZERO = new Vec3(0);
    public static final Vec3 IDENTITY = new Vec3(1);

    public static final Vec3 UNIT_X = new Vec3(1,0,0);
    public static final Vec3 UNIT_Y = new Vec3(0,1,0);
    public static final Vec3 UNIT_Z = new Vec3(0,0,1);

    public static Vec3 of(double x, double y, double z){
        return new Vec3(x, y, z);
    }

    public static Vec3 of(double[] data){
        Require.equal(data.length, 3);
        return new Vec3(data);
    }

    public static Vec3 of(Vec2 v, double z){
        return new Vec3(v, z);
    }

    public Vec3(double fill){
        this(fill, fill, fill);
    }

    public Vec3(double[] data){
        this(data[0], data[1], data[2]);
    }

    public Vec3(Vec2 v, double z){
        this(v.x(), v.y(), z);
    }

    public Vec3 add(Vec3 v){
        return Vec3.of(x + v.x, y + v.y, z + v.z);
    }

    public Vec3 subtract(Vec3 v){
        return Vec3.of(x - v.x, y - v.y, z - v.z);
    }

    public Vec3 negate(){
        return multiply(-1);
    }

    public Vec3 multiply(double factor){
        return scale(factor);
    }

    public Vec3 divide(double factor){
        return scale(1.0/factor);
    }

    public Vec3 scale(double factor){
        return Vec3.of(x * factor, y * factor, z * factor);
    }

    public double length(){
        return Math.sqrt(lengthSquared());
    }

    public double lengthSquared(){
        return x*x + y*y + z*z;
    }

    public double distanceSquared(Vec3 other){
        double dx = x - other.x;
        double dy = y - other.y;
        double dz = z - other.z;

        return dx * dx + dy * dy + dz * dz;
    }

    public double distance(Vec3 other){
        return Math.sqrt(distanceSquared(other));
    }

    public Vec3 normalize(){
        return divide(length());
    }

    public double dot(Vec3 v){
        return x*v.x + y*v.y + z*v.z;
    }

    public Vec3 cross(Vec3 v){
        return Vec3.of(
                y*v.z - z*v.y,
                z*v.x - x*v.z,
                x*v.y - y*v.x
        );
    }
}
