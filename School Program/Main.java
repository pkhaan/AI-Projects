import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Main {
    public static ArrayList<Teacher> teachers;
    public static ArrayList<Lesson> lessons;
    public static ArrayList<Class> classes;
    public static String[] weekDays={"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
    public static int weekHours=7;
    public static ArrayList<Task> tasks;
    public static ArrayList<Slot> slots;


    public static void main(String[] args) {
        double start = System.currentTimeMillis();
        // Initialization phase
        teachers = initTeachers("input/teachers.csv"); // read teacher data
        lessons = initLessons("input/lessons.csv"); // read lesson data
        classes = initClasses(new String[]{"A1", "A2", "A3", "B1", "B2", "B3", "C1", "C2", "C3"});
        initTasks();
        initSlots();
        // Run simulated annealing with above data
        SimulatedAnnealing.saAlgorithm();
        double finish = System.currentTimeMillis();
        System.out.println("Time of execution: "+(finish - start) / 1000 + " sec");
    }


    private static ArrayList<Teacher> initTeachers(String fileName) {
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";
        ArrayList<Teacher> teachers = new ArrayList<Teacher>();

        try {
            br = new BufferedReader(new FileReader(fileName));
            br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] teacher = line.split(cvsSplitBy);
                ArrayList<String> lessonCodes = new ArrayList<>(Arrays.asList(teacher[2].split(" ")));
                teachers.add(new Teacher(teacher[0], teacher[1], lessonCodes, Integer.parseInt(teacher[3]), Integer.parseInt(teacher[4])));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return teachers;
    }

    private static ArrayList<Lesson> initLessons(String fileName) {
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";
        ArrayList<Lesson> lessons = new ArrayList<Lesson>();

        try {
            br = new BufferedReader(new FileReader(fileName));
            br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] lesson = line.split(cvsSplitBy);
                lessons.add(new Lesson(lesson[0], lesson[1], lesson[2], Integer.parseInt(lesson[3])));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return lessons;
    }

    private static ArrayList<Class> initClasses(String[] classNames) {
        classes = new ArrayList<>();
        for (String clssName : classNames) {
            classes.add(new Class(clssName));
        }
        return classes;
    }

    private static void initTasks() {
        tasks = new ArrayList<>();
        for (Class clss : Main.classes) {
            for (Lesson lsn : clss.getLessonsOfClass()) {
                for (int i = 0; i < clss.getHoursPerLesson().get(lsn); i++) {
                    tasks.add(new Task(clss, lsn, i + 1));
                }
            }
        }
    }

    private static void initSlots() {
        slots = new ArrayList<>();
        for (Teacher tchr : teachers) {
            for (String d : weekDays) {
                for (int i = 0; i < weekHours; i++) {
                    slots.add(new Slot(tchr, new MeetingTime(d, i + 1)));
                }
            }
        }
    }

//    public static void writeTimetable(Chromosome x) {
//        char delim = ';';
//        Schedule sch = x.getGenes();
//        try (PrintWriter writer = new PrintWriter(new File("out/test.csv"))) {
//            for (Class clss : Main.classes) {
//                writer.append("CLASS " + clss.getClassName() + " TIMETABLE:");
//                writer.append("\n");
//                writer.append("Monday" + delim + "Tuesday" + delim + "Wednesday" + delim + "Thursday" + delim + "Friday");
//                writer.append("\n");
//                for (int hr = 0; hr < 7; hr++) {
//                    for (String day : weekDays) {
//                        for (Session ev : sch.getSessionsOfClass().get(clss)) {
//                            if (ev.getTime().getDay().equals(day) && ev.getTime().getHour() == hr + 1) {
//                                writer.append(ev.getLesson().getLessonId() + "-" + ev.getTeacher().getTeacherId() + " ");
//                            }
//                        }
//                        writer.append(delim);
//                    }
//                    writer.append("\n");
//                }
//                writer.append("\n");
//            }
//            writer.close();
//        } catch (FileNotFoundException e) {
//            System.out.println(e.getMessage());
//        }
//    }


    public static void writeTimetable(HashMap<Task, Slot> sch) {
        char delim = ';';

        try (PrintWriter writer = new PrintWriter(new File("out/schedule.csv"))) {
            for (Class clss : Main.classes) {
                writer.append("CLASS " + clss.getClassName() + " TIMETABLE:");
                writer.append("\n");
                writer.append("Monday" + delim + "Tuesday" + delim + "Wednesday" + delim + "Thursday" + delim + "Friday");
                writer.append("\n");
                for (int hr = 0; hr < 7; hr++) {
                    for (String day : weekDays) {
                        for (Task task : Main.tasks) {
                            Slot slot = sch.get(task);
                            if (task.getClss() == clss && slot.getTime().getDay() == day && slot.getTime().getHour() == hr + 1) {
                                writer.append(task.getLsn().getLessonId() + "." + task.numOfOccurence + "-" + slot.getTchr().getTeacherId() + " ");
                            }
                        }
                        writer.append(delim);
                    }
                    writer.append("\n");
                }
                writer.append("\n");
            }
            writer.close();
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
}
