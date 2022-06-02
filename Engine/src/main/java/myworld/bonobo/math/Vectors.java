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
