package tracker;

public class Task {
    protected String heading;
    protected String description;
    protected Status status;
    protected int id;

    public Task(String heading, String description, int id) {
        this.heading = heading;
        this.description = description;
        this.id = id;
        this.status = Status.NEW;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public String printTask() {
        return "Обычная задача с идентификатором  " + id + "\n" + "Название: " + heading + "\n" + "Описание: " + description + "\n" + "Статус: " + status + "\n";
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHeading() {
        return heading;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return getId() + "," + Type.TASK + "," + getHeading() + "," + getStatus() + "," + getDescription() + ",";
    }

    public static Task fromString(String value) {
        String[] parts = value.split(",");
        if (parts.length < 5) {
            throw new IllegalArgumentException("Неверный формат строки: " + value);
        }

        int id = Integer.parseInt(parts[0]);
        String heading = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];

        Task task = new Task(heading, description, id);
        task.setStatus(status);
        return task;
    }
}