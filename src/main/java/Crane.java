import com.google.gson.annotations.SerializedName;

import java.util.*;

public class Crane {
    @SerializedName("x")
    private double x;
    @SerializedName("y")
    private double y;
    @SerializedName("ymin")
    private int ymin;
    @SerializedName("ymax")
    private int ymax;
    @SerializedName("id")
    private int id;
    @SerializedName("xmax")
    private int xmax;
    @SerializedName("xmin")
    private int xmin;
    @SerializedName("xspeed")
    private int Vx; // Velocity in X-direction
    @SerializedName("yspeed")
    private int Vy; // Velocity in Y-direction

    Map<Integer, Coordinate> trajectory;

    public Crane(int vx, int vy) {
        Vx = vx;
        Vy = vy;
        trajectory = new HashMap<>();
    }

    public int getVx() {
        return Vx;
    }

    public int getVy() {
        return Vy;
    }

    public Map<Integer, Coordinate> getTrajectory() {
        return trajectory;
    }


    public void addToTrajectory(Coordinate start, Coordinate end, int startTime, int endTime) {
        trajectory.put(startTime, start);

        double x_time_component = start.getXdistance(end)/Vx;
        double y_time_component = start.getYdistance(end)/Vy;
        // Put a critical point in the trajectory
        if(x_time_component>y_time_component) trajectory.put((int)(startTime + y_time_component), new Coordinate((start.getX() + Vx*y_time_component) ,end.getY()));
        else if(x_time_component<=y_time_component) trajectory.put((int)(startTime + x_time_component), new Coordinate(end.getX(), (start.getY() + Vy*x_time_component)));

        trajectory.put(endTime, end);
    }
    public boolean SafetyDistances(Crane crane2, int safeDistance) {
        List<Integer> times1 = new ArrayList(trajectory.keySet());
        Collections.sort(times1);
        for(int i=0; i<times1.size(); i++) {
            int t1 = times1.get(i);
            int t2 = times1.get(i+1);

            List<Integer> times2 = new ArrayList(crane2.getTrajectory().keySet());
            Collections.sort(times2);
            for(int j = 0; j <times2.size(); j++) {
                int t3 = times2.get(j);
                int t4 = times2.get(j);

                if(t1 < t2 && t2 < t3) {
                    Coordinate coordinate1 = trajectory.get(t1);
                    Coordinate coordinate2 = trajectory.get(t2);

                    Coordinate coordinate3 = crane2.getTrajectory().get(t3);
                    Coordinate coordinate4 = crane2.getTrajectory().get(t4);

                    double x1 = Math.min(coordinate1.getX(), coordinate2.getX());
                    double x2 = Math.max(coordinate1.getX(), coordinate2.getX());

                    double x3 = Math.min(coordinate4.getX(), coordinate3.getX());
                    double x4 = Math.max(coordinate4.getX(), coordinate3.getX());
;
                    return !(x2+safeDistance >= x3 && x1+safeDistance <= x4);
                }
            }
        }
        return true;
    }
}
