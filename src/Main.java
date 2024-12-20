import tracker.*;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        new KVServer().start();

        HttpTaskManager httpManager = (HttpTaskManager) Managers.getDefault("http://localhost:8078");
        new HttpTaskServer(httpManager).start();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            printMenu();
            int command = scanner.nextInt();
            scanner.nextLine();

            switch (command) {
                case 1: // Создать задачу
                    System.out.println("Введите название задачи:");
                    String heading = scanner.nextLine();
                    System.out.println("Введите описание задачи:");
                    String description = scanner.nextLine();
                    System.out.println("Введите идентификатор задачи:");
                    int id = scanner.nextInt();
                    System.out.println("Введите продолжительность задачи (в минутах):");
                    long durationMinutes = scanner.nextLong();
                    Duration duration = Duration.ofMinutes(durationMinutes);
                    System.out.println("Введите дату и время начала выполнения задачи (в формате YYYY-MM-DDTHH:MM):");
                    String startTimeInput = scanner.next();
                    LocalDateTime startTime = LocalDateTime.parse(startTimeInput);

                    Task newTask = new Task(heading, description, id, duration, startTime);
                    httpManager.addTask(newTask);
                    System.out.println("Задача успешно создана! Ее идентификатор: " + newTask.getId());
                    break;

                case 2: // Обновить задачу
                    System.out.println("Введите идентификатор задачи, которую хотите отредактировать: ");
                    id = scanner.nextInt();
                    if (httpManager.getTaskById(id) != null) {
                        System.out.println("Выберите нужное действие:");
                        System.out.println("1 - Редактировать название");
                        System.out.println("2 - Редактировать описание");
                        int comm = scanner.nextInt();
                        scanner.nextLine();

                        if (comm == 1) {
                            System.out.println("Введите новое название:");
                            String newHeading = scanner.nextLine();
                            httpManager.updateTaskName(id, newHeading);
                            System.out.println("Новое название сохранено!");
                        } else if (comm == 2) {
                            System.out.println("Введите новое описание:");
                            String newDescription = scanner.nextLine();
                            httpManager.updateTaskDescription(id, newDescription);
                            System.out.println("Новое описание сохранено!");
                        } else {
                            System.out.println("Такой команды нет!");
                        }
                    } else {
                        System.out.println("Задачи с таким id пока что нет");
                    }
                    break;

                case 3: // Вывести задачу по id
                    System.out.println("Введите идентификатор задачи, которую хотите посмотреть:");
                    id = scanner.nextInt();
                    Task task = httpManager.getTaskById(id);
                    if (task != null) {
                        System.out.println("Задача: " + task);
                    } else {
                        System.out.println("Задача с ID " + id + " не найдена.");
                    }
                    break;

                case 4: // Удалить все задачи
                    System.out.println("Уверены, что хотите удалить все задачи?");
                    System.out.println("1 - да");
                    System.out.println("2 - нет");
                    int answer = scanner.nextInt();
                    if (answer == 1) {
                        httpManager.removeAllTasks();
                        System.out.println("Все задачи удалены!");
                    }
                    break;

                case 5: // Удалить задачу по id
                    System.out.println("Введите идентификатор задачи, которую хотите удалить:");
                    id = scanner.nextInt();
                    if (httpManager.removeTaskById(id)) {
                        System.out.println("Задача успешно удалена!");
                    } else {
                        System.out.println("Задачи с таким идентификатором пока что нет");
                    }
                    break;

                case 6: // Вывести все задачи
                    System.out.println("== Список задач: ==");
                    List<Task> allTasks = httpManager.getAllTasks();
                    for (Task t : allTasks) {
                        System.out.println(t);
                    }
                    break;

                case 7: // Вывести приоритезированные задачи
                    System.out.println("== Приоритезированные задачи: ==");
                    List<Task> prioritizedTasks = httpManager.getPrioritizedTasks();
                    for (Task prioritizedTask : prioritizedTasks) {
                        System.out.println(prioritizedTask);
                    }
                    break;

                case 8:
                    return;

                default:
                    System.out.println("Неизвестная команда. Попробуйте снова.");
            }
        }
    }

    private static void printMenu() {
        System.out.println("Выберите нужную команду:");
        System.out.println("1 - Создать задачу");
        System.out.println("2 - Обновить задачу");
        System.out.println("3 - Вывести задачу по id");
        System.out.println("4 - Удалить все задачи");
        System.out.println("5 - Удалить задачу по id");
        System.out.println("6 - Вывести все задачи");
        System.out.println("7 - Вывести приоритезированные задачи");
        System.out.println("8 - Выход");
    }
}