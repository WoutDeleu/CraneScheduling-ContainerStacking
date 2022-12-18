public class FullMovement {
    public int craneId;
    // containerId = -1 represents movement without container
    public int containerId;
    public double pickupTime;
    public double endTime;
    public double pickupPosX;
    public double pickupPosY;
    public double endPosX;
    public double endPosY;

    // containerId = -1 represents movement without container
    public FullMovement(int craneId, int containerId, double pickupTime, double endTime) {
        this.craneId = craneId;
        this.containerId = containerId;
        this.pickupTime = pickupTime;
        this.endTime = endTime;
    }

    public FullMovement(int craneId, int containerId, double pickupTime, double endTime, Coordinate startPosition, Coordinate endPosition) {
        this.craneId = craneId;
        this.containerId = containerId;
        this.pickupTime = pickupTime;
        this.endTime = endTime;
        this.pickupPosX = startPosition.getX();
        this.pickupPosY = startPosition.getY();
        this.endPosX = endPosition.getX();
        this.endPosY = endPosition.getY();
    }

    public double getPickupTime() {
        return pickupTime;
    }

    public int getCraneId() {
        return craneId;
    }

    public double getEndTime() {
        return endTime;
    }

    public Coordinate getEndPoint() {
        return new Coordinate(endPosX, endPosY);
    }
    public Coordinate getStartPoint() {
        return new Coordinate(pickupPosX, pickupPosY);
    }
    public void setStartLocation(Coordinate coordinate) {
        pickupPosX = (coordinate.getX());
        pickupPosY = (coordinate.getY());
    }
    public void setEndLocation(Coordinate coordinate) {
        endPosX = (coordinate.getX());
        endPosX = (coordinate.getY());
    }

    @Override
    public String toString() {
        if(containerId != -1) return craneId + ";" + containerId + ";" + pickupTime + ";" + endTime + ";" + ";" + pickupPosX + ";" + pickupPosY + ";" + endPosX + ";" + endPosY + ";";
        else return craneId + ";" + ";" + pickupTime + ";" + endTime + ";" + ";" + pickupPosX + ";" + pickupPosY + ";" + endPosX + ";" + endPosY + ";";

    }
}
