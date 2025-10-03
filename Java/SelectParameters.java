/*
  @author: Kowshick Srinivasan
 * @version: 1.0
 * @Assignment: course project-1
 */


import java.lang.reflect.Field;

//Record to store the SQL select parameters
public record SelectParameters(Field[] columns, String table, Field sortColumn, String sortMethod, String sortAlgorithm) {
}
