# Gestionnaire de Tâches (Java)

---

## Description

Application Java de gestion de tâches avec interface graphique.

L'application permet de :
- Créer, modifier et supprimer des tâches
- Assigner une priorité (LOW, MEDIUM, HIGH) et un statut (TODO, IN_PROGRESS, DONE)
- Notifier les changements via un moteur de notifications
- Gérer des utilisateurs avec authentification basique
- Persister les données au format JSON (`tasks.json`)

---

## Structure du projet

```
Task-manager/
├── src/
│   ├── Main.java               # Point d'entrée de l'application
│   ├── Task.java               # Interface Task
│   ├── TaskImpl.java           # Implémentation de Task
│   ├── TaskList.java           # Interface TaskList
│   ├── TaskListImpl.java       # Implémentation de TaskList (avec JSON)
│   ├── TaskManagerGUI.java     # Interface graphique (Swing)
│   ├── AuthManager.java        # Gestion de l'authentification
│   ├── User.java               # Classe User
│   ├── NotificationEngine.java # Moteur de notifications
│   ├── Status.java             # Enum Status (TODO, IN_PROGRESS, DONE)
│   ├── Priority.java           # Enum Priority (LOW, MEDIUM, HIGH)
│   ├── tasks.json              # Données persistées
│   ├── _D1_useCase.puml        # Diagramme de cas d'utilisation (PlantUML)
│   ├── _D2_diagramClass.puml   # Diagramme de classes (PlantUML)
│   ├── _D3_diagramSequence.puml# Diagramme de séquence (PlantUML)
│   └── _D4_state_transition.puml # Diagramme d'état-transition (PlantUML)
└── en/
    └── README.md               # English version of this README
```

---

## Architecture

### Interfaces principales

| Interface | Rôle |
|-----------|------|
| `Task` | Contrat d'une tâche : edit(), delete(), setStatus() |
| `TaskList` | Contrat d'une liste de tâches : add, remove, filter |

### Classes principales

| Classe | Rôle |
|--------|------|
| `TaskImpl` | Implémentation concrète d'une tâche |
| `TaskListImpl` | Liste de tâches avec persistance JSON |
| `TaskManagerGUI` | Interface graphique Swing principale |
| `AuthManager` | Authentification et gestion des sessions |
| `NotificationEngine` | Envoi de notifications en cas de changement |

---

## Diagrammes UML

Les diagrammes sont au format **PlantUML** (`.puml`) :
- `_D1_useCase.puml` — Cas d'utilisation (acteurs et interactions)
- `_D2_diagramClass.puml` — Diagramme de classes complet
- `_D3_diagramSequence.puml` — Séquence d'une modification de tâche
- `_D4_state_transition.puml` — États et transitions d'une tâche

Pour les visualiser : [PlantUML Online](https://www.plantuml.com/plantuml/uml/) ou l'extension VS Code PlantUML.

---

## Prérequis & Lancement

- **Java 11+**
- Compilation et exécution :

```bash
cd task_manager
javac *.java
java Main
```

---

## Fonctionnalités

- Création de tâches avec nom, description, date d'échéance, statut, priorité
- Modification et suppression de tâches
- Filtrage par statut ou priorité
- Persistance automatique au format JSON
- Interface graphique intuitive (Java Swing)
- Système d'authentification utilisateur
- Notifications sur les changements de tâche
