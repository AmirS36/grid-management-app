package permissionsTable;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PermissionsTableItem {

    private StringProperty userName;
    private StringProperty accessLevel;
    private StringProperty requestStatus;

    public PermissionsTableItem(String userName, String accessLevel, String requestStatus) {
        this.userName = new SimpleStringProperty(userName);
        this.accessLevel = new SimpleStringProperty(accessLevel);
        this.requestStatus = new SimpleStringProperty(requestStatus);
    }

    public String getUserName() {
        return userName.get();
    }

    public StringProperty userNameProperty() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName.set(userName);
    }

    public String getAccessLevel() {
        return accessLevel.get();
    }

    public StringProperty accessLevelProperty() {
        return accessLevel;
    }

    public void setAccessLevel(String accessLevel) {
        this.accessLevel.set(accessLevel);
    }

    public String getRequestStatus() {
        return requestStatus.get();
    }

    public StringProperty requestStatusProperty() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus.set(requestStatus);
    }
}

