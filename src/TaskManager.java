import java.util.HashMap;
import java.util.Scanner;


public class TaskManager {
    HashMap<Integer, Task> tasks;
    Scanner scanner = new Scanner(System.in);
    Task task;
    int id = 0;

    public TaskManager(HashMap<Integer, Task> tasks) {
        this.tasks = tasks;
    }


    public void getAllTasks(){
        for (Integer key: tasks.keySet()){
            System.out.println(tasks.get(key).printTask());
        }
    }

    public HashMap<Integer, Task> removeAllTasks(HashMap<Integer, Task> tasks){
        tasks.clear();
        return tasks;
    }

    public String getTaskById(int id){
        Task task = tasks.get(id);
        if (task != null) { // Проверка на null
            return task.printTask();
        } else {
            return ("Задачи под этим индексом пока нет");
        }
    }

    public Task createTask(String heading, String description){
        Task task = new Task(heading, description, ++id);
        task.setStatus();
        tasks.put(id, task);
        return task;
    }

    public void updateTask(int id, int comm){
        if (comm == 1) {
            System.out.println("Введите новое название:");
            String newHeading = scanner.next();
            tasks.get(id).setHeading(newHeading);
            System.out.println("Новое название сохранено!");
        } else if (comm == 2) {
            System.out.println("Введите новое описание:");
            String newDescription = scanner.next();
            tasks.get(id).setDescription(newDescription);
            System.out.println("Новое описание сохранено!");
        }
    }

    public HashMap<Integer, Task> removeTaskById(int id){
        tasks.remove(id);
        return tasks;
    }

    public void getSubtasks(){

    }
}