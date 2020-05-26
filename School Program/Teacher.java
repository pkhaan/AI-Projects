import java.util.ArrayList;

class Teacher {
    private String teacherId;
    private String teacherName;
    private ArrayList<String> lessonIDs;
    private int maxHoursPerDay;
    private int maxHoursPerWeek;

    public Teacher(String teacherId, String teacherName, ArrayList<String> lessonIDs, int maxHoursPerDay, int maxHoursPerWeek) {
        this.teacherId = teacherId;
        this.teacherName = teacherName;
        this.lessonIDs = lessonIDs;
        this.maxHoursPerDay = maxHoursPerDay;
        this.maxHoursPerWeek = maxHoursPerWeek;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public ArrayList<String> getLessonIDs() {
        return lessonIDs;
    }

    public void setLessonIDs(ArrayList<String> lessonIDs) {
        this.lessonIDs = lessonIDs;
    }

    public int getMaxHoursPerDay() {
        return maxHoursPerDay;
    }

    public void setMaxHoursPerDay(int maxHoursPerDay) {
        this.maxHoursPerDay = maxHoursPerDay;
    }

    public int getMaxHoursPerWeek() {
        return maxHoursPerWeek;
    }

    public void setMaxHoursPerWeek(int maxHoursPerWeek) {
        this.maxHoursPerWeek = maxHoursPerWeek;
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "teacherId='" + teacherId + '\'' +
//                ", teacherName='" + teacherName + '\'' +
//                ", maxHoursPerDay=" + maxHoursPerDay +
//                ", maxHoursPerWeek=" + maxHoursPerWeek +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        return ((Teacher) obj).getTeacherId().equals(this.teacherId);
    }
}
