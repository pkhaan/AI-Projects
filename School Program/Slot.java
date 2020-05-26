public class Slot {
    Teacher tchr;
    MeetingTime time;

    public Slot(Teacher tchr, MeetingTime time) {
        this.tchr = tchr;
        this.time = time;
    }

    public Teacher getTchr() {
        return tchr;
    }

    public MeetingTime getTime() {
        return time;
    }

}
