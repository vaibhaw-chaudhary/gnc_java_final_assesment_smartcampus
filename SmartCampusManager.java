import java.util.*;
import java.io.*;

// 1. Custom Exception
class InvalidCampusDataException extends Exception {
    public InvalidCampusDataException(String message) {
        super(message);
    }
}

// 2. Student Class
class CampusStudent implements Serializable {
    private int studentId;
    private String fullName;
    private String contactEmail;

    public CampusStudent(int id, String name, String email) {
        this.studentId = id;
        this.fullName = name;
        this.contactEmail = email;
    }

    public int getStudentId() { return studentId; }
    public String getFullName() { return fullName; }
    
    @Override
    public String toString() {
        return "ID: " + studentId + " | Name: " + fullName + " | Email: " + contactEmail;
    }
}

// 3. Course Class
class CampusCourse implements Serializable {
    private int courseId;
    private String title;
    private double tuitionFee;

    public CampusCourse(int id, String title, double fee) throws InvalidCampusDataException {
        if (fee < 0) throw new InvalidCampusDataException("Tuition fee cannot be negative!");
        this.courseId = id;
        this.title = title;
        this.tuitionFee = fee;
    }

    public int getCourseId() { return courseId; }
    public String getTitle() { return title; }
    
    @Override
    public String toString() {
        return "Course ID: " + courseId + " | Title: " + title + " | Fee: ₹" + tuitionFee;
    }
}

// 4. Multithreading: Enrollment Processor
class AsyncEnrollmentTask implements Runnable {
    private String studentName;
    private String courseName;
    private boolean isScholarshipStudent; // Updated unique feature

    public AsyncEnrollmentTask(String studentName, String courseName, boolean isScholarshipStudent) {
        this.studentName = studentName;
        this.courseName = courseName;
        this.isScholarshipStudent = isScholarshipStudent;
    }

    @Override
    public void run() {
        try {
            System.out.println("⏳ Processing " + (isScholarshipStudent ? "[SCHOLARSHIP] " : "") + "enrollment for " + studentName + " in " + courseName + "...");
            Thread.sleep(isScholarshipStudent ? 500 : 2000); // Scholarship processes faster
            System.out.println("✅ SUCCESS: " + studentName + " is officially enrolled in " + courseName + "!");
        } catch (InterruptedException e) {
            System.out.println("❌ Enrollment interrupted for " + studentName);
        }
    }
}

// 5. Main System & Menu
public class SmartCampusManager {
    // Updated Variable Names
    private static HashMap<Integer, CampusStudent> studentDirectory = new HashMap<>();
    private static HashMap<Integer, CampusCourse> availableCourses = new HashMap<>();
    private static HashMap<Integer, ArrayList<CampusCourse>> enrollmentRecords = new HashMap<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean systemRunning = true;

        // --- PRE-LOADED STUDENT & COURSE DATA ---
        studentDirectory.put(24665211, new CampusStudent(24665211, "Vaibhaw Chaudhary", "vaibhawchaudhary006@gmail.com"));
        try {
            availableCourses.put(24665, new CampusCourse(24665, "B.Tech CSE", 100000.0)); // 1 lac fee
        } catch (InvalidCampusDataException e) {
            System.out.println("Error loading default course data.");
        }

        // Updated Welcome Message
        System.out.println("--- Welcome to the GNC University Student Portal ---");

        while (systemRunning) {
            System.out.println("\n1. Add Student\n2. Add Course\n3. Enroll Student\n4. View Students\n5. View Enrollments\n6. Process Enrollment (Thread)\n7. Exit");
            System.out.print("Select an option: ");
            
            try {
                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1:
                        System.out.print("Enter Student ID: ");
                        int sId = Integer.parseInt(scanner.nextLine());
                        System.out.print("Enter Name: ");
                        String name = scanner.nextLine();
                        System.out.print("Enter Email: ");
                        String email = scanner.nextLine();
                        studentDirectory.put(sId, new CampusStudent(sId, name, email));
                        System.out.println("✅ New student registered in the system."); // Updated Print
                        break;

                    case 2:
                        System.out.print("Enter Course ID: ");
                        int cId = Integer.parseInt(scanner.nextLine());
                        System.out.print("Enter Course Name: ");
                        String cName = scanner.nextLine();
                        System.out.print("Enter Fee: ");
                        double fee = Double.parseDouble(scanner.nextLine());
                        
                        availableCourses.put(cId, new CampusCourse(cId, cName, fee)); 
                        System.out.println("✅ New course added to the catalog."); // Updated Print
                        break;

                    case 3:
                        System.out.print("Enter Student ID to enroll: ");
                        int enrollSId = Integer.parseInt(scanner.nextLine());
                        System.out.print("Enter Course ID to enroll in: ");
                        int enrollCId = Integer.parseInt(scanner.nextLine());

                        if (!studentDirectory.containsKey(enrollSId)) throw new InvalidCampusDataException("Student record not found.");
                        if (!availableCourses.containsKey(enrollCId)) throw new InvalidCampusDataException("Course not available in catalog.");

                        enrollmentRecords.putIfAbsent(enrollSId, new ArrayList<>());
                        enrollmentRecords.get(enrollSId).add(availableCourses.get(enrollCId));
                        System.out.println("📌 Enrollment successfully staged. (Run Option 6 to finalize)."); // Updated Print
                        break;

                    case 4:
                        System.out.println("\n--- Registered Students Directory ---");
                        for (CampusStudent s : studentDirectory.values()) System.out.println(s);
                        break;

                    case 5:
                        System.out.println("\n--- Official Enrollment Records ---");
                        for (Integer id : enrollmentRecords.keySet()) {
                            System.out.println("Student: " + studentDirectory.get(id).getFullName());
                            for (CampusCourse c : enrollmentRecords.get(id)) {
                                System.out.println("  -> " + c.getTitle());
                            }
                        }
                        break;

                    case 6:
                        System.out.print("Enter Student ID to process: ");
                        int processId = Integer.parseInt(scanner.nextLine());
                        System.out.print("Is this a scholarship student? (true/false): "); // Updated unique feature text
                        boolean isScholarship = Boolean.parseBoolean(scanner.nextLine());
                        
                        if (enrollmentRecords.containsKey(processId)) {
                            CampusStudent student = studentDirectory.get(processId);
                            for (CampusCourse course : enrollmentRecords.get(processId)) {
                                Thread t = new Thread(new AsyncEnrollmentTask(student.getFullName(), course.getTitle(), isScholarship));
                                t.start();
                            }
                        } else {
                            System.out.println("⚠️ No pending enrollments found for this student ID."); // Updated Print
                        }
                        break;

                    case 7:
                        systemRunning = false;
                        System.out.println("🚪 Exiting GNC Student Portal. Goodbye!"); // Updated Print
                        break;

                    default:
                        System.out.println("⚠️ Invalid option. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Error: Please enter valid numeric values where required.");
            } catch (InvalidCampusDataException e) {
                System.out.println("❌ Campus Data Error: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("❌ An unexpected error occurred: " + e.getMessage());
            }
        }
        scanner.close();
    }
}