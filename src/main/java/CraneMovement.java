public class CraneMovement {
    private Crane crane;
    private Coordinate startPoint;
    private Coordinate intermediatePoint;
    private Coordinate endPoint;
    private double startTime;

    public CraneMovement(Crane crane, Coordinate end, double startTime) {
        this.crane = crane;
        this.startPoint = crane.getLocation();
        this.endPoint = end;
        this.startTime = startTime;
        this.intermediatePoint = crane.calculateIntermediatePoint(endPoint);
    }
    public CraneMovement(Crane crane, Coordinate begin, Coordinate end, double startTime) {
        this.crane = crane;
        this.startPoint = begin;
        this.endPoint = end;
        this.startTime = startTime;
        this.intermediatePoint = crane.calculateIntermediatePoint(begin, endPoint);
    }

    public double getStartTime() {
        return startTime;
    }
    public double getEndTime() {
        return startTime + travelTime();
    }

    public Crane getCrane() {
        return crane;
    }
    public int getCraneId() {
        return crane.getId();
    }

    public Coordinate getStartPoint() {
        return startPoint;
    }

    public Coordinate getEndPoint() {
        return endPoint;
    }

    public double travelTime() {
        return crane.travelTime(endPoint);
    }

    public boolean colides(int safeDistance, Crane otherCrane) {
        for(CraneMovement move : otherCrane.getTrajectory()) {
            if(hasOverlapTime(move)) {
                if(hasOverlapTraject(safeDistance, move)) return true;
            }
        }
        return false;
    }
    private boolean hasOverlapTime(CraneMovement move) {
     return (move.getStartTime() <= getEndTime() && move.getEndTime() >= startTime);
    }
    private boolean hasOverlapTraject(int safeDistance, CraneMovement move) {
        return (move.getStartPoint().getX()+safeDistance <= endPoint.getX() && move.getEndPoint().getX() >= startPoint.getX()+safeDistance);

    }

    public void updateTimer(double timer) {
        startTime = timer;
    }
}
