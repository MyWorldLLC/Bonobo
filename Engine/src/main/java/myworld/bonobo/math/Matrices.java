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

import java.util.Arrays;

public final class Matrices {

    public static double get(double[] data, int n, int i, int j){
        return data[n*i + j];
    }

    public static void set(double[] data, int n, int i, int j, double value){
        data[n*i + j] = value;
    }

    public static double[] getColumn(double[] data, int n, int m, int i, double[] storage){
        for(int j = 0; j < m; j++){
            storage[j] = get(data, n, i, j);
        }
        return storage;
    }

    public static void setColumn(double[] matrix, int n, int m, int i, double[] col){
        for(int j = 0; j < m; j++){
            set(matrix, n, i, j, col[j]);
        }
    }

    public static double[] allocateColumn(int m){
        return new double[m];
    }

    public static double[] allocateRow(int n){
        return new double[n];
    }

    public static double[] allocateMatrix(int n, int m){
        return new double[n*m];
    }

    public static float[] allocateFloatMatrix(int n, int m){
        return new float[n*m];
    }

    public static double[] getRow(double[] data, int n, int j, double[] storage){
        for(int i = 0; i < n; i++){
            storage[i] = get(data, n, i, j);
        }
        return storage;
    }

    public static void setRow(double[] matrix, int n, int j, double[] row){
        for(int i = 0; i < n; i++){
            set(matrix, n, i, j, row[i]);
        }
    }

    public static double[] fill(double value, double[] storage){
        Arrays.fill(storage, value);
        return storage;
    }

    public static double[] identity(int n, double[] storage){
        for(int i = 0; i < n; i++){
            set(storage, n, i, i, 1);
        }
        return storage;
    }

    public static double[] add(double[] a, double[] b, double[] storage){
        for(int i = 0; i < a.length; i++){
            storage[i] = a[i] + b[i];
        }
        return storage;
    }

    public static double[] subtract(double[] a, double[] b, double[] storage){
        for(int i = 0; i < a.length; i++){
            storage[i] = a[i] - b[i];
        }
        return storage;
    }

    public static double[] multiply(double[] a, int na, int ma, double[] b, int nb, int mb, double[] storage){
        double[] rowA = allocateRow(na);
        double[] colB = allocateColumn(mb);

        for(int row = 0; row < ma; row++){
            for(int col = 0; col < nb; col++){
                getRow(a, na, row, rowA);
                getColumn(b, nb, mb, col, colB);

                set(storage, nb, col, row, Vectors.dot(rowA, colB));
            }
        }
        return storage;
    }

    public static double[] multiplyVector(double[] a, int na, int ma, double[] v, double[] storage){
        double[] rowA = allocateRow(na);

        for(int row = 0; row < ma; row++){
            getRow(a, na, row, rowA);
            set(storage, 1, 0, row, Vectors.dot(rowA, v));
        }
        return storage;
    }

    public static double[] transpose(double[] matrix, int n, int m, double[] storage){
        double[] tmp = new double[n];
        for(int row = 0; row < m; row++){
            getRow(matrix, n, row, tmp);
            setColumn(storage, m, n, row, tmp);
        }
        return storage;
    }

}
