import com.google.gson.Gson;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    private static final int MAX_HEIGHT = 4;

    private static Field field;
    private static Map<Integer,Container> containers = new HashMap<>();

    public static void main(String[] args) {
        InputData inputData = readFile("data/terminal_4_3.json");

        containers = inputData.getContainersMap();
        field = new Field(inputData.getSlots(), inputData.getAssignmentsMap(), MAX_HEIGHT);

        inputData.makeStacks(field);
        ArrayList<Integer> list = new ArrayList<>(1);
        Container container = new Container(7, 1);
        containers.put(7, container);
        field.placeContainer(new Container(6, 2), new ArrayList<Integer>(Arrays.asList(1, 2)));
        field.placeContainer(new Container(5, 1), new ArrayList<Integer>(Arrays.asList(3)));
        field.placeContainer(container, new ArrayList<Integer>(Arrays.asList(3)));
        if(field.isValidContainerDestination(container, new ArrayList<Integer>(Arrays.asList(2))) && field.isMovableContainer(container)) field.moveContainer(container, new ArrayList<Integer>(Arrays.asList(2)));

        visualizeField();
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


