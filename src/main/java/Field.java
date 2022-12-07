import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Field {
    private final int MAX_HEIGHT;
    private List<Slot> slots;
    private Map<Integer, Assignment> assignment;

    public List<Slot> getSlots() {
        return slots;
    }
    public Field(List<Slot> slot, Map<Integer, Assignment> assignment, int MAX_HEIGHT) {
        this.MAX_HEIGHT = MAX_HEIGHT;
        this.slots = slot;
        this.assignment = assignment;
    }
    // Return a list of slots on which the container is placed
    public List<Slot> getSlot_containerId(int containerId) {
        List<Integer> slot_ids = assignment.get(containerId).getSlot_ids();
        List<Slot> returnList = new ArrayList<>();
        for(int id : slot_ids) {
            for(Slot slot : slots) {
                if(slot.getId() == (id)) {
                    returnList.add(slot);
                }
            }
        }
        return returnList;
    }

    public Slot getSlot_slotId(int slotId) {
        for(Slot slot : slots) {
            if(slot.getId() == slotId) {
                return slot;
            }
        }
        return null;
    }

    public boolean isContainerPlaced(int containerId) {
        return assignment.containsKey(containerId);
    }

    // For each slots, determine how many and which containers are on top.
    public Stack<Container> getContainers(int slotId, Map<Integer, Container> containers) {
        Stack<Container> returnList = new Stack<>();
        // loop over all assignments, where value == slotId
        // add to return list
        for(Assignment assignment : this.assignment.values()){
            for(int id : assignment.getSlot_ids()) {
                if (slotId==id) {
                    returnList.push(containers.get(id));
                }
            }
        }
        return returnList;
    }

    public int getHeightContainer(int containerId) {
        return getSlot_containerId(containerId).get(0).getHeightContainer(containerId);
    }

    // Eventueel nog een find available slots...

    // Check if destinationslots don't exceed maxHeight/on the same height/long enough
    public boolean isValidContainerDestination(Container container, ArrayList<Integer>  destinationSlots) {
        // Check if all slots are the same height
        int height = 0;
        if(slots.get(0) != null) height = slots.get(0).getTotalHeight();
        for(int slotId : destinationSlots) {
            if(slots.get(slotId) != null) {
                System.out.println("Container "+ container.getId()+" cannot be placed.");
                System.out.println("Destination surface is not flat.");
                if(height != slots.get(slotId).getTotalHeight()) return false;
            }
            else {
                System.out.println("Container "+ container.getId()+" cannot be placed.");
                System.out.println("Destination surface is not flat.");
                if(height != 0) return false;
            }
        }

        // Check if new height doesn't maxHeight
        if(height == MAX_HEIGHT) {
            System.out.println("Container "+ container.getId()+" cannot be placed.");
            System.out.println("Max height exceeded.");
            return false;
        }

        // Check if destinationSlots have enough space
        if(destinationSlots.size() != container.getLength()) {
            System.out.println("Container "+ container.getId()+" cannot be placed.");
            System.out.println("Not enough space to place the container.");
            return false;
        }

        // Check if corners container can snap
        for(int slotId : destinationSlots) {
            Slot slot = getSlot_slotId(slotId);
            int container_id = slot.peekStack();
            ArrayList<Integer> slot_ids = assignment.get(container_id).getSlot_ids();
            for(int slot_id : slot_ids) {
                if(!destinationSlots.contains(slot_id)) return false;
            }
        }
        return true;
    }

    // Check if container is on top
    public boolean isMovableContainer(Container container) {
        // Via assignment, get the right slots. Due to stacking constraints, if
        // one slots has the container on top, the whole container is on top.
        List<Slot> slotids = getSlot_containerId(container.getId());
        Slot slot = slotids.get(slotids.size()-1);
        if(container.getId() == slot.getStack().peek()) {
            return true;
        }
        return false;
    }

    // Call only when all the prerequisites are met
    public void moveContainer(Container container, ArrayList<Integer> destinationSlots_id) {

        // Remove container from original: delete container from stacks of
        // the old slots, and clear the slot ids in the assignment.
        List<Slot> oldSlots = getSlot_containerId(container.getId());
        for (Slot s : oldSlots) {
            s.deleteTopStack();
        }
        assignment.remove(container.getId());

        placeContainer(container, destinationSlots_id);
    }

    public void placeContainer(Container container, ArrayList<Integer> destinationSlots_id) {
        // Add container to new slots
        // Add container to slots itself
        for(int slotId : destinationSlots_id) {
            Slot slot =  getSlot_slotId(slotId);
            slot.addToStack(container.getId());
        }

        // Add container to assignments
        assignment.put(container.getId(), new Assignment(container.getId(), destinationSlots_id));
    }

    public Slot[][] getFieldMatrix() {
        int length = 0, depth = 0;
        for(Slot slot : slots) {
            length = Math.max(slot.getX(), length);
            depth = Math.max(slot.getY(), depth);
        }
        depth++;
        length++;
        Slot[][] matrix = new Slot[length][depth];
        for(int i = 0; i< matrix.length; i++) {
            for(int j = 0; j< matrix[0].length; j++) {
                matrix[i][j] = null;
            }
        }
        for(Slot slot : slots) {
            int x = slot.getX();
            int y = slot.getY();
            matrix[x][y] = slot;

        }
        return matrix;
    }
}
