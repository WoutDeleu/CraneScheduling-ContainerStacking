import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InputTarget {
    @SerializedName("maxHeight")
    private int maxheight;
    @SerializedName("assignments")
    private List<Assignment> assignments = new ArrayList<>();


    public int getMaxheight() {
        return maxheight;
    }

    public Map<Integer, Assignment> getAssignmentsMap() {
        Map<Integer, Assignment> assign = new HashMap<>();
        for(Assignment assignment : this.assignments) {
            assign.put(assignment.getContainer_id(), assignment);
        }
        return assign;
    }

    public Slot getSlot_x_y(int x, int y, List<Slot> slots) {
        for (Slot slot : slots) {
            if(slot.getX() == x && slot.getY() == y) {
                return slot;
            }
        }
        assert false: "No slot found for the given x and y coordinates";
        return null;
    }


    public void formatAssignment(List<Container> containers, List<Slot> slots) {
        for(Assignment assignment : assignments) {
            int length = getContainerFromId(assignment.getContainer_id(), containers).getLength();
            Slot slot = getSlotFromId(assignment.getSlot_id(), slots);
            int y = slot.getY();
            for(int i= slot.getX(); i<length+slot.getX(); i++) {
                assignment.addSlot(getSlot_x_y(i, y, slots));
            }
        }
    }
    public Container getContainerFromId(int id, List<Container> containers) {
        for(Container container : containers) {
            if(container.getId() == id) {
                return container;
            }
        }
        assert false: "No container with id " + id;
        return null;
    }
    public void makeStacks(Field targetField, List<Container> containers) {
        for(Container container : containers) {
            int containerId = container.getId();
            List<Slot> slotsContainer = targetField.getSlot_containerId(containerId);
            for(Slot slot : slotsContainer) {
                slot.addToContainerStack(containerId);
            }
        }
    }

    public Slot getSlotFromId(int id, List<Slot> slots) {
        for(Slot slot : slots) {
            if(slot.getId() == id) {
                return slot;
            }
        }
        assert false: "No slot with id " + id;
        return null;
    }

    public static InputTarget readFile(String path) {
        InputTarget inputData = null;
        try {
            String jsonString = Files.readString(Paths.get(path));
            Gson gson = new Gson();
            inputData = gson.fromJson(jsonString, InputTarget.class);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return inputData;
    }

}
