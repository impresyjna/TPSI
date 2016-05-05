package model;

import org.bson.types.ObjectId;
import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;
import other.GradeValue;
import other.ObjectIdJaxbAdapter;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
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
            @InjectLink(resource = rest.GradeResource.class, rel = "parent_course", method = "getAllGrades", style = InjectLink.Style.ABSOLUTE,
                    bindings = {@Binding(name = "courseId", value = "${instance.courseId}"),
                            @Binding(name = "index", value = "${instance.student.index}")}),
            @InjectLink(resource = rest.CourseResource.class, rel = "self_course", method = "getOneGrade", style = InjectLink.Style.ABSOLUTE,
                    bindings = {@Binding(name = "courseId", value = "${instance.courseId}"),
                            @Binding(name = "index", value = "${instance.student.index}"),
                            @Binding(name = "gradeId", value = "${instance.id}")})
    })
    @XmlElement(name = "link_grades")
    @XmlElementWrapper(name = "links_grades")
    @XmlJavaTypeAdapter(Link.JaxbAdapter.class)
    List<Link> links;
//    @XmlJavaTypeAdapter(ObjectIdJaxbAdapter.class)
//    @Id
//    ObjectId id;
    @NotNull
    double gradeValue;
    Date date = new Date();
    @Reference
    Student student;
    int courseId;

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

    public Grade() {
    }

    public Grade(double gradeValue, Student student, int id) {
        this.gradeValue = gradeValue;
        this.student = student;
        this.date = new Date();
        //this.id = new ObjectId();
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

//    @XmlTransient
//    public ObjectId getId() {
//        return id;
//    }
//
//    public void setId(ObjectId id) {
//        this.id = id;
//    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }
}
