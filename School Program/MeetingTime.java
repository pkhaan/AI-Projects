import java.util.Random;

public class MeetingTime {
    private String day;
    private int hour;

    public MeetingTime() {
        Random r = new Random();
        this.day = Main.weekDays[r.nextInt(Main.weekDays.length)];
        this.hour = r.nextInt(Main.weekHours) + 1;
    }

    public MeetingTime(String day, int hour) {
        this.day = day;
        this.hour = hour;
    }

    public String getDay() {
        return day;
    }

    public int getHour() {
        return hour;
    }

    @Override
    public String toString() {
        return "MeetingTime{" +
                "day='" + day + '\'' +
                ", hour=" + hour +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        return ((MeetingTime) obj).getDay().equals(this.day) && ((MeetingTime) obj).getHour() == this.hour;
    }
}
