public class ContainerMovement {
    private int containerId;
    private Coordinate start;
    private Coordinate end;

    public ContainerMovement(int containerId, Coordinate start, Coordinate end) {
        this.containerId = containerId;
        this.start = start;
        this.end = end;
    }

    public Coordinate getStart() {
        return start;
    }

    public Coordinate getEnd() {
        return end;
    }

    public int getContainerId() {
        return containerId;
    }
}
