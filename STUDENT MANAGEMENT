import java.io.*;
import java.util.*;


class InvalidMarksException extends Exception {
    public InvalidMarksException(String message) {
        super(message);
    }
}

class StudentNotFoundException extends Exception {
    public StudentNotFoundException(String message) {
        super(message);
    }
}


abstract class Person {
    protected String name;
    protected String email;

    Person(String name, String email) {
        this.name = name;
        this.email = email;
    }

    abstract void displayDetails();
}

interface RecordActions {
    void addStudent(Scanner sc) throws InvalidMarksException;
    void deleteStudent(int rollNo) throws StudentNotFoundException;
    void updateStudent(Scanner sc, int rollNo) throws StudentNotFoundException, InvalidMarksException;
    void viewStudent(int rollNo) throws StudentNotFoundException;
    void viewAllStudents();
    void saveToFile(String filename) throws IOException;
    void loadFromFile(String filename) throws IOException;
}

// -------------------- Student class --------------------
class Student extends Person {
    int rollNo;
    String course;
    double marks; // primitive, but we'll use Double wrapper where helpful
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

    // Overloaded update methods (method overloading)
    public void update(double newMarks) {
        this.marks = newMarks;
        calculateGrade();
    }

    public void update(String newEmail) {
        this.email = newEmail;
    }

    @Override
    void displayDetails() {
        System.out.println("RollNo: " + rollNo +
                ", Name: " + name +
                ", Email: " + email +
                ", Course: " + course +
                ", Marks: " + marks +
                ", Grade: " + grade);
    }

    public String toFileString() {
        return rollNo + "|" + name + "|" + email + "|" + course + "|" + marks;
    }

    public static Student fromFileString(String line) {
        String[] p = line.split("\\|");
        int r = Integer.parseInt(p[0]);
        String n = p[1];
        String e = p[2];
        String c = p[3];
        double m = Double.parseDouble(p[4]);
        return new Student(r, n, e, c, m);
    }
}


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
            while (running && dots < 6) { // limit to avoid infinite loop in demo
                System.out.print(".");
                Thread.sleep(500);
                dots++;
            }
        } catch (InterruptedException ignored) {}
        System.out.println(" done.");
    }
}

class StudentManager implements RecordActions {
    private final Student[] students;
    private int count;

    public StudentManager(int capacity) {
        students = new Student[capacity];
        count = 0;
    }

    // Prevent duplicate roll numbers
    private boolean isDuplicate(int rollNo) {
        for (int i = 0; i < count; i++) if (students[i].rollNo == rollNo) return true;
        return false;
    }

    // Validate marks using wrapper Double to show boxing/unboxing
    private void validateMarks(Double marksWrapper) throws InvalidMarksException {
        if (marksWrapper == null) throw new InvalidMarksException("Marks is null");
        double marks = marksWrapper.doubleValue(); // unboxing
        if (marks < 0 || marks > 100)
            throw new InvalidMarksException("Marks out of range (0-100): " + marks);
    }

    // Add student - synchronized for thread-safety
    @Override
    public synchronized void addStudent(Scanner sc) throws InvalidMarksException {
        System.out.print("Enter Roll No: ");
        String rollInput = sc.nextLine().trim();
        if (rollInput.isEmpty()) {
            System.out.println("Roll No cannot be empty.");
            return;
        }
        Integer rollWrapper;
        try {
            rollWrapper = Integer.valueOf(rollInput);
        } catch (NumberFormatException e) {
            System.out.println("Invalid roll number format.");
            return;
        }
        int rollNo = rollWrapper;

        if (isDuplicate(rollNo)) {
            System.out.println("Error: Duplicate roll number. Add aborted.");
            return;
        }

        System.out.print("Enter Name: ");
        String name = sc.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Name cannot be empty. Add aborted.");
            return;
        }

        System.out.print("Enter Email: ");
        String email = sc.nextLine().trim();
        if (email.isEmpty()) {
            System.out.println("Email cannot be empty. Add aborted.");
            return;
        }

        System.out.print("Enter Course: ");
        String course = sc.nextLine().trim();
        if (course.isEmpty()) {
            System.out.println("Course cannot be empty. Add aborted.");
            return;
        }

        System.out.print("Enter Marks (0-100): ");
        String marksStr = sc.nextLine().trim();
        Double marksWrapper;
        try {
            marksWrapper = Double.valueOf(marksStr); // wrapper
        } catch (NumberFormatException e) {
            System.out.println("Invalid marks format.");
            return;
        }

        validateMarks(marksWrapper);

        // simulate loading with thread
        Loader loader = new Loader("Adding student");
        Thread loaderThread = new Thread(loader);
        loaderThread.start();

        try {
            Thread.sleep(1600);
        } catch (InterruptedException ignored) {}

        // Ensure insertion is atomic and visible
        students[count++] = new Student(rollNo, name, email, course, marksWrapper);

        loader.stop();
        try { loaderThread.join(); } catch (InterruptedException ignored) {}

