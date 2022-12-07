public class Coordinate {
    private double x; // Crane itself moves over X
    private double y; // Crane head moves over Y


    public Coordinate(double x, double y) {
        this.x = x;
        this.y = y;
    }
    public double getXdistance(Coordinate coordinate) {
        return Math.abs(x - coordinate.x);
    }
    public double getYdistance(Coordinate coordinate) {
        return Math.abs(y - coordinate.y);
    }

    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }

}
