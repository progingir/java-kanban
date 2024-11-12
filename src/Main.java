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


                    if (tasks.containsKey(id)) {
                        System.out.println(manager.getTaskById(id).printTask());
                    } else if (epicTasks.containsKey(id)) {
                        System.out.println(manager.getEpicTaskById(id).printTask());
                    } else {
                        for (List<Subtask> subtaskList : subTasks.values()) {
                            for (Subtask subtask : subtaskList) {
                                if (subtask.getId() == id) {
                                    System.out.println(manager.getSubTaskById(id).printTask());
                                }
                            }
                        }
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
                    index = manager.getTaskIndex(heading, description, "epic task");

                    if (index != -1) {
                        System.out.println("Такая глобальная задача уже есть! Ее идентификатор - " + epicTasks.get(index).getId());
                    } else {
                        EpicTask newTask = new EpicTask(heading, description, index);
                        manager.createEpicTask(newTask).printTask();
                        System.out.println("Глобальная задача успешно создана! Ее идентификатор: " + newTask.getId());
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
                    epicTasks.get(epicId).addSubtask(newSubtask);
                    Subtask createdSubtask = manager.createSubTask(newSubtask);
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
                    runOptionalScen(manager);
                    return;
                case 13:
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
        System.out.println("12 - Запустить опциональный сценарий");
        System.out.println("13 - Выход");
    }

    private static void runOptionalScen(InMemoryTaskManager manager) {
        Task task1 = new Task("Task 1", "Description for Task 1", 1);
        Task task2 = new Task("Task 2", "Description for Task 2", 2);
        manager.createTask(task1);
        manager.createTask(task2);

        EpicTask epicWithoutSubtasks = new EpicTask("Epic without Subtasks", "Description for Epic without Subtasks", 3);
        manager.createEpicTask(epicWithoutSubtasks);

        EpicTask epicWithSubtasks = new EpicTask("Epic with Subtasks", "Description for Epic with Subtasks", 4);
        manager.createEpicTask(epicWithSubtasks);

        Subtask subtask1 = new Subtask("Subtask 1", "Description for Subtask 1", 5, epicWithSubtasks.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description for Subtask 2", 6, epicWithSubtasks.getId());
        Subtask subtask3 = new Subtask("Subtask 3", "Description for Subtask 3", 7, epicWithSubtasks.getId());

        manager.createSubTask(subtask1);
        manager.createSubTask(subtask2);
        manager.createSubTask(subtask3);
        epicWithSubtasks.addSubtask(subtask1);
        epicWithSubtasks.addSubtask(subtask2);
        epicWithSubtasks.addSubtask(subtask3);

        System.out.println(manager.getTaskById(task1.getId()).printTask());
        System.out.println(manager.getTaskById(task2.getId()).printTask());
        System.out.println(manager.getEpicTaskById(epicWithoutSubtasks.getId()).printTask());
        System.out.println(manager.getEpicTaskById(epicWithSubtasks.getId()).printTask());
        System.out.println(manager.getEpicTaskById(epicWithoutSubtasks.getId()).printTask());
        System.out.println(manager.getTaskById(task2.getId()).printTask());

        System.out.println("История просмотров: " + Managers.getDefaultHistory().getHistory());

        manager.removeTaskById(task2.getId());
        System.out.println("Удалили задачу Task 2");
        System.out.println("История просмотров после удаления Task 2: " + Managers.getDefaultHistory().getHistory());

        manager.removeTaskById(epicWithSubtasks.getId());
        System.out.println("Удалили эпик с подзадачами.");
        System.out.println("История просмотров после удаления эпика с подзадачами: " + Managers.getDefaultHistory().getHistory());
    }
}