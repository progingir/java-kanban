import java.util.ArrayList;
import java.util.HashMap;
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

        TaskManager manager = new TaskManager(tasks, epicTasks, subTasks);

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

                    Task newTask = manager.createTask(heading, description);

                    System.out.println("Задача успешно создана! Ее индекс: " + newTask.getId());
                    System.out.println("Ее статус: " + newTask.status);
                    break;
                case 2:
                    System.out.println("Введите идентификатор задачи, которую хотите отредактировать: ");
                    id = scanner.nextInt();
                    while (true) {
                        System.out.println("Выберите нужное действие:");
                        System.out.println("1 - Редактировать название.");
                        System.out.println("2 - Редактировать описание.");
                        System.out.println("0 - Закончить редактирование.");
                        int comm = scanner.nextInt();

                        manager.updateTask(id, comm);
                        if (comm == 0) {
                            break;
                        }
                    }
                    break;
                case 3:
                    System.out.println("Введите идентификатор задачи, которую хотите посмотреть:");
                    id = scanner.nextInt();
                    System.out.println(manager.getTaskById(id));
                    break;
                case 4:
                    System.out.println("Уверены, что хотите удалить все задачи?");
                    System.out.println("1 - да");
                    System.out.println("2 - нет");
                    int answer = scanner.nextInt();
                    if (answer == 1) {
                        manager.removeAllTasks();
                        System.out.println("Все задачи удалены!");
                    } else {
                        break;
                    }
                    break;
                case 5:
                    System.out.println("Введите идентификатор задачи, которую хотите удалить:");
                    id = scanner.nextInt();
                    manager.removeTaskById(id);
                    System.out.println("Задача успешно удалена!");
                    break;
                case 6:
                    manager.getAllTasks();
                    break;
                case 7:
                    System.out.println("Введите идентификатор эпика");
                    id = scanner.nextInt();
                    manager.getSubtasks(id);
                    break;
                case 8:
                    System.out.println("Введите название задачи:");
                    heading = scanner.nextLine();
                    System.out.println("Введите описание задачи:");
                    description = scanner.nextLine();
                    EpicTask newEpicTask = manager.createEpicTask(heading, description);
                    System.out.println("Глобальная задача создана! Ее индекс - " + newEpicTask.id);

                    break;
                case 9:
                    System.out.println("Укажите идентификатор эпика:");
                    id = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("Введите заголовок подзадачи");
                    heading = scanner.nextLine();
                    System.out.println("Введите описание подзадачи:");
                    description = scanner.nextLine();

                    Subtask newSubtask = manager.createSubTask(heading, description, id);
                    System.out.println("Подзадача успешно создана! Ее индекс - " + newSubtask.id);
                    break;
                case 10:
                    System.out.println("Введите идентификатор задачи");
                    id = scanner.nextInt();
                    System.out.println("Укажите статус задачи:");
                    System.out.println("1 - в процессе");
                    System.out.println("2 - сделана");
                    int status = scanner.nextInt();
                    manager.checkStatus(id, status);
                    System.out.println("Статус изменен!");
                    break;
                case 11:
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
        System.out.println("11 - Выход");
    }
}