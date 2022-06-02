package myworld.bonobo.math;

public record Matrix3x3(double[] data) {

    public static final int N = 3;
    public static final int M = 3;

    public Matrix3x3(){
        this(new double[N*M]);
    }

    public double get(int i, int j){
        return Matrices.get(data, N, i, j);
    }

    public Vec3 getColumn(int i){
        return Vec3.of(Matrices.getColumn(data, N, M, i, Matrices.allocateColumn(M)));
    }

    public Vec3 getRow(int j){
        return Vec3.of(Matrices.getRow(data, N, j, Matrices.allocateRow(N)));
    }

    public Matrix3x3 multiply(Matrix3x3 b){
        return new Matrix3x3(Matrices.multiply(data, N, M, b.data, N, M, Matrices.allocateMatrix(N, M)));
    }

    public Matrix3x3 add(Matrix3x3 b){
        return new Matrix3x3(Matrices.add(data, b.data, Matrices.allocateMatrix(N, M)));
    }

    public Matrix3x3 subtract(Matrix3x3 b){
        return new Matrix3x3(Matrices.subtract(data, b.data, Matrices.allocateMatrix(N, M)));
    }

    public Matrix3x3 transpose(){
        return new Matrix3x3(Matrices.transpose(data, N, M, Matrices.allocateMatrix(N, M)));
    }

    public static Matrix3x3 identity(){
        return new Matrix3x3(Matrices.identity(N, Matrices.allocateMatrix(N, M)));
    }

}
