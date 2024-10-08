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

    public Status getStatus(){
        return status;
    }

    public String getDescription(){
        return description;
    }

    public String printTask() {
        return "Обычная задача с идентификатором  " + id + "\n" +
                "Название: " + heading + "\n" +
                "Описание: " + description + "\n" +
                "Статус: " + status + "\n";
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHeading(){
        return heading;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}