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

public record Vec4(double x, double y, double z, double w) {

    public static final Vec4 ZERO = new Vec4(0);
    public static final Vec4 IDENTITY = new Vec4(1);

    public static Vec4 of(double x, double y, double z, double w){
        return new Vec4(x, y, z, w);
    }

    public static Vec4 of(double[] data){
        Require.equal(data.length, 4);
        return new Vec4(data);
    }

    public static Vec4 of(Vec3 v, double w){
        return new Vec4(v, w);
    }

    public Vec4(double fill){
        this(fill, fill, fill, fill);
    }

    public Vec4(double[] data){
        this(data[0], data[1], data[2], data[3]);
    }

    public Vec4(Vec3 v, double w){
        this(v.x(), v.y(), v.z(), w);
    }
}
