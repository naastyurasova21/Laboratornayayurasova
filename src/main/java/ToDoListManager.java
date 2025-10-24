import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

class Task {
    private static int nextId = 1;

    private final int id;
    private String title;
    private String description;
    private Date dueDate;
    private Priority priority;
    private boolean completed;

    public enum Priority {
        LOW, MEDIUM, HIGH
    }

    public Task(String title, String description, Date dueDate, Priority priority) {
        this.id = nextId++;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.completed = false;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }
    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        return String.format("ID: %d | %s | %s | Срок: %s | Приоритет: %s | Статус: %s",
                id, title, description, sdf.format(dueDate),
                priority, completed ? "Выполнена" : "Активна");
    }
}

public class ToDoListManager {
    private static final List<Task> tasks = new ArrayList<>();
    private static final Scanner scanner = new Scanner(System.in);
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

    public static void main(String[] args) {
        while (true) {
            printMenu();
            int choice = getIntInput("Выберите действие: ");

            switch (choice) {
                case 1 -> createTask();
                case 2 -> editTask();
                case 3 -> deleteTask();
                case 4 -> markTaskCompleted();
                case 5 -> showAllTasks();
                case 6 -> sortTasks();
                case 7 -> searchTasks();
                case 0 -> {
                    System.out.println("Выход из программы");
                    return;
                }
                default -> System.out.println("Неверный выбор");
            }
        }
    }

    private static void printMenu() {
        System.out.println("\nTO-DO LIST Manager");
        System.out.println("1. Создать задачу");
        System.out.println("2. Редактировать задачу");
        System.out.println("3. Удалить задачу");
        System.out.println("4. Отметить как выполненную");
        System.out.println("5. Показать все задачи");
        System.out.println("6. Сортировать задачи");
        System.out.println("7. Поиск задач");
        System.out.println("0. Выход");
    }

    private static void createTask() {
        System.out.println("\nСоздание новой задачи...");
        System.out.print("Название: ");
        String title = scanner.nextLine();

        System.out.print("Описание: ");
        String description = scanner.nextLine();

        Date dueDate = getDateInput("Срок выполнения (дд.мм.гггг): ");

        System.out.println("Приоритет (1-Низкий, 2-Средний, 3-Высокий): ");
        int priorityChoice = getIntInput("Выберите приоритет: ");
        Task.Priority priority = switch (priorityChoice) {
            case 1 -> Task.Priority.LOW;
            case 2 -> Task.Priority.MEDIUM;
            case 3 -> Task.Priority.HIGH;
            default -> Task.Priority.MEDIUM;
        };

        Task task = new Task(title, description, dueDate, priority);
        tasks.add(task);
        System.out.println("Задача создана успешно");
    }

    private static void editTask() {
        if (tasks.isEmpty()) {
            System.out.println("Список задач пуст");
            return;
        }

        showAllTasks();
        int id = getIntInput("Введите ID задачи для редактирования: ");

        Task task = findTaskById(id);
        if (task == null) {
            System.out.println("Задача не найдена");
            return;
        }

        System.out.println("Редактирование задачи: " + task);

        System.out.print("Новое название (текущее: " + task.getTitle() + "): ");
        String title = scanner.nextLine();
        if (!title.isEmpty()) task.setTitle(title);

        System.out.print("Новое описание (текущее: " + task.getDescription() + "): ");
        String description = scanner.nextLine();
        if (!description.isEmpty()) task.setDescription(description);

        System.out.println("Изменить срок выполнения? (y/n)");
        if (scanner.nextLine().equalsIgnoreCase("y")) {
            task.setDueDate(getDateInput("Новый срок (дд.мм.гггг): "));
        }

        System.out.println("Изменить приоритет? (y/n)");
        if (scanner.nextLine().equalsIgnoreCase("y")) {
            System.out.println("Приоритет (1-Низкий, 2-Средний, 3-Высокий): ");
            int priorityChoice = getIntInput("Выберите приоритет: ");
            task.setPriority(switch (priorityChoice) {
                case 1 -> Task.Priority.LOW;
                case 2 -> Task.Priority.MEDIUM;
                case 3 -> Task.Priority.HIGH;
                default -> task.getPriority();
            });
        }

        System.out.println("Задача обновлена успешно");
    }

    private static void deleteTask() {
        if (tasks.isEmpty()) {
            System.out.println("Список задач пуст");
            return;
        }

        showAllTasks();
        int id = getIntInput("Введите ID задачи для удаления: ");

        Task task = findTaskById(id);
        if (task != null) {
            tasks.remove(task);
            System.out.println("Задача удалена успешно");
        } else {
            System.out.println("Задача не найдена");
        }
    }

