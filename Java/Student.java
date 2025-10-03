
/*
  @author: Kowshick Srinivasan
 * @version: 1.0
 * @Assignment: Hw2
 */

import java.lang.reflect.Field;
import java.util.Objects;

public class Student /*implements Comparable<Student>*/ {
    String school;
    String mJob;
    String fJob;
    String reason;
    String guardian;
    String famSize;

    //Can convert one letters to char instead of default string
    //for efficient memory usage
    char sex;
    char address;
    char pStatus;

    //Can convert Integers to int instead of default string
    //for efficient memory usage
    int age;
    int famRel;
    int freeTime;
    int goOut;
    int dalc;
    int walc;
    int health;
    int absence;
    int travelTime;
    int studyTime;
    int failures;
    int medu;
    int fedu;


    //Can convert yes/no to boolean instead of default string
    //for efficient memory usage
    boolean schoolsUp;
    boolean farmsUp;
    boolean paid;
    boolean activities;
    boolean nursery;
    boolean higher;
    boolean internet;
    boolean romantic;
    boolean passed;

    public Student() {
    }

    //Constructor to initialize the class variables
    public Student(String school, char sex, int age, char address,
                   String famSize, char pStatus, int medu, int fedu,
                   String mJob, String fJob, String reason,
                   String guardian, int travelTime, int studyTime,
                   int failures, boolean schoolsUp, boolean farmsUp,
                   boolean paid, boolean activities, boolean nursery,
                   boolean higher, boolean internet, boolean romantic,
                   int famRel, int freeTime, int goOut, int dalc, int walc,
                   int health, int absence, boolean passed) {
        this.school = school;
        this.sex = sex;
        this.age = age;
        this.address = address;
        this.famSize = famSize;
        this.pStatus = pStatus;
        this.medu = medu;
        this.fedu = fedu;
        this.mJob = mJob;
        this.fJob = fJob;
        this.reason = reason;
        this.guardian = guardian;
        this.travelTime = travelTime;
        this.studyTime = studyTime;
        this.failures = failures;
        this.schoolsUp = schoolsUp;
        this.farmsUp = farmsUp;
        this.paid = paid;
        this.activities = activities;
        this.nursery = nursery;
        this.higher = higher;
        this.internet = internet;
        this.romantic = romantic;
        this.famRel = famRel;
        this.freeTime = freeTime;
        this.goOut = goOut;
        this.dalc = dalc;
        this.walc = walc;
        this.health = health;
        this.absence = absence;
        this.passed = passed;
    }

    //To String method to convert student object to comma separated Strings
    @Override
    public String toString() {
        return school +
                "," + sex +
                "," + age +
                "," + address +
                "," + famSize +
                "," + pStatus +
                "," + medu +
                "," + fedu +
                "," + mJob +
                "," + fJob +
                "," + reason +
                "," + guardian +
                "," + travelTime +
                "," + studyTime +
                "," + failures +
                "," + convertBoolToString(schoolsUp) +
                "," + convertBoolToString(farmsUp) +
                "," + convertBoolToString(paid) +
                "," + convertBoolToString(activities) +
                "," + convertBoolToString(nursery) +
                "," + convertBoolToString(higher) +
                "," + convertBoolToString(internet) +
                "," + convertBoolToString(romantic) +
                "," + famRel +
                "," + freeTime +
                "," + goOut +
                "," + dalc +
                "," + walc +
                "," + health +
                "," + absence +
                "," + convertBoolToString(passed);
    }

    //Convert the boolean object back to user specified String
    private String convertBoolToString(boolean studentVariable) {
        return studentVariable ? "yes" : "no"; //If bool is True convert to yes else no
    }

    //Equals method compare two object of same type (deep comparison) to tell if they are both equal
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;                   //If both are the same object
        if (!(obj instanceof Student student)) return false;   // If your comparing with some other object tye
        //Convert the type of Object to Student
        return Objects.equals(school, student.school) &&
                Objects.equals(mJob, student.mJob) &&
                Objects.equals(fJob, student.fJob) &&
                Objects.equals(reason, student.reason) &&
                Objects.equals(guardian, student.guardian) &&
                Objects.equals(famSize, student.famSize) &&
                Objects.equals(sex, student.sex) &&
                Objects.equals(address, student.address) &&
                Objects.equals(pStatus, student.pStatus) &&
                Objects.equals(age, student.age) &&
                Objects.equals(famRel, student.famRel) &&
                Objects.equals(freeTime, student.freeTime) &&
                Objects.equals(goOut, student.goOut) &&
                Objects.equals(dalc, student.dalc) &&
                Objects.equals(walc, student.walc) &&
                Objects.equals(health, student.health) &&
                Objects.equals(absence, student.absence) &&
                Objects.equals(travelTime, student.travelTime) &&
                Objects.equals(studyTime, student.studyTime) &&
                Objects.equals(failures, student.failures) &&
                Objects.equals(medu, student.medu) &&
                Objects.equals(fedu, student.fedu) &&
                Objects.equals(schoolsUp, student.schoolsUp) &&
                Objects.equals(farmsUp, student.farmsUp) &&
                Objects.equals(paid, student.paid) &&
                Objects.equals(activities, student.activities) &&
                Objects.equals(nursery, student.nursery) &&
                Objects.equals(higher, student.higher) &&
                Objects.equals(internet, student.internet) &&
                Objects.equals(romantic, student.romantic) &&
                Objects.equals(passed, student.passed);

    }

    public int compare(Student student, Field matchedField) throws IllegalAccessException {
        throw new RuntimeException("Unsupported");
    }


    /**
     * @param columns - Required column
     * @return - A filtered student object that contain only the filter columns from student class
     * @throws IllegalAccessException - Column not found
     */
    public FilteredStudent filterColumns(Field[] columns) throws IllegalAccessException {
        Object[] values = new Object[columns.length];
        for (int i = 0; i < columns.length; i++) {
            columns[i].setAccessible(true);  //give access if variable is private
            values[i] = columns[i].get(this); //Get the data of the student variable
        }

        return new FilteredStudent(columns, values);  //map the columns and values to a filteredStudent object
    }
}

