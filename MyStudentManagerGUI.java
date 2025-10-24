import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

// Class to hold student info
class Student {
    private int id;
    private String name;
    private String major;

    public Student(int id, String name, String major) {
        this.id = id;
        this.name = name;
        this.major = major;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getMajor() { return major; }
    public void setName(String name) { this.name = name; }
    public void setMajor(String major) { this.major = major; }

    @Override
    public String toString() {
        return id + " - " + name; // Nice string for JComboBoxes
    }
}

// Class to hold course info
class Course {
    private String code;
    private String name;

    public Course(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() { return code; }
    public String getName() { return name; }

    @Override
    public String toString() {
        return code + " - " + name;
    }
}

// Class to link Students, Courses, and Grades
class Enrollment {
    private Student student;
    private Course course;
    private String grade;

    public Enrollment(Student student, Course course, String grade) {
        this.student = student;
        this.course = course;
        this.grade = grade;
    }

    public Student getStudent() { return student; }
    public Course getCourse() { return course; }
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
}

// Main application class
public class StudentManagerGUI extends JFrame {

    // --- Data Storage (static lists, keeping it simple) ---
    private static ArrayList<Student> studentList = new ArrayList<>();
    private static ArrayList<Course> courseList = new ArrayList<>();
    private static ArrayList<Enrollment> enrollmentList = new ArrayList<>();

    // --- GUI Components we need to update dynamically ---
    private JTabbedPane mainTabs;
    private DefaultTableModel studentTableModel;
    private JTable studentTable;
    private JComboBox<Student> studentComboBox;
    private JComboBox<Course> courseComboBox;
    private JTable enrollmentGradeTable;
    private DefaultTableModel gradeTableModel;
    
    // --- Initial Data Load ---
    static {
        // Adding some fake data so the system isn't empty
        courseList.add(new Course("CS101", "Intro to Code"));
        courseList.add(new Course("MA101", "Basic Math"));
        courseList.add(new Course("PH105", "Philosophy"));
        
        studentList.add(new Student(100, "Alice Smith", "CS"));
        studentList.add(new Student(101, "Bob Johnson", "MA"));
        
        enrollmentList.add(new Enrollment(studentList.get(0), courseList.get(0), "92"));
    }
    
    // --- Constructor (sets up the whole window) ---
    public StudentManagerGUI() {
        super("Simple Student Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 600);
        
        mainTabs = new JTabbedPane();
        
        // Setting up the three main panels/tabs
        mainTabs.addTab("Student Records", createStudentPanel());
        mainTabs.addTab("Course Enrollment", createEnrollmentPanel());
        mainTabs.addTab("Grade Management", createGradePanel());
        
        add(mainTabs);
        setVisible(true);
    }
    
    // --- Helper to start the application ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentManagerGUI());
    }

    // --- Tab 1: Student Management ---
    private JPanel createStudentPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        
        // 1. Table View (Center)
        String[] columnNames = {"ID", "Name", "Major"};
        studentTableModel = new DefaultTableModel(columnNames, 0);
        studentTable = new JTable(studentTableModel);
        panel.add(new JScrollPane(studentTable), BorderLayout.CENTER);

        // Update table on startup
        updateStudentTable(); 
        
        // 2. Control Buttons (South)
        JPanel controlPanel = new JPanel(new FlowLayout());
        JButton addBtn = new JButton("Add New Student");
        JButton updateBtn = new JButton("Update Selected Student");

        controlPanel.add(addBtn);
        controlPanel.add(updateBtn);
        panel.add(controlPanel, BorderLayout.SOUTH);

        // Event Handler for Add Button
        addBtn.addActionListener(e -> showAddStudentForm());
        
        // Event Handler for Update Button
        updateBtn.addActionListener(e -> showUpdateStudentForm());

        return panel;
    }
    
    // --- Dynamic Table Refresh ---
    private void updateStudentTable() {
        // Clears out the old rows
        studentTableModel.setRowCount(0); 
        
        // Adds all students back in
        for (Student s : studentList) {
            studentTableModel.addRow(new Object[]{s.getId(), s.getName(), s.getMajor()});
        }
    }
    
