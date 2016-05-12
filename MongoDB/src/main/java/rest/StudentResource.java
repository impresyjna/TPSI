package rest;

import com.mongodb.WriteResult;
import model.Student;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import other.DbSingleton;
import other.Model;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;

@Path("/students")
public class StudentResource {
    private DbSingleton dbSingleton = DbSingleton.getInstance();

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List<Student> getAllStudents() {
        System.out.println("Students index");
        Query<Student> q = dbSingleton.getDs().createQuery(Student.class);
        List<Student> students = q.asList();
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
        dbSingleton.getDs().save(student);

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
        ops = dbSingleton.getDs().createUpdateOperations(Student.class).set("birthDate", student.getBirthDate());
        dbSingleton.getDs().update(query, ops);

        return Response.status(Response.Status.OK).entity(student).build();
    }

    @Path("/{index}")
    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteStudent(@PathParam("index") final long index) {
        Response response = null;
        WriteResult wr = dbSingleton.getDs().delete(dbSingleton.getDs().createQuery(Student.class).filter("index =", index));
        if (wr.getN() == 1) {
            response = Response.ok("Student  " + index + " removed").build();
        }

        if (response == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("404 Not found").build());
        }

        return response;
    }
}
