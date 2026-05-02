
// Représentation d'un utilisateur
public class User {

    private String username;
    private String password;
    private TaskListImpl privateTasks;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        privateTasks = new TaskListImpl();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public TaskListImpl getPrivateTasks() {
        return privateTasks;
    }

    public String toString() {
        return "Utilisateur : " + username;
    }
}