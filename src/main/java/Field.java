import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Field {
    private List<Slot> slot;
    private Map<Integer, Assignment> assignment;

    public Field(List<Slot> slot, Map<Integer, Assignment> assignment) {
        this.slot=slot;
        this.assignment = assignment;
    }
    public List<Slot> getSlot(int containerId) {
        List<Integer> slot_ids = assignment.get(containerId).getSlot_id();
        List<Slot> returnList = new ArrayList<>();
        for(int id : slot_ids) {
            for(Slot slot : slot) {
                if(slot.getId() == (id)) {
                    returnList.add(slot);
                }
            }
        }
        return returnList;
    }

    public boolean isContainerPlaced(int containerId) {
        return assignment.containsKey(containerId);
    }
    // For each slot, determine how many and which containers are on top.
    public List<Container> getContainers(int slotId, Map<Integer, Container> containers) {
        List<Container> returnList = new ArrayList<>();
        // loop over all assignments, where value == slotId
        // add to return list
        for(Assignment assignment : this.assignment.values()){
            for(int id : assignment.getSlot_id()) {
                if (slotId==id) {
                    returnList.add(containers.get(id));
                }
            }
        }
        return returnList;
    }

    public int getHeightContainer(int containerId) {
        return getSlot(containerId).get(0).getHeightContainer(containerId);
    }

}
