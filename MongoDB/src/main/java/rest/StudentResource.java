package rest;

import model.Course;
import model.Grade;
import model.Student;
import model.StudentIterator;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import other.DbSingleton;
import other.GradeListUtil;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Path("/students")
public class StudentResource {
    private DbSingleton dbSingleton = DbSingleton.getInstance();

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List<Student> getAllStudents(@QueryParam("firstName") String firstName,
                                     @QueryParam("lastName") String lastName,
                                     @DefaultValue("0") @QueryParam("direction") int direction,
                                     @QueryParam("indexQuery") Long index,
                                     @QueryParam("dateOfBirthQuery") Date date, @QueryParam("nameQuery") String nameQuery,
                                     @QueryParam("surnameQuery") String surnameQuery) {
        Datastore datastore = dbSingleton.getDs();

        List<Student> students = datastore.find(Student.class).asList();
        if(index != null) {
            students = students.stream().filter(student -> student.getIndex() == index).collect(Collectors.toList());
        }
        if(firstName != null) {
            students = students.stream().filter(student -> student.getName().equals(firstName)).
                    collect(Collectors.toList());
        }
        if(lastName != null) {
            students = students.stream().filter(student -> student.getSurname().equals(lastName)).
                    collect(Collectors.toList());
        }

        if(date != null) {
            switch(direction) {
                case -1:
                    students = students.stream().filter(student -> student.getDateOfBirth().before(date))
                            .collect(Collectors.toList());
                    break;
                case 0:
                    students = students.stream().filter(student -> student.getDateOfBirth().equals(date))
                            .collect(Collectors.toList());
                    break;
                case 1:
                    students = students.stream().filter(student -> student.getDateOfBirth().after(date))
                            .collect(Collectors.toList());
                    break;
                default:
                    break;
            }
        }

        if(nameQuery != null && nameQuery.length() > 0) {
            students = students.stream().filter(student -> student.getName().toLowerCase()
                    .contains(nameQuery.toLowerCase())).collect(Collectors.toList());
        }

        if(surnameQuery != null && surnameQuery.length() > 0) {
            students = students.stream().filter(student -> student.getSurname().toLowerCase()
                    .contains(surnameQuery.toLowerCase())).collect(Collectors.toList());
        }

        return students;
    }

    @GET
    @Path("/{index}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getOneStudent(@PathParam("index") final long index) {
        Student returnedStudent = null;
        Query q = dbSingleton.getDs().createQuery(Student.class).filter("index =", index);
        returnedStudent = (Student) q.get();
        if (returnedStudent == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("404 Not found").build());
        }

        return Response
                .ok(returnedStudent)
                .build();
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createStudent(@Valid Student student, @Context UriInfo uriInfo) {
        StudentIterator studentIterator = dbSingleton.getDs().createQuery(StudentIterator.class).get();
        student.setIndex(studentIterator.getValue()+1);
        dbSingleton.getDs().save(student);
        Query<StudentIterator> qPom = dbSingleton.getDs().createQuery(StudentIterator.class);
        UpdateOperations<StudentIterator> pomOps;
        pomOps = dbSingleton.getDs().createUpdateOperations(StudentIterator.class).set("value", studentIterator.getValue() + 1);
        dbSingleton.getDs().update(qPom, pomOps);
        URI uri = uriInfo.getAbsolutePathBuilder().path(Long.toString(student.getIndex())).build();
        return Response.created(uri).entity(student).build();
    }

    @PUT
    @Path("/{index}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updateStudent(@PathParam("index") final long index, @Valid Student student) {
        Query query = dbSingleton.getDs().createQuery(Student.class).filter("index =", index);
        UpdateOperations<Student> ops;
        ops = dbSingleton.getDs().createUpdateOperations(Student.class).set("index", student.getIndex());
        dbSingleton.getDs().update(query, ops);
        ops = dbSingleton.getDs().createUpdateOperations(Student.class).set("name", student.getName());
        dbSingleton.getDs().update(query, ops);
        ops = dbSingleton.getDs().createUpdateOperations(Student.class).set("surname", student.getSurname());
        dbSingleton.getDs().update(query, ops);
        ops = dbSingleton.getDs().createUpdateOperations(Student.class).set("dateOfBirth", student.getDateOfBirth());
        dbSingleton.getDs().update(query, ops);

        return Response.status(Response.Status.OK).entity(student).build();
    }

    @Path("/{index}")
    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteStudent(@PathParam("index") final long index) {
        Student student = dbSingleton.getDs().find(Student.class).field("index").equal(index).get();

        if(student == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Not found").build());
        }

        List<Course> courses = dbSingleton.getDs().find(Course.class).asList();
        for(Course course: courses) {
            course.getGrades().removeAll(course.getStudentGradesList(index));
            dbSingleton.getDs().save(course);
        }
        dbSingleton.getDs().delete(student);

        return Response.ok("Student with index " + index + " removed").build();
    }


    @Path("/{index}/grades")
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List<Grade> getGrades(@PathParam("index") final long index,
                                 @QueryParam("gradeValueQuery") Double gradeValue, @QueryParam("dateQuery") Date date) {
        Datastore datastore = dbSingleton.getDs();
        List<Course> courses = datastore.find(Course.class).asList();

        List<Grade> grades = courses.stream().
                flatMap(course -> course.getStudentGradesList(index).stream()).collect(Collectors.toList());

        if(gradeValue != null) {
            grades = GradeListUtil.getGradesByGradeValue(grades, gradeValue, 0);
        }

        if(date != null) {
            grades = GradeListUtil.getGradesByDate(grades, date);
        }

        return grades;
    }
}
