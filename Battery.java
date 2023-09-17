import java.io.Serializable;

// Fixa ett serialversionUID. googla eller fråga chatgpt.
public class Battery implements Serializable {
     private static final long serialVersionUID = 1922996569013195057L;

    private String serialNumber;
    private String model;
    private String description;

    public Battery(String serialNumber, String model, String description) {
        this.serialNumber = serialNumber;
        this.model = model;
        this.description = description;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getModel() {
        return model;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
