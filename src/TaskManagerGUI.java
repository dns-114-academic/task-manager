import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

// Interface graphique principale
public class TaskManagerGUI {

    // — Composants principaux —
    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private TaskListImpl taskListImpl;
    private AuthManager authManager;
    private NotificationEngine notificationEngine;
    private Timer notificationTimer;

    // — Écran de connexion —
    private JTextField loginUsernameField;
    private JPasswordField loginPasswordField;

    // — Formulaire de tâche —
    private JTextField nameField;
    private JTextField descField;
    private JTextField dueDateField;
    private JComboBox<String> statusCombo;
    private JComboBox<String> priorityCombo;
    private JCheckBox sharedCheckBox;
    private JCheckBox recurringCheckBox;
    private JComboBox<String> intervalCombo;

    // — Affichage —
    private JTable taskTable;
    private DefaultTableModel tableModel;
    private JTextArea outputArea;
    private JLabel statsLabel;
    private JLabel userLabel;

    // — Actions secondaires —
    private JTextField searchField;
    private JTextField commentField;
    private JTextField fileField;
    private JTextField shareField;

    // =========================================================
    // CONSTRUCTEUR
    // =========================================================

    public TaskManagerGUI() {
        authManager = new AuthManager();
        notificationEngine = new NotificationEngine();
        taskListImpl = new TaskListImpl();

        authManager.register("admin", "admin");
        authManager.register("user1", "1234");

        buildGUI();
    }

    // =========================================================
    // CONSTRUCTION DE LA FENÊTRE
    // =========================================================

    private void buildGUI() {
        frame = new JFrame("Gestionnaire de Tâches");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1100, 750);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.add(buildLoginPanel(), "LOGIN");
        mainPanel.add(buildAppPanel(), "APP");
        cardLayout.show(mainPanel, "LOGIN");

        frame.add(mainPanel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // =========================================================
    // ÉCRAN DE CONNEXION
    // =========================================================

    private JPanel buildLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);

