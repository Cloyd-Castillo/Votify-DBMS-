package models;

public class User {
    private int id;
    private String username;
    private String password;
    private boolean isActive;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.isActive = true;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isActive() {
        return isActive;
    }

    public void deactivate() {
        this.isActive = false;
    }
}
