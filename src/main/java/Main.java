import java.util.*;

public class Main {
    private static Field field;
    private static Map<Integer,Container> containers = new HashMap<>();

    public static void main(String[] args) {
        String fileName = "terminal22_1_100_1_10";
//        String fileName = "5t/TerminalB_20_10_3_2_160";
        InputData inputData = InputData.readFile("data/" + fileName+ ".json");
        inputData.formatAssignment();

        containers = inputData.getContainersMap();
        field = new Field(inputData.getSlots(), inputData.getAssignmentsMap(), inputData.getMaxHeight());

        inputData.makeStacks(field);

        if(inputData.getTargetHeight() == 0) {
            // Reformat stacks equal to the target field
//            String target = "5t/targetTerminalB_20_10_3_2_160";
//            InputTarget inputTarget = InputTarget.readFile("data/" + target + ".json");
            InputTarget inputTarget = InputTarget.readFile("data/" + fileName + "target.json");
            inputTarget.formatAssignment(inputData.getContainers(), inputData.getSlots());

            Field targetField = new Field(inputData.getSlots(), inputTarget.getAssignmentsMap(), inputTarget.getMaxheight());
            inputTarget.makeStacks(targetField, inputData.getContainers());

            List<Difference> differences = findDifferences(targetField);
            generateSchedule_newTargetField(differences);
            assert findDifferences(targetField).isEmpty(): "There are still differences between targetfield and own field";
        }
        else {
            // Format the containerStack so they don't exceed the target height
        }


//        visualizeField();
    }

    private static void generateSchedule_newTargetField(List<Difference> differences) {
//        visualizeField();
        boolean hardStuck, changed = false;
        int currentIndex = 0;
        List<CraneMovement> craneMoves = new ArrayList<>();
        // Contains the movements of the container - with coordinates of the center of the container
        List<ContainerMovement> containerMoves = new ArrayList<>();
        // Stack containing all the indexes which are changed, so they can be removed from the differences
        // Stack -> Reverse order -> Easier to remove
        Stack<Integer> differencesExecuted = new Stack<>();
        while(!differences.isEmpty()) {
            Difference diff = differences.get(currentIndex);
            int containerId = diff.getContainerId();

            List<Integer> destinationSlotIds = diff.getSlotIds();
            Container container = containers.get(containerId);
            if(field.isValidContainerDestination(container, destinationSlotIds) && field.isMovableContainer(container)) {
                Coordinate start = field.getGrabbingPoint(containerId);

                field.moveContainer(container, destinationSlotIds);

                Coordinate end = field.getGrabbingPoint(containerId);

                differencesExecuted.push(currentIndex);
                containerMoves.add(new ContainerMovement(start, end));
                changed = true;
            }

            // todo -> Wout

            currentIndex++;
            if(currentIndex >= differences.size()) {
                if(changed) {
                    changed = false;
                    assert !differencesExecuted.isEmpty(): " There has been a change, but the executed changes are empty.";
                    while(!differencesExecuted.isEmpty()) {
                        // De tussenvariabele index is om de een of andere reden nodig... Het werkt niet in 1 lijn
                        int index = differencesExecuted.pop();
                        differences.remove(index);
                    }
                }
                else {
                    hardStuck = true;
                    System.out.println("No containers could be moved");
                    break;
                }
                currentIndex = 0;
            }
        }
        System.out.println("All containers are succesfully moved");
    }



    /**************************************FIND DIFFERENCES**************************************/
    private static List<Difference> findDifferences(Field targetField) {
        ArrayList<Integer[]> differences = new ArrayList<>();
        for(Slot slot :  field.getSlots()) {
            if(slot.getId() ==  16) {
                System.out.println();
            }
            Slot targetSlot = targetField.getSlot_slotId(slot.getId());
            if(slot.getTotalHeight() == 0) {
                // If a slot is empty for the original, but contains containers for the target
                // All the target slots need to be saved in differences
                if(targetSlot.getTotalHeight() != 0) {
                    for(int i = 0; i < targetSlot.getTotalHeight(); i++) {
                        int containerId = targetSlot.getContainerStack().get(i);
                        differences.add(new Integer[]{containerId,  targetSlot.getHeightContainer(containerId) ,targetSlot.getId()});
                    }
                }
            }
            else {
                // Compare 2 stacks, and extract the differences
                int minHeight = Math.min(slot.getTotalHeight(), targetSlot.getTotalHeight());
                for(int i = 0; i < minHeight; i++) {
                    Stack<Integer> original = targetSlot.getContainerStack();
                    Stack<Integer> target = targetSlot.getContainerStack();
                    if(!original.get(i).equals(target.get(i))) {
                        int containerId = target.get(i);
                        differences.add(new Integer[]{containerId,  targetSlot.getHeightContainer(containerId) ,targetSlot.getId()});
                    }
                }
                if(targetSlot.getTotalHeight() > minHeight) {
                    for(int i = minHeight; i < targetSlot.getTotalHeight(); i++) {
                        int containerId = targetSlot.getContainerStack().get(i);
                        differences.add(new Integer[]{containerId,  targetSlot.getHeightContainer(containerId) ,targetSlot.getId()});
                    }
                }

            }
        }
        return convertDifferencesToAssignments(differences);
    }
    private static List<Difference> convertDifferencesToAssignments(ArrayList<Integer[]> differences_slotId_height_containerId) {
        List<Difference> differences = new ArrayList<>();
        for(Integer[] diff : differences_slotId_height_containerId) {
            int containerId = diff[0];
            int height = diff[1];
            int slotId = diff[2];

            // Check if assignment of this container is already filled in
            boolean containsContainer = false;
            for(Difference diff_alreayPresent : differences) {
                if(diff_alreayPresent.getContainerId() == containerId) containsContainer = true;
            }
            // Find the other slots on which this container must be placed
            if(!containsContainer) {
                List<Integer> slots = new ArrayList<>();
                slots.add(slotId);
                for(Integer[] values : differences_slotId_height_containerId) {
                    if(values[0] == containerId && !slots.contains(values[2])) {
                        slots.add(values[2]);
                    }
                }
                // Format to a Difference object
                slots.sort(Comparator.comparingInt(id -> id));
                differences.add(new Difference(height, new Assignment(containerId, slots)));
            }
        }
        return differences;
    }
    /**************************************FIND DIFFERENCES**************************************/


    /**************************************TESTING**************************************/
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

        for(Container container2 : containers.values()) {
            System.out.println(field.getGrabbingPoint(container.getId()));
        }
    }
    /**************************************TESTING**************************************/
}
