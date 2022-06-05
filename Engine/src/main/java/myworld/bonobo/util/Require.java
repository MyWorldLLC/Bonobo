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

package myworld.bonobo.util;

import java.util.Arrays;

public class Require {

    public static <T> void equal(T test, T reference){
        if(!test.equals(reference)){
            throw new IllegalArgumentException("%s does not equal %s".formatted(test, reference));
        }
    }

    public static <T> void notEqual(T test, T reference){
        if(test.equals(reference)){
            throw new IllegalArgumentException("%s equals %s".formatted(test, reference));
        }
    }

    public static <T extends Number> void inRange(T test, T lowerBound, T upperBound){
        if(test.doubleValue() < lowerBound.doubleValue() || test.doubleValue() > upperBound.doubleValue()){
            throw new IllegalArgumentException("%s is out of the range [%s, %s]".formatted(test, lowerBound, upperBound));
        }
    }

    @SafeVarargs
    public static <T> void in(T test, T... values){
        for(T value : values){
            if(test.equals(value)){
                return;
            }
        }

        throw new IllegalArgumentException("%s is not contained in %s".formatted(test, Arrays.toString(values)));
    }

}
