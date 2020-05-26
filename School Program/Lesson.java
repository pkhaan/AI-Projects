class Lesson {
    private String lessonId;
    private String lessonName;
    private String className;
    private int hoursPerWeek;

    public Lesson(String lessonId, String lessonName, String className, int hoursPerWeek) {
        this.lessonId = lessonId;
        this.lessonName = lessonName;
        this.className = className;
        this.hoursPerWeek = hoursPerWeek;
    }

    public String getLessonId() {
        return lessonId;
    }

    public void setLessonId(String lessonId) {
        this.lessonId = lessonId;
    }

    public String getLessonName() {
        return lessonName;
    }

    public void setLessonName(String lessonName) {
        this.lessonName = lessonName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getHoursPerWeek() {
        return hoursPerWeek;
    }

    public void setHoursPerWeek(int hoursPerWeek) {
        this.hoursPerWeek = hoursPerWeek;
    }

    public boolean equals(Lesson l) {
        return this.getLessonId().equals(l.getLessonId());
    }

    @Override
    public String toString() {
        return "Lesson{" +
                "lessonId='" + lessonId + '\'' +
                ", lessonName='" + lessonName + '\'' +
                ", className='" + className + '\'' +
                ", hoursPerWeek='" + hoursPerWeek + '\'' +
                '}';
    }
}
