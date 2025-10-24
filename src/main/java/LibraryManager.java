import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

class Book {
    private static int nextId = 1;

    private int id;
    private String title;
    private String author;
    private String isbn;
    private int year;
    private String genre;
    private boolean available;

    public Book(String title, String author, String isbn, int year, String genre) {
        this.id = nextId++;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.year = year;
        this.genre = genre;
        this.available = true;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    @Override
    public String toString() {
        return String.format("ID: %d | %s - %s | ISBN: %s | Год: %d | Жанр: %s | %s",
                id, title, author, isbn, year, genre,
                available ? "Доступна" : "Выдана");
    }
}

public class LibraryManager {
    private static List<Book> books = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);
    private static final String FILE_NAME = "library.txt";

    public static void main(String[] args) {
        loadFromFile();

        while (true) {
            printMenu();
            int choice = getIntInput("Выберите действие: ");

            switch (choice) {
                case 1 -> addBook();
                case 2 -> editBook();
                case 3 -> showAllBooks();
                case 4 -> searchBooks();
                case 5 -> changeBookStatus();
                case 6 -> saveToFile();
                case 7 -> loadFromFile();
                case 0 -> {
                    saveToFile();
                    System.out.println("Выход из программы");
                    return;
                }
                default -> System.out.println("Неверный выбор!");
            }
        }
    }

    private static void printMenu() {
        System.out.println("\nLibrary Manager");
        System.out.println("1. Добавить книгу");
        System.out.println("2. Редактировать книгу");
        System.out.println("3. Показать все книги");
        System.out.println("4. Поиск книг");
        System.out.println("5. Изменить статус книги");
        System.out.println("6. Сохранить в файл");
        System.out.println("7. Загрузить из файла");
        System.out.println("0. Выход");
    }

    private static void addBook() {
        System.out.println("\nДобавление новой книги...");
        System.out.print("Название: ");
        String title = scanner.nextLine();

        System.out.print("Автор: ");
        String author = scanner.nextLine();

        System.out.print("ISBN: ");
        String isbn = scanner.nextLine();

        int year = getIntInput("Год издания: ");

        System.out.print("Жанр: ");
        String genre = scanner.nextLine();

        Book book = new Book(title, author, isbn, year, genre);
        books.add(book);
        System.out.println("Книга добавлена успешно");
    }

    private static void editBook() {
        if (books.isEmpty()) {
            System.out.println("Библиотека пуста");
            return;
        }

        showAllBooks();
        int id = getIntInput("Введите ID книги для редактирования: ");

        Book book = findBookById(id);
        if (book == null) {
            System.out.println("Книга не найдена");
            return;
        }

        System.out.println("Редактирование книги: " + book);

        System.out.print("Новое название (текущее: " + book.getTitle() + "): ");
        String title = scanner.nextLine();
        if (!title.isEmpty()) book.setTitle(title);

        System.out.print("Новый автор (текущее: " + book.getAuthor() + "): ");
        String author = scanner.nextLine();
        if (!author.isEmpty()) book.setAuthor(author);

        System.out.print("Новый ISBN (текущий: " + book.getIsbn() + "): ");
        String isbn = scanner.nextLine();
        if (!isbn.isEmpty()) book.setIsbn(isbn);

        System.out.println("Изменить год издания? (y/n)");
        if (scanner.nextLine().equalsIgnoreCase("y")) {
            book.setYear(getIntInput("Новый год издания: "));
        }

        System.out.print("Новый жанр (текущий: " + book.getGenre() + "): ");
        String genre = scanner.nextLine();
        if (!genre.isEmpty()) book.setGenre(genre);

        System.out.println("Книга обновлена успешно");
    }

    private static void showAllBooks() {
        if (books.isEmpty()) {
            System.out.println("Библиотека пуста");
            return;
        }

        System.out.println("\nВсе книги в библиотеке:");
        books.forEach(System.out::println);
    }

