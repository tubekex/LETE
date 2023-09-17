import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class LETE extends JFrame {
    private Map<String, Battery> batteryInventory;
    private JComboBox<String> modelDropdown;
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton registerButton;
    private JButton viewButton;
    private JButton deleteButton;
    private JButton adminButton;
    private static final String DATA_FILE_PATH = "data" + File.separator + "lete_data.dat";
    private Map<String, String> batteryModelsMap = new HashMap<>(); // Initialize the map to store battery models
    private boolean initialized = false; // Track whether models are initialized
    private boolean modelAdded = false; // Track whether a model has been added

    public LETE() {
        batteryInventory = InventoryDataHandler.loadInventory(DATA_FILE_PATH);

        setTitle("Battery Inventory");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        modelDropdown = new JComboBox<>();
        tableModel = new DefaultTableModel(new Object[]{"Serial Number", "Model"}, 0);
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setDefaultEditor(Object.class, null);

        registerButton = new JButton("Register Battery");
        viewButton = new JButton("View Inventory");
        deleteButton = new JButton("Delete Battery");
        adminButton = new JButton("Admin");

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerBattery();
            }
        });

        viewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewInventory();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedBattery();
            }
        });

        adminButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openAdminPanel();
            }
        });

        JPanel inputPanel = new JPanel(new GridLayout(3, 2));
        inputPanel.add(new JLabel("Model:"));
        inputPanel.add(modelDropdown);
        inputPanel.add(registerButton);
        inputPanel.add(viewButton);
        inputPanel.add(deleteButton);
        inputPanel.add(adminButton);

        JScrollPane tableScrollPane = new JScrollPane(table);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);

        add(mainPanel);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            InventoryDataHandler.saveInventory(batteryInventory, DATA_FILE_PATH);
            BatteryModelManager.saveBatteryModels(batteryModelsMap); // Save battery models on shutdown
        }));

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = table.getSelectedRow();
                    String serialNumber = (String) table.getValueAt(selectedRow, 0);
                    openBatteryDetailsWindow(serialNumber);
                }
            }
        });
    }

    private void registerBattery() {
        String model = (String) modelDropdown.getSelectedItem();
        String serialNumber = generateRandomSerialNumber();

        if (!model.isEmpty()) {
            Battery battery = new Battery(serialNumber, model, "");
            batteryInventory.put(serialNumber, battery);
            tableModel.addRow(new Object[]{serialNumber, model});
            InventoryDataHandler.saveInventory(batteryInventory, DATA_FILE_PATH);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a model.");
        }
    }

    private void viewInventory() {
        if (batteryInventory.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Inventory is empty.");
        } else {
            tableModel.getDataVector().clear();
            batteryInventory.values().forEach(v -> {
                tableModel.addRow(new Object[]{v.getSerialNumber(), v.getModel()});
            });
        }
    }

    private void deleteSelectedBattery() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            String serialNumber = (String) table.getValueAt(selectedRow, 0);
            Battery battery = batteryInventory.get(serialNumber);
            if (battery != null) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to delete this battery?",
                        "Confirm Deletion", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    batteryInventory.remove(serialNumber);
                    tableModel.removeRow(selectedRow);
                    InventoryDataHandler.saveInventory(batteryInventory, DATA_FILE_PATH);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a battery to delete.");
        }
    }

    private String generateRandomSerialNumber() {
        Random rand = new Random();
        StringBuilder serialNumber = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            serialNumber.append(rand.nextInt(10));
        }
        return serialNumber.toString();
    }

    private void openBatteryDetailsWindow(String serialNumber) {
        Battery battery = batteryInventory.get(serialNumber);
        if (battery != null) {
            BatteryDetailsWindow detailsWindow = new BatteryDetailsWindow(battery, batteryInventory, DATA_FILE_PATH);
            detailsWindow.setVisible(true);
        }
    }

    private void openAdminPanel() {
        if (!initialized) {
            batteryModelsMap = BatteryModelManager.loadBatteryModels();
            initialized = true;
            updateModelDropdown(); // Update the model dropdown
        }

        if (!modelAdded) {
            AdminPanel adminPanel = new AdminPanel(batteryModelsMap, this);
            adminPanel.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "A model has already been added. Please restart the program to add another.");
        }
    }

    // Initialize battery models
    private void initializeBatteryModels() {
        batteryModelsMap = BatteryModelManager.loadBatteryModels();
        initialized = true;
        updateModelDropdown(); // Update the model dropdown
    }

    // Update the model dropdown with the latest models
    public void updateModelDropdown() {
        modelDropdown.removeAllItems(); // Clear existing items
        for (String model : batteryModelsMap.keySet()) {
            modelDropdown.addItem(model);
        }
    }

    // Method to add a new battery model to the list
    public void addNewBatteryModel(String newModel) {
        if (!batteryModelsMap.containsKey(newModel)) {
            batteryModelsMap.put(newModel, "");
            updateModelDropdown(); // Update the dropdown with the new model
            modelAdded = true;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LETE app = new LETE();
                app.initializeBatteryModels(); // Initialize battery models
                app.setVisible(true);
            }
        });
    }
}