        JLabel title = new JLabel("Connexion au Gestionnaire de Tâches");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(title, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        panel.add(new JLabel("Nom d'utilisateur :"), gbc);
        gbc.gridx = 1;
        loginUsernameField = new JTextField(15);
        panel.add(loginUsernameField, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(new JLabel("Mot de passe :"), gbc);
        gbc.gridx = 1;
        loginPasswordField = new JPasswordField(15);
        panel.add(loginPasswordField, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        JButton loginBtn = new JButton("Se connecter");
        panel.add(loginBtn, gbc);
        gbc.gridx = 1;
        JButton registerBtn = new JButton("S'inscrire");
        panel.add(registerBtn, gbc);

        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JLabel hint = new JLabel("Comptes de test : admin/admin  ou  user1/1234");
        hint.setFont(new Font("Arial", Font.ITALIC, 11));
        panel.add(hint, gbc);

        loginBtn.addActionListener(e -> handleLogin());
        registerBtn.addActionListener(e -> handleRegister());

        return panel;
    }

    private void handleLogin() {
        String username = loginUsernameField.getText().trim();
        String password = new String(loginPasswordField.getPassword());
        if (authManager.login(username, password)) {
            taskListImpl = authManager.getLoggedUser().getPrivateTasks();
            userLabel.setText("Connecté : " + username);
            refreshTable();
            updateStats();
            startNotificationTimer();
            cardLayout.show(mainPanel, "APP");
        } else {
            JOptionPane.showMessageDialog(frame,
                    "Identifiants incorrects.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleRegister() {
        String username = loginUsernameField.getText().trim();
        String password = new String(loginPasswordField.getPassword());
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(frame,
                    "Les champs ne peuvent pas être vides.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (authManager.register(username, password)) {
            JOptionPane.showMessageDialog(frame,
                    "Inscription réussie, vous pouvez vous connecter.", "OK", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame,
                    "Ce nom d'utilisateur est déjà pris.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =========================================================
    // ÉCRAN PRINCIPAL
    // =========================================================

    private JPanel buildAppPanel() {
        JPanel app = new JPanel(new BorderLayout(5, 5));
        app.add(buildTopBar(), BorderLayout.NORTH);
        app.add(buildCenter(), BorderLayout.CENTER);
        app.add(buildRightPanel(), BorderLayout.EAST);
        app.add(buildOutputArea(), BorderLayout.SOUTH);
        return app;
    }

    private JPanel buildTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        userLabel = new JLabel("Connecté : ");
        userLabel.setFont(new Font("Arial", Font.BOLD, 12));
        topBar.add(userLabel, BorderLayout.WEST);
        JButton logoutBtn = new JButton("Déconnexion");
        logoutBtn.addActionListener(e -> handleLogout());
        topBar.add(logoutBtn, BorderLayout.EAST);
        return topBar;
    }

    private void handleLogout() {
        stopNotificationTimer();
        authManager.logout();
        loginUsernameField.setText("");
        loginPasswordField.setText("");
        cardLayout.show(mainPanel, "LOGIN");
    }

    private JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout(5, 5));
        center.add(buildForm(), BorderLayout.NORTH);
        center.add(buildTable(), BorderLayout.CENTER);
        return center;
    }

    // — Formulaire —

    private JPanel buildForm() {
        JPanel form = new JPanel(new GridLayout(8, 2, 5, 5));
        form.setBorder(BorderFactory.createTitledBorder("Nouvelle tâche / Modifier"));

        form.add(new JLabel("Nom :"));
        nameField = new JTextField();
        form.add(nameField);

        form.add(new JLabel("Description :"));
        descField = new JTextField();
        form.add(descField);

        form.add(new JLabel("Date limite (yyyy-MM-dd ou yyyy-MM-dd HH:mm) :"));
        dueDateField = new JTextField();
        form.add(dueDateField);

        form.add(new JLabel("Statut :"));
        statusCombo = new JComboBox<>(new String[] { "TODO", "IN_PROGRESS", "COMPLETED", "ABANDONED" });
        form.add(statusCombo);

        form.add(new JLabel("Priorité :"));
        priorityCombo = new JComboBox<>(new String[] { "LOW", "MEDIUM", "HIGH" });
        form.add(priorityCombo);

        form.add(new JLabel("Partagée :"));
        sharedCheckBox = new JCheckBox();
        form.add(sharedCheckBox);

        form.add(new JLabel("Récurrente :"));
        JPanel recurringPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        recurringCheckBox = new JCheckBox();
        intervalCombo = new JComboBox<>(new String[] { "daily", "weekly" });
        recurringPanel.add(recurringCheckBox);
        recurringPanel.add(new JLabel(" Intervalle : "));
        recurringPanel.add(intervalCombo);
        form.add(recurringPanel);

        JPanel crudBtns = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addBtn = new JButton("Ajouter");
        JButton modifyBtn = new JButton("Modifier");
        JButton deleteBtn = new JButton("Supprimer");
        crudBtns.add(addBtn);
        crudBtns.add(modifyBtn);
        crudBtns.add(deleteBtn);
        form.add(new JLabel(""));
        form.add(crudBtns);

        addBtn.addActionListener(e -> handleAdd());
        modifyBtn.addActionListener(e -> handleModify());
        deleteBtn.addActionListener(e -> handleDelete());

        return form;
    }

    // — Table —

    private JScrollPane buildTable() {
        String[] cols = { "Nom", "Description", "Date limite", "Statut", "Priorité", "Partagée", "Récurrente" };
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        taskTable = new JTable(tableModel);
        taskTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        taskTable.getSelectionModel().addListSelectionListener(ev -> {
            if (ev.getValueIsAdjusting())
                return;
            int row = taskTable.getSelectedRow();
            if (row >= 0)
                populateFormFromRow(row);
        });
        JScrollPane scroll = new JScrollPane(taskTable);
        scroll.setBorder(BorderFactory.createTitledBorder("Liste des tâches"));
        return scroll;
    }

    // — Panneau droit —

    private JPanel buildRightPanel() {
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBorder(BorderFactory.createTitledBorder("Actions"));
        right.setPreferredSize(new Dimension(280, 0));

        // Recherche
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Rechercher :"));
        searchField = new JTextField(10);
        searchPanel.add(searchField);
        JButton searchBtn = new JButton("Chercher");
        searchBtn.addActionListener(e -> handleSearch());
        searchPanel.add(searchBtn);
        right.add(searchPanel);

        // Filtres
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton filterStatusBtn = new JButton("Filtrer statut");
        JButton filterPriorityBtn = new JButton("Filtrer priorité");
        filterStatusBtn.addActionListener(e -> handleFilterStatus());
        filterPriorityBtn.addActionListener(e -> handleFilterPriority());
        filterPanel.add(filterStatusBtn);
        filterPanel.add(filterPriorityBtn);
        right.add(filterPanel);

        // Tris
        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton sortDateBtn = new JButton("Trier par date");
        JButton sortPriorityBtn = new JButton("Trier par priorité");
        sortDateBtn.addActionListener(e -> {
            taskListImpl.sortByDueDate();
            refreshTable();
            outputArea.setText("Tâches triées par date.");
        });
        sortPriorityBtn.addActionListener(e -> {
            taskListImpl.sortByPriority();
            refreshTable();
            outputArea.setText("Tâches triées par priorité (HIGH en premier).");
        });
        sortPanel.add(sortDateBtn);
        sortPanel.add(sortPriorityBtn);
        right.add(sortPanel);

        // Afficher tout
        JPanel resetPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton showAllBtn = new JButton("Afficher tout");
        showAllBtn.addActionListener(e -> {
            refreshTable();
            outputArea.setText("Affichage de toutes les tâches.");
        });
        resetPanel.add(showAllBtn);
        right.add(resetPanel);

        // Commentaire
        JPanel commentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        commentPanel.add(new JLabel("Commentaire :"));
        commentField = new JTextField(10);
        commentPanel.add(commentField);
        JButton commentBtn = new JButton("Ajouter");
        commentBtn.addActionListener(e -> handleAddComment());
        commentPanel.add(commentBtn);
        right.add(commentPanel);

        // Export / Import
        JPanel filePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filePanel.add(new JLabel("Fichier :"));
        fileField = new JTextField(8);
        fileField.setText("tasks.json");
        filePanel.add(fileField);
        JButton exportBtn = new JButton("Exporter");
        JButton importBtn = new JButton("Importer");
        exportBtn.addActionListener(e -> handleExport());
        importBtn.addActionListener(e -> handleImport());
        filePanel.add(exportBtn);
        filePanel.add(importBtn);
        right.add(filePanel);

        // Partage
        JPanel sharePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sharePanel.add(new JLabel("Partager avec :"));
        shareField = new JTextField(8);
        sharePanel.add(shareField);
        JButton shareBtn = new JButton("Partager");
        shareBtn.addActionListener(e -> handleShare());
        sharePanel.add(shareBtn);
        right.add(sharePanel);

        // Statistiques
        right.add(Box.createVerticalStrut(10));
        statsLabel = new JLabel("<html><b>=== Statistiques ===</b><br>Total : 0</html>");
        statsLabel.setBorder(BorderFactory.createTitledBorder("Statistiques"));
        right.add(statsLabel);

        return right;
    }

    // — Zone de messages —

    private JScrollPane buildOutputArea() {
        outputArea = new JTextArea(5, 50);
        outputArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(outputArea);
        scroll.setBorder(BorderFactory.createTitledBorder("Messages / Notifications"));
        return scroll;
    }

    // =========================================================
    // HANDLERS — CRUD TÂCHES
    // =========================================================

    private void handleAdd() {
        String name = nameField.getText().trim();
        String desc = descField.getText().trim();
        String dueDate = dueDateField.getText().trim();
        Status status = Status.valueOf((String) statusCombo.getSelectedItem());
        Priority prio = Priority.valueOf((String) priorityCombo.getSelectedItem());

        if (!taskListImpl.validateInput(name, desc, dueDate)) {
            outputArea.setText(
                    "Erreur : nom/description obligatoires, date invalide (formats : yyyy-MM-dd ou yyyy-MM-dd HH:mm).");
            return;
        }

        if (!dueDate.isEmpty()) {
            Date due = parseDueDate(dueDate);
            if (due != null && due.before(new Date())) {
                if (status != Status.COMPLETED && status != Status.ABANDONED) {
                    outputArea.setText("Erreur : la date est dépassée, le statut doit être COMPLETED ou ABANDONED.");
                    return;
                }
            }
        }

        taskListImpl.add(buildTaskFromForm(name, desc, dueDate, status, prio));
        afterAction("Tâche \"" + name + "\" ajoutée.");
    }

    private void handleModify() {
        int row = taskTable.getSelectedRow();
        if (row < 0) {
            outputArea.setText("Sélectionnez une tâche dans la table.");
            return;
        }

        String oldName = (String) tableModel.getValueAt(row, 0);
        String name = nameField.getText().trim();
        String desc = descField.getText().trim();
        String dueDate = dueDateField.getText().trim();
        Status status = Status.valueOf((String) statusCombo.getSelectedItem());
        Priority prio = Priority.valueOf((String) priorityCombo.getSelectedItem());

        if (!taskListImpl.validateInput(name, desc, dueDate)) {
            outputArea.setText(
                    "Erreur : nom/description obligatoires, date invalide (formats : yyyy-MM-dd ou yyyy-MM-dd HH:mm).");
            return;
        }

        if (!dueDate.isEmpty()) {
            Date due = parseDueDate(dueDate);
            if (due != null && due.before(new Date())) {
                if (status != Status.COMPLETED && status != Status.ABANDONED) {
                    outputArea.setText("Erreur : la date est dépassée, le statut doit être COMPLETED ou ABANDONED.");
                    return;
                }
            }
        }

        taskListImpl.update(oldName, buildTaskFromForm(name, desc, dueDate, status, prio),
                authManager.getLoggedUser().getUsername());
        afterAction("Tâche \"" + oldName + "\" modifiée.");
    }

    private void handleDelete() {
        int row = taskTable.getSelectedRow();
        if (row < 0) {
            outputArea.setText("Sélectionnez une tâche à supprimer.");
            return;
        }
        String name = (String) tableModel.getValueAt(row, 0);
        taskListImpl.remove(name);
        afterAction("Tâche \"" + name + "\" supprimée.");
    }

    // =========================================================
    // HANDLERS — ACTIONS SECONDAIRES
    // =========================================================

    private void handleSearch() {
        String kw = searchField.getText().trim();
        if (kw.isEmpty()) {
            outputArea.setText("Entrez un mot-clé.");
            return;
        }
        ArrayList<TaskImpl> results = taskListImpl.search(kw);
        refreshTableWithList(results);
        outputArea.setText(results.size() + " tâche(s) trouvée(s) pour \"" + kw + "\".");
    }

    private void handleFilterStatus() {
        Status s = Status.valueOf((String) statusCombo.getSelectedItem());
        ArrayList<TaskImpl> results = taskListImpl.filterByStatus(s);
        refreshTableWithList(results);
        outputArea.setText("Filtre statut : " + s.name() + " (" + results.size() + " résultat(s)).");
    }

    private void handleFilterPriority() {
        Priority p = Priority.valueOf((String) priorityCombo.getSelectedItem());
        ArrayList<TaskImpl> results = taskListImpl.filterByPriority(p);
        refreshTableWithList(results);
        outputArea.setText("Filtre priorité : " + p.name() + " (" + results.size() + " résultat(s)).");
    }

    private void handleAddComment() {
        int row = taskTable.getSelectedRow();
        if (row < 0) {
            outputArea.setText("Sélectionnez une tâche.");
            return;
        }
        String taskName = (String) tableModel.getValueAt(row, 0);
        String comment = commentField.getText().trim();
        if (comment.isEmpty()) {
            outputArea.setText("Le commentaire ne peut pas être vide.");
            return;
        }
        taskListImpl.addComment(taskName, comment);
        commentField.setText("");
        outputArea.setText("Commentaire ajouté à \"" + taskName + "\".");
    }

    private void handleExport() {
        String filename = fileField.getText().trim();
        if (filename.isEmpty()) {
            outputArea.setText("Entrez un nom de fichier.");
            return;
        }
        taskListImpl.exportToJson(filename);
        outputArea.setText("Export réussi vers \"" + filename + "\".");
    }

    private void handleImport() {
        String filename = fileField.getText().trim();
        if (filename.isEmpty()) {
            outputArea.setText("Entrez un nom de fichier.");
            return;
        }
        // Charger dans une liste temporaire pour éviter les doublons
        TaskListImpl temp = new TaskListImpl();
        temp.importFromJson(filename);
        int count = 0;
        for (TaskImpl t : temp.getTasks()) {
            if (!taskAlreadyExists(t.getName())) {
                taskListImpl.add(t);
                count++;
            }
        }
        refreshTable();
        updateStats();
        outputArea.setText(count + " tâche(s) importée(s) depuis \"" + filename + "\".");
    }

    /**
     * Partage par référence directe.
     * Les TaskImpl cochées isShared sont ajoutées dans la privateTasks de la cible
     * —
     * ce sont les mêmes objets Java, donc toute modification est immédiatement
     * visible des deux côtés sans aucun mécanisme supplémentaire.
     * Double condition : isShared = true ET bouton "Partager" cliqué explicitement.
     */
    private void handleShare() {
        String targetName = shareField.getText().trim();
        if (targetName.isEmpty()) {
            outputArea.setText("Entrez un nom d'utilisateur cible.");
            return;
        }

        User target = authManager.findUser(targetName);
        if (target == null) {
            outputArea.setText("Utilisateur \"" + targetName + "\" introuvable.");
            return;
        }
        if (target == authManager.getLoggedUser()) {
            outputArea.setText("Vous ne pouvez pas partager avec vous-même.");
            return;
        }

        int count = 0;
        for (TaskImpl t : taskListImpl.getTasks()) {
            // Double condition : cochée isShared ET pas déjà présente chez la cible
            if (t.isShared() && !targetAlreadyHasTask(target, t)) {
                target.getPrivateTasks().add(t); // même référence Java
                count++;
            }
        }

        if (count == 0) {
            outputArea.setText("Aucune tâche partageable (cochez la case \"Partagée\" sur les tâches concernées).");
            return;
        }

        refreshTable();
        outputArea.setText(count + " tâche(s) partagée(s) avec \"" + targetName + "\".");
    }

    /**
     * Vérifie si la cible possède déjà exactement cet objet TaskImpl (même
     * référence).
     */
    private boolean targetAlreadyHasTask(User target, TaskImpl task) {
        for (TaskImpl t : target.getPrivateTasks().getTasks()) {
            if (t == task)
                return true; // comparaison par référence intentionnelle
        }
        return false;
    }

    // =========================================================
    // MÉTHODES UTILITAIRES
    // =========================================================

    private TaskImpl buildTaskFromForm(String name, String desc, String dueDate, Status status, Priority prio) {
        TaskImpl task = new TaskImpl(name, desc, dueDate, status, prio);
        task.setShared(sharedCheckBox.isSelected());
        task.setRecurring(recurringCheckBox.isSelected());
        if (recurringCheckBox.isSelected())
            task.setRecurringInterval((String) intervalCombo.getSelectedItem());
        return task;
    }

    private Date parseDueDate(String dueDate) {
        if (dueDate == null || dueDate.trim().isEmpty())
            return null;
        String fmt = dueDate.contains(":") ? "yyyy-MM-dd HH:mm" : "yyyy-MM-dd";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(fmt);
            sdf.setLenient(false);
            return sdf.parse(dueDate.trim());
        } catch (Exception ex) {
            return null;
        }
    }

    private void populateFormFromRow(int row) {
        nameField.setText((String) tableModel.getValueAt(row, 0));
        descField.setText((String) tableModel.getValueAt(row, 1));
        dueDateField.setText((String) tableModel.getValueAt(row, 2));
        statusCombo.setSelectedItem(tableModel.getValueAt(row, 3));
        priorityCombo.setSelectedItem(tableModel.getValueAt(row, 4));
        sharedCheckBox.setSelected("Oui".equals(tableModel.getValueAt(row, 5)));
        recurringCheckBox.setSelected("Oui".equals(tableModel.getValueAt(row, 6)));
    }

    private void afterAction(String message) {
        refreshTable();
        updateStats();
        clearForm();
        outputArea.setText(message);
    }

    public void refreshTable() {
        refreshTableWithList(taskListImpl.getTasks());
    }

    private void refreshTableWithList(ArrayList<TaskImpl> list) {
        tableModel.setRowCount(0);
        for (TaskImpl t : list) {
            tableModel.addRow(new String[] {
                    t.getName(),
                    t.getDescription(),
                    t.getDueDate(),
                    t.getStatus().name(),
                    t.getPriority().name(),
                    t.isShared() ? "Oui" : "Non",
                    t.isRecurring() ? "Oui" : "Non"
            });
        }
    }

    private void clearForm() {
        nameField.setText("");
        descField.setText("");
        dueDateField.setText("");
        statusCombo.setSelectedIndex(0);
        priorityCombo.setSelectedIndex(0);
        sharedCheckBox.setSelected(false);
        recurringCheckBox.setSelected(false);
        intervalCombo.setSelectedIndex(0);
    }

    private void updateStats() {
        String stats = taskListImpl.getStats();
        StringBuilder html = new StringBuilder("<html>");
        for (String line : stats.split("\n"))
            html.append(line.startsWith("===") ? "<b>" + line + "</b><br>" : line + "<br>");
        statsLabel.setText(html + "</html>");
    }

    private boolean taskAlreadyExists(String name) {
        for (TaskImpl t : taskListImpl.getTasks())
            if (t.getName().equals(name))
                return true;
        return false;
    }

    // =========================================================
    // NOTIFICATIONS
    // =========================================================

    private void startNotificationTimer() {
        checkAndDisplayNotifications();
        notificationTimer = new Timer(30000, e -> checkAndDisplayNotifications());
        notificationTimer.start();
    }

    private void checkAndDisplayNotifications() {
        String currentUser = authManager.getLoggedUser().getUsername();
        ArrayList<String> alerts = new ArrayList<>();
        alerts.addAll(notificationEngine.checkNotifications(taskListImpl, currentUser));
        if (!alerts.isEmpty()) {
            StringBuilder sb = new StringBuilder("=== NOTIFICATIONS ===\n");
            for (String alert : alerts)
                sb.append(alert).append("\n");
            outputArea.setText(sb.toString());
        }
    }

    private void stopNotificationTimer() {
        if (notificationTimer != null)
            notificationTimer.stop();
    }

    // =========================================================
    // ACCESSEURS
    // =========================================================

    public TaskListImpl getTaskListImpl() {
        return taskListImpl;
    }

    public AuthManager getAuthManager() {
        return authManager;
    }

    public JFrame getFrame() {
        return frame;
    }
}