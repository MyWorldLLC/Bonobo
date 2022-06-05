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

public class TransformBuffer {

    protected final double[] matrix;
    protected final double[] tmpMatrix;
    protected final double[] tmpVec;
    protected final float[] gpuTransform;
    protected boolean transformDirty;

    public TransformBuffer(){
        matrix = Matrices.allocateMatrix(4, 4);
        tmpMatrix = Matrices.allocateMatrix(4, 4);
        tmpVec = Vectors.allocateVector(4);
        gpuTransform = Matrices.allocateFloatMatrix(4, 4);
        identity();
        buildGpuTransform();
    }

    public double get(int i, int j){
        return Matrices.get(matrix, 4, i, j);
    }

    public void set(int i, int j, double v){
        Matrices.set(matrix, 4, i, j, v);
        markDirty();
    }

    public TransformBuffer zero(){
        Arrays.fill(matrix, 0);
        markDirty();
        return this;
    }

    public TransformBuffer identity(){
        Matrices.identity(4, matrix);
        markDirty();
        return this;
    }

    public TransformBuffer translate(double x, double y, double z){
        set(0, 3, get(0, 3) + x);
        set(1, 3, get(1, 3) + y);
        set(2, 3, get(2, 3) + z);
        markDirty();
        return this;
    }

    public TransformBuffer concatenate(Matrix4x4 b){
        Matrices.multiply(matrix, 4, 4, b.data(), 4, 4, tmpMatrix);
        System.arraycopy(tmpMatrix, 0, matrix, 0, 4);
        markDirty();
        return this;
    }

    public TransformBuffer rotate(Quaternion q){
        // TODO - convert q to rotation matrix, then multiply
        System.arraycopy(tmpMatrix, 0, matrix, 0, 4);
        markDirty();
        return this;
    }

    public void column(int i, double[] storage){
        for(int x = 0; x < 4; x++){
            storage[x] = get(i, x);
        }
    }

    public Vec4 column(int i){
        return Vec4.of(get(i, 0), get(i, 1), get(i, 2), get(i, 3));
    }

    public void row(int j, double[] storage){
        for(int x = 0; x < 4; x++){
            storage[x] = get(x, j);
        }
    }

    public Vec4 row(int j){
        return Vec4.of(get(j, 0), get(j, 1), get(j, 2), get(j, 3));
    }

    protected double[] fillTmpVec(double a, double b, double c, double d){
        tmpVec[0] = a;
        tmpVec[1] = b;
        tmpVec[2] = c;
        tmpVec[3] = d;
        return tmpVec;
    }

    public boolean needsGpuTransformRefresh(){
        return transformDirty;
    }

    public float[] buildGpuTransform(){
        for(int i = 0; i < gpuTransform.length; i++){
            gpuTransform[i] = (float) matrix[i];
        }
        transformDirty = false;
        return gpuTransform;
    }

    public float[] getGpuTransform(){
        return gpuTransform;
    }

    private void markDirty(){
        transformDirty = true;
    }

}