        System.out.println("Student added successfully.");
    }

    @Override
    public synchronized void deleteStudent(int rollNo) throws StudentNotFoundException {
        for (int i = 0; i < count; i++) {
            if (students[i].rollNo == rollNo) {
                students[i] = students[count - 1];
                students[count - 1] = null;
                count--;
                System.out.println("Student deleted successfully.");
                return;
            }
        }
        throw new StudentNotFoundException("Student with roll " + rollNo + " not found.");
    }

    @Override
    public synchronized void updateStudent(Scanner sc, int rollNo) throws StudentNotFoundException, InvalidMarksException {
        for (int i = 0; i < count; i++) {
            if (students[i].rollNo == rollNo) {
                System.out.println("Update Options: 1) Marks  2) Email");
                System.out.print("Choose: ");
                String opt = sc.nextLine().trim();
                if (opt.equals("1")) {
                    System.out.print("Enter new marks: ");
                    String mstr = sc.nextLine().trim();
                    Double marksWrapper;
                    try { marksWrapper = Double.valueOf(mstr); }
                    catch (NumberFormatException e) { System.out.println("Invalid marks format."); return; }

                    validateMarks(marksWrapper);
                    students[i].update(marksWrapper); // autounboxing
                    System.out.println("Marks updated.");
                    return;
                } else if (opt.equals("2")) {
                    System.out.print("Enter new email: ");
                    String newEmail = sc.nextLine().trim();
                    if (newEmail.isEmpty()) { System.out.println("Email cannot be empty."); return; }
                    students[i].update(newEmail);
                    System.out.println("Email updated.");
                    return;
                } else {
                    System.out.println("Invalid option.");
                    return;
                }
            }
        }
        throw new StudentNotFoundException("Student with roll " + rollNo + " not found.");
    }

    @Override
    public synchronized void viewStudent(int rollNo) throws StudentNotFoundException {
        for (int i = 0; i < count; i++) {
            if (students[i].rollNo == rollNo) {
                students[i].displayDetails();
                return;
            }
        }
        throw new StudentNotFoundException("Student with roll " + rollNo + " not found.");
    }

    @Override
    public synchronized void viewAllStudents() {
        if (count == 0) {
            System.out.println("No records.");
            return;
        }
        for (int i = 0; i < count; i++) students[i].displayDetails();
    }

    @Override
    public synchronized void saveToFile(String filename) throws IOException {
        Loader loader = new Loader("Saving to file");
        Thread t = new Thread(loader);
        t.start();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (int i = 0; i < count; i++) {
                bw.write(students[i].toFileString());
                bw.newLine();
            }
            bw.flush();
        } finally {
            loader.stop();
            try { t.join(); } catch (InterruptedException ignored) {}
        }
        System.out.println("Save completed.");
    }

    @Override
    public synchronized void loadFromFile(String filename) throws IOException {
        File f = new File(filename);
        if (!f.exists()) {
            System.out.println("File not found: " + filename);
            return;
        }

        Loader loader = new Loader("Loading from file");
        Thread t = new Thread(loader);
        t.start();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            count = 0;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                Student s = Student.fromFileString(line);
                if (!isDuplicate(s.rollNo)) students[count++] = s;
            }
        } finally {
            loader.stop();
            try { t.join(); } catch (InterruptedException ignored) {}
        }
        System.out.println("Load completed. " + count + " record(s) loaded.");
    }
}

public class StudentManagementEnhanced {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        StudentManager manager = new StudentManager(200);
        String filename = "students_db.txt";

        System.out.println("=== Student Management System (Enhanced) ===");
        while (true) {
            System.out.println("\n1. Add Student");
            System.out.println("2. Delete Student");
            System.out.println("3. Update Student");
            System.out.println("4. View Student");
            System.out.println("5. View All Students");
            System.out.println("6. Save Records to File");
            System.out.println("7. Load Records from File");
            System.out.println("8. Exit");
            System.out.print("Choice: ");

            String choice = sc.nextLine().trim();
            try {
                switch (choice) {
                    case "1":
                        try {
                            manager.addStudent(sc);
                        } catch (InvalidMarksException ime) {
                            System.out.println("InvalidMarksException: " + ime.getMessage());
                        }
                        break;

                    case "2":
                        System.out.print("Enter roll to delete: ");
                        String rdel = sc.nextLine().trim();
                        try {
                            int roll = Integer.parseInt(rdel);
                            manager.deleteStudent(roll);
                        } catch (NumberFormatException nfe) {
                            System.out.println("Invalid roll format.");
                        } catch (StudentNotFoundException snf) {
                            System.out.println(snf.getMessage());
                        }
                        break;

                    case "3":
                        System.out.print("Enter roll to update: ");
                        String rup = sc.nextLine().trim();
                        try {
                            int roll = Integer.parseInt(rup);
                            manager.updateStudent(sc, roll);
                        } catch (NumberFormatException nfe) {
                            System.out.println("Invalid roll format.");
                        } catch (StudentNotFoundException | InvalidMarksException ex) {
                            System.out.println(ex.getMessage());
                        }
                        break;

                    case "4":
                        System.out.print("Enter roll to view: ");
                        String rv = sc.nextLine().trim();
                        try {
                            int roll = Integer.parseInt(rv);
                            manager.viewStudent(roll);
                        } catch (NumberFormatException nfe) {
                            System.out.println("Invalid roll format.");
                        } catch (StudentNotFoundException snf) {
                            System.out.println(snf.getMessage());
                        }
                        break;

                    case "5":
                        manager.viewAllStudents();
                        break;

                    case "6":
                        try {
                            manager.saveToFile(filename);
                        } catch (IOException ioe) {
                            System.out.println("I/O Error: " + ioe.getMessage());
                        }
                        break;

                    case "7":
                        try {
                            manager.loadFromFile(filename);
                        } catch (IOException ioe) {
                            System.out.println("I/O Error: " + ioe.getMessage());
                        }
                        break;

                    case "8":
                        System.out.println("Exiting. Goodbye!");
                        sc.close();
                        return;

                    default:
                        System.out.println("Invalid choice.");
                }
            } finally {
                System.out.println("-- Operation Completed --");
            }
        }
    }
}
