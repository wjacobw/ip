package duke;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


/**
 * Represents a list of tasks.
 */
public class TaskList {
    String task;
    private static List<Task> list = new ArrayList<Task>();

    public List<Task> getList() {
        return this.list;
    }

    /**
     * Trims leading and trailing spaces from a string.
     *
     * @param input The input string to be trimmed.
     * @return The trimmed string or null if input is null.
     */
    public static String trimString(String input) {
        if (input == null) {
            return null; // Handle null input if needed
        }
        return input.trim();
    }

    /**
     * Converts a string representation of an event into an Event object and adds it to the list.
     *
     * @param arr The array containing the parts of the event string.
     */
    private void stringDeadline(String[] arr) {
        String firstBy = arr[1].substring(arr[1].indexOf("/by") + 4);
        String secondBy = firstBy.substring(0, firstBy.indexOf("|"));
        String done = firstBy.substring(firstBy.indexOf("|") + 1);
        String byDate = secondBy.substring(0, secondBy.indexOf("/"));
        String firstByMonth = secondBy.substring(secondBy.indexOf("/") + 1);
        String byMonth = firstByMonth.substring(0, firstByMonth.indexOf("/"));
        String byYear = firstByMonth.substring(firstByMonth.indexOf("/") + 1);
        byYear = byYear.substring(0, byYear.indexOf(" "));
        LocalDate by = LocalDate.of(Integer.parseInt(byYear), Integer.parseInt(byMonth), Integer.parseInt(byDate));
        Deadline deadline = new Deadline(arr[1].substring(0, arr[1].indexOf("/by ")), by);
        if (done.equals("1")) {
            deadline.finish();
            list.add(deadline);
        } else {
            list.add(deadline);
        }
    }

    /**
     * Converts a string representation of an event into an Event object and adds it to the list.
     *
     * @param arr The array containing the parts of the event string.
     */
    private void stringEvent(String[] arr) {
        String firstFrom = arr[1].substring(arr[1].indexOf("/from") + 6); //
        String secondFrom = firstFrom.substring(0, firstFrom.indexOf("/to"));
        String firstTo = arr[1].substring(arr[1].indexOf("/to")+ 4);
        String secondTo = firstTo.substring(0, firstTo.indexOf("|"));
        String content = arr[1].substring(0, arr[1].indexOf("/from "));
        String done = firstTo.substring(firstTo.indexOf("|") + 1);
        String fromDate = secondFrom.substring(0, secondFrom.indexOf("/"));
        String firstFromMonth = secondFrom.substring(secondFrom.indexOf("/") + 1);
        String fromMonth = firstFromMonth.substring(0, firstFromMonth.indexOf("/"));
        String fromYear = firstFromMonth.substring(firstFromMonth.indexOf("/") + 1);

        String toDate = secondTo.substring(0, secondTo.indexOf("/"));
        String firstToMonth = secondTo.substring(secondTo.indexOf("/") + 1);
        String toMonth = firstToMonth.substring(0, firstToMonth.indexOf("/"));
        String toYear = firstToMonth.substring(firstToMonth.indexOf("/") + 1);
        toYear = toYear.substring(0, toYear.indexOf(" "));
        LocalDate to = LocalDate.of(Integer.parseInt(trimString(toYear)), Integer.parseInt(toMonth), Integer.parseInt(toDate));
        LocalDate from = LocalDate.of(Integer.parseInt(trimString(fromYear)), Integer.parseInt(fromMonth), Integer.parseInt(fromDate));

        Event event = new Event(content, from, to);
        if (done.equals("1")) {
            event.finish();
        }
        list.add(event);


    }

    /**
     * Converts a string representation of a task into a Task object and adds it to the list.
     *
     * @param str The string representation of the task.
     */
    private void StringToArray(String str) {
        // might need to add corrupted file exception
        String arr[] = str.split(" ", 2);
        if (arr[0].equals("event")) {
            this.stringEvent(arr);

        }
        else if (arr[0].equals("deadline")) {
            stringDeadline(arr);
        }
        else if (arr[0].equals("todo")){
            try {
                String firstTodo = arr[1];
                String secondToDo = firstTodo.substring(0, firstTodo.indexOf("|"));
                String done = firstTodo.substring(firstTodo.indexOf("|") + 1);
                Todo todo = new Todo(secondToDo);
                if (done.equals("1")) {
                    todo.finish();
                    list.add(todo);
                } else {
                    list.add(todo);
                }
            } catch (ArrayIndexOutOfBoundsException e){
                Ui specify = new Ui();
                specify.specify();
            }
        }
    }

