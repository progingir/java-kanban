public class Subtask extends Task {
    int count = 0;

    public Subtask(String heading, String description, int id) {
        super(heading, description, id);
        count++;
    }

    @Override
    public String printTask() {
        return "** " + heading + " **" +
                " - " + description +
                ". Статус: " + status;
    }
}