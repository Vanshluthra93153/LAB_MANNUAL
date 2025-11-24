import java.util.Scanner;

// Abstract class Person
abstract class Person {
    String name;
    String email;

    // Constructor
    Person(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // Abstract method
    abstract void displayDetails();
}

// Interface for operations
interface RecordActions {
    void addStudent(Scanner sc);
    void deleteStudent(int rollNo);
    void updateStudent(Scanner sc, int rollNo);
    void viewStudent(int rollNo);
    void viewAllStudents();
}

// Student class extending Person
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

    // Method Overloading -> Update Marks
    public void update(double marks) {
        this.marks = marks;
        calculateGrade();
    }

    // Method Overloading -> Update Contact Info
    public void update(String email) {
        this.email = email;
    }

    void calculateGrade() {
        if (marks >= 90)
            grade = 'A';
        else if (marks >= 75)
            grade = 'B';
        else if (marks >= 60)
            grade = 'C';
        else if (marks >= 45)
            grade = 'D';
        else
            grade = 'F';
    }

    // Method overriding
    @Override
    void displayDetails() {
        System.out.println("Roll No: " + rollNo +
                ", Name: " + name +
                ", Email: " + email +
                ", Course: " + course +
                ", Marks: " + marks +
                ", Grade: " + grade);
    }
}

// Manager Class to manage records
class StudentManager implements RecordActions {
    Student[] students;
    int count;

    public StudentManager(int size) {
        students = new Student[size];
        count = 0;
    }

    private boolean isDuplicate(int rollNo) {
        for (int i = 0; i < count; i++) {
            if (students[i].rollNo == rollNo)
                return true;
        }
        return false;
    }

    @Override
    public void addStudent(Scanner sc) {
        if (count == students.length) {
            System.out.println("Student list is full!");
            return;
        }

        System.out.print("Enter Roll No: ");
        int rollNo = sc.nextInt();
        sc.nextLine();
        if (isDuplicate(rollNo)) {
            System.out.println("Error: Roll number already exists!");
            return;
        }

        System.out.print("Enter Name: ");
        String name = sc.nextLine();
        System.out.print("Enter Email: ");
        String email = sc.nextLine();
        System.out.print("Enter Course: ");
        String course = sc.nextLine();
        System.out.print("Enter Marks: ");
        double marks = sc.nextDouble();

        students[count++] = new Student(rollNo, name, email, course, marks);
        System.out.println("Student added successfully!");
    }

    @Override
    public void deleteStudent(int rollNo) {
        for (int i = 0; i < count; i++) {
            if (students[i].rollNo == rollNo) {
                students[i] = students[count - 1];
                students[count - 1] = null;
                count--;
                System.out.println("Student deleted successfully!");
                return;
            }
        }
        System.out.println("Student not found!");
    }

    @Override
    public void updateStudent(Scanner sc, int rollNo) {
        for (int i = 0; i < count; i++) {
            if (students[i].rollNo == rollNo) {
                System.out.println("Update Options:\n1. Marks\n2. Email");
                System.out.print("Choose option: ");
                int ch = sc.nextInt();
                sc.nextLine();
                if (ch == 1) {
                    System.out.print("Enter new Marks: ");
                    double marks = sc.nextDouble();
                    students[i].update(marks);
                } else if (ch == 2) {
                    System.out.print("Enter new Email: ");
                    String email = sc.nextLine();
                    students[i].update(email);
                } else {
                    System.out.println("Invalid choice!");
                }
                System.out.println("Record updated successfully!");
                return;
            }
        }
        System.out.println("Student not found!");
    }

    @Override
    public void viewStudent(int rollNo) {
        for (int i = 0; i < count; i++) {
            if (students[i].rollNo == rollNo) {
                students[i].displayDetails();
                return;
            }
        }
        System.out.println("Student not found!");
    }

    @Override
    public void viewAllStudents() {
        if (count == 0) {
            System.out.println("No student records found!");
            return;
        }
        for (int i = 0; i < count; i++) {
            students[i].displayDetails();
        }
    }
}

// Main Class
public class StudentManagementSystem {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        StudentManager manager = new StudentManager(100);
        int choice;

        System.out.println("===== Student Management System =====");

        do {
            System.out.println("\n1. Add Student");
            System.out.println("2. Delete Student");
            System.out.println("3. Update Student");
            System.out.println("4. View Specific Student");
            System.out.println("5. View All Students");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");
            choice = sc.nextInt();

            switch (choice) {
                case 1:
                    manager.addStudent(sc);
                    break;
                case 2:
                    System.out.print("Enter Roll No: ");
                    manager.deleteStudent(sc.nextInt());
                    break;
                case 3:
                    System.out.print("Enter Roll No to update: ");
                    manager.updateStudent(sc, sc.nextInt());
                    break;
                case 4:
                    System.out.print("Enter Roll No: ");
                    manager.viewStudent(sc.nextInt());
                    break;
                case 5:
                    manager.viewAllStudents();
                    break;
                case 6:
                    System.out.println("Exiting system... Goodbye!");
                    break;
                default:
                    System.out.println("Invalid Choice! Try again.");
            }

        } while (choice != 6);

        sc.close();
    }
}
