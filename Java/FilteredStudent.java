/*
  @author: Kowshick Srinivasan
 * @version: 1.0
 * @Assignment: course project-1
 */


import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Reflection/subclass of the student class
 */
public class FilteredStudent extends Student{  //Inherit from parent class.
    private final Field[] filteredColumns;   //List of columns that needs to be filtered
    //The values of each column,
    // since we don't know the exact datatype we use the parent type Object.
    private final Object[] values;

    //Constructor parameter to inject list of filed and values
    public FilteredStudent(Field[] filteredColumns, Object[] values) {
        super();
        this.filteredColumns = filteredColumns;
        this.values = values;
    }

    /**
     * Converts the filterStudent object to a string
     *
     * @return Comma separated String value
     */
    @Override
    public String toString() {
        return Arrays.stream(values).map(e -> {
            if (e instanceof Boolean b)  //If element is of type Boolean, convert to "yes", "no"
                return b ? "yes" : "no";
            return String.valueOf(e);  //get the element as string
        }).collect(Collectors.joining(",")); //separate them using ","
    }
}