    // --- Form for Adding Student ---
    private void showAddStudentForm() {
        JTextField nameField = new JTextField(15);
        JTextField majorField = new JTextField(15);
        
        JPanel myPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        myPanel.add(new JLabel("Name:"));
        myPanel.add(nameField);
        myPanel.add(new JLabel("Major:"));
        myPanel.add(majorField);

        int result = JOptionPane.showConfirmDialog(null, myPanel, 
                 "Add New Student", JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            String major = majorField.getText();
            
            if (name.isEmpty() || major.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name and Major cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Generate a simple unique ID
            int newId = studentList.stream().mapToInt(Student::getId).max().orElse(100) + 1;
            
            studentList.add(new Student(newId, name, major));
            
            // Dynamic Update: Refresh table and combo boxes
            updateStudentTable(); 
            updateStudentComboBoxes();
            JOptionPane.showMessageDialog(this, "Student " + name + " added with ID: " + newId);
        }
    }

    // --- Form for Updating Student ---
    private void showUpdateStudentForm() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student from the list first!", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get the actual student object using the ID from the selected table row
        int studentId = (int) studentTableModel.getValueAt(selectedRow, 0);
        Student studentToUpdate = studentList.stream().filter(s -> s.getId() == studentId).findFirst().orElse(null);

        if (studentToUpdate == null) return;
        
        JTextField nameField = new JTextField(studentToUpdate.getName(), 15);
        JTextField majorField = new JTextField(studentToUpdate.getMajor(), 15);
        
        JPanel myPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        myPanel.add(new JLabel("ID (Cannot Change):"));
        myPanel.add(new JLabel(String.valueOf(studentToUpdate.getId())));
        myPanel.add(new JLabel("New Name:"));
        myPanel.add(nameField);
        myPanel.add(new JLabel("New Major:"));
        myPanel.add(majorField);

        int result = JOptionPane.showConfirmDialog(null, myPanel, 
                 "Update Student: " + studentToUpdate.getName(), JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            studentToUpdate.setName(nameField.getText());
            studentToUpdate.setMajor(majorField.getText());
            
            // Dynamic Update: Refresh everything
            updateStudentTable();
            updateStudentComboBoxes();
            JOptionPane.showMessageDialog(this, "Student " + studentToUpdate.getName() + " updated!");
        }
    }
    
    // --- Dynamic Combo Box Refresh ---
    private void updateStudentComboBoxes() {
        // Refreshes the model used in the combo boxes (Enrollment & Grade Tabs)
        if (studentComboBox != null) {
            studentComboBox.setModel(new DefaultComboBoxModel<>(studentList.toArray(new Student[0])));
        }
    }

    // --- Tab 2: Course Enrollment ---
    private JPanel createEnrollmentPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        
        // Components in the North (Selectors)
        JPanel selectors = new JPanel(new FlowLayout());
        
        studentComboBox = new JComboBox<>(studentList.toArray(new Student[0]));
        courseComboBox = new JComboBox<>(courseList.toArray(new Course[0]));

        selectors.add(new JLabel("Select Student:"));
        selectors.add(studentComboBox);
        selectors.add(new JLabel("Select Course:"));
        selectors.add(courseComboBox);
        
        JButton enrollBtn = new JButton("Enroll Student");
        selectors.add(enrollBtn);
        
        panel.add(selectors, BorderLayout.NORTH);
        
        // Event Handler for Enroll Button
        enrollBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Student selectedStudent = (Student) studentComboBox.getSelectedItem();
                Course selectedCourse = (Course) courseComboBox.getSelectedItem();
                
