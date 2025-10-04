
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

    /**
     * Method that compares this filterStudent object to the given studentObject using the comparision column
     *
     * @param matchedField              - Sort based on this column
     * @param student - to be compared class
     * @return integer specifying comparision result
     */
    public int compare(Student student, Field matchedField) throws IllegalAccessException {
        Object v1 = matchedField.get(this);
        Object v2 = matchedField.get(student);

        if (v1 instanceof Comparable && v2 instanceof Comparable) { //All Non-primitive datatypes are instance of Comparable interface
            return ((Comparable) v1).compareTo(v2);  //Compare the two column values
        } else {
            throw new IllegalArgumentException("Cannot not comparable this column");  // if not instance of comparable
        }
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

