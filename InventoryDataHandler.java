import java.io.*;
import java.util.*;

public class InventoryDataHandler {
    private static final String DATA_FILE = "inventory_data.dat";

    public static void saveInventory(Map<String, Battery> inventory, String filePath) {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(filePath))) {
            outputStream.writeObject(inventory);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Battery> loadInventory(String filePath) {
        Map<String, Battery> inventory = new HashMap<>();
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(filePath))) {
            Object obj = inputStream.readObject();
            if (obj instanceof Map) {
                inventory = (Map<String, Battery>) obj;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return inventory;
    }
}
