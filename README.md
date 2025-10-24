Student Management System (Swing GUI) 👨‍🎓

This is a simple desktop application built with Java Swing to manage student records, course enrollments, and grades. The primary goal was to demonstrate event-driven programming and dynamic interface updates without relying on a database (all data is stored in memory using static Java lists).

Key Features 📝

Modular Interface: Uses a JTabbedPane to separate functions into three clean sections: Student Records, Course Enrollment, and Grade Management.

Dynamic Updates: When you add a new student or assign a grade, the relevant tables and selection menus (JComboBoxes) update automatically.

Student CRUD: Allows adding new students and updating details for existing students via pop-up forms (JOptionPane).

Grade Tracking: Administrators can select a student, see their enrolled courses, and assign/update grades through the UI.

Simple Error Handling: Includes checks to prevent blank input and stops duplicate student enrollments in the same course.

How to Run This Application 🚀

Since this is a classic Java Swing application, you need a desktop environment (like Windows, macOS, or Linux) to see the GUI window. It won't work in basic online terminals due to the HeadlessException.

Save the Code: Save the entire file as StudentManagerGUI.java.

Compile: Open your terminal or command prompt and compile the file.

javac StudentManagerGUI.java


Run: Execute the compiled class file.

java StudentManagerGUI


A separate desktop window will open with the Student Management interface.
