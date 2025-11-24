import java.util.Scanner;

class Student {
    int rollNumber;
    String name;
    String course;
    float marks;
    char grade;

    
    public void inputDetails(Scanner sc) {
        System.out.print("Enter Roll Number: ");
        rollNumber = sc.nextInt();
        sc.nextLine(); 

        System.out.print("Enter Name: ");
        name = sc.nextLine();

        System.out.print("Enter Course: ");
        course = sc.nextLine();

        System.out.print("Enter Marks (0-100): ");
        marks = sc.nextFloat();

        calculateGrade();
    }


    public void calculateGrade() {
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

   
    public void displayDetails() {
        System.out.println("\n--- Student Details ---");
        System.out.println("Roll Number : " + rollNumber);
        System.out.println("Name        : " + name);
        System.out.println("Course      : " + course);
        System.out.println("Marks       : " + marks);
        System.out.println("Grade       : " + grade);
        System.out.println("------------------------");
    }
}

public class StudentRecordSystem {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Student[] students = new Student[100];
        int count = 0;
        int choice;

        do {
            System.out.println("\n===== Student Record Management System =====");
            System.out.println("1. Add Student Record");
            System.out.println("2. Display All Student Records");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");
            choice = sc.nextInt();

            switch (choice) {
                case 1:
                    students[count] = new Student();
                    students[count].inputDetails(sc);
                    count++;
                    System.out.println("Student Record Added Successfully!");
                    break;

                case 2:
                    if (count == 0) {
                        System.out.println("No Records Available!");
                    } else {
                        for (int i = 0; i < count; i++) {
                            students[i].displayDetails();
                        }
                    }
                    break;

                case 3:
                    System.out.println("Exiting... Thank you!");
                    break;

                default:
                    System.out.println("Invalid choice! Please try again.");
            }

        } while (choice != 3);

        sc.close();
    }
}
