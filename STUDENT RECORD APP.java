import java.io.*;
import java.util.*;

// --------------------- Custom Exceptions ---------------------
class InvalidMarksException extends Exception {
    public InvalidMarksException(String msg) { super(msg); }
}

class StudentNotFoundException extends Exception {
    public StudentNotFoundException(String msg) { super(msg); }
}

// --------------------- Abstract Person ---------------------
abstract class Person {
    protected String name;
    protected String email;

    Person(String name, String email) {
        this.name = name;
        this.email = email;
    }

    abstract void displayDetails();
}

// --------------------- Interface for actions ---------------------
interface RecordActions {
    void addStudent(Scanner sc) throws InvalidMarksException;
    void updateStudent(Scanner sc, int rollNo) throws StudentNotFoundException, InvalidMarksException;
    void deleteStudent(int rollNo) throws StudentNotFoundException;
    Student searchStudent(int rollNo) throws StudentNotFoundException;
    List<Student> getAllStudents();
    void loadFromFile(String filename) throws IOException;
    void saveToFile(String filename) throws IOException;
    void randomRead(String filename) throws IOException;
}

// --------------------- Student class ---------------------
class Student extends Person {
    int rollNo;
    String course;
    double marks;
    char grade;

    public Student(int rollNo, String name, String email, String course, double marks) {
        super(name, email);
        this.rollNo = rollNo;
        this.course = course;
        this.marks = marks;
        calculateGrade();
    }

    public void calculateGrade() {
        if (marks >= 90) grade = 'A';
        else if (marks >= 75) grade = 'B';
        else if (marks >= 60) grade = 'C';
        else if (marks >= 45) grade = 'D';
        else grade = 'F';
    }

    // overloaded update
    public void update(double newMarks) { this.marks = newMarks; calculateGrade(); }
    public void update(String newEmail) { this.email = newEmail; }

    @Override
    void displayDetails() {
        System.out.printf("Roll: %d | Name: %s | Email: %s | Course: %s | Marks: %.2f | Grade: %c%n",
                rollNo, name, email, course, marks, grade);
    }

    public String toFileString() {
        // CSV safe: escape commas by replacing with space (simple approach)
        return rollNo + "," + name.replace(",", " ") + "," + email.replace(",", " ") + "," + course.replace(",", " ") + "," + marks;
    }

    public static Student fromFileString(String line) {
        String[] p = line.split(",", 5);
        int r = Integer.parseInt(p[0]);
        String n = p[1];
        String e = p[2];
        String c = p[3];
        double m = Double.parseDouble(p[4]);
        return new Student(r, n, e, c, m);
    }
}

// --------------------- Loader (multithreaded UI effect) ---------------------
class Loader implements Runnable {
    private final String message;
    private volatile boolean running = true;

    Loader(String message) { this.message = message; }

    public void stop() { running = false; }

    @Override
    public void run() {
        System.out.print(message);
        try {
            int dots = 0;
            while (running && dots < 10) {
                System.out.print(".");
                Thread.sleep(300);
                dots++;
            }
        } catch (InterruptedException ignored) { }
        System.out.println(" done.");
    }
}

// --------------------- StudentManager ---------------------
class StudentManager implements RecordActions {
    private final HashMap<Integer, Student> studentMap = new HashMap<>();

    // synchronized for thread-safety when modifying collection
    private synchronized boolean isDuplicate(int rollNo) {
        return studentMap.containsKey(rollNo);
    }

    private void validateMarks(Double marks) throws InvalidMarksException {
        if (marks == null) throw new InvalidMarksException("Marks cannot be null");
        if (marks < 0 || marks > 100) throw new InvalidMarksException("Marks must be between 0 and 100");
    }

