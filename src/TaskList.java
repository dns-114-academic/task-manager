// Classe abstraite qui définit le contrat de gestion d'une liste de tâches (cours Prog Objet - Java)
public abstract class TaskList {

    public abstract void add(TaskImpl t);
    public abstract void remove(String name);
    public abstract void update(String name, TaskImpl t, String modifiedBy);
    public abstract void display();
}
