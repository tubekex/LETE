import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Map;
import java.util.Set;

public class AdminPanel extends JFrame {
    private Map<String, String> batteryModelsMap; // Updated data structure
    private LETE mainApp;
    private JTextField newModelTextField;
    private JButton addButton;
    private JButton deleteButton; // Added "Delete Model" button
    private JList<String> modelList; // Added JList

    public AdminPanel(Map<String, String> batteryModelsMap, LETE mainApp) {
        this.batteryModelsMap = batteryModelsMap;
        this.mainApp = mainApp;

        setTitle("Admin Panel");
        setSize(400, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        newModelTextField = new JTextField();
        addButton = new JButton("Add New Model");
        deleteButton = new JButton("Delete Model"); // Create "Delete Model" button
        modelList = new JList<>(batteryModelsMap.keySet().toArray(new String[0]));

        // Create a double-click listener for the model list
        modelList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    openModelInfoDialog();
                }
            }
        });

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addNewModel();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedModel();
            }
        });

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.add(new JLabel("Battery Models:"));

        // Add the JList within a JScrollPane
        JScrollPane listScrollPane = new JScrollPane(modelList);
        inputPanel.add(listScrollPane);

        inputPanel.add(new JLabel("New Model:"));
        inputPanel.add(newModelTextField);
        inputPanel.add(addButton);
        inputPanel.add(deleteButton); // Add "Delete Model" button

        getContentPane().add(inputPanel);
    }

    private void addNewModel() {
        String newModel = newModelTextField.getText().trim();
        if (!newModel.isEmpty()) {
            if (!batteryModelsMap.containsKey(newModel)) {
                batteryModelsMap.put(newModel, ""); // Initially, set model info as empty
                modelList.setListData(batteryModelsMap.keySet().toArray(new String[0]));
                mainApp.addNewBatteryModel(newModel);
                newModelTextField.setText("");

                // Call the method to update the dropdown list in LETE
                mainApp.updateModelDropdown();

                // Save the updated models with information
                BatteryModelManager.saveBatteryModels(batteryModelsMap);
            } else {
                JOptionPane.showMessageDialog(this, "Model already exists.");
            }
        }
    }

    // Method to delete the selected model
    private void deleteSelectedModel() {
        int selectedIndex = modelList.getSelectedIndex();
        if (selectedIndex != -1) {
            String selectedModel = modelList.getSelectedValue();
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete this model?",
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                batteryModelsMap.remove(selectedModel);
                modelList.setListData(batteryModelsMap.keySet().toArray(new String[0]));
                mainApp.updateModelDropdown();

                // Save the updated models with information
                BatteryModelManager.saveBatteryModels(batteryModelsMap);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a model to delete.");
        }
    }

    // Method to open a dialog for editing model information
    private void openModelInfoDialog() {
        int selectedIndex = modelList.getSelectedIndex();
        if (selectedIndex != -1) {
            String selectedModel = modelList.getSelectedValue();
            String modelInfo = batteryModelsMap.get(selectedModel); // Get model information

            // Create a dialog for editing model information
            JDialog modelInfoDialog = new JDialog(this, "Edit Model Info", true);
            modelInfoDialog.setSize(400, 300);
            modelInfoDialog.setLocationRelativeTo(this);

            // Add components to the dialog
            JPanel dialogPanel = new JPanel(new BorderLayout());

            // Add JTextArea for editing model information
            JTextArea modelInfoTextArea = new JTextArea(modelInfo);
            modelInfoTextArea.setWrapStyleWord(true);
            modelInfoTextArea.setLineWrap(true);
            JScrollPane scrollPane = new JScrollPane(modelInfoTextArea);
            dialogPanel.add(scrollPane, BorderLayout.CENTER);

            JButton saveButton = new JButton("Save");
            saveButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Save the edited model information
                    String updatedModelInfo = modelInfoTextArea.getText().trim();
                    batteryModelsMap.put(selectedModel, updatedModelInfo);

                    // Save the updated models with information
                    BatteryModelManager.saveBatteryModels(batteryModelsMap);

                    modelInfoDialog.dispose(); // Close the dialog
                }
            });

            dialogPanel.add(saveButton, BorderLayout.SOUTH);

            modelInfoDialog.add(dialogPanel);
            modelInfoDialog.setVisible(true);
        }
    }
}