    @Override
    public void addStudent(Scanner sc) throws InvalidMarksException {
        System.out.print("Enter Roll No: ");
        String rs = sc.nextLine().trim();
        if (rs.isEmpty()) { System.out.println("Roll No required."); return; }
        int roll;
        try { roll = Integer.parseInt(rs); } catch (NumberFormatException e) { System.out.println("Invalid roll format."); return; }

        synchronized (this) {
            if (isDuplicate(roll)) { System.out.println("Duplicate roll number. Aborted."); return; }
        }

        System.out.print("Enter Name: ");
        String name = sc.nextLine().trim();
        if (name.isEmpty()) { System.out.println("Name required."); return; }

        System.out.print("Enter Email: ");
        String email = sc.nextLine().trim();
        if (email.isEmpty()) { System.out.println("Email required."); return; }

        System.out.print("Enter Course: ");
        String course = sc.nextLine().trim();
        if (course.isEmpty()) { System.out.println("Course required."); return; }

        System.out.print("Enter Marks (0-100): ");
        String ms = sc.nextLine().trim();
        Double marks;
        try { marks = Double.valueOf(ms); } catch (NumberFormatException e) { System.out.println("Invalid marks."); return; }

        validateMarks(marks);

        Loader loader = new Loader("Adding student");
        Thread t = new Thread(loader);
        t.start();

        // simulate work
        try { Thread.sleep(900); } catch (InterruptedException ignored) { }

        synchronized (this) {
            studentMap.put(roll, new Student(roll, name, email, course, marks)); // autoboxing/usage of wrappers happened above
        }

        loader.stop();
        try { t.join(); } catch (InterruptedException ignored) { }

        System.out.println("Student added successfully.");
    }

    @Override
    public void updateStudent(Scanner sc, int rollNo) throws StudentNotFoundException, InvalidMarksException {
        Student s;
        synchronized (this) {
            s = studentMap.get(rollNo);
        }
        if (s == null) throw new StudentNotFoundException("Student not found: " + rollNo);

        System.out.println("Update Options: 1) Marks  2) Email");
        System.out.print("Choice: ");
        String opt = sc.nextLine().trim();
        if ("1".equals(opt)) {
            System.out.print("Enter new marks: ");
            String ms = sc.nextLine().trim();
            Double marks;
            try { marks = Double.valueOf(ms); } catch (NumberFormatException e) { System.out.println("Invalid marks."); return; }
            validateMarks(marks);
            synchronized (this) { s.update(marks); }
            System.out.println("Marks updated.");
        } else if ("2".equals(opt)) {
            System.out.print("Enter new email: ");
            String email = sc.nextLine().trim();
            if (email.isEmpty()) { System.out.println("Email required."); return; }
            synchronized (this) { s.update(email); }
            System.out.println("Email updated.");
        } else {
            System.out.println("Invalid option.");
        }
    }

    @Override
    public void deleteStudent(int rollNo) throws StudentNotFoundException {
        synchronized (this) {
            if (studentMap.remove(rollNo) == null) throw new StudentNotFoundException("Student not found: " + rollNo);
        }
        System.out.println("Student deleted: " + rollNo);
    }

    @Override
    public Student searchStudent(int rollNo) throws StudentNotFoundException {
        Student s;
        synchronized (this) { s = studentMap.get(rollNo); }
        if (s == null) throw new StudentNotFoundException("Student not found: " + rollNo);
        return s;
    }

    @Override
    public List<Student> getAllStudents() {
        synchronized (this) {
            return new ArrayList<>(studentMap.values());
        }
    }

    @Override
    public void loadFromFile(String filename) throws IOException {
        File f = new File(filename);
        if (!f.exists()) {
            // create empty file
            f.createNewFile();
            System.out.println("Data file created: " + f.getAbsolutePath());
            return;
        }

        Loader loader = new Loader("Loading records");
        Thread t = new Thread(loader);
        t.start();

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            synchronized (this) {
                studentMap.clear();
                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    try {
                        Student s = Student.fromFileString(line);
                        if (!studentMap.containsKey(s.rollNo)) studentMap.put(s.rollNo, s);
                    } catch (Exception e) {
                        System.out.println("Skipping invalid record: " + line);
                    }
                }
            }
        } finally {
            loader.stop();
            try { t.join(); } catch (InterruptedException ignored) { }
        }

        System.out.println("Load completed. " + studentMap.size() + " record(s) loaded.");
    }

    @Override
    public void saveToFile(String filename) throws IOException {
        File f = new File(filename);
        Loader loader = new Loader("Saving records");
        Thread t = new Thread(loader);
        t.start();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {
            synchronized (this) {
                for (Student s : studentMap.values()) {
                    bw.write(s.toFileString());
                    bw.newLine();
                }
            }
            bw.flush();
        } finally {
            loader.stop();
            try { t.join(); } catch (InterruptedException ignored) { }
        }
        System.out.println("Save completed. File: " + f.getAbsolutePath());
    }

    @Override
    public void randomRead(String filename) throws IOException {
        File f = new File(filename);
        if (!f.exists()) { System.out.println("File does not exist."); return; }

        System.out.println("--- RandomAccessFile read (line by line demo) ---");
        try (RandomAccessFile raf = new RandomAccessFile(f, "r")) {
            long ptr = 0;
            String line;
            while ((line = raf.readLine()) != null) {
                System.out.printf("pos=%d -> %s%n", ptr, line);
                ptr = raf.getFilePointer();
            }
        }
    }
}

