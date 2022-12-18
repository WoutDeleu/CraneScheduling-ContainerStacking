import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Util {
    public static List<Integer>[] addToArray(List<Integer>[] availableSlots, List<Integer> possibleDestinations) {
        int length = availableSlots.length;
        List<Integer>[] result = new List[length+1];
        for (int i = 0; i < length; i++) {
            result[i] = availableSlots[i];
        }
        result[length] = possibleDestinations;
        return result;
    }

    public static double getSmallestValue(ArrayList<Double> values) {
        double smallest = Double.MAX_VALUE;  // Initialiseer met hoogst mogelijke waarde

        if (!values.isEmpty()) {  // Controleer of de lijst leeg is
            smallest = values.get(0);  // Maak de eerste waarde de huidige kleinste waarde
            for (double value : values) {
                if (value < smallest) {  // Als value kleiner is, vervang de huidige kleinste waarde
                    smallest = value;
                }
            }
        }
        return smallest;
    }

    public static List<Integer> getValueFromMap(Map<Integer, Double> craneTimeLocks, double time) {
        List<Integer> result = new ArrayList<>();
        for(Map.Entry<Integer, Double> entry : craneTimeLocks.entrySet()) {
            if(entry.getValue() == time) {
                result.add(entry.getKey());
            }
        }
        assert false: "No crane entry found for crane with release time " + time + ".";
        return result;
    }
}
