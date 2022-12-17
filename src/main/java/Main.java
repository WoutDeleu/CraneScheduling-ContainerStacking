import java.util.*;
import java.util.List;

public class Main {
    private static Field field;
    private static List<Crane> cranes;
    private static Map<Integer,Container> containers = new HashMap<>();

    public static void main(String[] args) {
        int choice = chooseInputFile();
        System.out.println("inputFile: " + inputFiles[choice]);
        InputData inputData = InputData.readFile("data/" + inputFiles[choice] + ".json");
        inputData.formatAssignment();

        cranes = inputData.getCranes();
        containers = inputData.getContainersMap();
        field = new Field(inputData.getSlots(), inputData.getAssignmentsMap(), inputData.getMaxHeight());

        inputData.makeStacks(field);

        List<ContainerMovement> containerMovements;
        if(inputData.getTargetHeight() == 0) {
            String targetFile = targetFiles[choice];
            System.out.println("Reformat to target field " + targetFile);
            InputTarget inputTarget = InputTarget.readFile("data/" + targetFile + ".json");
            inputTarget.formatAssignment(inputData.getContainers(), inputData.getSlots());

            Field targetField = new Field(inputData.getSlots(), inputTarget.getAssignmentsMap(), inputTarget.getMaxheight());
            inputTarget.makeStacks(targetField, inputData.getContainers());

            List<Difference> differences = findDifferences(targetField);
            containerMovements = generateContainerMovements(differences);
            assert findDifferences(targetField).isEmpty(): "There are still differences between targetfield and own field";
        }
        else {
            // Format the container stack, so they don't exceed the target height
            System.out.println("Reformat so field doesn't exceed the target height");
            containerMovements = generateContainerMovements(inputData.getTargetHeight());
        }
        List<FullMovement> schedule = addCranesToMovement(containerMovements);
    }

    private static List<FullMovement> addCranesToMovement(List<ContainerMovement> containerMovements) {
        List<FullMovement> schedule = new ArrayList<>();
        int timer = 0;
        for (ContainerMovement containerMovement : containerMovements) {
            // Get the mov
            FullMovement moveToContainer = new FullMovement();
            moveToContainer.setStart(containerMovement.getStart());
            moveToContainer.setContainerId(-1);
            moveToContainer.setPickupTime(timer);

        }

        return schedule;
    }

    /**************************************************MAX HEIGHT**************************************************/
    public static List<ContainerMovement> generateContainerMovements(int targetHeight) {
        List<ContainerMovement> containerMoves = new ArrayList<>();
        field.setMAX_HEIGHT(targetHeight);
        int currentIndex = 0;

        List<Integer> containersToMove = field.findContainersExceedingHeight(targetHeight);
        Stack<Integer> executed = new Stack<>();
        while(!containersToMove.isEmpty()) {
            int containerId = containersToMove.get(currentIndex);
            Container container = containers.get(containerId);

            List<Integer>[] possibleDestinations = field.findAvailableSlots(container);

            moveContainerMovement(container, possibleDestinations[0], containerMoves);
            executed.push(currentIndex);
            currentIndex++;

            if(currentIndex >= containersToMove.size()) {
                cleanDifferences(containersToMove, executed);
                currentIndex = 0;
            }
        }
        return containerMoves;
    }
    /**************************************************MAX HEIGHT**************************************************/



    /**************************************************TARGET FIELD**************************************************/
    // Used when creating new field based on target field
    private static List<ContainerMovement> generateContainerMovements(List<Difference> differences) {
        int currentIndex = 0;
        // Contains the movements of the container - with coordinates of the center of the container
        List<ContainerMovement> containerMoves = new ArrayList<>();
        // Stack containing all the indexes which are changed, so they can be removed from the differences
        // Stack -> Reverse order -> Easier to remove
        Stack<Integer> executed = new Stack<>();
        while (!differences.isEmpty()) {
            Difference diff = differences.get(currentIndex);
            int containerId = diff.getContainerId();

            List<Integer> destinationSlotIds = diff.getSlotIds();
            Container container = containers.get(containerId);
            // Try and place container to correct destination

            if (field.isValidContainerDestination(container, destinationSlotIds) && field.isMovableContainer(container)) {
                if (field.containerHasCorrectHeight(destinationSlotIds, diff.getHeight(), containerId)) {
                    moveContainerMovement(container, destinationSlotIds, containerMoves);
                    executed.push(currentIndex);
                }
            }
            currentIndex++;

            if (currentIndex >= differences.size()) {
                cleanDifferences(differences, executed);
                currentIndex = 0;
            }
        }
        System.out.println("All containers are succesfully moved");
        return containerMoves;
    }
    private static <T> void cleanDifferences(List<T> differences, Stack<Integer> executed) {
        while(!executed.isEmpty()) {
            // De tussenvariabele index is om de een of andere reden nodig... Het werkt niet in 1 lijn
            int index = executed.pop();
            differences.remove(index);
        }
    }

    private static void moveContainerMovement(Container container, List<Integer> destinationSlotIds, List<ContainerMovement> containerMoves) {
        Coordinate start = field.getGrabbingPoint(container.getId());
        field.moveContainer(container, destinationSlotIds);
        Coordinate end = field.getGrabbingPoint(container.getId());
        containerMoves.add(new ContainerMovement(container.getId(), start, end));
    }
    /**************************************************TARGET FIELD*************************************************/


    /*************************************************FIND DIFFERENCES*************************************************/
    private static List<Difference> findDifferences(Field targetField) {
        ArrayList<Integer[]> differences = new ArrayList<>();
        for(Slot slot :  field.getSlots()) {
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
                    Stack<Integer> original = slot.getContainerStack();
                    Stack<Integer> target = targetSlot.getContainerStack();
                    if(!original.get(i).equals(target.get(i))) {
                        int containerId = target.get(i);
                        differences.add(new Integer[]{containerId,  targetSlot.getHeightContainer(containerId), targetSlot.getId()});
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

            if(containerId == 88) {
                System.out.println();
            }
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
    /*************************************************FIND DIFFERENCES*************************************************/


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
    /*************************************************TESTING*************************************************/



    /*************************************************INPUT*************************************************/
    private static String[] inputFiles = new String[]{"terminal22_1_100_1_10", "Terminal_20_10_3_2_100-HEIGHT", "1t/TerminalA_20_10_3_2_100", "2mh/MH2Terminal_20_10_3_2_100","3t/TerminalA_20_10_3_2_160", "4mh/MH2Terminal_20_10_3_2_160", "5t/TerminalB_20_10_3_2_160" , "6t/Terminal_10_10_3_1_100"};
    private static String[] targetFiles = new String[]{"terminal22_1_100_1_10target", null, "1t/targetTerminalA_20_10_3_2_100", null, "3t/targetTerminalA_20_10_3_2_160", null, "5t/targetTerminalB_20_10_3_2_160" , "6t/targetTerminal_10_10_3_1_100"};
    private static int chooseInputFile() {
        for(int i=0; i<inputFiles.length; i++) {
            System.out.print(i + ": ");
            System.out.println(inputFiles[i]);
        }
        System.out.println("Select the input file");
        Scanner sc = new Scanner(System.in);
        int choice = sc.nextInt();
        System.out.println();
        return choice;
    }
    /*************************************************INPUT*************************************************/
}
