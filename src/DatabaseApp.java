import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class DatabaseApp extends JFrame {

    // Stałe dla połączenia z bazą danych
    private static final String DB_URL = "jdbc:oracle:thin:@//155.158.112.45:1521/oltpstud";
    private static final String USER = "sbd3";
    private static final String PASS = "haslo2024";

    // Pola tekstowe dla danych wejściowych
    private JTextField idField;
    private JTextField nameField;
    private JTextField ageField;
    private JTextField yearOfStudyField;
    private JTextField courseIdField;
    private JTextField courseTitleField;
    private JTextField courseCreditsField;
    private JTextField enrollmentStudentIdField;
    private JTextField enrollmentCourseIdField;
    private JTextArea outputArea;

    // Obiekt połączenia z bazą danych
    private Connection conn;

    // Konstruktor klasy DatabaseApp
    public DatabaseApp() {
        initUI(); // Inicjalizacja interfejsu użytkownika
        connectToDatabase(); // Połączenie do bazy danych
    }

    // Inicjalizacja interfejsu użytkownika
    private void initUI() {
        setTitle("Student, Course, and Enrollment Database App");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Zakładka dla Student
        JPanel studentPanel = new JPanel(new GridLayout(0, 2));
        idField = new JTextField(10);
        nameField = new JTextField(20);
        ageField = new JTextField(5);
        yearOfStudyField = new JTextField(5);

        studentPanel.add(new JLabel("Student ID:"));
        studentPanel.add(idField);
        studentPanel.add(new JLabel("Name:"));
        studentPanel.add(nameField);
        studentPanel.add(new JLabel("Age:"));
        studentPanel.add(ageField);
        studentPanel.add(new JLabel("Year of Study ID:"));
        studentPanel.add(yearOfStudyField);

        JButton insertStudentButton = new JButton("Insert Student");
        JButton selectStudentButton = new JButton("Select All Students");
        JButton updateStudentButton = new JButton("Update Student");
        JButton deleteStudentButton = new JButton("Delete Student");

        insertStudentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                insertStudent(); // Dodanie nowego studenta
            }
        });

        selectStudentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectStudents(); // Wyświetlenie wszystkich studentów
            }
        });

        updateStudentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateStudent(); // Aktualizacja danych studenta
            }
        });

        deleteStudentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteStudent(); // Usunięcie studenta
            }
        });

        studentPanel.add(insertStudentButton);
        studentPanel.add(selectStudentButton);
        studentPanel.add(updateStudentButton);
        studentPanel.add(deleteStudentButton);
        tabbedPane.addTab("Student", studentPanel);

        // Zakładka dla Course
        JPanel coursePanel = new JPanel(new GridLayout(0, 2));
        courseIdField = new JTextField(10);
        courseTitleField = new JTextField(20);
        courseCreditsField = new JTextField(5);

        coursePanel.add(new JLabel("Course ID:"));
        coursePanel.add(courseIdField);
        coursePanel.add(new JLabel("Title:"));
        coursePanel.add(courseTitleField);
        coursePanel.add(new JLabel("Credits:"));
        coursePanel.add(courseCreditsField);

        JButton insertCourseButton = new JButton("Insert Course");
        JButton selectCourseButton = new JButton("Select All Courses");

        insertCourseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                insertCourse(); // Dodanie nowego kursu
            }
        });

        selectCourseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectCourses(); // Wyświetlenie wszystkich kursów
            }
        });

        coursePanel.add(insertCourseButton);
        coursePanel.add(selectCourseButton);
        tabbedPane.addTab("Course", coursePanel);

        // Zakładka dla Enrollment
        JPanel enrollmentPanel = new JPanel(new GridLayout(0, 2));
        enrollmentStudentIdField = new JTextField(10);
        enrollmentCourseIdField = new JTextField(10);

        enrollmentPanel.add(new JLabel("Enrollment Student ID:"));
        enrollmentPanel.add(enrollmentStudentIdField);
        enrollmentPanel.add(new JLabel("Enrollment Course ID:"));
        enrollmentPanel.add(enrollmentCourseIdField);

        JButton insertEnrollmentButton = new JButton("Insert Enrollment");
        JButton selectEnrollmentButton = new JButton("Select All Enrollments");

        insertEnrollmentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                insertEnrollment(); // Dodanie nowego zapisu na kurs
            }
        });

        selectEnrollmentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectEnrollments(); // Wyświetlenie wszystkich zapisów na kursy
            }
        });

        enrollmentPanel.add(insertEnrollmentButton);
        enrollmentPanel.add(selectEnrollmentButton);
        tabbedPane.addTab("Enrollment", enrollmentPanel);

        // Zakładka dla Year of Study
        JPanel yearOfStudyPanel = new JPanel(new GridLayout(0, 2));
        JTextField yearOfStudyIdField = new JTextField(10);
        JTextField yearField = new JTextField(5);

        yearOfStudyPanel.add(new JLabel("Year of Study ID:"));
        yearOfStudyPanel.add(yearOfStudyIdField);
        yearOfStudyPanel.add(new JLabel("Year:"));
        yearOfStudyPanel.add(yearField);

        JButton insertYearOfStudyButton = new JButton("Insert Year of Study");
        JButton selectYearOfStudyButton = new JButton("Select All Years of Study");

        insertYearOfStudyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                insertYearOfStudy(Integer.parseInt(yearOfStudyIdField.getText()), Integer.parseInt(yearField.getText()));
            }
        });

        selectYearOfStudyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectYearsOfStudy(); // Wyświetlenie wszystkich lat studiów
            }
        });

        yearOfStudyPanel.add(insertYearOfStudyButton);
        yearOfStudyPanel.add(selectYearOfStudyButton);
        tabbedPane.addTab("Year of Study", yearOfStudyPanel);

        // Zakładka dla wszystkich selectów
        JPanel selectPanel = new JPanel(new GridLayout(0, 1));

        JButton joinButton = new JButton("Select Students with Courses");
        JButton yearJoinButton = new JButton("Select Students with Year");
        JButton insertSampleDataButton = new JButton("Insert Sample Data");

        joinButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectStudentsWithCourses(); // Wyświetlenie studentów z kursami
            }
        });

        yearJoinButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectStudentsWithYear(); // Wyświetlenie studentów z rokiem studiów
            }
        });

        insertSampleDataButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                insertSampleData(); // Dodanie przykładowych danych
            }
        });

        selectPanel.add(joinButton);
        selectPanel.add(yearJoinButton);
        selectPanel.add(insertSampleDataButton);
        tabbedPane.addTab("Selects", selectPanel);

        // Wyświetlanie wyników
        outputArea = new JTextArea(10, 50);
        add(tabbedPane, BorderLayout.CENTER);
        add(new JScrollPane(outputArea), BorderLayout.SOUTH);
    }

    // Połączenie do bazy danych
    private void connectToDatabase() {
        try {
            // Załaduj sterownik Oracle JDBC
            Class.forName("oracle.jdbc.driver.OracleDriver");
            // Nawiąż połączenie z bazą danych
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // Pobierz informacje o bazie danych
            DatabaseMetaData metaData = conn.getMetaData();
            String dbProductName = metaData.getDatabaseProductName();
            String dbProductVersion = metaData.getDatabaseProductVersion();

            // Wykonaj zapytanie SQL aby pobrać SID
            String sid = null;
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT SYS_CONTEXT('USERENV', 'DB_NAME') AS DB_NAME FROM DUAL");
            if (rs.next()) {
                sid = rs.getString("DB_NAME");
            }

            outputArea.append("Connected to the database.\n");
            outputArea.append("Database: " + dbProductName + "\n");
            outputArea.append("Version: " + dbProductVersion + "\n");
            outputArea.append("Database Name (SID): " + sid + "\n");

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            outputArea.append("Connection failed: " + e.getMessage() + "\n");
        }
    }

    // Metody operacji na danych (dodawanie, usuwanie, aktualizacja, selekcja)

    private void insertStudent() {
        try {
            String sql = "INSERT INTO Student (id, name, age, year_of_study_id) VALUES (?, ?, ?, ?)";
            outputArea.append("Executing insert: " + sql + "\n");
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, Integer.parseInt(idField.getText()));
            pstmt.setString(2, nameField.getText());
            pstmt.setInt(3, Integer.parseInt(ageField.getText()));
            pstmt.setInt(4, Integer.parseInt(yearOfStudyField.getText()));
            pstmt.executeUpdate();
            outputArea.append("Student inserted.\n");
        } catch (SQLException e) {
            e.printStackTrace();
            outputArea.append("Insert failed: " + e.getMessage() + "\n");
        } catch (NumberFormatException e) {
            e.printStackTrace();
            outputArea.append("Invalid input: " + e.getMessage() + "\n");
        }
    }

    private void selectStudents() {
        try {
            String sql = "SELECT * FROM Student ORDER BY ID ASC";
            outputArea.append("Executing query: " + sql + "\n");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            outputArea.setText("ID\tName\tAge\tYear of Study ID\n");
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int age = rs.getInt("age");
                int yearOfStudyId = rs.getInt("year_of_study_id");
                outputArea.append(id + "\t" + name + "\t" + age + "\t" + yearOfStudyId + "\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            outputArea.append("Select failed: " + e.getMessage() + "\n");
        }
    }

    private void updateStudent() {
        try {
            String sql = "UPDATE Student SET name = ?, age = ?, year_of_study_id = ? WHERE id = ?";
            outputArea.append("Executing update: " + sql + "\n");
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, nameField.getText());
            pstmt.setInt(2, Integer.parseInt(ageField.getText()));
            pstmt.setInt(3, Integer.parseInt(yearOfStudyField.getText()));
            pstmt.setInt(4, Integer.parseInt(idField.getText()));
            pstmt.executeUpdate();
            outputArea.append("Student updated.\n");
        } catch (SQLException e) {
            e.printStackTrace();
            outputArea.append("Update failed: " + e.getMessage() + "\n");
        } catch (NumberFormatException e) {
            e.printStackTrace();
            outputArea.append("Invalid input: " + e.getMessage() + "\n");
        }
    }

    private void deleteStudent() {
        try {
            String sql = "DELETE FROM Student WHERE id = ?";
            outputArea.append("Executing delete: " + sql + "\n");
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, Integer.parseInt(idField.getText()));
            pstmt.executeUpdate();
            outputArea.append("Student deleted.\n");
        } catch (SQLException e) {
            e.printStackTrace();
            outputArea.append("Delete failed: " + e.getMessage() + "\n");
        } catch (NumberFormatException e) {
            e.printStackTrace();
            outputArea.append("Invalid input: " + e.getMessage() + "\n");
        }
    }

    private void insertCourse() {
        try {
            String sql = "INSERT INTO Course (id, title, credits) VALUES (?, ?, ?)";
            outputArea.append("Executing insert: " + sql + "\n");
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, Integer.parseInt(courseIdField.getText()));
            pstmt.setString(2, courseTitleField.getText());
            pstmt.setInt(3, Integer.parseInt(courseCreditsField.getText()));
            pstmt.executeUpdate();
            outputArea.append("Course inserted.\n");
        } catch (SQLException e) {
            e.printStackTrace();
            outputArea.append("Insert failed: " + e.getMessage() + "\n");
        } catch (NumberFormatException e) {
            e.printStackTrace();
            outputArea.append("Invalid input: " + e.getMessage() + "\n");
        }
    }

    private void selectCourses() {
        try {
            String sql = "SELECT * FROM Course ORDER BY ID ASC";
            outputArea.append("Executing query: " + sql + "\n");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            outputArea.setText("ID\tTitle\tCredits\n");
            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                int credits = rs.getInt("credits");
                outputArea.append(id + "\t" + title + "\t" + credits + "\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            outputArea.append("Select failed: " + e.getMessage() + "\n");
        }
    }

    private void insertEnrollment() {
        try {
            String sql = "INSERT INTO Enrollment (student_id, course_id) VALUES (?, ?)";
            outputArea.append("Executing insert: " + sql + "\n");
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, Integer.parseInt(enrollmentStudentIdField.getText()));
            pstmt.setInt(2, Integer.parseInt(enrollmentCourseIdField.getText()));
            pstmt.executeUpdate();
            outputArea.append("Enrollment inserted.\n");
        } catch (SQLException e) {
            e.printStackTrace();
            outputArea.append("Insert failed: " + e.getMessage() + "\n");
        } catch (NumberFormatException e) {
            e.printStackTrace();
            outputArea.append("Invalid input: " + e.getMessage() + "\n");
        }
    }

    private void selectEnrollments() {
        try {
            String sql = "SELECT * FROM Enrollment ORDER BY COURSE_ID ASC";
            outputArea.append("Executing query: " + sql + "\n");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            outputArea.setText("Student ID\tCourse ID\n");
            while (rs.next()) {
                int studentId = rs.getInt("student_id");
                int courseId = rs.getInt("course_id");
                outputArea.append(studentId + "\t" + courseId + "\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            outputArea.append("Select failed: " + e.getMessage() + "\n");
        }
    }

    private void selectStudentsWithCourses() {
        try {
            String sql = "SELECT Student.id, Student.name, Student.age, Course.title, Course.credits FROM Student " +
                    "JOIN Enrollment ON Student.id = Enrollment.student_id " +
                    "JOIN Course ON Enrollment.course_id = Course.id";
            outputArea.append("Executing query: " + sql + "\n");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            outputArea.setText("Student ID\tName\tAge\tCourse Title\tCredits\n");
            while (rs.next()) {
                int studentId = rs.getInt("id");
                String name = rs.getString("name");
                int age = rs.getInt("age");
                String title = rs.getString("title");
                int credits = rs.getInt("credits");
                outputArea.append(studentId + "\t" + name + "\t" + age + "\t" + title + "\t" + credits + "\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            outputArea.append("Select failed: " + e.getMessage() + "\n");
        }
    }

    private void selectStudentsWithYear() {
        try {
            String sql = "SELECT Student.id, Student.name, Student.age, YearOfStudy.year FROM Student " +
                    "JOIN YearOfStudy ON Student.year_of_study_id = YearOfStudy.id";
            outputArea.append("Executing query: " + sql + "\n");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            outputArea.setText("Student ID\tName\tAge\tYear of Study\n");
            while (rs.next()) {
                int studentId = rs.getInt("id");
                String name = rs.getString("name");
                int age = rs.getInt("age");
                int year = rs.getInt("year");
                outputArea.append(studentId + "\t" + name + "\t" + age + "\t" + year + "\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            outputArea.append("Select failed: " + e.getMessage() + "\n");
        }
    }

    private void insertYearOfStudy(int id, int year) {
        try {
            String sql = "INSERT INTO YearOfStudy (id, year) VALUES (?, ?)";
            outputArea.append("Executing insert: " + sql + "\n");
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.setInt(2, year);
            pstmt.executeUpdate();
            outputArea.append("Year of Study inserted.\n");
        } catch (SQLException e) {
            e.printStackTrace();
            outputArea.append("Insert failed: " + e.getMessage() + "\n");
        } catch (NumberFormatException e) {
            e.printStackTrace();
            outputArea.append("Invalid input: " + e.getMessage() + "\n");
        }
    }

    private void selectYearsOfStudy() {
        try {
            String sql = "SELECT * FROM YearOfStudy ORDER BY ID ASC";
            outputArea.append("Executing query: " + sql + "\n");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            outputArea.setText("ID\tYear\n");
            while (rs.next()) {
                int id = rs.getInt("id");
                int year = rs.getInt("year");
                outputArea.append(id + "\t" + year + "\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            outputArea.append("Select failed: " + e.getMessage() + "\n");
        }
    }

    private void insertSampleData() {
        try {
            // Insert Year of Study
            insertYearOfStudy(1, 1);
            insertYearOfStudy(2, 2);
            insertYearOfStudy(3, 3);

            // Insert Students
            insertStudentData(1, "Bartek", 24, 3);
            insertStudentData(2, "Rafal", 29, 3);
            insertStudentData(3, "Julia", 22, 2);

            // Insert Courses
            insertCourseData(1, "Systemy baz danych", 4);
            insertCourseData(2, "Struktury danych", 3);
            insertCourseData(3, "MongoDB", 4);

            // Insert Enrollments
            insertEnrollmentData(1, 1);
            insertEnrollmentData(1, 2);
            insertEnrollmentData(2, 2);
            insertEnrollmentData(2, 3);
            insertEnrollmentData(3, 1);
            insertEnrollmentData(3, 3);

            outputArea.append("Sample data inserted.\n");
        } catch (SQLException e) {
            e.printStackTrace();
            outputArea.append("Insert sample data failed: " + e.getMessage() + "\n");
        }
    }

    private void insertStudentData(int id, String name, int age, int yearOfStudyId) throws SQLException {
        String sql = "INSERT INTO Student (id, name, age, year_of_study_id) VALUES (?, ?, ?, ?)";
        outputArea.append("Inserting student: " + id + ", " + name + ", " + age + ", " + yearOfStudyId + "\n");
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, id);
        pstmt.setString(2, name);
        pstmt.setInt(3, age);
        pstmt.setInt(4, yearOfStudyId);
        pstmt.executeUpdate();
    }

    private void insertCourseData(int id, String title, int credits) throws SQLException {
        String sql = "INSERT INTO Course (id, title, credits) VALUES (?, ?, ?)";
        outputArea.append("Inserting course: " + id + ", " + title + ", " + credits + "\n");
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, id);
        pstmt.setString(2, title);
        pstmt.setInt(3, credits);
        pstmt.executeUpdate();
    }

    private void insertEnrollmentData(int studentId, int courseId) throws SQLException {
        String sql = "INSERT INTO Enrollment (student_id, course_id) VALUES (?, ?)";
        outputArea.append("Inserting enrollment: student " + studentId + ", course " + courseId + "\n");
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, studentId);
        pstmt.setInt(2, courseId);
        pstmt.executeUpdate();
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                DatabaseApp app = new DatabaseApp();
                app.setVisible(true);
            }
        });
    }
}
