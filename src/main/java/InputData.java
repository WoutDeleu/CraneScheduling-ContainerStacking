import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class InputData {
    @SerializedName("length")
    private int length;
    @SerializedName("width")
    private int width;
    @SerializedName("maxheight")
    private int maxHeight;
    @SerializedName("targetheight")
    private int targetHeight;


    @SerializedName("slots")
    private List<Slot> slots = new ArrayList<>();

    @SerializedName("cranes")
    private List<Crane> cranes = new ArrayList<>();
    @SerializedName("assignments")
    private List<Assignment> assignments = new ArrayList<>();
    @SerializedName("containers")
    private List<Container> containers = new ArrayList<>();


    public List<Crane> getCranes() {
        return cranes;
    }
    public List<Slot> getSlots() {
        return slots;
    }
    public int getTargetHeight() {
        return targetHeight;
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
    public Map<Integer, Assignment> getAssignmentsMap() {
        Map<Integer, Assignment> assign = new HashMap<>();
        for(Assignment assignment : this.assignments) {
            assign.put(assignment.getContainerId(), assignment);
        }
        return assign;
    }

    public void makeStacks(Field field) {
        Queue<Container> containerQ = new LinkedList<>(containers);
        while(!containerQ.isEmpty()) {
            Container container = containerQ.poll();
            List<Slot> slotsContainer = field.getSlot_containerId(container.getId());
            Slot firstSlot = slotsContainer.get(0);

            if (field.isSameHeight(firstSlot.getTotalHeight(), slotListToId(slotsContainer), container.getId()) && field.canContainerSnap(firstSlot, slotListToId(slotsContainer), container.getId())) {
                for (Slot slot : slotsContainer) {
                    slot.addToContainerStack(container.getId());
                }
            }
            else {
                containerQ.add(container);
            }
        }
    }
    private List<Integer> slotListToId(List<Slot> slotsContainer) {
        List<Integer> result = new ArrayList<Integer>();
        for(Slot slot : slotsContainer) {
            result.add(slot.getId());
        }
        return result;
    }


    public void formatAssignment() {
        for(Assignment assignment : assignments) {
            int length = getContainerFromId(assignment.getContainerId()).getLength();
            Slot slot = getSlotFromId(assignment.getSlot_id());
            int y = slot.getY();
            for(int i= slot.getX(); i<length+slot.getX(); i++) {
                assignment.addSlot(getSlot_x_y(i, y));
            }
        }
    }
    public Container getContainerFromId(int id) {
        for(Container container : this.containers) {
            if(container.getId() == id) {
                return container;
            }
        }
        assert false: "No container with id " + id;
        return null;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public Slot getSlotFromId(int id) {
        for(Slot slot : this.slots) {
            if(slot.getId() == id) {
                return slot;
            }
        }
        assert false: "No slot with id " + id;
        return null;
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

    public static InputData readFile(String path) {
        InputData inputData = null;
        try {
            String jsonString = Files.readString(Paths.get(path));
            Gson gson = new Gson();
            inputData = gson.fromJson(jsonString, InputData.class);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return inputData;
    }
}
