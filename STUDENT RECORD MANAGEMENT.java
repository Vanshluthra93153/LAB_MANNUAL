
import java.io.*;
import java.util.*;

// Student Class
class Student {
    int rollNo;
    String name;
    double marks;

    public Student(int rollNo, String name, double marks) {
        this.rollNo = rollNo;
        this.name = name;
        this.marks = marks;
    }

    @Override
    public String toString() {
        return rollNo + " | " + name + " | " + marks;
    }

    public String toFileString() {
        return rollNo + "," + name + "," + marks;
    }

    public static Student fromFileString(String line) {
        String[] parts = line.split(",");
        return new Student(Integer.parseInt(parts[0]), parts[1], Double.parseDouble(parts[2]));
    }
}

// Management System
class StudentManager {
    HashMap<Integer, Student> studentMap = new HashMap<>();
    File file = new File("students.txt");

    // Load Records using File Handling
    public void loadFromFile() throws Exception {
        if (!file.exists()) {
            file.createNewFile();
            return;
        }

        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;

        while ((line = br.readLine()) != null) {
            if (!line.trim().isEmpty()) {
                Student s = Student.fromFileString(line);
                studentMap.put(s.rollNo, s);
            }
        }
        br.close();
    }

    // Save Records to File
    public void saveToFile() throws Exception {
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        for (Student s : studentMap.values()) {
            bw.write(s.toFileString());
            bw.newLine();
        }
        bw.close();
    }

    // Add Student
    public void addStudent(Student s) {
        studentMap.put(s.rollNo, s);
        System.out.println("Student Added Successfully!");
    }

    // Delete
    public void deleteStudent(int rollNo) {
        if (studentMap.remove(rollNo) != null)
            System.out.println("Student Deleted!");
        else
            System.out.println("Student Not Found!");
    }

    // Update marks
    public void updateMarks(int rollNo, double newMarks) {
        Student s = studentMap.get(rollNo);
        if (s != null) {
            s.marks = newMarks;
            System.out.println("Marks Updated!");
        } else {
            System.out.println("Student Not Found!");
        }
    }

    // Simple View
    public void viewStudent(int rollNo) {
        Student s = studentMap.get(rollNo);
        if (s != null) System.out.println(s);
        else System.out.println("Student Not Found!");
    }

    // Display using Iterator
    public void displayAll() {
        if (studentMap.isEmpty()) {
            System.out.println("No Records Available!");
            return;
        }

        Iterator<Student> it = studentMap.values().iterator();
        while (it.hasNext()) System.out.println(it.next());
    }

    // Sort by Marks using Comparator
    public void sortByMarks() {
        if (studentMap.isEmpty()) {
            System.out.println("No Records to Sort!");
            return;
        }

        ArrayList<Student> list = new ArrayList<>(studentMap.values());
        list.sort(Comparator.comparingDouble(s -> s.marks));

        System.out.println("--- Sorted by Marks ---");
        for (Student s : list) System.out.println(s);
    }

    // Random Access Reading using RandomAccessFile
    public void randomRead() throws IOException {
        if (!file.exists()) {
            System.out.println("File Not Found!");
            return;
        }

        RandomAccessFile raf = new RandomAccessFile(file, "r");
        System.out.println("\n--- Random Access File Content ---");

        String line;
        while ((line = raf.readLine()) != null)
            System.out.println(line);

        raf.close();
    }

    // Show File Properties
    public void showFileDetails() {
        System.out.println("\nFile Name: " + file.getName());
        System.out.println("Path: " + file.getAbsolutePath());
        System.out.println("Size: " + file.length() + " bytes");
        System.out.println("Readable: " + file.canRead());
        System.out.println("Writable: " + file.canWrite());
        System.out.println("--------------------");
    }
}

// Main Class
public class StudentRecordManagement {
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        StudentManager manager = new StudentManager();

        manager.loadFromFile();

        while (true) {
            System.out.println("\n===== Student Record Management System =====");
            System.out.println("1. Add Student");
            System.out.println("2. Delete Student");
            System.out.println("3. Update Marks");
            System.out.println("4. View Student");
            System.out.println("5. Display All Students");
            System.out.println("6. Sort by Marks");
            System.out.println("7. Show File Details");
            System.out.println("8. Random Read from File");
            System.out.println("9. Save & Exit");
            System.out.print("Enter choice: ");

            int ch = sc.nextInt();
            sc.nextLine();

            switch (ch) {
                case 1:
                    System.out.print("Roll No: ");
                    int r = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Name: ");
                    String n = sc.nextLine();
                    System.out.print("Marks: ");
                    double m = sc.nextDouble();
                    manager.addStudent(new Student(r, n, m));
                    break;

                case 2:
                    System.out.print("Enter Roll No to Delete: ");
                    manager.deleteStudent(sc.nextInt());
                    break;

                case 3:
                    System.out.print("Enter Roll No: ");
                    int rn = sc.nextInt();
                    System.out.print("New Marks: ");
                    double nm = sc.nextDouble();
                    manager.updateMarks(rn, nm);
                    break;

                case 4:
                    System.out.print("Enter Roll No: ");
                    manager.viewStudent(sc.nextInt());
                    break;

                case 5:
                    manager.displayAll();
                    break;

                case 6:
                    manager.sortByMarks();
                    break;

                case 7:
                    manager.showFileDetails();
                    break;

                case 8:
                    manager.randomRead();
                    break;

                case 9:
                    manager.saveToFile();
                    System.out.println("Records Saved! Exiting...");
                    return;

                default:
                    System.out.println("Invalid Choice!");
            }
        }
    }
}
