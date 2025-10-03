import java.io.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
/*
  @author: Kowshick Srinivasan
 * @version: 1.0
 * @Assignment: course project-1
 */

/**
 * Memory database class that does:
 * <p>
 * a) load the student-data.csv file into the memory database through recursive function.
 * <p>
 * b) Perform SQL select query, and order the result in ASC/DSC based on the selected sorting algorithm
 * <p>
 * c) export the memory database into a csv file through recursive function.
 */
public class MemoryDatabase {

    LinkedList list;  //Object of the linked list

    private static final String BASE_PATH = "D:\\Algorithms\\CourseProject-1\\Data"; // target directory

    private static final String FILENAME = "student-data.csv";  //input file name

    private static final String OUTFILE = "student-op-data.csv"; //output file name

    //Constructor based dependency injection
    public MemoryDatabase(LinkedList list) {
        this.list = list;

    }

    /**
     * Method to filter out only the required columns from the Linked List/in-memory database
     *
     * @param columns - Required columns
     * @return List of filtered students
     */
    private LinkedList.Node filterQuery(Field[] columns)
            throws IllegalAccessException {
        //List to store the filtered column objects
        //Since filterStudent is child of student, we can use polymorphism
        LinkedList filteredStudents = new LinkedList(); //Create a new LinkedList for filtered students
        LinkedList.Node current = null; //current node pointer
        while (list.head != null) {  //Loop until end of LinkedList
            //Can extend with here for where clause
            //Filter only yhe required columns and add it to the List
            current = filteredStudents.insert(current, list.head.student.filterColumns(columns), null);
            list.head = list.head.next; //Move to next element
        }
        return filteredStudents.head;
    }

    /**
     * Method to verify the correctness of user query against the SQL grammar and
     * retrieve the select query parameters
     *
     * @param userInput - User input select query
     * @return -  object of select query parameters
     */
    private SelectParameters retrieveParameters(String userInput) {
        //Select query grammar, the user input needs to satisfy this grammar to pass
        String sqlGrammar = "select\\s+(.*?)\\s+" +   // group 1 = columns list
                "from\\s+(\\w+)\\s+" +                // group 2 = table name
                "order\\s+by\\s+(\\w+)\\s+(ASC|DSC)\\s+" +  // group 3 = sort column, group 4 = order
                "with\\s+(bubble_sort|insertion_sort|merge_sort|quick_sort)\\.?"; // group 5 = sort algorithm
        //Compile the SQL grammar i.e. convert Regex string to Grammar
        Pattern pattern = Pattern.compile(sqlGrammar);
        //Match the User input against out grammar
        Matcher m = pattern.matcher(userInput);
        SelectParameters parameters;
        //If user input matches
        if (m.matches()) {
            //Retrieve the parameters from the user input
            parameters = new SelectParameters(Arrays.stream(m.group(1).split(",\\s?")).  //There can be multiple COMMA separated select columns
                    map(MemoryDatabase::convertStringToField).toArray(Field[]::new),   //Convert String to Object.Field
                    m.group(2),    //Retrieve Table name
                    convertStringToField(m.group(3)),  //Get and Convert the sort columns to Object.field
                    m.group(4),  //Get the sort method ASC/DSC
                    m.group(5)); //Get the sort algorithm to be used

        } else
            throw new IllegalArgumentException("Syntax not matching");  //If user-input not matched
        return parameters;
    }

    /**
     * Method to convert user input column string to java object field parameter,
     * it is promised the user string will be same as column name in student class (in memory database table),
     * unless invalid
     *
     * @param columnString - Raw string obtained from user-input
     * @return Object.field equivalent of the string
     */
    private static Field convertStringToField(String columnString) {
        Field matchedField = null;
        for (Field f : Student.class.getDeclaredFields())
            if (f.getName().equalsIgnoreCase(columnString)) {
                matchedField = f;
                break;
            }
        assert matchedField != null;
        return matchedField;
    }


