import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Assignment {
    private int container_id;
    @SerializedName("slot_id")
    private int slot_id;
    private ArrayList<Integer> slot_id_array;

    public Assignment(int container_id, ArrayList<Integer> slot_id) {
        this.container_id = container_id;
        this.slot_id_array = slot_id;
    }

    public ArrayList<Integer> getSlot_ids() {
        return slot_id_array;
    }
    public int getContainer_id() {
        return container_id;
    }


    public void setContainer_id(int container_id) {
        this.container_id = container_id;
    }

    public int getSlot_id() {
        return slot_id;
    }

    public void addSlot(Slot slot_x_y) {
        if(slot_id_array == null) slot_id_array = new ArrayList<>();
        slot_id_array.add(slot_x_y.getId());
    }
}
