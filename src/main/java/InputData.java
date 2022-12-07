import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InputData {
    @SerializedName("length")
    private int length;
    @SerializedName("width")
    private int width;
    @SerializedName("maxHeight")
    private int maxHeight;

    @SerializedName("slots")
    private List<Slot> slots = new ArrayList<>();
    @SerializedName("cranes")
    private List<Crane> cranes = new ArrayList<>();
    @SerializedName("assignments")
    private List<Assignment> assignments = new ArrayList<>();
    @SerializedName("containers")
    private List<Container> containers = new ArrayList<>();



    public List<Slot> getSlots() {
        return slots;
    }
    public List<Container> getContainers() {
        return containers;
    }
    public Map<Integer, Container> getContainersMap() {
        Map<Integer, Container> cont = new HashMap<>();
        for(Container container : this.containers) {
            cont.put(container.getId(), container);
        }
        return cont;
    }
    public List<Assignment> getAssignments() {
        return assignments;
    }
    public Map<Integer, Assignment> getAssignmentsMap() {
        Map<Integer, Assignment> assign = new HashMap<>();
        for(Assignment assignment : this.assignments) {
            assign.put(assignment.getContainer_id(), assignment);
        }
        return assign;
    }

    public void makeStacks(Field field) {
        for(Container container : containers) {
            int containerId = container.getId();
            List<Slot> slotsContainer = field.getSlot_containerId(containerId);
            for(Slot slot : slotsContainer) {
                slot.addToStack(containerId);
            }
        }
    }

    public void formatAssignment() {
        for(Assignment assignment : assignments) {
            int length = containers.get(assignment.getContainer_id()).getLength();
            Slot slot = slots.get(assignment.getSlot_id());
            int y = slot.getY();
            for(int i= slot.getX(); i<=length+slot.getX(); i++) {
                assignment.addSlot(getSlot_x_y(i, y));
            }
        }
    }
    public Slot getSlot_x_y(int x, int y) {
        for (Slot slot : slots) {
            if(slot.getX() == x && slot.getY() == y) {
                return slot;
            }
        }
        assert false: "No slot found for the given x and y coordinates";
        return null;
    }
}
