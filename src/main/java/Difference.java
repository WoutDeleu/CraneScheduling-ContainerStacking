import java.util.List;

public class Difference {
    private Assignment assignment;
    private int height;

    public Difference(int height, Assignment assignment) {
        this.assignment = assignment;
        this.height = height;
    }

    public int getHeight() {
        return height;
    }

    public int getContainerId() { return assignment.getContainerId(); }

    public List<Integer> getSlotIds() { return assignment.getSlotIds(); }
}