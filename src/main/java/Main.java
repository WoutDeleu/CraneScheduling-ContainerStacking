import com.google.gson.Gson;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    private static final int MAX_HEIGHT = 4;
    private static Field field;
    private static Map<Integer,Container> containers = new HashMap<>();

    private static boolean NEW_FIELD = true;
    public static void main(String[] args) {
        String fileName = "terminal22_1_100_1_10";
//        String fileName = "terminal_4_3";
        InputData inputData = InputData.readFile("data/" + fileName+ ".json");
        inputData.formatAssignment();

        containers = inputData.getContainersMap();
        field = new Field(inputData.getSlots(), inputData.getAssignmentsMap(), MAX_HEIGHT);

        inputData.makeStacks(field);

        if(inputData.getTargetHeight() == 0) {
            // Reformat stacks equal to the target field
            InputTarget inputTarget = InputTarget.readFile("data/" + fileName + "target.json");
            inputTarget.formatAssignment(inputData.getContainers(), inputData.getSlots());

            Field targetField = new Field(inputData.getSlots(), inputTarget.getAssignmentsMap(), inputTarget.getMaxheight());

            findDifferences(targetField);

        }
        else {
            // Format the stack so they don't exceed the target height
        }


        visualizeField();
    }


    private static List<Integer[]> findDifferences(Field targetField) {
        ArrayList<Integer[]> differences_slotId_containerId = new ArrayList<>();
        for(Slot slot :  field.getSlots()) {
            Slot targetSlot = targetField.getSlot_slotId(slot.getId());
            int minHeight = Math.min(slot.getTotalHeight(), targetSlot.getTotalHeight());
            System.out.println(minHeight);
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


    /*****************************TESTING**************************************/
    public static void visualizeField() {
        System.out.println("x ->");
        System.out.println("y |");
        System.out.println();
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
    private static void testBasicFunctionality() {
        ArrayList<Integer> list = new ArrayList<>(1);
        Container container = new Container(7, 1);
        containers.put(7, container);
        field.placeContainer(new Container(6, 2), new ArrayList<>(Arrays.asList(1, 2)));
        field.placeContainer(new Container(5, 1), new ArrayList<>(Arrays.asList(3)));
        field.placeContainer(container, new ArrayList<>(Arrays.asList(3)));
        if(field.isValidContainerDestination(container, new ArrayList<>(Arrays.asList(2))) && field.isMovableContainer(container)) field.moveContainer(container, new ArrayList<Integer>(Arrays.asList(2)));
    }
    /*****************************TESTING**************************************/
}
