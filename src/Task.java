public interface Task {

    // Modifier les détails d'une tâche
    void edit(String name, String description, String dueDate, Status status, Priority priority);

    // Réinitialise les champs (suppression logique)
    void delete();

    // Changer uniquement le statut
    void setStatus(Status status);
}
