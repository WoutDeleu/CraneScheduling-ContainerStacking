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

    private int getMaxY() {
        int maxY = -1;
        for(Slot slot : slots) {
            if(slot.getY() > maxY) maxY = slot.getY();
        }
        return maxY;
    }
    private int getMaxX() {
        int maxX = -1;
        for(Slot slot : slots) {
            if(slot.getX() > maxX) maxX = slot.getX();
        }
        return maxX;
    }
    private int getMinX() {
        int minX = Integer.MAX_VALUE;
        for(Slot slot : slots) {
            if(slot.getX() < minX) minX = slot.getX();
        }
        return minX;
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

    private List<Slot> getSlots_slotIds(List<Integer> slotIds) {
        List<Slot> result = new ArrayList<>();
        for(int id : slotIds) {
            result.add(getSlot_slotId(id));
        }
        return result;
    }

    public Slot getSlot_slotId(int slotId) {
        for(Slot slot : slots) {
            if(slot.getId() == slotId) {
                return slot;
            }
        }
        return null;
    }

    public Coordinate calculatetMeetingPoint(double xMax, double xMin, ContainerMovement movingContainer, Container container) {
        boolean leftToRightMovement = (movingContainer.getStart().getX() <= movingContainer.getEnd().getX());
        Coordinate meetingPoint = null;
        if(xMax > getMaxX()) xMax = getMaxX();
        if(xMin < getMinX()) xMin = getMinX();
        while(meetingPoint == null) {
            for(int y = 0; y<getMaxY(); y++) {
                List<Integer> destinationSlots = new ArrayList<>();
                for(int i =0; i<container.getLength(); i++) {
                    if(leftToRightMovement) destinationSlots.add(getSlotId_coordinates(xMax - i, y));
                    else destinationSlots.add(getSlotId_coordinates(xMin + i, y));
                }
                Collections.sort(destinationSlots);
                assert slotsAreSequential(destinationSlots) : "Slots are not sequential! Fault";
                if(isValidContainerDestination(container, destinationSlots)) {
                    meetingPoint =  getCenterPoint(destinationSlots);
                }
            }
            if(leftToRightMovement) xMax--;
            else xMin++;
        }
        return meetingPoint;
    }

    private int getSlotId_coordinates(double x, int y) {
        for(Slot slot : slots) {
            if(slot.getX() == x && slot.getY() == y) return slot.getId();
        }
        assert false :  "No slot found for that coordinate";
        return 0;
    }


    private List<Integer> findBestAvailableSlot(Container container, List<Integer> containersToMove) {
        List<Integer>[] availableSlots = findAvailableSlots(container);
        availableSlots = findLowest(availableSlots, containersToMove);
        return findMostFittingSlot(container, availableSlots, containersToMove);
    }
    public List<Integer> findBestAvailableSlot(Container container, List<Integer> containersToMove, List<Integer>[] availableSlots) {
        availableSlots = findLowest(availableSlots, containersToMove);
        return findMostFittingSlot(container, availableSlots, containersToMove);
    }
    // Find fittintg slots to move a container to
    public List<Integer>[] findAvailableSlots(Container container) {
        int length = container.getLength();
        List<Integer>[] availableSlots = new List[0];
        List<Integer> possibleDestinations = new ArrayList<>();
        for (int slotId1 = 0; slotId1 < slots.size() - length; slotId1++) {
            for (int i = 0; i < length; i++) {
                possibleDestinations.add(slotId1 + i);
            }
            if (slotsAreSequential(possibleDestinations)) {
                if (isValidContainerDestination(container, possibleDestinations)) {
                    availableSlots = Util.addToArray(availableSlots, new ArrayList<>(possibleDestinations));
                }
            }
            possibleDestinations.clear();
        }
//        assert availableSlots.length == 0: "No available slots";
        return availableSlots;
    }

    public List<Integer> findMostFittingSlot(Container container, List<Integer>[] availableSlots, List<Integer> containersToMove) {
        // Check if there aren't any slots with containers who still have to be moved
        for (int i = 0; i < availableSlots.length; i++) {
            List<Integer> currentSlotSet = availableSlots[i];
            for (int j = 0; j < currentSlotSet.size(); j++) {
                Slot slot =  getSlot_slotId(currentSlotSet.get(j));
                for (int k = 0; k <containersToMove.size(); k++) {
                    //delete the list with containers to move
                    if (slot.containsContainer(containersToMove.get(k))) {
                        for (int l = i; l < availableSlots.length-1; l++) {
                            availableSlots[l] = availableSlots[l+1];
                        }
                    }
                }
            }
        }
        // Calculate distances, the returned list contains on index i the distance from the container to the slots on availableSLots[i]
        // then, we look up the minimum value, and finally we return availableSlots[minIndex]
        double[] destinationLenghts = calculateDistances(availableSlots, container);
        double min = destinationLenghts[0];
        int minindex = 0;
        for (int i = 0; i < destinationLenghts.length; i++) {
            if(destinationLenghts[i] < min) {
                minindex = i;
                min = destinationLenghts[i];
            }
        }

        return availableSlots[minindex];
    }

    public List<Integer>[] findLowest(List<Integer>[] availableSlots, List<Integer> containersToMove) {
        // Check if there aren't any slots with containers who still have to be moved
        for (int i = 0; i < availableSlots.length; i++) {
            List<Integer> currentSlotSet = availableSlots[i];
            for (int j = 0; j < currentSlotSet.size(); j++) {
                Slot slot =  getSlot_slotId(currentSlotSet.get(j));
                for (int k = 0; k <containersToMove.size(); k++) {
                    //delete the list with containers to move
                    if (slot.containsContainer(containersToMove.get(k))) {
                        for (int l = i; l < availableSlots.length-1; l++) {
                            availableSlots[l] = availableSlots[l+1];
                        }
                    }
                }
            }
        }

        List<Integer>[] possibleSlots = new List[0];
        int minHeight = Integer.MAX_VALUE;
        for(List<Integer> availalbeSlot : availableSlots) {
            Slot currentSlot = getSlot_slotId(availalbeSlot.get(0));
            if(minHeight >= currentSlot.getTotalHeight()) {
                if(minHeight > currentSlot.getTotalHeight()) {
                    possibleSlots = new List[0];
                    minHeight = currentSlot.getTotalHeight();
                }
                possibleSlots = Util.addToArray(possibleSlots, new ArrayList<>(availalbeSlot));
            }
        }
        return possibleSlots;
    }

    public ContainerMovement lowerContainers(int targetHeight, List<Integer> containersToMove) {
        List<Integer> containersToMoveLower = findContainersExceedingHeight(targetHeight);
        for(int containerId : containersToMoveLower) {
            Container containerToMove = Main.containers.get(containerId);
            List<Integer> destinationSlot = findBestAvailableSlot(containerToMove, containersToMove);

            if(targetHeight >= getSlot_slotId(destinationSlot.get(0)).getTotalHeight() +1) {
                return generateContainerMovement(containerToMove, destinationSlot);
            }
        }
        return null;
    }

    private ContainerMovement generateContainerMovement(Container container, List<Integer> destinationSlot) {
        Coordinate start = getGrabbingPoint(container.getId());
        moveContainer(container, destinationSlot);
        Coordinate end = getGrabbingPoint(container.getId());
        System.out.println("Made room by moving container " + container.getId() + " -> " + slotIdsToString(destinationSlot));
        return (new ContainerMovement(container.getId(), start, end));
    }
    public ContainerMovement makeRoom(Container container, List<Integer> containersToMove) {
        int length = container.getLength();
        ArrayList<Integer> possibleLengths = Util.calculatePossibleSums(length);
        Collections.sort(possibleLengths, Collections.reverseOrder());
        Map<Integer, Container> containers = Main.containers;
        for(int curr_length : possibleLengths) {
            for (Container tempContainer : containers.values()) {
                if(tempContainer.getLength() == curr_length && isMovableContainer(container)) {
                    for(Slot slot : slots) {
                        List<Integer> tempDestinationSlots = new ArrayList<>();
                        int id = slot.getId();
                        int x = slot.getX();
                        boolean edgeReached = false;
                        for(int i = 0; i<curr_length; i++) {
                            if(x+i>getMaxX()) {
                                edgeReached = true;
                                break;
                            }
                            else tempDestinationSlots.add(getSlotId_coordinates(x+i, slot.getY()));
                        }
                        if(!edgeReached) {
                            assert slotsAreSequential(tempDestinationSlots) : "Slots are not sequential...";
                            assert tempDestinationSlots.size() == tempContainer.getLength() : "Not correct destination slots";
                            if (isValidContainerDestination(tempContainer, tempDestinationSlots)) {
                                List<Slot> originSlots = getSlot_containerId(tempContainer.getId());
                                for(Slot s : originSlots) {
                                    s.popTopStack();
                                }
                                List<Slot> tryoutSlots = getSlots_slotIds(tempDestinationSlots);
                                for(Slot s : tryoutSlots) {
                                    s.addToContainerStack(tempContainer.getId());
                                }
                                List<Integer> finalSlots_forward = new ArrayList<>(tempDestinationSlots);
                                while(finalSlots_forward.size() < length) {
                                    int currentMaxX = getSlot_slotId(finalSlots_forward.get(finalSlots_forward.size() -1)).getX();
                                    if(currentMaxX +1 > getMaxX()) break;
                                    else finalSlots_forward.add(getSlotId_coordinates(currentMaxX + 1, slot.getY()));
                                }

                                Collections.sort(tempDestinationSlots);
                                List<Integer> finalSlots_reversed = new ArrayList<>(tempDestinationSlots);
                                while(finalSlots_reversed.size() < length) {
                                    int currentMinX = getSlot_slotId(finalSlots_reversed.get(0)).getX();
                                    if(currentMinX-1 < getMinX()) break;
                                    else finalSlots_reversed.add(0, getSlotId_coordinates(currentMinX-1, slot.getY()));
                                }

                                boolean forward = isValidContainerDestination(container, finalSlots_forward);
                                boolean reverse = isValidContainerDestination(container, finalSlots_reversed);

                                for(Slot s : tryoutSlots) {
                                    s.popTopStack();
                                }
                                for(Slot s : originSlots) {
                                    s.addToContainerStack(tempContainer.getId());
                                }

                                if(forward) {
                                    Coordinate start = getGrabbingPoint(tempContainer.getId());
                                    moveContainer(tempContainer, tempDestinationSlots);
                                    Coordinate end = getGrabbingPoint(tempContainer.getId());
                                    System.out.println("Made room by moving container " + tempContainer.getId() +  " -> " + slotIdsToString(tempDestinationSlots));
                                    return (new ContainerMovement(tempContainer.getId(), start, end));
                                }
                                if(reverse) {
                                    Coordinate start = getGrabbingPoint(tempContainer.getId());
                                    moveContainer(tempContainer, tempDestinationSlots);
                                    Coordinate end = getGrabbingPoint(tempContainer.getId());
                                    System.out.println("Made room by moving container " + tempContainer.getId() + " -> " + slotIdsToString(tempDestinationSlots));
                                    return (new ContainerMovement(tempContainer.getId(), start, end));
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    private double[] calculateDistances(List<Integer>[] availableSlots, Container container) {
        double[] lengths = new double[availableSlots.length];
        Assignment assignment = assignments.get(container.getId());
        List<Integer> container_slot_ids = assignment.getSlotIds();
        int middle_containerslot_id = container_slot_ids.get(0);
        Slot middle_containerSlot = getSlot_slotId(middle_containerslot_id);

        for (int i = 0; i < availableSlots.length; i++) {
            List<Integer> currentSlotSet = availableSlots[i];
            Slot middleSlot = getSlot_slotId(currentSlotSet.get(0));
            double xcoordinateAvailable = middleSlot.getX();
            lengths[i] = Math.abs(xcoordinateAvailable - middle_containerSlot.getX());
        }
        return lengths;
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
        if(height >= MAX_HEIGHT) {
//            System.out.println("Container "+ container.getId()+" cannot be placed -> Max height exceeded.");
            return false;
        }

        // Check if destinationSlots have enough space
        if(destinationSlotIds.size() != container.getLength()) {
//            System.out.println("Container "+ container.getId()+" cannot be placed -> Not enough space to place the container.");
            return false;
        }

        // Check if slots contain current container

        if(slotsContainCurrentContainer(container, destinationSlotIds)) return false;

        // Check if corners container can snap
        if(!canContainerSnap(firstSlot, destinationSlotIds, container.getId())) return false;

        return true;
    }

    private boolean slotsContainCurrentContainer(Container container, List<Integer> destinationSlotIds) {
        for(int slotId : destinationSlotIds) {
            Slot slot =  getSlot_slotId(slotId);
            if (slot.containsContainer(container.getId())) {
//                System.out.println("The destinationslot is the slot which has the container already on it.");
                return true;
            }
        }
        return false;
    }

    public boolean canContainerSnap(Slot firstSlot, List<Integer> destinationSlotIds, int id) {
        if(firstSlot.isStackEmpty()) {
            for(int slotId : destinationSlotIds) {
                Slot slot = getSlot_slotId(slotId);
                int container_id = slot.peekStack();
                List<Integer> slot_ids = assignments.get(container_id).getSlotIds();
                for(int slot_id : slot_ids) {
                    if(!destinationSlotIds.contains(slot_id)) {
//                        System.out.println("Container "+ id + " cannot be placed -> Edges don't snap.");
                        return false;
                    }
                }
            }
        }
        return true;
    }
    public boolean isSameHeight(int startingHeight, List<Integer> slotIds, int containerId) {
        for(int slotId : slotIds) {
            if(getSlot_slotId(slotId) != null) {
                if(startingHeight != getSlot_slotId(slotId).getTotalHeight()) {
//                    System.out.println("Container "+ containerId+" cannot be placed -> Destination surface is not flat.");
                    return false;
                }
            }
            else {
                if(startingHeight != 0) {
//                    System.out.println("Container "+ containerId+" cannot be placed -> Destination surface is not flat.");
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
            s.popTopStack();
        }
        assignments.remove(container.getId());

        placeContainer(container, destinationSlotIs);

        System.out.println("Container " + container.getId() + " will be moved from " + slotsToString(oldSlots) + " -> " + slotIdsToString(destinationSlotIs));
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
        double y = slots.get(0).getY();
        List<Integer> x = new ArrayList<>();
        double sum = 0;
        for (Slot slot : slots) {
            assert slot.getY() == y: "Fault in grabbing slots...";
            x.add(slot.getX());
        }
        Collections.sort(x);
        double resultingX = x.get(0) + slots.size()/2;
        return new Coordinate(resultingX, y+0.5);
    }
    public Coordinate getCenterPoint(List<Integer> slotIds) {
        List<Slot> slots = getSlots_slotIds(slotIds);
        List<Integer> x = new ArrayList<>();
        double y = slots.get(0).getY();
        double sum = 0;
        for (Slot slot : slots) {
            assert slot.getY() == y: "Fault in grabbing slots...";
            sum += slot.getX();
        }
        return new Coordinate(sum/ slots.size(), y+0.5);
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
    private String slotIdsToString(List<Integer> slots) {
        String str = "[";
        for(int i : slots) {
            str += " (";
            Slot s = getSlot_slotId(i);
            if (s.getX() <10) str += " " + s.getX();
            else str += s.getX();
            str += ", ";
            if (s.getY() <10) str += " " + s.getY();
            else str += s.getY();
            str += ") ";
        }
        str += " ]";
        return str;
    }
    private String slotsToString(List<Slot> slots) {
        String str = "[";
        for(Slot s : slots) {
            str += " (";
            if (s.getX() <10) str += " " + s.getX();
            else str += s.getX();
            str += ", ";
            if (s.getY() <10) str += " " + s.getY();
            else str += s.getY();
            str += ") ";
        }
        str += " ]";
        return str;
    }

}
