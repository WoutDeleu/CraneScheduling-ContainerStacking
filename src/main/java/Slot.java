import java.util.Stack;

public class Slot {
    private int id;
    private int x;
    private int y;

    public Slot(Slot s) {
        this.id = s.getId();
        this.x = s.getX();
        this.y = s.getY();
    }

    Stack<Integer> containerStack = new Stack<>();

    public void addToContainerStack(int id) {
        containerStack.push(id);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void deleteTopStack() {
        containerStack.pop();
    }

    public int getId() {
        return id;
    }
    public int getContainerIdByHeight(int height){
        int stackHeight = getTotalHeight();
        int containerId= containerStack.get(stackHeight-height);

        return containerId;
    }

    public boolean isStackEmpty() {
        return !containerStack.isEmpty();
    }
    public int getHeightContainer(int containerId) {
        return containerStack.indexOf(containerId)+1;
    }
    public int getTotalHeight() {
        return containerStack.size();
    }
    public Stack<Integer> getContainerStack() {
        return containerStack;
    }

    public String printStackInfo() {
        if(containerStack.empty()) {
            return "(.. , ..)";
        }
        return "(" + containerStack.peek() + ", " + containerStack.size() + ")";
    }

    public int peekStack() {
        return containerStack.peek();

    }

    public boolean containsContainer(int containerId) {
        return containerStack.contains(containerId);
    }
}
