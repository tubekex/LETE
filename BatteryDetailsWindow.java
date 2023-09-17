import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

public class BatteryDetailsWindow extends JFrame {
    private JTextArea descriptionTextArea;
    private Map<String, Battery> batteryInventory;
    private String dataFilePath;

    public BatteryDetailsWindow(Battery battery, Map<String, Battery> batteryInventory, String dataFilePath) {
        this.batteryInventory = batteryInventory;
        this.dataFilePath = dataFilePath;

        setTitle("Battery Details");
        setSize(400, 200);
        setLocationRelativeTo(null);

        descriptionTextArea = new JTextArea(5, 30);
        descriptionTextArea.setText(battery.getDescription());

        JButton saveButton = new JButton("Save Description");

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String serialNumber = battery.getSerialNumber();
                String updatedDescription = descriptionTextArea.getText();
                Battery updatedBattery = batteryInventory.get(serialNumber);
                if (updatedBattery != null) {
                    updatedBattery.setDescription(updatedDescription);
                    batteryInventory.put(serialNumber, updatedBattery);
                    InventoryDataHandler.saveInventory(batteryInventory, dataFilePath);
                }
            }
        });

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(new JScrollPane(descriptionTextArea), BorderLayout.CENTER);
        mainPanel.add(saveButton, BorderLayout.SOUTH);

        add(mainPanel);
    }
}
