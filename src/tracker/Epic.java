package tracker;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    protected List<Integer> subtasksIds = new ArrayList<>();
    protected static Instant endTime = Instant.ofEpochSecond(32503669200000L);

    public Epic(String title, String description, Integer id, Status status) {
        super(title, description, id, status);
        this.type = Type.EPIC;
    }

    public Epic(int id, String title, String description, Status status) {
        super(id, title, description, status);
        this.type = Type.EPIC;
    }

    public Epic(int id, String title, String description, long duration, Instant startTime, Status status) {
        super(id, title, description, duration, startTime, status);
        this.endTime = super.getEndTime();
        this.type = Type.EPIC;
    }

    public Epic(ArrayList<Integer> subtasksIds,
                int id, String title,
                String description, long duration,
                Instant startTime, Status status) {
        super(id, title, description, duration, startTime, status);
        this.subtasksIds = subtasksIds;
        this.endTime = super.getEndTime();
        this.type = Type.EPIC;
    }

    public Epic() {}

    public List<Integer> getSubtasksIds() {
        return subtasksIds;
    }

    public void setSubtasksIds(int id) {
        subtasksIds.add(id);
    }

    @Override
    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        Epic.endTime = endTime;
    }

    @Override
    public Type getType() {
        return Type.EPIC;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtasks ids=" + subtasksIds.size() +
                ", title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status='" + getStatus() + ", start time=" + getStartTime().toEpochMilli() +
                ", duration=" + getDuration() + ", end time=" + getEndTime().toEpochMilli() + '\'' + '}';
    }

    @Override
    public String toStringFromFile() {
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s", subtasksIds, getId(), getType(), getTitle(),
                getStatus(), getDescription(), getStartTime(), getDuration(), "");
    }

}
