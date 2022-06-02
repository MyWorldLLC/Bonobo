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