    private static void markTaskCompleted() {
        if (tasks.isEmpty()) {
            System.out.println("Список задач пуст");
            return;
        }

        showAllTasks();
        int id = getIntInput("Введите ID задачи для отметки как выполненной: ");

        Task task = findTaskById(id);
        if (task != null) {
            task.setCompleted(true);
            System.out.println("Задача отмечена как выполненная");
        } else {
            System.out.println("Задача не найдена");
        }
    }

    private static void showAllTasks() {
        if (tasks.isEmpty()) {
            System.out.println("Список задач пуст");
            return;
        }

        System.out.println("\nВсе задачи...");
        tasks.forEach(System.out::println);
    }

    private static void sortTasks() {
        if (tasks.isEmpty()) {
            System.out.println("Список задач пуст");
            return;
        }

        System.out.println("\nСортировка задач...");
        System.out.println("1. По дате выполнения");
        System.out.println("2. По приоритету");
        System.out.println("3. По статусу");

        int choice = getIntInput("Выберите тип сортировки: ");

        switch (choice) {
            case 1 -> {
                tasks.sort(Comparator.comparing(Task::getDueDate));
                System.out.println("Задачи отсортированы по дате выполнения");
            }
            case 2 -> {
                tasks.sort(Comparator.comparing(Task::getPriority).reversed());
                System.out.println("Задачи отсортированы по приоритету");
            }
            case 3 -> {
                tasks.sort(Comparator.comparing(Task::isCompleted));
                System.out.println("Задачи отсортированы по статусу");
            }
            default -> System.out.println("Неверный выбор");
        }

        showAllTasks();
    }

    private static void searchTasks() {
        if (tasks.isEmpty()) {
            System.out.println("Список задач пуст");
            return;
        }

        System.out.println("\nПоиск задач...");
        System.out.println("1. По названию");
        System.out.println("2. По описанию");
        System.out.println("3. По статусу");
        System.out.println("4. По приоритету");

        int choice = getIntInput("Выберите тип поиска: ");
        List<Task> results = new ArrayList<>();

        switch (choice) {
            case 1 -> {
                System.out.print("Введите название для поиска: ");
                String keyword = scanner.nextLine().toLowerCase();
                results = tasks.stream()
                        .filter(task -> task.getTitle().toLowerCase().contains(keyword))
                        .collect(Collectors.toList());
            }
            case 2 -> {
                System.out.print("Введите описание для поиска: ");
                String keyword = scanner.nextLine().toLowerCase();
                results = tasks.stream()
                        .filter(task -> task.getDescription().toLowerCase().contains(keyword))
                        .collect(Collectors.toList());
            }
            case 3 -> {
                System.out.println("Статус (1-Активные, 2-Выполненные): ");
                int statusChoice = getIntInput("Выберите статус: ");
                boolean completed = statusChoice == 2;
                results = tasks.stream()
                        .filter(task -> task.isCompleted() == completed)
                        .collect(Collectors.toList());
            }
            case 4 -> {
                System.out.println("Приоритет (1-Низкий, 2-Средний, 3-Высокий): ");
                int priorityChoice = getIntInput("Выберите приоритет: ");
                Task.Priority priority = switch (priorityChoice) {
                    case 1 -> Task.Priority.LOW;
                    case 2 -> Task.Priority.MEDIUM;
                    case 3 -> Task.Priority.HIGH;
                    default -> null;
                };
                if (priority != null) {
                    results = tasks.stream()
                            .filter(task -> task.getPriority() == priority)
                            .collect(Collectors.toList());
                }
            }
            default -> {
                System.out.println("Неверный выбор!");
                return;
            }
        }

        if (results.isEmpty()) {
            System.out.println("Задачи не найдены!");
        } else {
            System.out.println("\n--- Результаты поиска ---");
            results.forEach(System.out::println);
        }
    }

    private static Task findTaskById(int id) {
        return tasks.stream()
                .filter(task -> task.getId() == id)
                .findFirst()
                .orElse(null);
    }

    private static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Пожалуйста, введите число");
            }
        }
    }

    private static Date getDateInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String dateStr = scanner.nextLine();
                return sdf.parse(dateStr);
            } catch (ParseException e) {
                System.out.println("Неверный формат даты! Используйте дд.мм.гггг");
            }
        }
    }
}