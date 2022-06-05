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

public class Vectors {
    public static double dot(double[] a, double[] b){
        Require.equal(a.length, b.length);
        double result = 0;
        for(int i = 0; i < a.length; i++){
            result += a[i] * b[i];
        }
        return result;
    }

    public static double[] allocateVector(int n){
        return new double[n];
    }
}