    /**
     * Handles the "bye" command and displays a goodbye message.
     *
     * @param ui The UI object for displaying messages.
     * @return A string representation of the goodbye message.
     */
    public String stringBye(Ui ui) {
        Ui bye = new Ui();
        bye.bye();
        assert bye.toString() != null : "ui.toString() cannot be null";
        return bye.toString();
    }

    /**
     * Handles the "list" command and displays the list of tasks.
     *
     * @param ui The UI object for displaying messages.
     * @param onetwo A string indicating whether there is one or more than one task.
     * @return A string representation of the task list.
     */
    public String stringList(Ui ui, String onetwo) {
        ui.currentlist((list.size()), onetwo);

        assert ui.toString() != null : "ui.toString() cannot be null";
        //System.out.println("start" + List() + "stop");
        return List() + ui.toString();

    }

    /**
     * Handles the "delete" command and deletes a task from the list.
     *
     * @param parser The parser object for parsing user input.
     * @param ui The UI object for displaying messages.
     * @param onetwo A string indicating whether there is one or more than one task.
     * @return A string representation of the updated task list.
     */
    public String stringDelete(Parser parser, Ui ui, String onetwo){
        try {
            String[] arr = parser.getArr();
            list.get(Integer.parseInt(arr[1]) - 1).getDescription();
            ui.remove(list.get(Integer.parseInt(arr[1]) - 1).toString());
            list.remove(Integer.parseInt(arr[1]) - 1);
            ui.currentlist((list.size()), onetwo);
            refreshData();
            assert ui.toString() != null : "ui.toString() cannot be null";
            return ui.toString();
        }
        catch(NumberFormatException e) {
            return ui.toString();

        }
        catch(IndexOutOfBoundsException e) {
            return ui.toString();

        }
    }

    /**
     * Handles the "mark" command and marks a task as done.
     *
     * @param parser The parser object for parsing user input.
     * @param onetwo A string indicating whether there is one or more than one task.
     * @param ui The UI object for displaying messages.
     * @return A string representation of the updated task list.
     */
    public String stringMark(Parser parser, String onetwo, Ui ui) {
        try {
            String[] arr = parser.getArr();
            arr = parser.getArr();
            ui.mark(list.size(), onetwo, list.get(Integer.parseInt(arr[1]) - 1).getDescription());
            list.get(Integer.parseInt(arr[1]) - 1).getDescription();
            list.get(Integer.parseInt(arr[1]) - 1).finish();
            refreshData();
            assert ui.toString() != null : "ui.toString() cannot be null";
            return ui.toString();


        }
        catch(NumberFormatException e) {
            return ui.toString();

        }
        catch(IndexOutOfBoundsException e) {
            return ui.toString();

        }
    }

    /**
     * Handles the "find" command and searches for tasks containing a specific substring.
     *
     * @param parser The parser object for parsing user input.
     * @param ui The UI object for displaying messages.
     * @return A string representation of the matching tasks.
     */
    public String stringFind(Parser parser,  Ui ui) {
        String subString = parser.getArr()[1];
        List<Task> matchingList = new ArrayList<>();
        for (Task task: list) {
            if (task.getDescription().contains(subString)) {
                matchingList.add(task);
            }
        }
        ui.matchingList(matchingList);
        assert ui.toString() != null : "ui.toString() cannot be null";
        return ui.toString();
    }

    /**
     * Handles the "unmark" command and unmarks a previously marked task.
     *
     * @param parser The parser object for parsing user input.
     * @param onetwo A string indicating whether there is one or more than one task.
     * @param ui The UI object for displaying messages.
     * @return A string representation of the updated task list.
     */
    public String stringUnmark(Parser parser, String onetwo, Ui ui) {
        try {
            String[] arr = parser.getArr();
            list.get(Integer.parseInt(arr[1]) - 1).getDescription();
            ui.unmark(list.size(), onetwo, list.get(Integer.parseInt(arr[1]) - 1).getDescription());
            list.get(Integer.parseInt(arr[1]) - 1).unfinish();
            refreshData();
            assert ui.toString() != null : "ui.toString() cannot be null";
            return ui.toString();
        }
        catch(NumberFormatException e) {
            ui.numExc();
            return ui.toString();
        }
        catch(IndexOutOfBoundsException e) {
            ui.indexOut();
            return ui.toString();
        }
    }

    /**
     * Handles the "event" command and adds an event task to the list.
     *
     * @param parser The parser object for parsing user input.
     * @param onetwo A string indicating whether there is one or more than one task.
     * @param ui The UI object for displaying messages.
     * @param zenithData The path to the data file.
     * @return A string representation of the updated task list.
     * @throws Exception If an error occurs during execution.
     */

