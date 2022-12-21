//
//import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.util.*;
//import java.util.List;
//import javax.swing.*;
//import javax.swing.table.DefaultTableModel;
//
//public class GUI {
//    private static Field field;
//    private static Map<Integer,Container> containers = new HashMap<>();
//
//    private static List<Difference> differences = new ArrayList<>();
//    private static boolean stuck = false, changed = false;
//    private static int currentIndex = 0;
//    private static List<CraneMovement> craneMoves = new ArrayList<>();
//    // Contains the movements of the container - with coordinates of the center of the container
//    private static List<ContainerMovement> containerMoves = new ArrayList<>();
//    // Stack containing all the indexes which are changed, so they can be removed from the differences
//    // Stack -> Reverse order -> Easier to remove
//    private static Stack<Integer> executed = new Stack<>();
//
//    public static void main(String[] args) {
//        JScrollPane scrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
//        JScrollBar bar = scrollPane.getVerticalScrollBar();
//        bar.setPreferredSize(new Dimension(40, 0));
//        JFrame frame = new JFrame();
//        JButton showField = new JButton("Show current state of the field");
//        JButton oneMovement = new JButton("Move one container");
//        JButton wholeAlgorithm = new JButton("Let whole algorithm run");
//        JPanel panel = new JPanel(new GridLayout(10, 1, 100, 5));
//        panel.setPreferredSize(new Dimension(1280,700));
//        panel.setBackground(Color.lightGray);
//
//        panel.add(oneMovement);
//        oneMovement.setEnabled(true);
//        panel.add(wholeAlgorithm);
//        wholeAlgorithm.setEnabled(true);
//        panel.add(showField);
//        showField.setEnabled(true);
//        panel.add(scrollPane);
//
//
//        frame.setTitle("Field");
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setLayout(new BorderLayout());
//        frame.setResizable(true);
//        frame.setSize(1280,700);
//        frame.setLocationRelativeTo(null);
//        frame.add(panel);
//        frame.setVisible(true);
//        showField.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                JTable table = visualizeField();
//                scrollPane.setViewportView(table);
//                scrollPane.setSize(1280, 300);
//            }
//        });
//        oneMovement.addActionListener(new ActionListener(){
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                generateSchedule_newTargetField_singleStep(differences);
//            }
//        });
//
//        wholeAlgorithm.addActionListener(new ActionListener(){
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                generateSchedule_newTargetField(differences);
//            }
//        });
//
//        //String fileName = "terminal22_1_100_1_10";
//        //String fileName = "1t/targetTerminalA_20_10_3_2_100";
//        String fileName = "6t/Terminal_10_10_3_1_100";
//        InputData inputData = InputData.readFile("data/" + fileName+ ".json");
//        inputData.formatAssignment();
//
//        containers = inputData.getContainersMap();
//        field = new Field(inputData.getSlots(), inputData.getAssignmentsMap(), inputData.getMaxHeight());
//
//        inputData.makeStacks(field);
//
//        if(inputData.getTargetHeight() == 0) {
//            // Reformat stacks equal to the target field
//            //String target = "1t/TerminalA_20_10_3_2_100";
//            String target = "6t/targetTerminal_10_10_3_1_100";
//            InputTarget inputTarget = InputTarget.readFile("data/" + target + ".json");
////            InputTarget inputTarget = InputTarget.readFile("data/" + fileName + "target.json");
//            inputTarget.formatAssignment(inputData.getContainers(), inputData.getSlots());
//
//            Field targetField = new Field(inputData.getSlots(), inputTarget.getAssignmentsMap(), inputTarget.getMaxheight());
//            inputTarget.makeStacks(targetField, inputData.getContainers());
//
//            differences = findDifferences(targetField);
//            //generateSchedule_newTargetField(differences);
//            assert findDifferences(targetField).isEmpty(): "There are still differences between targetfield and own field";
//        }
//        else {
//            // Format the containerStack so they don't exceed the target height
//            // todo
//        }
//
//    }
//
///**************************************************TARGET FIELD*************************************************/
//
//    private static void generateSchedule_newTargetField(List<Difference> differences) {
//
//        while(!differences.isEmpty()) {
//            Difference diff = differences.get(currentIndex);
//            int containerId = diff.getContainerId();
//
//            List<Integer> destinationSlotIds = diff.getSlotIds();
//            Container container = containers.get(containerId);
//            // Try and place container to correct destination
//
//            if(field.isValidContainerDestination(container, destinationSlotIds) && field.isMovableContainer(container)) {
//                if(field.containerHasCorrectHeight(destinationSlotIds, diff.getHeight(), containerId)) {
//                    moveContainerMovement(containerId, container, destinationSlotIds, containerMoves);
//                    executed.push(currentIndex);
//                    changed = true;
//                }
//            }
//            currentIndex++;
//
//            if(currentIndex >= differences.size()) {
//                if(changed) {
//                    changed = false;
//                    assert !executed.isEmpty(): " There has been a change, but the executed changes are empty.";
//                    cleanDifferences(differences, executed);
//                }
//                // Nothing has changed in a full iteration, so the program is stuck
//                else {
//                    // todo
//                    stuck = true;
//                    System.out.println("No containers could be moved");
//                }
//                currentIndex = 0;
//            }
//            if(stuck) {
//                stuck = false;
//            }
//        }
//        System.out.println("All containers are succesfully moved");
//    }
//
//
//    private static void generateSchedule_newTargetField_singleStep(List<Difference> differences) {
//
//        while(!differences.isEmpty()) {
//            Difference diff = differences.get(currentIndex);
//            int containerId = diff.getContainerId();
//
//            List<Integer> destinationSlotIds = diff.getSlotIds();
//            Container container = containers.get(containerId);
//            // Try and place container to correct destination
//
//            if(field.isValidContainerDestination(container, destinationSlotIds) && field.isMovableContainer(container)) {
//                if(field.containerHasCorrectHeight(destinationSlotIds, diff.getHeight(), containerId)) {
//                    moveContainerMovement(containerId, container, destinationSlotIds, containerMoves);
//                    executed.push(currentIndex);
//                    changed = true;
//                }
//            }
//            currentIndex++;
//
//            if(currentIndex >= differences.size()) {
//                if(changed) {
//                    changed = false;
//                    assert !executed.isEmpty(): " There has been a change, but the executed changes are empty.";
//                    cleanDifferences(differences, executed);
//                }
//                // Nothing has changed in a full iteration, so the program is stuck
//                else {
//                    // todo
//                    stuck = true;
//                    System.out.println("No containers could be moved");
//                }
//                currentIndex = 0;
//            }
//            if(stuck) {
//                stuck = false;
//            }
//            break;
//        }
//        System.out.println("All containers are succesfully moved");
//    }
//    private static void cleanDifferences(List<Difference> differences, Stack<Integer> executed) {
//        while(!executed.isEmpty()) {
//            // De tussenvariabele index is om de een of andere reden nodig... Het werkt niet in 1 lijn
//            int index = executed.pop();
//            differences.remove(index);
//        }
//    }
//
//    private static void moveContainerMovement(int containerId, Container container, List<Integer> destinationSlotIds, List<ContainerMovement> containerMoves) {
//        Coordinate start = field.getGrabbingPoint(containerId);
//        field.moveContainer(container, destinationSlotIds);
//        Coordinate end = field.getGrabbingPoint(containerId);
////        containerMoves.add(new ContainerMovement(start, end));
//    }
//
///**************************************************TARGET FIELD*************************************************/
//
//
//
///**************************************FIND DIFFERENCES**************************************/
//
//    private static List<Difference> findDifferences(Field targetField) {
//        ArrayList<Integer[]> differences = new ArrayList<>();
//        for(Slot slot :  field.getSlots()) {
//            if(slot.getId() ==  16) {
//                System.out.println();
//            }
//            Slot targetSlot = targetField.getSlot_slotId(slot.getId());
//            if(slot.getTotalHeight() == 0) {
//                // If a slot is empty for the original, but contains containers for the target
//                // All the target slots need to be saved in differences
//                if(targetSlot.getTotalHeight() != 0) {
//                    for(int i = 0; i < targetSlot.getTotalHeight(); i++) {
//                        int containerId = targetSlot.getContainerStack().get(i);
//                        differences.add(new Integer[]{containerId,  targetSlot.getHeightContainer(containerId) ,targetSlot.getId()});
//                    }
//                }
//            }
//            else {
//                // Compare 2 stacks, and extract the differences
//                int minHeight = Math.min(slot.getTotalHeight(), targetSlot.getTotalHeight());
//                for(int i = 0; i < minHeight; i++) {
//                    Stack<Integer> original = targetSlot.getContainerStack();
//                    Stack<Integer> target = targetSlot.getContainerStack();
//                    if(!original.get(i).equals(target.get(i))) {
//                        int containerId = target.get(i);
//                        differences.add(new Integer[]{containerId,  targetSlot.getHeightContainer(containerId) ,targetSlot.getId()});
//                    }
//                }
//                if(targetSlot.getTotalHeight() > minHeight) {
//                    for(int i = minHeight; i < targetSlot.getTotalHeight(); i++) {
//                        int containerId = targetSlot.getContainerStack().get(i);
//                        differences.add(new Integer[]{containerId,  targetSlot.getHeightContainer(containerId) ,targetSlot.getId()});
//                    }
//                }
//
//            }
//        }
//        return convertDifferencesToAssignments(differences);
//    }
//    private static List<Difference> convertDifferencesToAssignments(ArrayList<Integer[]> differences_slotId_height_containerId) {
//        List<Difference> differences = new ArrayList<>();
//        for(Integer[] diff : differences_slotId_height_containerId) {
//            int containerId = diff[0];
//            int height = diff[1];
//            int slotId = diff[2];
//
//            // Check if assignment of this container is already filled in
//            boolean containsContainer = false;
//            for(Difference diff_alreayPresent : differences) {
//                if(diff_alreayPresent.getContainerId() == containerId) containsContainer = true;
//            }
//            // Find the other slots on which this container must be placed
//            if(!containsContainer) {
//                List<Integer> slots = new ArrayList<>();
//                slots.add(slotId);
//                for(Integer[] values : differences_slotId_height_containerId) {
//                    if(values[0] == containerId && !slots.contains(values[2])) {
//                        slots.add(values[2]);
//                    }
//                }
//                // Format to a Difference object
//                slots.sort(Comparator.comparingInt(id -> id));
//                differences.add(new Difference(height, new Assignment(containerId, slots)));
//            }
//        }
//        return differences;
//    }
//
///**************************************FIND DIFFERENCES**************************************//*
//
//
//
//
///**************************************TESTING**************************************/
//
//    public static JTable visualizeField() {
//        DefaultTableModel model = new DefaultTableModel();
//        model.addColumn("slot id");
//        model.addColumn("Stack of the slot");
//        Slot[][] fieldMatrix = field.getFieldMatrix();
//        for (int i = 0; i < fieldMatrix.length; i++) {
//            for (int j = 0; j < fieldMatrix[0].length; j++){
//                String slotid;
//                String containerStack;
//                Slot currentSlot = fieldMatrix[i][j];
//                slotid = Integer.toString(currentSlot.getId());
//                containerStack = currentSlot.printStackContent();
//                model.insertRow(model.getRowCount(),new Object[]{slotid, containerStack});
//            }
//        }
//        JTable result = new JTable(model);
//        return result;
//    }
//    private static void testBasicFunctionality() {
//        ArrayList<Integer> list = new ArrayList<>(1);
//        Container container = new Container(7, 1);
//        containers.put(7, container);
//        field.placeContainer(new Container(6, 2), new ArrayList<>(Arrays.asList(1, 2)));
//        field.placeContainer(new Container(5, 1), new ArrayList<>(Arrays.asList(3)));
//        field.placeContainer(container, new ArrayList<>(Arrays.asList(3)));
//        if(field.isValidContainerDestination(container, new ArrayList<>(Arrays.asList(2))) && field.isMovableContainer(container)) field.moveContainer(container, new ArrayList<Integer>(Arrays.asList(2)));
//
//        for(Container container2 : containers.values()) {
//            System.out.println(field.getGrabbingPoint(container.getId()));
//        }
//    }
///**************************************TESTING**************************************/
//
//
//
///*************************************************INPUT*************************************************/
//
//    private static String[] inputFiles = new String[]{"terminal22_1_100_1_10", "Terminal_20_10_3_2_100-HEIGHT", "1t/TerminalA_20_10_3_2_100", "2mh/MH2Terminal_20_10_3_2_100","3t/TerminalA_20_10_3_2_160", "4mh/MH2Terminal_20_10_3_2_160", "5t/TerminalB_20_10_3_2_160" , "6t/Terminal_10_10_3_1_100"};
//    private static String[] targetFiles = new String[]{"terminal22_1_100_1_10target", null, "1t/targetTerminalA_20_10_3_2_100", null, "3t/targetTerminalA_20_10_3_2_160", null, "5t/targetTerminalB_20_10_3_2_160" , "6t/targetTerminal_10_10_3_1_100"};
//    private static int chooseInputFile() {
//        for(int i=0; i<inputFiles.length; i++) {
//            System.out.print(i + ": ");
//            System.out.println(inputFiles[i]);
//        }
//        System.out.println("Select the input file");
//        Scanner sc = new Scanner(System.in);
//        int choice = sc.nextInt();
//        System.out.println();
//        return choice;
//    }
//
///*************************************************INPUT*************************************************/
//
//}
