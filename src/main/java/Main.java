import com.google.gson.Gson;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    private static final int MAX_HEIGHT = 4;

    private static Field field;
    private static Map<Integer,Container> containers = new HashMap<>();

    public static void main(String[] args) {
        InputData inputData = readFile("data/terminal22_1_100_1_10.json");

        containers = inputData.getContainersMap();
        field = new Field(inputData.getSlots(), inputData.getAssignmentsMap(), MAX_HEIGHT);

        inputData.makeStacks(field);

//        testBasicFunctionality();
        testSchedule();


        visualizeField();
    }

    private static void testSchedule() {
        InputData inputData = readFile("data/terminal_4_3.json");
        Field targetField = new Field(inputData.getSlots(), inputData.getAssignmentsMap(), MAX_HEIGHT);
        List<Integer[]> differences = findDifferences(targetField);
    }

    private static void testBasicFunctionality() {
        ArrayList<Integer> list = new ArrayList<>(1);
        Container container = new Container(7, 1);
        containers.put(7, container);
        field.placeContainer(new Container(6, 2), new ArrayList<>(Arrays.asList(1, 2)));
        field.placeContainer(new Container(5, 1), new ArrayList<>(Arrays.asList(3)));
        field.placeContainer(container, new ArrayList<>(Arrays.asList(3)));
        if(field.isValidContainerDestination(container, new ArrayList<>(Arrays.asList(2))) && field.isMovableContainer(container)) field.moveContainer(container, new ArrayList<Integer>(Arrays.asList(2)));
    }

    private static List<Integer[]> findDifferences(Field targetField) {
        ArrayList<Integer[]> differences_slotId_containerId = new ArrayList<>();
        for(Slot slot :  field.getSlots()) {
            Slot targetSlot = targetField.getSlot_slotId(slot.getId());
            int minHeight = Math.min(slot.getTotalHeight(), targetSlot.getTotalHeight());
            for(int i = 0; i < minHeight; i++) {
                Stack<Integer> original = targetSlot.getStack();
                Stack<Integer> target = targetSlot.getStack();
                if(!original.get(i).equals(target.get(i))) {
                    differences_slotId_containerId.add(new Integer[]{target.get(i), targetSlot.getId()});
                }
            }
        }
        return differences_slotId_containerId;
    }
    public static InputData readFile(String path) {
        InputData inputData = null;
        try {
            String jsonString = Files.readString(Paths.get(path));
            Gson gson = new Gson();
            inputData = gson.fromJson(jsonString, InputData.class);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return inputData;
    }

    public static void visualizeField() {
        Slot[][] fieldMatrix = field.getFieldMatrix();
        for(int i = 0 ; i < fieldMatrix.length; i++) {
            for(int j = 0 ; j < fieldMatrix[i].length; j++) {
                if(fieldMatrix[i][j] != null) {
                    System.out.print(fieldMatrix[i][j].printStackInfo() + "\t");
                }
                else System.out.print("....\t" );
            }
            System.out.println();
        }
    }
}


// todo
// coordinate system for crane