package tracker;

import java.util.List;

public interface HistoryManager { // отдельный интерфейс для хранения необходимых методов

    Task add(Task task); // добавляет Задачу в лист просмотренных задач

    void remove(int id); // удаляет Задачу по идентификатору

    List<Task> getHistory();

}