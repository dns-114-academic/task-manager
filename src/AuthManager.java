import java.util.ArrayList;

// Gestion (incription, connexion, déconnexion)
public class AuthManager {

    private ArrayList<User> users;
    private User loggedUser;

    public AuthManager() {
        users = new ArrayList<User>();
        loggedUser = null;
    }

    public boolean register(String username, String password) {
        if (username == null || username.trim().isEmpty()) return false;
        if (password == null || password.trim().isEmpty()) return false;

        // Chaque nom doit être unique (donc pas de doublon)
        if (findUser(username) != null) return false;

        users.add(new User(username.trim(), password));
        return true;
    }

    public boolean login(String username, String password) {
        for (User u : users) {
            if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
                loggedUser = u;
                return true;
            }
        }
        return false;
    }

    public void logout() {
        loggedUser = null;
    }

    public User getLoggedUser() { return loggedUser; }
    public boolean isLoggedIn()  { return loggedUser != null; }

    public User findUser(String username) {
        for (User u : users) {
            if (u.getUsername().equals(username)) return u;
        }
        return null;
    }

    public ArrayList<User> getUsers() { return users; }
}
