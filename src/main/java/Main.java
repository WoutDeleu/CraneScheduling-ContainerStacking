import com.google.gson.Gson;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    private static Field field;
    private static Map<Integer,Container> containers = new HashMap<>();

    public static void main(String[] args) {
        InputData inputData = readFile("data/terminal_4_3.json");

        containers = inputData.getContainersMap();
        field = new Field(inputData.getSlots(), inputData.getAssignmentsMap());

        inputData.makeStacks(field);
        System.out.println(field.getHeightContainer(3));
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
}