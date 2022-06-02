package myworld.bonobo.math.geometry;

import myworld.bonobo.math.Vec3;

public record Box(Vec3 origin, double dx, double dy, double dz) {

    public boolean intersects(Box other){
        return Geometry.boxIntersects(this, other);
    }
}
