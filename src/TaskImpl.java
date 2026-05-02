import java.util.ArrayList;

// Interface Task
public class TaskImpl implements Task {

    private String name;
    private String description;
    private String dueDate;
    private Status status;        
    private Priority priority;   
    private ArrayList<String> comments;

    // Attributs Partie 2
    private boolean isShared;
    private boolean isRecurring;
    private String recurringInterval;
    private String lastModifiedBy;

    public TaskImpl(String name, String description, String dueDate, Status status, Priority priority) {
        this.name = name;
        this.description = description;
        this.dueDate = dueDate;
        this.status = status;
        this.priority = priority;
        comments = new ArrayList<String>();
        isShared = false;
        isRecurring = false;
        recurringInterval = "";
        lastModifiedBy = null;
    }

    public void edit(String name, String description, String dueDate, Status status, Priority priority) {
        this.name = name;
        this.description = description;
        this.dueDate = dueDate;
        this.status = status;
        this.priority = priority;
    }

    public void delete() {
        name = "";
        description = "";
        dueDate = "";
        status = Status.ABANDONED;
        priority = Priority.LOW;
        comments.clear();
        isShared = false;
        isRecurring = false;
        recurringInterval = "";
        lastModifiedBy = null;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void addComment(String comment) {
        if (comment != null && !comment.trim().isEmpty()) {
            comments.add(comment.trim());
        }
    }



    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getDueDate() { return dueDate; }
    public Status getStatus() { return status; }
    public Priority getPriority() { return priority; }
    public ArrayList<String> getComments() { return comments; }

    public boolean isShared() { return isShared; }
    public void setShared(boolean shared) { isShared = shared; }

    public boolean isRecurring() { return isRecurring; }
    public void setRecurring(boolean recurring) { isRecurring = recurring; }

    public String getRecurringInterval() { return recurringInterval; }
    public void setRecurringInterval(String interval) { recurringInterval = interval; }

    public String getLastModifiedBy() { return lastModifiedBy; }
    public void setLastModifiedBy(String username) { lastModifiedBy = username; }

    public String toString() {
        String result = "Tâche : " + name + "\n";
        result += "  Description : " + description + "\n";
        result += "  Date limite : " + dueDate + "\n";
        result += "  Statut : " + status.name() + "\n";
        result += "  Priorité : " + priority.name() + "\n";
        if (isShared) result += "  [Partagée]\n";
        if (isRecurring) result += "  [Récurrente : " + recurringInterval + "]\n";
        if (lastModifiedBy != null) result += "  [Dernière modif par : " + lastModifiedBy + "]\n";
        if (!comments.isEmpty()) {
            result += "  Commentaires :\n";
            for (String c : comments) {
                result += "    - " + c + "\n";
            }
        }
        return result;
    }
}
