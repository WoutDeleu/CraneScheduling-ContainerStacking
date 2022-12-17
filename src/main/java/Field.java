import java.util.*;

public class Field {
    private int MAX_HEIGHT;
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


    public void setMAX_HEIGHT(int targetHeight) {
        this.MAX_HEIGHT = targetHeight;
    }

    public List<Slot> getSlots() {
        return slots;
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

/*    //as soon as it finds a free space, return this free space
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
    }*/
    // Find fittintg slots to move a container to
    public List<Integer>[] findAvailableSlots(Container container) {
        int length = container.getLength();
        List<Integer>[] availableSlots = new List[0];
        List<Integer> possibleDestinations = new ArrayList<>();
        for(int slotId1 = 0; slotId1 < slots.size()-length; slotId1++) {
            for(int i = 0; i < length; i++) {
                possibleDestinations.add(slotId1 + i);
            }
            if (slotsAreSequential(possibleDestinations)) {
                if(isValidContainerDestination(container, possibleDestinations)) {
                    availableSlots = Util.addToArray(availableSlots, possibleDestinations);
                }
            }
            possibleDestinations.clear();
        }
        assert availableSlots.length != 0 : "No slots available...";
        return availableSlots;
    }


    public List<Integer> findContainersExceedingHeight(int targetHeight) {
        List<Integer> containersToMove = new ArrayList<>();
        for(Slot slot : slots) {
            if(targetHeight < slot.getTotalHeight()) {
                slot.addContainersExceedingHeight(targetHeight, containersToMove);
            }
        }
        return containersToMove;
    }

    /**************************************CHECK MOVABLE**************************************/
    // Check if destinationslots don't exceed maxHeight/on the same height/long enough
    public boolean isValidContainerDestination(Container container, List<Integer> destinationSlotIds) {
        Collections.sort(destinationSlotIds);
        assert slotsAreSequential(destinationSlotIds): "Destination slots are not sequential";

        // Check if all slots are the same height
        Slot firstSlot = slots.get(destinationSlotIds.get(0));
        int height = firstSlot.getTotalHeight();

        if(!isSameHeight(height, destinationSlotIds, container.getId())) return false;

        // Check if new height doesn't maxHeight
        if(height == MAX_HEIGHT) {
            System.out.println("Container "+ container.getId()+" cannot be placed -> Max height exceeded.");
            return false;
        }

        // Check if destinationSlots have enough space
        if(destinationSlotIds.size() != container.getLength()) {
            System.out.println("Container "+ container.getId()+" cannot be placed -> Not enough space to place the container.");
            return false;
        }

        // Check if slots contain current container
        if(!slotsContainCurrentContainer(container, destinationSlotIds))

        // Check if corners container can snap
        if(!canContainerSnap(firstSlot, destinationSlotIds, container.getId())) return false;

        return true;
    }

    private boolean slotsContainCurrentContainer(Container container, List<Integer> destinationSlotIds) {
        for(int slotId : destinationSlotIds) {
            Slot slot =  getSlot_slotId(slotId);
            if (slot.containsContainer(container.getId())) {
                System.out.println("The destinationslot is the slot which has the container already on it.");
                return true;
            }
        }
        return false;
    }

    private boolean canContainerSnap(Slot firstSlot, List<Integer> destinationSlotIds, int id) {
        if(firstSlot.isStackEmpty()) {
            for(int slotId : destinationSlotIds) {
                Slot slot = getSlot_slotId(slotId);
                int container_id = slot.peekStack();
                List<Integer> slot_ids = assignments.get(container_id).getSlotIds();
                for(int slot_id : slot_ids) {
                    if(!destinationSlotIds.contains(slot_id)) {
                        System.out.println("Container "+ id + " cannot be placed -> Edges don't snap.");
                        return false;
                    }
                }
            }
        }
        return true;
    }
    public boolean isSameHeight(int startingHeight, List<Integer> slotIds, int containerId) {
        for(int slotId : slotIds) {
            if(slots.get(slotId) != null) {
                if(startingHeight != getSlot_slotId(slotId).getTotalHeight()) {
                    System.out.println("Container "+ containerId+" cannot be placed -> Destination surface is not flat.");
                    return false;
                }
            }
            else {
                if(startingHeight != 0) {
                    System.out.println("Container "+ containerId+" cannot be placed -> Destination surface is not flat.");
                    return false;
                }
            }
        }
        return true;
    }
    public boolean slotsAreSequential(List<Integer> slotIds) {
        int x = getSlot_slotId(slotIds.get(0)).getX();
        for(int i = 1; i < slotIds.size(); i++) {
            if(x+i != getSlot_slotId(slotIds.get(i)).getX()) {
                return false;
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


    public boolean containerHasCorrectHeight(List<Integer> destinationSlotIds, int requiredHeight, int containerId) {
        Slot slot = getSlot_slotId(destinationSlotIds.get(0));
        boolean result = (slot.getTotalHeight() == requiredHeight -1);
        if(!result) System.out.println("Container "+ containerId+" cannot be placed -> Destination surface has not the required height.");
        return result;
    }
    /**************************************CHECK MOVABLE**************************************/



    /**************************************MOVE CONTAINER**************************************/
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
    /**************************************MOVE CONTAINER**************************************/

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
