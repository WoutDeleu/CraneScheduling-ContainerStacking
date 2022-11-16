import java.util.Stack;

public class Slot {
    private int id;
    private int x;
    private int y;
    Stack<Integer> stack = new Stack<>();

    public void addToStack(int id) {
        stack.push(id);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void deleteTopStack() {
        stack.pop();
    }

    public int getId() {
        return id;
    }


    public int getHeightContainer(int containerId) {
        return stack.indexOf(containerId)+1;
    }
    public int getTotalHeight() {
        return stack.size();
    }
    public Stack<Integer> getStack() {
        return stack;
    }

    public String printStackInfo() {
        return "(" + stack.peek() + ", " + stack.size() + ")";
    }

    public int peekStack() {
        return stack.peek();

    }
}
