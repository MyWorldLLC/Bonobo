package myworld.bonobo.math.geometry;

import myworld.bonobo.math.Vec3;

public record Sphere(Vec3 origin, double radius) {

    public boolean overlaps(Sphere other){
        return Geometry.spheresOverlap(origin, radius, other.origin, other.radius);
    }

    public boolean contains(Sphere other){
        return Geometry.sphereContains(origin, radius, other.origin, other.radius);
    }

    public boolean contains(Vec3 point){
        return Geometry.sphereContains(origin, radius, point);
    }
}
