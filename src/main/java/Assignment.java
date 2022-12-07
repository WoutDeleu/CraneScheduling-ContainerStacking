import java.util.ArrayList;

public class Assignment {
    private int container_id;
//    private ArrayList<Integer> slot_id;
    private int lot_id;

    public Assignment(int container_id, ArrayList<Integer> slot_id) {
        this.container_id = container_id;
        this.slot_id = slot_id;
    }

    public ArrayList<Integer> getSlot_ids() {
        return slot_id;
    }
    public int getContainer_id() {
        return container_id;
    }


    public void setContainer_id(int container_id) {
        this.container_id = container_id;
    }
    public void setSlot_id(ArrayList<Integer> slot_id) {
        this.slot_id = slot_id;
    }

}
