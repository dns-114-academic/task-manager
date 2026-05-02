import java.util.ArrayList;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

// Gérer les tâches et contrôler les données
public class TaskListImpl extends TaskList {

    private ArrayList<TaskImpl> tasks;

    public TaskListImpl() {
        tasks = new ArrayList<TaskImpl>();
    }

    public void add(TaskImpl t) {
        if (t != null)
            tasks.add(t);
    }

    public void remove(String name) {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getName().equals(name)) {
                tasks.remove(i);
                return;
            }
        }
    }

    public void update(String name, TaskImpl updated, String modifiedBy) {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getName().equals(name)) {
                // Modifier l'objet existant sur place — préserve les références partagées
                TaskImpl existing = tasks.get(i);
                existing.edit(
                        updated.getName(),
                        updated.getDescription(),
                        updated.getDueDate(),
                        updated.getStatus(),
                        updated.getPriority());
                existing.setShared(updated.isShared());
                existing.setRecurring(updated.isRecurring());
                existing.setRecurringInterval(updated.getRecurringInterval());
                existing.setLastModifiedBy(modifiedBy);
                return;
            }
        }
    }

    public void display() {
        if (tasks.isEmpty()) {
            System.out.println("La liste est vide.");
            return;
        }
        for (TaskImpl t : tasks) {
            System.out.println(t);
            System.out.println("---");
        }
    }

    // FILTRAGE
    public ArrayList<TaskImpl> filterByStatus(Status status) {
        ArrayList<TaskImpl> result = new ArrayList<TaskImpl>();
        for (TaskImpl t : tasks)
            if (t.getStatus() == status)
                result.add(t);
        return result;
    }

    public ArrayList<TaskImpl> filterByPriority(Priority priority) {
        ArrayList<TaskImpl> result = new ArrayList<TaskImpl>();
        for (TaskImpl t : tasks)
            if (t.getPriority() == priority)
                result.add(t);
        return result;
    }

    // TRI
    public void sortByDueDate() {
        for (int i = 1; i < tasks.size(); i++) {
            TaskImpl current = tasks.get(i);
            int j = i - 1;
            while (j >= 0 && tasks.get(j).getDueDate().compareTo(current.getDueDate()) > 0) {
                tasks.set(j + 1, tasks.get(j));
                j--;
            }
            tasks.set(j + 1, current);
        }
    }

    public void sortByPriority() {
        for (int i = 1; i < tasks.size(); i++) {
            TaskImpl current = tasks.get(i);
            int currentVal = priorityToInt(current.getPriority());
            int j = i - 1;
            while (j >= 0 && priorityToInt(tasks.get(j).getPriority()) < currentVal) {
                tasks.set(j + 1, tasks.get(j));
                j--;
            }
            tasks.set(j + 1, current);
        }
    }

    private int priorityToInt(Priority p) {
        if (p == Priority.HIGH)
            return 3;
        if (p == Priority.MEDIUM)
            return 2;
        return 1;
    }

    // RECHERCHE
    public ArrayList<TaskImpl> search(String keyword) {
        ArrayList<TaskImpl> result = new ArrayList<TaskImpl>();
        String kw = keyword.toLowerCase();
        for (TaskImpl t : tasks)
            if (t.getName().toLowerCase().contains(kw) || t.getDescription().toLowerCase().contains(kw))
                result.add(t);
        return result;
    }

    // COMMENTAIRES
    public void addComment(String taskName, String comment) {
        for (TaskImpl t : tasks)
            if (t.getName().equals(taskName)) {
                t.addComment(comment);
                return;
            }
    }

    // VALIDATION
    public boolean validateInput(String name, String description, String dueDate) {
        if (name == null || name.trim().isEmpty())
            return false;
        if (description == null || description.trim().isEmpty())
            return false;
        if (name.trim().length() > 100)
            return false;
        if (description.trim().length() > 500)
            return false;

        if (dueDate != null && !dueDate.trim().isEmpty()) {
            boolean valid = false;
            String[] formats = { "yyyy-MM-dd HH:mm", "yyyy-MM-dd" };
            for (String fmt : formats) {
                try {
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(fmt);
                    sdf.setLenient(false);
                    sdf.parse(dueDate.trim());
                    valid = true;
                    break;
                } catch (Exception e) {
                }
            }
            if (!valid)
                return false;
        }
        return true;
    }

    // STATISTIQUES
    public String getStats() {
        int todo = 0, inProgress = 0, completed = 0, abandoned = 0;
        for (TaskImpl t : tasks) {
            switch (t.getStatus()) {
                case TODO:
                    todo++;
                    break;
                case IN_PROGRESS:
                    inProgress++;
                    break;
                case COMPLETED:
                    completed++;
                    break;
                case ABANDONED:
                    abandoned++;
                    break;
            }
        }
        return "=== Statistiques ===\n"
                + "Total : " + tasks.size() + "\n"
                + "À faire : " + todo + "\n"
                + "En cours : " + inProgress + "\n"
                + "Terminées : " + completed + "\n"
                + "Abandonnées : " + abandoned + "\n";
    }

    // JSON — sauvegarde personnelle uniquement
    public void exportToJson(String filename) {
        try {
            StringBuilder sb = new StringBuilder("[\n");
            for (int i = 0; i < tasks.size(); i++) {
                TaskImpl t = tasks.get(i);
                sb.append("  {\n");
                sb.append("    \"name\": \"").append(escapeJson(t.getName())).append("\",\n");
                sb.append("    \"description\": \"").append(escapeJson(t.getDescription())).append("\",\n");
                sb.append("    \"dueDate\": \"").append(escapeJson(t.getDueDate())).append("\",\n");
                sb.append("    \"status\": \"").append(t.getStatus().name()).append("\",\n");
                sb.append("    \"priority\": \"").append(t.getPriority().name()).append("\",\n");
                sb.append("    \"isShared\": ").append(t.isShared()).append(",\n");
                sb.append("    \"isRecurring\": ").append(t.isRecurring()).append(",\n");
                sb.append("    \"recurringInterval\": \"").append(escapeJson(t.getRecurringInterval())).append("\",\n");
                sb.append("    \"comments\": [");
                ArrayList<String> comments = t.getComments();
                for (int j = 0; j < comments.size(); j++) {
                    sb.append("\"").append(escapeJson(comments.get(j))).append("\"");
                    if (j < comments.size() - 1)
                        sb.append(", ");
                }
                sb.append("]\n  }");
                if (i < tasks.size() - 1)
                    sb.append(",");
                sb.append("\n");
            }
            sb.append("]\n");
            FileWriter writer = new FileWriter(filename);
            writer.write(sb.toString());
            writer.close();
            System.out.println("Export OK -> " + filename);
        } catch (IOException e) {
            System.out.println("Erreur export : " + e.getMessage());
        }
    }

    public void importFromJson(String filename) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null)
                content.append(line.trim());
            reader.close();

            String json = content.toString().trim();
            if (json.startsWith("["))
                json = json.substring(1);
            if (json.endsWith("]"))
                json = json.substring(0, json.length() - 1);

            tasks.clear();

            for (String obj : splitJsonObjects(json)) {
                String name = extractJsonValue(obj, "name");
                String description = extractJsonValue(obj, "description");
                String dueDate = extractJsonValue(obj, "dueDate");
                String statusStr = extractJsonValue(obj, "status");
                String priorityStr = extractJsonValue(obj, "priority");

                Status status;
                try {
                    status = Status.valueOf(statusStr);
                } catch (Exception ex) {
                    status = Status.TODO;
                }

                Priority priority;
                try {
                    priority = Priority.valueOf(priorityStr);
                } catch (Exception ex) {
                    priority = Priority.LOW;
                }

                TaskImpl task = new TaskImpl(name, description, dueDate, status, priority);
                if (extractJsonBoolean(obj, "isShared").equals("true"))
                    task.setShared(true);
                if (extractJsonBoolean(obj, "isRecurring").equals("true"))
                    task.setRecurring(true);
                task.setRecurringInterval(extractJsonValue(obj, "recurringInterval"));
                for (String comment : extractJsonArray(obj, "comments"))
                    task.addComment(comment);
                tasks.add(task);
            }
            System.out.println("Import OK <- " + filename);
        } catch (IOException e) {
            System.out.println("Erreur import : " + e.getMessage());
        }
    }

    private String escapeJson(String value) {
        if (value == null)
            return "";
        String result = "";
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c == '"')
                result += "\\\"";
            else if (c == '\\')
                result += "\\\\";
            else if (c == '\n')
                result += "\\n";
            else
                result += c;
        }
        return result;
    }

    private ArrayList<String> splitJsonObjects(String json) {
        ArrayList<String> objects = new ArrayList<String>();
        int depth = 0, start = -1;
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '{') {
                if (depth == 0)
                    start = i;
                depth++;
            } else if (c == '}') {
                depth--;
                if (depth == 0 && start >= 0) {
                    objects.add(json.substring(start, i + 1));
                    start = -1;
                }
            }
        }
        return objects;
    }

    private String extractJsonValue(String json, String key) {
        String search = "\"" + key + "\"";
        int keyIdx = json.indexOf(search);
        if (keyIdx < 0)
            return "";
        int colon = json.indexOf(":", keyIdx + search.length());
        if (colon < 0)
            return "";
        int openQuote = json.indexOf("\"", colon + 1);
        if (openQuote < 0)
            return "";
        int closeQuote = openQuote + 1;
        while (closeQuote < json.length()) {
            if (json.charAt(closeQuote) == '\\')
                closeQuote += 2;
            else if (json.charAt(closeQuote) == '"')
                break;
            else
                closeQuote++;
        }
        return json.substring(openQuote + 1, closeQuote)
                .replace("\\\"", "\"").replace("\\\\", "\\").replace("\\n", "\n");
    }

    private String extractJsonBoolean(String json, String key) {
        String search = "\"" + key + "\"";
        int keyIdx = json.indexOf(search);
        if (keyIdx < 0)
            return "false";
        int colon = json.indexOf(":", keyIdx + search.length());
        if (colon < 0)
            return "false";
        return json.substring(colon + 1).trim().startsWith("true") ? "true" : "false";
    }

    private ArrayList<String> extractJsonArray(String json, String key) {
        ArrayList<String> result = new ArrayList<String>();
        int keyIdx = json.indexOf("\"" + key + "\"");
        if (keyIdx < 0)
            return result;
        int open = json.indexOf("[", keyIdx);
        int close = json.indexOf("]", open);
        if (open < 0 || close < 0)
            return result;
        String content = json.substring(open + 1, close);
        boolean inQuote = false;
        int start = -1;
        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            if (c == '"' && !inQuote) {
                inQuote = true;
                start = i + 1;
            } else if (c == '"' && inQuote) {
                if (i > 0 && content.charAt(i - 1) == '\\')
                    continue;
                result.add(content.substring(start, i).replace("\\\"", "\""));
                inQuote = false;
            }
        }
        return result;
    }

    public ArrayList<TaskImpl> getTasks() {
        return tasks;
    }
}