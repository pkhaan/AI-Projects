import java.util.ArrayList;
import java.util.HashMap;

public class Class {
    private String className;
    private ArrayList<Lesson> lessonsOfClass;
    private HashMap<Lesson, Integer> hoursPerLesson;
    private HashMap<Lesson, ArrayList<Teacher>> teachersOfLesson;

    public Class(String className) {
        this.className = className;
        lessonsOfClass = new ArrayList<>();
        hoursPerLesson = new HashMap<>();
        teachersOfLesson = new HashMap<>();
        for (Lesson lsn : Main.lessons) {
            if (className.startsWith(lsn.getClassName())) {
                lessonsOfClass.add(lsn);
                hoursPerLesson.put(lsn, lsn.getHoursPerWeek());
                ArrayList<Teacher> tchrList = new ArrayList<>();
                for (Teacher tchr : Main.teachers) {
                    if (tchr.getLessonIDs().contains(lsn.getLessonId())) {
                        tchrList.add(tchr);
                    }
                }
                teachersOfLesson.put(lsn, tchrList);
            }
        }
    }

    public String getClassName() {
        return className;
    }

    public ArrayList<Lesson> getLessonsOfClass() {
        return lessonsOfClass;
    }

    public HashMap<Lesson, Integer> getHoursPerLesson() {
        return hoursPerLesson;
    }

    public HashMap<Lesson, ArrayList<Teacher>> getTeachersOfLesson() {
        return teachersOfLesson;
    }

    @Override
    public boolean equals(Object obj) {
        return ((Class) obj).getClassName().equals(this.className);
    }

    @Override
    public String toString() {
        return "Class{" +
                "className='" + className + '\'' +
                '}';
    }
}
