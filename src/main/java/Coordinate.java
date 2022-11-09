public class Coordinate {
    private int x; // Crane itself moves over X
    private int y; // Crane head moves over Y
    private int z; // Height


    public Coordinate(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public int getXdistance(Coordinate coordinate) {
        return Math.abs(x - coordinate.x);
    }
    public int getYdistance(Coordinate coordinate) {
        return Math.abs(y - coordinate.y);
    }

    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }

}