    /**
     * Method used bubble sorting algorithm to sort the filtered student list
     *
     * @param head             - First element of filtered students object
     * @param comparatorColumn -column used to compare the list object
     * @param sortMethod       - To be sorting in Ascending or descending order
     */
    private LinkedList.Node bubbleSort(LinkedList.Node head, Field comparatorColumn, String sortMethod) throws IllegalAccessException {
        boolean flag; //Flag variable
        LinkedList.Node current;  //current node pointer
        LinkedList.Node sortedNode = null; //Before this node all the nodes are sorted

        do {  //uses exit condition loop
            flag = false;
            current = head;

            while (current.next != sortedNode) { //Keep track of the sorted elements
                if (Objects.equals(sortMethod, "ASC")) {  //If ascending order sort ascending order
                    if (current.student.compare(current.next.student, comparatorColumn) > 0) {
                        //Swap two elements
                        Student temp = current.student;
                        current.student = current.next.student;
                        current.next.student = temp;
                        flag = true;
                    }
                } else { //Else order sort descending order
                    if (current.student.compare(current.next.student, comparatorColumn) < 0) {
                        //Swap two elements
                        Student temp = current.student;
                        current.student = current.next.student;
                        current.next.student = temp;
                        flag = true;
                    }
                }

                current = current.next; //move pointer to the next
            }
            sortedNode = current;
            //last element is sorted
        } while (flag); //while every element is swapped, and list is in order

        return head;
    }

    /**
     * Method to use merge sort algorithm to sort the filtered student list
     *
     * @param head-            head of filtered students object
     * @param comparatorColumn - column used to compare the list object
     * @param sortMethod       -To be sorting in Ascending or descending order
     */
    private LinkedList.Node mergeSort(LinkedList.Node head, Field comparatorColumn, String sortMethod) throws IllegalAccessException {
        if (head == null || head.next == null) return head; //base case
        //Find the middle element
        LinkedList.Node middle = findMiddle(head);
        LinkedList.Node nextOfMiddle = middle.next;
        middle.next = null;

        //create left sub list from 0th element to middle-1 element
        LinkedList.Node left = mergeSort(head, comparatorColumn, sortMethod);
        //create left sub list from middle element to last element
        LinkedList.Node right = mergeSort(nextOfMiddle, comparatorColumn, sortMethod);        //recursively split the sub list until the base condition


        //merge the lists to sort the elements
        return merge(left, right, comparatorColumn, sortMethod);
    }

