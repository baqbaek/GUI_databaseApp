CREATE TABLE Student (
    id NUMBER PRIMARY KEY,
    name VARCHAR2(50),
    age NUMBER,
    year_of_study_id NUMBER,
    FOREIGN KEY (year_of_study_id) REFERENCES YearOfStudy(id)
);

CREATE TABLE Course (
    id NUMBER PRIMARY KEY,
    title VARCHAR2(100),
    credits NUMBER
);

CREATE TABLE Enrollment (
    student_id NUMBER,
    course_id NUMBER,
    PRIMARY KEY (student_id, course_id),
    FOREIGN KEY (student_id) REFERENCES Student(id),
    FOREIGN KEY (course_id) REFERENCES Course(id)
);

CREATE TABLE YearOfStudy (
    id NUMBER PRIMARY KEY,
    year NUMBER
);

-- Tabela YearOfStudy
INSERT INTO YearOfStudy (id, year) VALUES (1, 1);
INSERT INTO YearOfStudy (id, year) VALUES (2, 2);
INSERT INTO YearOfStudy (id, year) VALUES (3, 3);

-- Tabela Student
INSERT INTO Student (id, name, age, year_of_study_id) VALUES (1, 'Bartek', 24, 3);
INSERT INTO Student (id, name, age, year_of_study_id) VALUES (2, 'Rafal', 29, 3);
INSERT INTO Student (id, name, age, year_of_study_id) VALUES (3, 'Julia', 22, 2);

-- Tabela Course
INSERT INTO Course (id, title, credits) VALUES (1, 'Systemy baz danych', 4);
INSERT INTO Course (id, title, credits) VALUES (2, 'Struktury danych', 3);
INSERT INTO Course (id, title, credits) VALUES (3, 'MongoDB', 4);

-- Tabela Enrollment
INSERT INTO Enrollment (student_id, course_id) VALUES (1, 1);
INSERT INTO Enrollment (student_id, course_id) VALUES (1, 2);
INSERT INTO Enrollment (student_id, course_id) VALUES (2, 2);
INSERT INTO Enrollment (student_id, course_id) VALUES (2, 3);
INSERT INTO Enrollment (student_id, course_id) VALUES (3, 1);
INSERT INTO Enrollment (student_id, course_id) VALUES (3, 3);
