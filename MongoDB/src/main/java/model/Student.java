package model;

import org.bson.types.ObjectId;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.List;

/**
 * Created by impresyjna on 19.04.2016.
 */
@XmlRootElement
@Entity
public class Student {
    @InjectLinks({
            @InjectLink(resource = rest.StudentResource.class, rel = "parent", method = "getAllStudents",style = InjectLink.Style.ABSOLUTE),
            @InjectLink(resource = rest.StudentResource.class, rel = "self", method = "getOneStudent", style = InjectLink.Style.ABSOLUTE)
    })
    @XmlElement(name = "links_students")
    @XmlElementWrapper(name = "links_students")
    @XmlJavaTypeAdapter(Link.JaxbAdapter.class)
    List<Link> links;

    @XmlTransient
    @Id ObjectId id;

    @NotNull
    @Indexed(unique = true, name="index")
    private long index;

    @NotNull
    String name;
    @NotNull
    String surname;
    @NotNull
    String birthDate;

    public Student() {
    }

    public Student(long index, String name, String surname, String birthDate) {

        this.index = index;
        this.name = name;
        this.surname = surname;
        this.birthDate = birthDate;
        this.id = new ObjectId();
    }

    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }
}
