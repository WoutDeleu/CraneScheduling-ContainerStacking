import java.util.ArrayList;

public class Assignment {
    private ArrayList<Integer> slot_id;
    private int container_id;


    public ArrayList<Integer> getSlot_id() {
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
