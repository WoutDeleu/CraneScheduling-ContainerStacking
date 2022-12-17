public class CraneMovement {
    Crane crane;
    Coordinate start;
    Coordinate end;

    public CraneMovement(Crane crane, Coordinate start, Coordinate end) {
        this.crane = crane;
        this.start = start;
        this.end = end;
    }

    public void setCrane(Crane crane) {
        this.crane = crane;
    }

    public int travelTime() {
        double x_time_component = start.getXdistance(end)/crane.getVx();
        double y_time_component = start.getYdistance(end)/crane.getVy();
        return Math.max((int)y_time_component, (int) x_time_component);
    }
}