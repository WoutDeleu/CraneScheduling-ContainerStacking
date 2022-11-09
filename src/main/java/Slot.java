import java.util.Stack;

public class Slot {
    private int id;
    private int x;
    private int y;
    Stack<Integer> stack = new Stack<>();

    public void addToStack(int id) {
        stack.push(id);
    }

    public int getId() {
        return id;
    }

    public int getHeightContainer(int containerId) {
        return stack.indexOf(containerId);
    }
}