// --------------------- Main application ---------------------
public class StudentRecordApp {
    private static final String DATA_FILE = "students.txt";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        StudentManager manager = new StudentManager();

        // Load at start
        try {
            manager.loadFromFile(DATA_FILE);
        } catch (IOException e) {
            System.out.println("Error loading file: " + e.getMessage());
        }

        boolean running = true;
        while (running) {
            System.out.println("\n==== Student Record Management ====");
            System.out.println("1. Add Student");
            System.out.println("2. Update Student");
            System.out.println("3. Delete Student");
            System.out.println("4. Search Student");
            System.out.println("5. View All Students (unsorted)");
            System.out.println("6. View All Students (sorted by marks)");
            System.out.println("7. Save Now");
            System.out.println("8. Random Read File (demo)");
            System.out.println("9. File Info");
            System.out.println("10. Exit (auto-save)");
            System.out.print("Choice: ");

            String choice = sc.nextLine().trim();
            try {
                switch (choice) {
                    case "1":
                        try {
                            manager.addStudent(sc);
                        } catch (InvalidMarksException ime) {
                            System.out.println("InvalidMarks: " + ime.getMessage());
                        }
                        break;

                    case "2":
                        System.out.print("Enter roll to update: ");
                        String upr = sc.nextLine().trim();
                        try {
                            int r = Integer.parseInt(upr);
                            try { manager.updateStudent(sc, r); }
                            catch (InvalidMarksException | StudentNotFoundException ex) { System.out.println(ex.getMessage()); }
                        } catch (NumberFormatException nfe) { System.out.println("Invalid roll format."); }
                        break;

                    case "3":
                        System.out.print("Enter roll to delete: ");
                        String dr = sc.nextLine().trim();
                        try {
                            int r = Integer.parseInt(dr);
                            manager.deleteStudent(r);
                        } catch (NumberFormatException nfe) { System.out.println("Invalid roll format."); }
                        catch (StudentNotFoundException snf) { System.out.println(snf.getMessage()); }
                        break;

                    case "4":
                        System.out.print("Enter roll to search: ");
                        String sr = sc.nextLine().trim();
                        try {
                            int r = Integer.parseInt(sr);
                            try {
                                Student s = manager.searchStudent(r);
                                s.displayDetails();
                            } catch (StudentNotFoundException snf) { System.out.println(snf.getMessage()); }
                        } catch (NumberFormatException nfe) { System.out.println("Invalid roll format."); }
                        break;

                    case "5":
                        List<Student> all = manager.getAllStudents();
                        if (all.isEmpty()) System.out.println("No records.");
                        else {
                            System.out.println("--- All Students ---");
                            Iterator<Student> it = all.iterator();
                            while (it.hasNext()) it.next().displayDetails();
                        }
                        break;

                    case "6":
                        List<Student> sorted = manager.getAllStudents();
                        if (sorted.isEmpty()) { System.out.println("No records."); }
                        else {
                            sorted.sort(Comparator.comparingDouble(s -> s.marks)); // ascending by marks
                            System.out.println("--- Students Sorted by Marks (ascending) ---");
                            for (Student s : sorted) s.displayDetails();
                        }
                        break;

                    case "7":
                        try { manager.saveToFile(DATA_FILE); } catch (IOException e) { System.out.println("Save error: " + e.getMessage()); }
                        break;

                    case "8":
                        try { manager.randomRead(DATA_FILE); } catch (IOException e) { System.out.println("Random read error: " + e.getMessage()); }
                        break;

                    case "9": {
                        File f = new File(DATA_FILE);
                        System.out.println("File: " + f.getName());
                        System.out.println("Path: " + f.getAbsolutePath());
                        System.out.println("Exists: " + f.exists());
                        System.out.println("Readable: " + f.canRead());
                        System.out.println("Writable: " + f.canWrite());
                        System.out.println("Size (bytes): " + (f.exists() ? f.length() : 0));
                        break;
                    }

                    case "10":
                        // auto-save and exit
                        try { manager.saveToFile(DATA_FILE); } catch (IOException e) { System.out.println("Save error: " + e.getMessage()); }
                        System.out.println("Exiting. Goodbye!");
                        running = false;
                        break;

                    default:
                        System.out.println("Invalid choice.");
                }
            } finally {
                System.out.println("-- Operation Completed --");
            }
        }

        sc.close();
    }
}
