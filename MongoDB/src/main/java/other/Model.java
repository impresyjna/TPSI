package other;

import com.mongodb.MongoClient;
import model.Course;
import model.Grade;
import model.GradeIterator;
import model.Student;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import resources.DateParamConverterProvider;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by impresyjna on 19.04.2016.
 */
public class Model {
    private static Model instance;
    private List<Student> students;
    private List<Course> courses;
    private int courseId = 0;
    private int gradeId = 0;
    private DbSingleton dbSingleton = DbSingleton.getInstance();

    public static Model getInstance() {
        if (instance == null) {
            try {
                instance = new Model();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    private Model() throws ParseException {
        students = new ArrayList<>();
        courses = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        students.add(new Student(109708, "Aaa", "Bbb", formatter.parse("1993-07-18")));
        students.add(new Student(109711, "NNN", "DDD", formatter.parse("1993-08-10")));
        students.add(new Student(109724, "EEE", "BFFFbb", formatter.parse("1993-11-22")));
        Student student4 = new Student(109742, "AAAAA", "WERR", formatter.parse("1993-01-21"));
        Student student1 = new Student(109712, "AAAAA", "WERR", formatter.parse("1993-01-11"));
        Student student2 = new Student(109709, "AAAaaaaAA", "WERRaaaa", formatter.parse("1993-01-31"));
        students.add(student4);
        students.add(student1);
        students.add(student2);


        Course course1 = new Course( "Analiza matematyczna", "Doktor AAA BBB");
        courses.add(course1);
        Course course2 = new Course("TPSI", "Doktor CCC  DDD BBB");
        courses.add(course2);
        Course course3 = new Course("PIT", "Doktor AAA GGG");
        courses.add(course3);

        Grade grade1 = new Grade(5.0, student1, 0);
        course1.getGrades().add(grade1);
        Grade grade2 = new Grade(4.5, student2, 1);
        course2.getGrades().add(grade2);
        Grade grade3 = new Grade(4.0, student4, 2);
        course3.getGrades().add(grade3);
        Grade grade4 = new Grade(3.0, student1, 3);
        course1.getGrades().add(grade4);
        Grade grade5 = new Grade(3.5, student2, 4);
        course1.getGrades().add(grade5);
        Grade grade6 = new Grade(2.0, student4, 5);
        course1.getGrades().add(grade6);

        dbSingleton.getDs().save(new GradeIterator(0));
//        dbSingleton.getDs().save(courses);
//        dbSingleton.getDs().save(students);


    }

    public List<Student> getStudents() {
        return students;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public int getCourseIndex() {
        courseId += 1;
        return courseId;
    }

    public int getGradeIndex() {
        gradeId += 1;
        return gradeId;
    }
}
