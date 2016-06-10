package model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Reference;
import rest.CourseResource;
import rest.GradeResource;
import rest.StudentResource;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;
import java.util.List;

/**
 * Created by impresyjna on 19.04.2016.
 */
@XmlRootElement
@Embedded
public class Grade {
    @InjectLinks({
            @InjectLink(resource = GradeResource.class, method = "getOneGrade", style = InjectLink.Style.ABSOLUTE,
                    bindings = {
                            @Binding(name = "index", value = "${instance.student.index}"),
                            @Binding(name = "courseId", value = "${instance.courseId}"),
                            @Binding(name = "gradeId", value = "${instance.gradeId}")
                    }, rel = "self"),
            @InjectLink(resource = CourseResource.class, method = "getCourse", style = InjectLink.Style.ABSOLUTE,
                    bindings = @Binding(name = "courseId", value = "${instance.courseId}"), rel = "course"),
            @InjectLink(resource = StudentResource.class, method = "getStudent", style = InjectLink.Style.ABSOLUTE,
                    bindings = @Binding(name = "index", value = "${instance.student.index}"), rel = "student")
    })
    @XmlElement(name="link")
    @XmlElementWrapper(name = "links")
    @XmlJavaTypeAdapter(Link.JaxbAdapter.class)
    private List<Link> links;

    @NotNull
    double gradeValue;
    @NotNull
    @JsonFormat(shape=JsonFormat.Shape.STRING,
            pattern="yyyy-MM-dd", timezone="CET")
    Date date = new Date();
    @Reference
    Student student;
    String courseId;
    int gradeId;

    private static final double[] noteScale = new double[]{2.0, 3.0, 3.5, 4.0, 4.5, 5.0};

    public boolean validateNote() {
        boolean result = false;

        for (double element : noteScale) {
            if (this.gradeValue == element) {
                result = true;
            }
        }

        return result;
    }

    public static boolean validateNoteWithValue(double gradeValue) {
        boolean result = false;

        for (double element : noteScale) {
            if (gradeValue == element) {
                result = true;
            }
        }

        return result;
    }

    public Grade() {
    }

    public Grade(double gradeValue, Student student, int gradeId) {
        this.gradeValue = gradeValue;
        this.student = student;
        this.date = new Date();
        this.gradeId = gradeId;
    }

    public double getGradeValue() {
        return gradeValue;
    }

    public void setGradeValue(double gradeValue) {
        this.gradeValue = gradeValue;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public int getGradeId() {
        return gradeId;
    }

    public void setGradeId(int gradeId) {
        this.gradeId = gradeId;
    }
}
