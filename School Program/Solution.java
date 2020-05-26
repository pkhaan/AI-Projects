import java.util.*;

class Solution {

    // function value of instance of solution
    public int cost;

    public HashMap<Task, Slot> map; // Configuration map that stores the solution's schedule

    public HashMap<Task, Integer> tasksInConflict; // Tasks participating to at least 1 conflict

    public Solution(HashMap<Task, Slot> map) {
        this.cost = cost(map);
        this.map = map;
    }

    // Calculates cost of schedule
    public int cost(HashMap<Task, Slot> map) {
        // Given specific map configuration, return cost
        int c = 0;
        tasksInConflict = new HashMap<>();
        for (Task task : Main.tasks) {
            tasksInConflict.put(task, 0);
        }
        int hardCost = 10;
        int softCost = 1;

        // C1 (hard): calculate simultaneous classes & teachers teaching more than one task at a time
        ArrayList<Task> closedSet = new ArrayList<>();
        for (Task task1 : Main.tasks) {
            Slot slotOfTask1 = map.get(task1);
            if (slotOfTask1 == null) continue;
            closedSet.add(task1);
            for (Task task2 : Main.tasks) {
                if (closedSet.contains(task2)) continue;
                Slot slotOfTask2 = map.get(task2);
                if (task1.getClss() == task2.getClss() && slotOfTask1.getTime() == slotOfTask2.getTime()) {
                    c += hardCost;
                    tasksInConflict.put(task1, tasksInConflict.get(task1) + 1);
                    tasksInConflict.put(task2, tasksInConflict.get(task2) + 1);
                }
                if (slotOfTask1.getTchr() == slotOfTask2.getTchr() && slotOfTask1.getTime() == slotOfTask2.getTime() && task1.getClss() != task2.getClss()) {
                    c += hardCost;
                    tasksInConflict.put(task1, tasksInConflict.get(task1) + 1);
                    tasksInConflict.put(task2, tasksInConflict.get(task2) + 1);
                }
            }
        }
        // C2 (hard): calculate teachers working more hours than they are supposed to
        HashMap<Teacher, HashMap<String, Integer>> workHoursOfTeacherPerDay = new HashMap<>();
        for (Teacher t : Main.teachers) {
            HashMap<String, Integer> daysHoursMap = new HashMap<>();
            for (String day : Main.weekDays) {
                daysHoursMap.put(day, 0);
            }
            workHoursOfTeacherPerDay.put(t, daysHoursMap);
        }
        for (Task task : Main.tasks) {
            Slot slotOfTask = map.get(task);
            if (slotOfTask == null) continue;
            Teacher teacher = slotOfTask.getTchr();
            String day = slotOfTask.getTime().getDay();
            workHoursOfTeacherPerDay.get(teacher).put(day, workHoursOfTeacherPerDay.get(teacher).get(day) + 1);
            if (workHoursOfTeacherPerDay.get(teacher).get(day) > teacher.getMaxHoursPerDay()) {
                c += hardCost;
                tasksInConflict.put(task, tasksInConflict.get(task) + 1);
            }
        }
        for (Teacher teacher : Main.teachers) {
            int weeklyWorkHours = 0;
            for (String day : Main.weekDays) {
                int dailyWorkHours = workHoursOfTeacherPerDay.get(teacher).get(day);
                weeklyWorkHours += dailyWorkHours;
            }
            if (weeklyWorkHours > teacher.getMaxHoursPerWeek()) {
                c += hardCost;
            }
        }

        // Create new array representation for the sake of calculations of soft constraints

        boolean[][][][][] tempArray = new boolean[Main.classes.size()][Main.lessons.size()][Main.weekDays.length][Main.weekHours][Main.teachers.size()];
        for (Task task : Main.tasks) {
            int curCls = Main.classes.indexOf(task.getClss());
            int curDay = Arrays.asList(Main.weekDays).indexOf(map.get(task).getTime().getDay());
            int curHr = map.get(task).getTime().getHour() - 1;
            int curTchr = Main.teachers.indexOf(map.get(task).getTchr());
            int curLsn = Main.lessons.indexOf(task.getLsn());
            tempArray[curCls][curLsn][curDay][curHr][curTchr] = true;
        }

        // C3 (soft): calculate class gap hours
        for (int clss = 0; clss < Main.classes.size(); clss++) {
            for (int day = 0; day < 5; day++) {
                // determine whether gaps exist
                boolean gapExists = false;
                int lastLsnHr = -1;
                for (int hr = 0; hr < 7; hr++) {
                    if (gapExists) break;
                    for (int lsn = 0; lsn < Main.lessons.size(); lsn++) {
                        if (gapExists) break;
                        for (int tchr = 0; tchr < Main.teachers.size(); tchr++) {
                            if (tempArray[clss][lsn][day][hr][tchr]) {
                                if (lastLsnHr == -1) {
                                    lastLsnHr = hr;
                                } else {
                                    if (hr - lastLsnHr > 1) {
                                        gapExists = true;
                                        c += softCost;
                                        break;
                                    }
                                    lastLsnHr = hr;
                                }
                            }
                        }
                    }
                }
            }
        }

        // C4 (soft): calculate teacher gap hours
        for (int tchr = 0; tchr < Main.teachers.size(); tchr++) {
            for (int day = 0; day < 5; day++) {
                int maxConsecHrs = 0;
                int currentConsecHrs = 0;
                for (int hr = 0; hr < 7; hr++) {
                    boolean teachesThisHr = false;
                    for (int clss = 0; clss < Main.classes.size(); clss++) {
                        if (teachesThisHr) break;
                        for (int lsn = 0; lsn < Main.lessons.size(); lsn++) {
                            if (tempArray[clss][lsn][day][hr][tchr]) {
                                teachesThisHr = true;
                                break;
                            }
                        }
                    }
                    if (teachesThisHr) {
                        currentConsecHrs++;
                        if (currentConsecHrs > maxConsecHrs) {
                            maxConsecHrs = currentConsecHrs;
                        }
                    } else {
                        currentConsecHrs = 0;
                    }
                }
                if (maxConsecHrs > 2) {
                    c += softCost;
                }
            }
        }

        // C5 (soft): calculate unevenness in class hours
        for (int clss = 0; clss < Main.classes.size(); clss++) {
            int currentLastHour;
            int dayLastHour;
            for (int day = 0; day < 5; day++) {
                dayLastHour = -1;
                for (int hour = 0; hour < 7; hour++) {
                    for (int lsn = 0; lsn < Main.lessons.size(); lsn++) {
                        for (int tchr = 0; tchr < Main.teachers.size(); tchr++) {
                            if (tempArray[clss][lsn][day][hour][tchr]) {
                                dayLastHour = hour;
                            }
                        }
                    }
                }
                for (int d = 0; d < 5; d++) {
                    if (d == day) {
                        continue;
                    }
                    currentLastHour = -1;
                    for (int hr = 0; hr < 7; hr++) {
                        for (int ls = 0; ls < Main.lessons.size(); ls++) {
                            for (int tr = 0; tr < Main.teachers.size(); tr++) {
                                if (tempArray[clss][ls][d][hr][tr]) {
                                    currentLastHour = hr;
                                }
                            }
                        }
                    }
                    if (Math.abs(dayLastHour - currentLastHour) > 1) {
                        c += softCost;

                    }
                }
            }
        }

        // C6 (soft): calculate unevenness in lesson hours
        for (int clss = 0; clss < Main.classes.size(); clss++) {
            for (int lsn = 0; lsn < Main.lessons.size(); lsn++) {
                int[] lessonCount = new int[5];
                for (int day = 0; day < 5; day++) {
                    for (int hr = 0; hr < 7; hr++) {
                        for (int tchr = 0; tchr < Main.teachers.size(); tchr++) {
                            if (tempArray[clss][lsn][day][hr][tchr]) {
                                lessonCount[day] += 1;
                            }
                        }
                    }
                }
                for (int day = 0; day < 5; day++) {
                    if (lessonCount[day] == 0) continue;
                    for (int d = 0; d < 5; d++) {
                        if (d == day) continue;
                        if (Math.abs(lessonCount[day] - lessonCount[d]) > 2) {
                            c += softCost;
                        }
                    }
                }
            }
        }

        // C7 (soft): calculate unevenness in teacher hours
        ArrayList<Teacher> closedSetT = new ArrayList<>();
        for (int tchr = 0; tchr < Main.teachers.size(); tchr++) {
            int numTchrHours = 0;
            for (int clss = 0; clss < Main.classes.size(); clss++) {
                for (int lsn = 0; lsn < Main.lessons.size(); lsn++) {
                    for (int day = 0; day < 5; day++) {
                        for (int hour = 0; hour < Main.weekHours; hour++) {
                            if (tempArray[clss][lsn][day][hour][tchr]) {
                                numTchrHours++;
                            }
                        }
                    }
                }
            }
            for (int tchr2 = 0; tchr2 < Main.teachers.size(); tchr2++) {
                if (tchr2<=tchr) continue;
                int numTchrHours2 = 0;
                for (int clss2 = 0; clss2 < Main.classes.size(); clss2++) {
                    for (int lsn2 = 0; lsn2 < Main.lessons.size(); lsn2++) {
                        for (int day2 = 0; day2 < 5; day2++) {
                            for (int hour2 = 0; hour2 < Main.weekHours; hour2++) {
                                if (tempArray[clss2][lsn2][day2][hour2][tchr2]) {
                                    numTchrHours2++;
                                }
                            }
                        }
                    }
                }
                if (Math.abs(numTchrHours - numTchrHours2) > 5) {
                    c += softCost;
                }
            }
        }
        return c;
    }
}