import com.google.gson.annotations.SerializedName;

import java.util.*;

public class Crane {
    @SerializedName("id")
    private int id;
    private boolean inUse = false;
    @SerializedName("x")
    private double x;
    @SerializedName("y")
    private double y;
    @SerializedName("ymin")
    private double ymin;
    @SerializedName("ymax")
    private double ymax;
    @SerializedName("xmax")
    private double xmax;
    @SerializedName("xmin")
    private double xmin;
    @SerializedName("xspeed")
    private double Vx; // Velocity in X-direction
    @SerializedName("yspeed")
    private double Vy; // Velocity in Y-direction
    List<CraneMovement> trajectory = new ArrayList<>();

    public double getXmax() {
        return xmax;
    }

    public double getXmin() {
        return xmin;
    }

    public List<CraneMovement> getTrajectory() {
        return trajectory;
    }
    public Coordinate getLocation() {
        return new Coordinate(x, y);
    }

    public int getId() {
        return id;
    }

    public boolean isInUse() {
        return inUse;
    }
    public void setInUse() {
        assert !inUse : "Crane was already in use";
        inUse = true;
    }
    public void setNotInUse() {
        assert inUse : "Crane not in use";
        inUse = false;
    }

    public double travelTime(Coordinate destination) {
        Coordinate startPoint = getLocation();
        double x_time_component = startPoint.getXdistance(destination)/Vx;
        double y_time_component = startPoint.getYdistance(destination)/Vy;
        return Math.max(y_time_component, x_time_component);
    }
    public Coordinate calculateIntermediatePoint(Coordinate end) {
        Coordinate start = new Coordinate(x, y);

        double x_time_component = start.getXdistance(end)/Vx;
        double y_time_component = start.getYdistance(end)/Vy;
        // Put a critical point in the trajectory
        if(x_time_component>y_time_component) return (new Coordinate((start.getX() + Vx*y_time_component) ,end.getY()));
        // else if(x_time_component<=y_time_component) ....
        else return (new Coordinate(end.getX(), (start.getY() + Vy*x_time_component)));
    }
    public Coordinate calculateIntermediatePoint(Coordinate start, Coordinate end) {
        double x_time_component = start.getXdistance(end)/Vx;
        double y_time_component = start.getYdistance(end)/Vy;
        // Put a critical point in the trajectory
        if(x_time_component>y_time_component) return (new Coordinate((start.getX() + Vx*y_time_component) ,end.getY()));
        // else if(x_time_component<=y_time_component) ....
        else return (new Coordinate(end.getX(), (start.getY() + Vy*x_time_component)));
    }
    public void addToTrajectory(CraneMovement movement) {
        trajectory.add(movement);
    }

    public boolean inRange(Coordinate start) {
        return xmin <= start.getX() && start.getX() <= xmax && ymin <= start.getY() && start.getY() <= ymax;
    }
    public void updateLocation(Coordinate containerLocation) {
        x = containerLocation.getX();
        y = containerLocation.getY();
    }

    public CraneMovement moveToSaveDistance(double timer, CraneMovement move1, CraneMovement move2) {
        double maxMovement = Math.max(Math.max(move1.getStartPoint().getX(), move2.getStartPoint().getX()), Math.max(move1.getEndPoint().getX(), move2.getEndPoint().getX()));
        double minMovement = Math.min(Math.max(move1.getStartPoint().getX(), move2.getStartPoint().getX()), Math.max(move1.getEndPoint().getX(), move2.getEndPoint().getX()));
        if (xmax >= maxMovement + 1) {
            Coordinate destination = new Coordinate(maxMovement+1, y);
            double endTime = timer + travelTime(destination);
            return new CraneMovement(this, destination, timer);
        }
//        else if (xmin + 1 <= minMovement) {
        else {
            Coordinate destination = new Coordinate(minMovement-1, y);
            double endTime = timer + travelTime(destination);
            return new CraneMovement(this, destination, timer);
        }
    }
    public CraneMovement moveToSaveDistance(double timer, CraneMovement move) {
        double maxMovement = Math.max(move.getStartPoint().getX(), move.getEndPoint().getX());
        double minMovement = Math.min(move.getStartPoint().getX(), move.getEndPoint().getX());
        if (xmax >= maxMovement + 1) {
            Coordinate destination = new Coordinate(maxMovement+1, y);
            double endTime = timer + travelTime(destination);
            return new CraneMovement(this, destination, timer);
        }
//        else if (xmin + 1 <= minMovement) {
        else {
            Coordinate destination = new Coordinate(minMovement-1, y);
            double endTime = timer + travelTime(destination);
            return new CraneMovement(this, destination, timer);
        }
    }

}