                if (selectedStudent == null || selectedCourse == null) {
                    JOptionPane.showMessageDialog(panel, "Must select both a student and a course.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Check if already enrolled (simple error handling)
                boolean alreadyEnrolled = enrollmentList.stream()
                        .anyMatch(en -> en.getStudent().getId() == selectedStudent.getId() && 
                                         en.getCourse().getCode().equals(selectedCourse.getCode()));
                
                if (alreadyEnrolled) {
                    JOptionPane.showMessageDialog(panel, selectedStudent.getName() + " is already enrolled in " + selectedCourse.getCode(), "Enrollment Failed", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                enrollmentList.add(new Enrollment(selectedStudent, selectedCourse, "N/A"));
                JOptionPane.showMessageDialog(panel, selectedStudent.getName() + " enrolled successfully!");
                
                // Dynamic Update: Refresh the grade table if that tab is open
                // NOTE: This is inefficient but works for a simple system.
            }
        });
        
        // Display Area (Center)
        panel.add(new JLabel("Enrollment History / List View goes here."), BorderLayout.CENTER);
        
        return panel;
    }

    // --- Tab 3: Grade Management ---
    private JPanel createGradePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        
        // Selectors in the North
        JPanel selectors = new JPanel(new FlowLayout());
        
        // This JComboBox will be dynamically updated by the other tabs
        studentComboBox = new JComboBox<>(studentList.toArray(new Student[0])); 
        selectors.add(new JLabel("Select Student:"));
        selectors.add(studentComboBox);

        JButton assignGradeBtn = new JButton("Assign/Update Grade");
        selectors.add(assignGradeBtn);
        
        panel.add(selectors, BorderLayout.NORTH);

        // Table View (Center)
        String[] gradeColumnNames = {"Course Code", "Course Name", "Current Grade"};
        gradeTableModel = new DefaultTableModel(gradeColumnNames, 0);
        enrollmentGradeTable = new JTable(gradeTableModel);
        panel.add(new JScrollPane(enrollmentGradeTable), BorderLayout.CENTER);

        // Event Handler for Student Selection Change
        studentComboBox.addActionListener(e -> {
            Student selectedStudent = (Student) studentComboBox.getSelectedItem();
            if (selectedStudent != null) {
                updateGradeTable(selectedStudent);
            }
        });

        // Event Handler for Assign Grade Button
        assignGradeBtn.addActionListener(e -> showAssignGradeDialog());

        return panel;
    }
    
    // --- Dynamic Grade Table Refresh ---
    private void updateGradeTable(Student student) {
        gradeTableModel.setRowCount(0); // Clear table
        
        // Find all enrollments for the selected student
        enrollmentList.stream()
            .filter(en -> en.getStudent().getId() == student.getId())
            .forEach(en -> gradeTableModel.addRow(new Object[]{
                en.getCourse().getCode(),
                en.getCourse().getName(),
                en.getGrade()
            }));
    }

    // --- Dialog for Assigning Grade ---
    private void showAssignGradeDialog() {
        int selectedRow = enrollmentGradeTable.getSelectedRow();
        Student selectedStudent = (Student) studentComboBox.getSelectedItem();

        if (selectedStudent == null) {
            JOptionPane.showMessageDialog(this, "Please select a student first!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an enrolled course from the table below!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Get the course code from the selected table row
        String courseCode = (String) gradeTableModel.getValueAt(selectedRow, 0);
        
        // Find the specific Enrollment object to update
        Enrollment enrollmentToUpdate = enrollmentList.stream()
                .filter(en -> en.getStudent().getId() == selectedStudent.getId() && en.getCourse().getCode().equals(courseCode))
                .findFirst().orElse(null);

        if (enrollmentToUpdate == null) return;

        String newGrade = JOptionPane.showInputDialog(this, 
            "Enter new grade for " + courseCode + " (" + selectedStudent.getName() + "):", 
            enrollmentToUpdate.getGrade());

        if (newGrade != null && !newGrade.trim().isEmpty()) {
            // Simple validation: make sure it's not just a blank string
            enrollmentToUpdate.setGrade(newGrade.trim().toUpperCase());
            
            // Dynamic Update: Refresh the grade display
            updateGradeTable(selectedStudent);
            JOptionPane.showMessageDialog(this, "Grade assigned successfully!");
        }
    }
}
