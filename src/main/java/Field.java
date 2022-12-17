import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Field {
    private final int MAX_HEIGHT;
    private List<Slot> slots;
    private Map<Integer, Assignment> assignments; // key = containerId


    public Field(List<Slot> slot, Map<Integer, Assignment> assignment, int MAX_HEIGHT) {
        this.MAX_HEIGHT = MAX_HEIGHT;
        this.slots = deepCopy(slot);
        this.assignments = assignment;
    }

    private List<Slot> deepCopy(List<Slot> slot) {
        List<Slot>ret_slots = new ArrayList<>();
        for (Slot s : slot) {
            ret_slots.add(new Slot(s));
        }
        return ret_slots;
    }



    public List<Slot> getSlots() {
        return slots;
    }
    private int getMaxHeight() {
        return MAX_HEIGHT;
    }

    public boolean isContainerPlaced(int containerId) {
        return assignments.containsKey(containerId);
    }


    // Return a list of slots on which the container is placed
    public List<Slot> getSlot_containerId(int containerId) {
        List<Integer> slot_ids = assignments.get(containerId).getSlotIds();
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

    // For each slot, determine how many and which containers are on top.
    public Stack<Container> getContainers(int slotId, Map<Integer, Container> containers) {
        Stack<Container> returnList = new Stack<>();
        // loop over all assignments, where value == slotId
        // add to return list
        for(Assignment assignment : this.assignments.values()){
            for(int id : assignment.getSlotIds()) {
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

    //as soon as it finds a free space, return this free space
    public List<Slot> findAvailableSlots(Container container){
        List<Integer> slotHeights = new ArrayList<>();
        boolean sameHeight = true;
        for (int i = 0; i< slots.size(); i++){
            List<Slot> availableSlots = new ArrayList<>();
            for (int j = 0; i < container.getLength(); j++) {
                if (!(slots.get(i+j).getContainerStack().contains(container.getId()))) {
                    availableSlots.add(slots.get(i + j));
                }
            }
            for (Slot s : availableSlots){
                slotHeights.add(s.getTotalHeight());
            }
            //check if heights in currentslots are the same
            for (int k : slotHeights){
               if (!(k==slotHeights.get(0))){
                   sameHeight =false;
                   break;
               }
            }
            // if they are all the same height, return currentslots. else, move on to next slot in field
            if (sameHeight) return availableSlots;

        }
        return null;
    }
    public Coordinate getGrabbingPoint(int containerId) {
        List<Slot> slots = getSlot_containerId(containerId);
        List<Integer> x = new ArrayList<>();
        double y = slots.get(0).getY();
        double sum = 0;
        for (Slot slot : slots) {
            assert slot.getY() == y: "Fault in grabbing slots...";
            sum += slot.getX();
        }
        return new Coordinate(sum/ slots.size(), y);
    }

    // Check if destinationslots don't exceed maxHeight/on the same height/long enough
    public boolean isValidContainerDestination(Container container, List<Integer> destinationSlotIds) {
        // Check if all slots are the same height
        Slot firstSlot = slots.get(destinationSlotIds.get(0));
        int height = firstSlot.getTotalHeight();
        for(int slotId : destinationSlotIds) {
            if(slots.get(slotId) != null) {
                if(height != getSlot_slotId(slotId).getTotalHeight()) {
                    System.out.println("Container "+ container.getId()+" cannot be placed. -> Destination surface is not flat.");
                    return false;
                }
            }
            else {
                if(height != 0) {
                    System.out.println("Container "+ container.getId()+" cannot be placed. -> Destination surface is not flat.");
                    return false;
                }
            }
        }

        // Check if new height doesn't maxHeight
        if(height == MAX_HEIGHT) {
            System.out.println("Container "+ container.getId()+" cannot be placed. -> Max height exceeded.");
            return false;
        }

        // Check if destinationSlots have enough space
        if(destinationSlotIds.size() != container.getLength()) {
            System.out.println("Container "+ container.getId()+" cannot be placed. -> Not enough space to place the container.");
            return false;
        }

        // Check if corners container can snap
        if(firstSlot.containsContainers()) {
            for(int slotId : destinationSlotIds) {
                Slot slot = getSlot_slotId(slotId);
                int container_id = slot.peekStack();
                List<Integer> slot_ids = assignments.get(container_id).getSlotIds();
                for(int slot_id : slot_ids) {
                    if(!destinationSlotIds.contains(slot_id)) {
                        System.out.println("Container "+ container.getId()+" cannot be placed.");
                        System.out.print("-> Edges don't snap.");
                        return false;
                    }
                }
            }
        }
        return true;
    }

    // Check if container is on top
    public boolean isMovableContainer(Container container) {
        // Via assignments, get the right slots. Due to stacking constraints, if
        // one slots has a container on top, the whole container is on top.
        List<Slot> slotids = getSlot_containerId(container.getId());
        Slot slot = slotids.get(slotids.size()-1);
        if(container.getId() == slot.getContainerStack().peek()) {
            return true;
        }
        System.out.println("Container " + container.getId()+" cannot be moved");
        return false;
    }



    // Call only when all the prerequisites are met
    public void moveContainer(Container container, List<Integer> destinationSlotIs) {

        // Remove container from original: delete container from stacks of
        // the old slots, and clear the slot ids in the assignments.
        List<Slot> oldSlots = getSlot_containerId(container.getId());
        for (Slot s : oldSlots) {
            s.deleteTopStack();
        }
        assignments.remove(container.getId());

        placeContainer(container, destinationSlotIs);

        System.out.println("Container " + container.getId() + " is succesfully moved");
    }

    public void placeContainer(Container container, List<Integer> destinationSlotIds) {
        // Add container to new slots
        // Add container to slots itself
        for(int slotId : destinationSlotIds) {
            Slot slot =  getSlot_slotId(slotId);
            slot.addToContainerStack(container.getId());
        }

        // Add container to assignments
        assignments.put(container.getId(), new Assignment(container.getId(), destinationSlotIds));
    }



    public Slot[][] getFieldMatrix() {
        int length = 0, depth = 0;
        for(Slot slot : slots) {
            length = Math.max(slot.getX(), length);
            depth = Math.max(slot.getY(), depth);
        }
        depth++;
        length++;
        Slot[][] matrix = new Slot[depth][length];
        for(int i = 0; i< matrix.length; i++) {
            for(int j = 0; j< matrix[0].length; j++) {
                matrix[i][j] = null;
            }
        }
        for(Slot slot : slots) {
            int x = slot.getX();
            int y = slot.getY();
            matrix[y][x] = slot;

        }
        return matrix;
    }
}
