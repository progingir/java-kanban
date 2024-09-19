public class EpicTask extends Task {
    public EpicTask(String heading, String description, int id) {
        super(heading, description, id);
    }

    @Override
    public String printTask() {
        return "Глобальная задача номер " + id
                + ". Название: " + heading +
                ". Описание: " + description +
                ". Статус: " + status;
    }
}
