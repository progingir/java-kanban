public class Task {
    protected String heading;
    protected String description;
    protected Status status;
    protected int id;


    public Task(String heading, String description, int id) {
        this.heading = heading;
        this.description = description;
        this.id = id;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String printTask() {
        return "Задача номер " + id
                + ". Название: " + heading +
                ". Описание: " + description +
                ". Статус: " + status;
    }

    public int getId() {
        return this.id;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}