    public String stringEvent(Parser parser, String onetwo, Ui ui, String zenithData) throws Exception{
        String[] arr = parser.getArr();
        String firstFrom = arr[1].substring(arr[1].indexOf("/from") + 6);
        String secondFrom = firstFrom.substring(0, firstFrom.indexOf("/to"));
        String to = arr[1].substring(arr[1].indexOf("/to")+ 4);
        String content = arr[1].substring(0, arr[1].indexOf("/from "));
        String toDate = to.substring(0, to.indexOf("/"));
        String firstToMonth = to.substring(to.indexOf("/") + 1);
        String toMonth = firstToMonth.substring(0, firstToMonth.indexOf("/"));
        String toYear = firstToMonth.substring(firstToMonth.indexOf("/") + 1);
        LocalDate secondTo = LocalDate.of(Integer.parseInt(toYear), Integer.parseInt(toMonth), Integer.parseInt(toDate));
        String fromDate = secondFrom.substring(0, secondFrom.indexOf("/"));
        String firstFromMonth = secondFrom.substring(secondFrom.indexOf("/") + 1);
        String fromMonth = firstFromMonth.substring(0, firstFromMonth.indexOf("/"));
        String fromYear = firstFromMonth.substring(firstFromMonth.indexOf("/") + 1);
        fromYear = fromYear.substring(0, fromYear.indexOf(" "));
        LocalDate from = LocalDate.of(Integer.parseInt(fromYear), Integer.parseInt(fromMonth), Integer.parseInt(fromDate));
        Event event = new Event(content, from, secondTo);
        list.add(event);
        ui.add(event, list.size(), onetwo);
        appendToFile(zenithData, parser.getStr());
        refreshData();
        assert ui.toString() != null : "ui.toString() cannot be null";
        return ui.toString();
    }

    /**
     * Handles the "deadline" command and adds a deadline task to the list.
     *
     * @param parser The parser object for parsing user input.
     * @param ui The UI object for displaying messages.
     * @param onetwo A string indicating whether there is one or more than one task.
     * @return A string representation of the updated task list.
     */
    public String stringDeadline(Parser parser,Ui ui, String onetwo) {
        String[] arr = parser.getArr();
        String by = arr[1].substring(arr[1].indexOf("/by") + 4);
        String byDate = by.substring(0, by.indexOf("/"));
        String firstByMonth = by.substring(by.indexOf("/") + 1);
        String byMonth = firstByMonth.substring(0, firstByMonth.indexOf("/"));
        String byYear = firstByMonth.substring(firstByMonth.indexOf("/") + 1);
        LocalDate secondBy = LocalDate.of(Integer.parseInt(byYear), Integer.parseInt(byMonth), Integer.parseInt(byDate));
        Deadline deadline = new Deadline(arr[1].substring(0, arr[1].indexOf("/by ")), secondBy);
        list.add(deadline);
        ui.add(deadline, list.size(), onetwo);
        refreshData();
        assert ui.toString() != null : "ui.toString() cannot be null";
        return ui.toString();
    }

    /**
     * Handles the "todo" command and adds a todo task to the list.
     *
     * @param parser The parser object for parsing user input.
     * @param ui The UI object for displaying messages.
     * @param onetwo A string indicating whether there is one or more than one task.
     * @param zenithData The path to the data file.
     * @return A string representation of the updated task list.
     * @throws Exception If an error occurs during execution.
     */
    public String stringToDo(Parser parser,Ui ui, String onetwo, String zenithData) throws Exception{
        try {
            String[] arr = parser.getArr();
            Todo todo = new Todo(arr[1]);
            list.add(todo);
            ui.add(todo, list.size(), onetwo);
            appendToFile(zenithData, parser.getStr());
            refreshData();
            assert ui.toString() != null : "ui.toString() cannot be null";
            return ui.toString();

        } catch (ArrayIndexOutOfBoundsException e){
            ui.specify();
            return ui.toString();
        }
    }

    /**
     * Handles the case where the user input is empty and returns a message.
     *
     * @param parser The parser object for parsing user input.
     * @param ui The UI object for displaying messages.
     * @return A string representation of a message indicating empty input.
     */
    public String stringEmpty(Parser parser, Ui ui) {
        try {
            String[] arr = parser.getArr();
            System.out.println(arr[1]);
        } catch(ArrayIndexOutOfBoundsException e) {
            ui.blank();
            assert ui.toString() != null : "ui.toString() cannot be null";
            return ui.toString();
        }
        return "";
    }

