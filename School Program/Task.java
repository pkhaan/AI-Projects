public class Task {
    Class clss;
    Lesson lsn;
    int numOfOccurence;

    public Task(Class clss, Lesson lsn, int numOfOccurence) {
        this.clss = clss;
        this.lsn = lsn;
        this.numOfOccurence = numOfOccurence;
    }

    public Class getClss() {
        return clss;
    }

    public Lesson getLsn() {
        return lsn;
    }

    public int getNumOfOccurence() {
        return numOfOccurence;
    }

    public boolean canBeAsngdToSlot(Slot slot) {
        // return true if teacher can teach lesson
        Teacher tchrOfSlot = slot.getTchr();
        return clss.getTeachersOfLesson().get(lsn).contains(tchrOfSlot);
    }

    @Override
    public String toString() {
        return "Task{" +
                "clss=" + clss +
                ", lsn=" + lsn +
                ", numOfOccurence=" + numOfOccurence +
                '}';
    }
}
