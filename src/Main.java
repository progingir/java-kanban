import java.util.HashMap;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int count = 0;
        int id;

        HashMap<Integer, Task> tasks = new HashMap<>();
        TaskManager manager = new TaskManager(tasks);

        while (true) {
            printMenu();
            int command = scanner.nextInt();

            switch (command) {
                case 1:
                    System.out.println("Введите название задачи:");
                    String heading = scanner.next();
                    System.out.println("Введите описание задачи:");
                    String description = scanner.next();

                    Task newTask = manager.createTask(heading,description);

                    System.out.println("Задача успешно создана! Ее индекс: " + newTask.getId());
                    count++;
                    break;
                case 2:
                    System.out.println("Введите идентификатор задачи, которую хотите отредактировать: ");
                    id = scanner.nextInt();
                    while (true) {
                        System.out.println("Выберите нужное действие:");
                        System.out.println("1 - Редактировать название");
                        System.out.println("2 - Редактировать описание");
                        System.out.println("0 - Закончить редактирование");
                        int comm = scanner.nextInt();
                        manager.updateTask(id,comm);
                        if (comm == 0){
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
                    System.out.println("Уверены, что хотите удалить все " + count + " задач?");
                    System.out.println("1 - да");
                    System.out.println("2 - нет");
                    int answer = scanner.nextInt();
                    if (answer == 1){
                        manager.removeAllTasks(tasks);
                        System.out.println("Все задачи удалены!");
                    } else{
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
                    break;
                case 8:
                    return;
            }
        }
    }

    private static void printMenu() {
        System.out.println("Выберите нужную команду:");
        System.out.println("1 - Создать задачу"); ///////
        System.out.println("2 - Обновить задачу"); //////
        System.out.println("3 - Вывести задачу по id"); ///////
        System.out.println("4 - Удалить все задачи");/////
        System.out.println("5 - Удалить задачу по id");////
        System.out.println("6 - Вывести все задачи"); ///////
        System.out.println("7 - Вывести подзадачи эпика");
        System.out.println("8 - Выход"); ///////
    }
}
