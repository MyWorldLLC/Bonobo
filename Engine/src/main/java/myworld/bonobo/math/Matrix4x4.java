package myworld.bonobo.math;

public record Matrix4x4(double[] data) {

    public static final int N = 4;
    public static final int M = 4;

    public Matrix4x4(){
        this(new double[N*M]);
    }

    public double get(int i, int j){
        return Matrices.get(data, N, i, j);
    }

    public Vec4 getColumn(int i){
        return Vec4.of(Matrices.getColumn(data, N, M, i, Matrices.allocateColumn(M)));
    }

    public Vec4 getRow(int j){
        return Vec4.of(Matrices.getRow(data, N, j, Matrices.allocateRow(N)));
    }

    public Matrix4x4 multiply(Matrix4x4 b){
        return new Matrix4x4(Matrices.multiply(data, N, M, b.data, N, M, Matrices.allocateMatrix(N, M)));
    }

    public Matrix4x4 add(Matrix4x4 b){
        return new Matrix4x4(Matrices.add(data, b.data, Matrices.allocateMatrix(N, M)));
    }

    public Matrix4x4 subtract(Matrix4x4 b){
        return new Matrix4x4(Matrices.subtract(data, b.data, Matrices.allocateMatrix(N, M)));
    }

    public Matrix4x4 transpose(){
        return new Matrix4x4(Matrices.transpose(data, N, M, Matrices.allocateMatrix(N, M)));
    }

    public static Matrix4x4 identity(){
        return new Matrix4x4(Matrices.identity(N, Matrices.allocateMatrix(N, M)));
    }
}
