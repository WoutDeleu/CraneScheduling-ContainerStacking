import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Assignment {
    private int container_id;
    @SerializedName("slot_id")
    private int slot_id;
    private List<Integer> slotIds;

    public Assignment(int container_id, List<Integer> slot_id) {
        this.container_id = container_id;
        this.slotIds = slot_id;
    }


    public List<Integer> getSlotIds() {
        return slotIds;
    }
    public int getContainerId() {
        return container_id;
    }


    public void setContainer_id(int container_id) {
        this.container_id = container_id;
    }

    // this method should only be called while formatting inputfile...
    public int getSlot_id() {
        return slot_id;
    }

    public void addSlot(Slot slot_x_y) {
        if(slotIds == null) slotIds = new ArrayList<>();
        slotIds.add(slot_x_y.getId());
    }
}