    /**
     * Handles user input and performs various actions based on the input.
     *
     * @throws Exception If an error occurs during execution.
     */
    public String Answer(String input) throws Exception{
        Path newp = Paths.get("data/zenith.txt");
        String zenithData = "./data/zenith.txt";
        Parser parser = new Parser(list, input);
        String arr[];
        String onetwo = list.size() > 1? " tasks": " task";
        Ui ui = new Ui();

        if (parser.getStr().equals("bye")) {
            return stringBye(ui);
        } else if (parser.getStr().equals("list")) {
            return stringList(ui, onetwo);
        } else if (parser.getArr()[0].equals("delete")) {
            return stringDelete(parser,  ui, onetwo);
        }
        else if (parser.getArr()[0].equals("mark")) {
            return stringMark(parser, onetwo, ui);

        } else if (parser.getArr()[0].equals("unmark")) {
            return stringUnmark(parser, onetwo, ui);
        }
        else if (parser.getArr()[0].equals("find")) {
            return stringFind(parser, ui);
        }
        else if (parser.getArr()[0].equals("event"))
        {
            return stringEvent(parser, onetwo, ui, zenithData);
        } else if (parser.getArr()[0].equals("deadline"))
        {
            return stringDeadline(parser, ui, onetwo);
        }
        else if (parser.getArr()[0].equals("todo")){
           return stringToDo(parser, ui, onetwo, zenithData);
        }
        else if(parser.getArr()[0].isEmpty()) {
           return stringEmpty(parser, ui);
        } else {
            try {
                throw new DukeException("");
            }
            catch (DukeException e) {
                ui.format();
                assert ui.toString() != null : "ui.toString() cannot be null";
                return ui.toString();
            }
        }
    }

    /**
     * Appends text to a specified file.
     *
     * @param filePath      The path to the file to which text will be appended.
     * @param textToAppend  The text to append to the file.
     * @throws IOException  If an I/O error occurs.
     */
    private static void appendToFile(String filePath, String textToAppend) throws IOException {
        FileWriter fw = new FileWriter(filePath, true);
        fw.write("\n" + textToAppend);
        fw.close();
    }

    /**
     * Displays the list of tasks to the user.
     */
    public String List() {
        Ui ui = new Ui();
        ui.list(list);
        assert ui.toString() != null : "ui.toString() cannot be null";
        return ui.toString();
    }

    /**
     * Converts a date string from "yyyy-MM-dd" format to "dd/MM/yyyy" format.
     *
     * @param inputDateStr The input date string in "yyyy-MM-dd" format.
     * @return The date string in "dd/MM/yyyy" format.
     */
    public static String convertDateFormat(String inputDateStr) {
        String[] parts = inputDateStr.split("-");

        if (parts.length != 3) {
            return "Invalid date format";
        }
        // Reorders the parts to form the desired output format
        String day = parts[2];
        String month = parts[1];
        String year = parts[0];

        String outputDateStr = day + "/" + month + "/" + year;
        assert outputDateStr != null : "ui.toString() cannot be null";
        return outputDateStr;
    }

    /**java -
     * Refreshes the data in the storage file to match the current list of tasks.
     */
    private static void refreshData() {
        String zenithData = "./data/zenith.txt";
        try {
            FileChannel fileChannel = FileChannel.open(Paths.get(zenithData),
                    StandardOpenOption.WRITE);
            fileChannel.truncate(0);
            fileChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (Task task : list) {
            String done = "";
            String by = "";
            String from = "";
            String to = "";
            String description = task.getDescription();
            String type = "";

            if (task.isDone == true) {
                done = " |1";
            }
            else {
                done = " |0";
            }
            if (task instanceof Event) {
                from = "/from " + convertDateFormat(((Event) task).getFrom().toString());
                to = "/to " + convertDateFormat(((Event) task).getBy().toString());
                type = "event ";
                String string = type + description + from + to  + done;
                try {
                    appendToFile(zenithData, string);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (task instanceof Deadline) {
                by = "/by " + convertDateFormat(((Deadline) task).getBy().toString());
                type = "deadline ";
                String string = type + description + by + done;
                try {
                    appendToFile(zenithData, string);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (task instanceof Todo) {
                type = "todo ";
                String string = type + description + done;
                try {
                    appendToFile(zenithData, string);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Constructs a TaskList object with a specified task.
     *
     * @param task The initial task for the TaskList.
     */
    public TaskList(String task) {
        this.task = task;

    }

    /**
     * Constructs an empty TaskList object.
     */
    public TaskList() {

    }

    /**
     * Gets the current task.
     *
     * @return The current task.
     */
    public String getTask() {
        return this.task;
    }

    /**
     * Loads tasks from the task string into the list.
     */
    public void load() {
        StringToArray(getTask());
    }
}

/*

 */