    private static void searchBooks() {
        if (books.isEmpty()) {
            System.out.println("Библиотека пуста");
            return;
        }

        System.out.println("\nПоиск книг:");
        System.out.println("1. По названию");
        System.out.println("2. По автору");
        System.out.println("3. По жанру");
        System.out.println("4. По году издания");
        System.out.println("5. По статусу доступности");
        System.out.println("6. По ISBN");

        int choice = getIntInput("Выберите тип поиска: ");
        List<Book> results = new ArrayList<>();

        switch (choice) {
            case 1 -> {
                System.out.print("Введите название для поиска: ");
                String keyword = scanner.nextLine().toLowerCase();
                results = books.stream()
                        .filter(book -> book.getTitle().toLowerCase().contains(keyword))
                        .collect(Collectors.toList());
            }
            case 2 -> {
                System.out.print("Введите автора для поиска: ");
                String keyword = scanner.nextLine().toLowerCase();
                results = books.stream()
                        .filter(book -> book.getAuthor().toLowerCase().contains(keyword))
                        .collect(Collectors.toList());
            }
            case 3 -> {
                System.out.print("Введите жанр для поиска: ");
                String keyword = scanner.nextLine().toLowerCase();
                results = books.stream()
                        .filter(book -> book.getGenre().toLowerCase().contains(keyword))
                        .collect(Collectors.toList());
            }
            case 4 -> {
                int year = getIntInput("Введите год издания: ");
                results = books.stream()
                        .filter(book -> book.getYear() == year)
                        .collect(Collectors.toList());
            }
            case 5 -> {
                System.out.println("Статус (1-Доступные, 2-Выданные): ");
                int statusChoice = getIntInput("Выберите статус: ");
                boolean available = statusChoice == 1;
                results = books.stream()
                        .filter(book -> book.isAvailable() == available)
                        .collect(Collectors.toList());
            }
            case 6 -> {
                System.out.print("Введите ISBN для поиска: ");
                String isbn = scanner.nextLine();
                results = books.stream()
                        .filter(book -> book.getIsbn().equals(isbn))
                        .collect(Collectors.toList());
            }
            default -> {
                System.out.println("Неверный выбор");
                return;
            }
        }

        if (results.isEmpty()) {
            System.out.println("Книги не найдены!");
        } else {
            System.out.println("\nРезультаты поиска:");
            results.forEach(System.out::println);
        }
    }

    private static void changeBookStatus() {
        if (books.isEmpty()) {
            System.out.println("Библиотека пуста");
            return;
        }

        showAllBooks();
        int id = getIntInput("Введите ID книги для изменения статуса: ");

        Book book = findBookById(id);
        if (book != null) {
            book.setAvailable(!book.isAvailable());
            System.out.println("Статус книги изменен на: " +
                    (book.isAvailable() ? "Доступна" : "Выдана"));
        } else {
            System.out.println("Книга не найдена");
        }
    }

    private static void saveToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Book book : books) {
                writer.println(book.getId() + "|" + book.getTitle() + "|" +
                        book.getAuthor() + "|" + book.getIsbn() + "|" +
                        book.getYear() + "|" + book.getGenre() + "|" +
                        book.isAvailable());
            }
            System.out.println("Данные успешно сохранены в файл: " + FILE_NAME);
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении в файл: " + e.getMessage());
        }
    }

    private static void loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            System.out.println("Файл с данными не найден. Будет создан новый.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            books.clear();
            String line;
            int maxId = 0;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 7) {
                    int id = Integer.parseInt(parts[0]);
                    String title = parts[1];
                    String author = parts[2];
                    String isbn = parts[3];
                    int year = Integer.parseInt(parts[4]);
                    String genre = parts[5];
                    boolean available = Boolean.parseBoolean(parts[6]);

                    Book book = new Book(title, author, isbn, year, genre);
                    try {
                        java.lang.reflect.Field idField = Book.class.getDeclaredField("id");
                        idField.setAccessible(true);
                        idField.set(book, id);
                    } catch (Exception e) {
                        System.out.println("Ошибка при загрузке ID книги");
                    }

                    book.setAvailable(available);
                    books.add(book);

                    if (id > maxId) maxId = id;
                }
            }

            try {
                java.lang.reflect.Field nextIdField = Book.class.getDeclaredField("nextId");
                nextIdField.setAccessible(true);
                nextIdField.set(null, maxId + 1);
            } catch (Exception e) {
                System.out.println("Ошибка при установке nextId");
            }

            System.out.println("Данные успешно загружены из файла: " + FILE_NAME);
        } catch (IOException e) {
            System.out.println("Ошибка при загрузке из файла: " + e.getMessage());
        }
    }

    private static Book findBookById(int id) {
        return books.stream()
                .filter(book -> book.getId() == id)
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
}