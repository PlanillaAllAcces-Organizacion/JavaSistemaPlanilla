package Grupo05.Utils;

public class CBOption {
    private String displayText;
    private Object value;

    public CBOption(String displayText, Object value) {
        this.displayText = displayText;
        this.value = value;
    }

    public String getDisplayText() {
        return displayText;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return displayText; // Esto es lo que se mostrará en el JComboBox
    }
    @Override
    public boolean equals(Object obj) {

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final CBOption other = (CBOption) obj;

        if (this.getValue() != other.getValue()) {
            return false;
        }

        return true;
    }
}