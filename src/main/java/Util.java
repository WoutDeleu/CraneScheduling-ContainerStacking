import java.util.ArrayList;
import java.util.List;

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
}
