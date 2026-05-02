import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;

// Notifications (alerte, fin d'échéance, collab)
public class NotificationEngine {

    private ArrayList<String> notifications;

    public NotificationEngine() {
        notifications = new ArrayList<String>();
    }

    public ArrayList<String> checkNotifications(TaskListImpl list, String currentUser) {
        notifications.clear();
        Date now = new Date();

        for (TaskImpl task : list.getTasks()) {

            if (task.getStatus() == Status.COMPLETED || task.getStatus() == Status.ABANDONED) {
                continue;
            }

            String lastModif = task.getLastModifiedBy();
            if (lastModif != null && !lastModif.equals(currentUser)) {
                notifications.add("[COLLABORATION] La tâche \"" + task.getName()
                    + "\" a été modifiée par " + lastModif + ".");
            }

            Date dueDate = parseDate(task.getDueDate());
            if (dueDate == null) continue;

            long diffMs      = dueDate.getTime() - now.getTime();
            long diffMinutes = diffMs / (1000 * 60);
            long diffDays    = diffMs / (1000 * 60 * 60 * 24);

            if (diffMs < 0) {
                notifications.add("[EN RETARD] \"" + task.getName() + "\" a dépassé sa date limite !");
            } else if (diffMinutes < 15) {
                notifications.add("[URGENT] \"" + task.getName() + "\" : moins de 15 minutes !");
            } else if (diffMinutes < 60) {
                notifications.add("[BIENTÔT] \"" + task.getName() + "\" : moins d'une heure.");
            } else if (diffDays < 1) {
                notifications.add("[RAPPEL] \"" + task.getName() + "\" : moins de 24 heures.");
            }

            if (task.isRecurring()) {
                String interval = task.getRecurringInterval();
                if ("daily".equalsIgnoreCase(interval)) {
                    notifications.add("[RÉCURRENCE] Rappel quotidien : \"" + task.getName() + "\".");
                } else if ("weekly".equalsIgnoreCase(interval)) {
                    notifications.add("[RÉCURRENCE] Rappel hebdomadaire : \"" + task.getName() + "\".");
                }
            }
        }

        return notifications;
    }

    private Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }

    public ArrayList<String> getNotifications() { return notifications; }
    public void clearNotifications() { notifications.clear(); }
}
