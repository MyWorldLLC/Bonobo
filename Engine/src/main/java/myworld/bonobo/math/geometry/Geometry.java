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

package myworld.bonobo.math.geometry;

import myworld.bonobo.math.Vec3;

public class Geometry {

    public static boolean spheresOverlap(Vec3 originA, double radiusA, Vec3 originB, double radiusB){
        double distanceSq = originA.distanceSquared(originB);
        return distanceSq < radiusA * radiusA || distanceSq < radiusB * radiusB;
    }

    public static boolean sphereContains(Vec3 origin, double radius, Vec3 point){
        double distanceSq = origin.distanceSquared(point);
        return distanceSq <= radius * radius;
    }

    public static boolean sphereContains(Vec3 originA, double radiusA, Vec3 originB, double radiusB){
        double distanceSq = originA.distanceSquared(originB);
        return distanceSq + radiusB * radiusB < radiusA * radiusA;
    }

    public static boolean boxIntersects(Box a, Box b) {
        return inRange(
                        a.origin().x() - a.dx(),
                        a.origin().x() + a.dx(),
                        b.origin().x() - b.dx(),
                        b.origin().x() + b.dx()) &&
                inRange(
                        a.origin().y() - a.dy(),
                        a.origin().y() + a.dy(),
                        b.origin().y() - b.dy(),
                        b.origin().y() + b.dy()) &&
                inRange(
                        a.origin().z() - a.dz(),
                        a.origin().z() + a.dz(),
                        b.origin().z() - b.dz(),
                        b.origin().z() + b.dz()
                );
    }

    public static boolean inRange(double aMin, double aMax, double bMin, double bMax){
        return aMin <= bMax && bMin <= aMax;
    }

}
