public class Task {
    private String heading;
    private String description;
    private Status status;
    private int id;


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

    public String printTask(){
        return "Задача номер " + id
                + ". Название: " + heading +
                ". Описание: " + description;
    }

    public int getId(){
        return this.id;
    }


    public Status setStatus() {
        status = Status.NEW;
        return status;
    }
}