    /**
     * Method to find the middle node of the given Linked List
     *
     * @param head - begin node of the linked list
     * @return the middle node
     */
    private LinkedList.Node findMiddle(LinkedList.Node head) {
        if (head == null) return null;
        LinkedList.Node fast = head;
        LinkedList.Node slow = head;
        while (fast.next != null && fast.next.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        return slow;
    }


    /**
     * @param left             Left sub list
     * @param right            Right sub List
     * @param comparatorColumn column used to compare
     * @param sortMethod       sorting method Ascending/descending
     * @return sorted head
     */
    private LinkedList.Node merge(LinkedList.Node left, LinkedList.Node right,
                                  Field comparatorColumn, String sortMethod) throws IllegalAccessException {
        if (left == null) return right;
        if (right == null) return left;
        if (Objects.equals(sortMethod, "ASC")) { //If Ascending sort in ascending
            if (left.student.compare(right.student, comparatorColumn) < 0) { // left is smaller than the right node
                //put left to the list
                left.next = merge(left.next, right, comparatorColumn, sortMethod);
                return left;
            } else {   //left is greater or equal
                //put right to the list
                right.next = merge(left, right.next, comparatorColumn, sortMethod);
                return right;
            }
        } else {   //Else sort in Descending
            if (left.student.compare(right.student, comparatorColumn) > 0) { // left is greater than the right node
                //put left to the list
                left.next = merge(left.next, right, comparatorColumn, sortMethod);
                return left;
            } else { //left is smaller or equal
                //put right to the list
                right.next = merge(left, right.next, comparatorColumn, sortMethod);
                return right;
            }
        }
    }

    /**
     * Method recursively read each student details from the CSV file and adds it to the end of the linked list
     *
     * @param br      : The BufferedReader object
     * @param current : Current Node/pointer of the linked list
     * @return : Completion status of the read operation
     */
    private int read(BufferedReader br, LinkedList.Node current) throws IOException {
        String l = br.readLine();  //Read each line
        if (l == null) return 0;  //When reached EOF return
        String[] line = l.split(",");  //Split the row using "," as the separator
        Student student = convertLineToStudent(line);  //Convert the String array to the student class
        return read(br, list.insert(current, student, null));   //Recursively call the function with the buffered reader object and updated last Node/Pointer
    }


    /**
     * Accepts the String array and convert it to student object
     *
     * @param line Original String array formed after comma separation of the line
     * @return Student object
     */
    private Student convertLineToStudent(String[] line) {
        return new Student(line[0], line[1].charAt(0), Integer.parseInt(line[2]), line[3].charAt(0), line[4], line[5].charAt(0),
                Integer.parseInt(line[6]), Integer.parseInt(line[7]), line[8], line[9], line[10], line[11],
                Integer.parseInt(line[12]), Integer.parseInt(line[13]), Integer.parseInt(line[14]), line[15].equals("yes"), line[16].equals("yes"), line[17].equals("yes"), line[18].equals("yes"), line[19].equals("yes"),
                line[20].equals("yes"), line[21].equals("yes"), line[22].equals("yes"), Integer.parseInt(line[23]), Integer.parseInt(line[24]), Integer.parseInt(line[25]), Integer.parseInt(line[26]),
                Integer.parseInt(line[27]), Integer.parseInt(line[28]), Integer.parseInt(line[29]), line[30].equals("yes"));

    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        MemoryDatabase database = new MemoryDatabase(
                new LinkedList()  //Initialize the list object
        );
        File inputFile = new File(BASE_PATH, FILENAME);
        File outputFile = new File(BASE_PATH, OUTFILE);
        //Since we used try with resources, we won't be needing a finally block
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile)); //Buffered reader to enable to the Java methods to read from the given file
             PrintWriter printWriter = new PrintWriter(new FileWriter(outputFile))) //Buffered writer to enable the Java methods to write on the given file
        {
            br.readLine();  //Skip the Header of the csv
            database.read(br, database.list.head);   //Read the student details from the file and add it to the linked list
            //Get the select query from the user
            String userInput = scanner.nextLine();
            //Check an retrieve parameters
            SelectParameters parameters = database.retrieveParameters(userInput);
            //filter only required column
            LinkedList.Node filteredStudentList = database.filterQuery(parameters.columns());
            //Sort the filtered Object using the selected algorithm
            switch (parameters.sortAlgorithm()) {
                case "bubble_sort":
                    filteredStudentList = database.bubbleSort(filteredStudentList, parameters.sortColumn(), parameters.sortMethod());
                    break;
                case "insertion_sort":
                    //TODO: Insertion Sort
                    System.out.println("insertion Sort");
                    break;
                case "merge_sort":
                    filteredStudentList = database.mergeSort(filteredStudentList, parameters.sortColumn(), parameters.sortMethod());
                    break;
                default:
                    //TODO: quick Sort
                    break;

            }
            //Get the columns header in a comma separated string
            String head = Arrays.stream(parameters.columns())
                    .map(Field::getName).
                    collect(Collectors.joining(","));


            printWriter.println(head);  //print the header row of the csv
            //Print rest of the rows
            database.print(filteredStudentList, printWriter);

        } catch (Exception e) { //Global catch block to handle all the exception thrown by the program
            e.printStackTrace(System.out);  //print the exception stack trace in console
        }
    }

    /**
     * Recursively prints the student data to the csv file
     *
     * @param current     : Accepts the current NOde/pointer of the Linked List
     * @param printWriter : Accepts the Printed writer object
     * @return : return the completion status of the printing action
     */
    private int print(LinkedList.Node current, PrintWriter printWriter) {
        String row = current.student.toString(); //Converts the student object to comma separated string
        printWriter.println(row);  //Prints the string to the csv file
        if (current.next == null)  //When reached end node return completion status
            return 0;
        return print(current.next, printWriter); //recursively call the print method with updated last node pointer
    }

}

