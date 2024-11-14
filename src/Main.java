import tracker.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int id;
        String heading;
        String description;

        HashMap<Integer, Task> tasks = new HashMap<>();
        HashMap<Integer, EpicTask> epicTasks = new HashMap<>();
        HashMap<Integer, ArrayList<Subtask>> subTasks = new HashMap<>();


        String fileName = "tasks.csv";
        File file = new File(fileName);

        FileBackedTaskManager manager;
        if (file.exists()) {
            manager = FileBackedTaskManager.loadFromFile(file);
        } else {
            manager = new FileBackedTaskManager(tasks, epicTasks, subTasks, fileName);
        }


        while (true) {
            printMenu();
            int command = scanner.nextInt();
            scanner.nextLine();

            switch (command) {
                case 1:
                    System.out.println("Введите название задачи:");
                    heading = scanner.nextLine();
                    System.out.println("Введите описание задачи:");
                    description = scanner.nextLine();
                    int index = manager.getTaskIndex(heading, description, "task");

                    if (index != -1) {
                        System.out.println("Такая задача уже есть! Ее индекс: " + tasks.get(index).getId());
                    } else {
                        Task newTask = new Task(heading, description, index);
                        manager.createTask(newTask);
                        System.out.println("Задача успешно создана! Ее индекс: " + newTask.getId());
                    }
                    break;
                case 2:
                    System.out.println("Введите идентификатор задачи, которую хотите отредактировать: ");
                    id = scanner.nextInt();
                    if (manager.checkTaskId(id) || manager.checkEpicTaskId(id)) {
                        while (true) {
                            System.out.println("Выберите нужное действие:");
                            System.out.println("1 - Редактировать название");
                            System.out.println("2 - Редактировать описание");
                            System.out.println("0 - Закончить редактирование");
                            int comm = scanner.nextInt();
                            scanner.nextLine();

                            if (comm == 1) {
                                System.out.println("Введите новое название:");
                                String newHeading = scanner.nextLine();
                                manager.updateTask(id, comm, newHeading);
                                System.out.println("Новое название сохранено!");
                            } else if (comm == 2) {
                                System.out.println("Введите новое описание:");
                                String newDescription = scanner.nextLine();
                                manager.updateTask(id, comm, newDescription);
                                System.out.println("Новое описание сохранено!");
                            } else if (comm == 0) {
                                break;
                            } else {
                                System.out.println("Такой команды нет!");
                            }
                        }
                    } else {
                        System.out.println("Задачи с таким id пока что нет");
                    }
                    break;
                case 3:
                    System.out.println("Введите идентификатор задачи, которую хотите посмотреть:");
                    id = scanner.nextInt();

                    String taskType = manager.getTaskTypeById(id);
                    if (taskType != null) {
                        switch (taskType) {
                            case "TASK":
                                Task task = manager.getTaskById(id);
                                System.out.println("Задача: " + task);
                                break;
                            case "EPIC":
                                EpicTask epic = manager.getEpicTaskById(id);
                                System.out.println("Эпик: " + epic);
                                break;
                            case "SUB":
                                Subtask subtask = manager.getSubTaskById(id);
                                System.out.println("Подзадача: " + subtask);
                                break;
                            default:
                                System.out.println("Неизвестный тип задачи.");
                                break;
                        }
                    } else {
                        System.out.println("Задача с ID " + id + " не найдена.");
                    }
                    break;
                case 4:
                    System.out.println("Уверены, что хотите удалить все задачи?");
                    System.out.println("1 - да");
                    System.out.println("2 - нет");
                    int answer = scanner.nextInt();
                    if (answer == 1) {
                        manager.removeAllTasks();
                        manager.removeAllEpicTasks();
                        manager.removeAllSubTasks();
                        System.out.println("Все задачи удалены!");
                    } else {
                        break;
                    }
                    break;
                case 5:
                    System.out.println("Введите идентификатор задачи, которую хотите удалить:");
                    id = scanner.nextInt();
                    if (manager.removeTaskById(id)) {
                        System.out.println("Задача успешно удалена!");
                    } else {
                        System.out.println("Задачи с таким идентификатором пока что нет");
                    }
                    break;
                case 6:
                    System.out.println("== Список обычных задач: ==");
                    System.out.println(manager.getAllTasks());
                    System.out.println();
                    System.out.println("== Список глобальных задач: ==");
                    System.out.println(manager.getAllEpicTasks());
                    System.out.println();
                    System.out.println("== Список подзадач: ==");
                    System.out.println(manager.getAllSubTasks());
                    System.out.println();
                    break;
                case 7:
                    System.out.println("Введите идентификатор эпика");
                    id = scanner.nextInt();
                    System.out.println(manager.getSubtasks(id));
                    break;
                case 8:
                    System.out.println("Введите название задачи:");
                    heading = scanner.nextLine();
                    System.out.println("Введите описание задачи:");
                    description = scanner.nextLine();

                    int epicIndex = manager.getTaskIndex(heading, description, "epic task");

                    if (epicIndex != -1) {
                        System.out.println("Такой глобальной задачи уже есть! Ее идентификатор - " + epicTasks.get(epicIndex).getId());
                    } else {
                        EpicTask newEpicTask = new EpicTask(heading, description, epicIndex);
                        manager.createEpicTask(newEpicTask);
                        System.out.println("Глобальная задача успешно создана! Ее идентификатор: " + newEpicTask.getId());
                    }
                    break;

                case 9:
                    System.out.println("Укажите идентификатор эпика:");
                    int epicId = scanner.nextInt();
                    scanner.nextLine();
                    if (!manager.checkEpicTaskId(epicId)) {
                        System.out.println("Эпика с таким индексом нет");
                        break;
                    }
                    System.out.println("Введите заголовок подзадачи");
                    heading = scanner.nextLine();
                    System.out.println("Введите описание подзадачи:");
                    description = scanner.nextLine();

                    Subtask newSubtask = new Subtask(heading, description, 0, epicId);
                    Subtask createdSubtask = manager.createSubTask(newSubtask); // Создаем подзадачу через менеджер
                    System.out.println("Подзадача успешно создана! Ее идентификатор - " + createdSubtask.getId());
                    break;

                case 10:
                    System.out.println("Введите идентификатор задачи");
                    id = scanner.nextInt();
                    int status = 0;
                    if (!epicTasks.containsKey(id)) {
                        System.out.println("Укажите статус задачи:");
                        System.out.println("1 - в процессе");
                        System.out.println("2 - сделана");
                        status = scanner.nextInt();
                    }
                    System.out.println(manager.checkStatus(id, status));
                    System.out.println("Статус изменен!");
                    break;
                case 11:
                    System.out.println(Managers.getDefaultHistory().getHistory());
                    break;
                case 12:
                    return;
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
        System.out.println("7 - Вывести подзадачи эпика");
        System.out.println("8 - Создать эпик");
        System.out.println("9 - Добавить подзадачи в эпик");
        System.out.println("10 - Отметить сделанную задачу");
        System.out.println("11 - Показать историю просмотров");
        System.out.println("12 - Выход");
    }
}