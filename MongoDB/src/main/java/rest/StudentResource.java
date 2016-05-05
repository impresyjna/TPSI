package rest;

import model.Student;
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
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List<Student> getAllStudents() {
        System.out.println("Students index");
        List<Student> students = Model.getInstance().getStudents();
        return students;
    }

    @GET
    @Path("/{index}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getOneStudent(@PathParam("index") final long index){
        Student returnedStudent = null;
        for (Student student: Model.getInstance().getStudents()) {
            if(student.getIndex() == index){
                returnedStudent = student;
                break;
            }
        }

        if(returnedStudent == null) {
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
        Model.getInstance().getStudents().add(student);

        URI uri = uriInfo.getAbsolutePathBuilder().path(Long.toString(student.getIndex())).build();
        return Response.created(uri).entity(student).build();
    }

    @PUT
    @Path("/{index}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updateStudent(@PathParam("index") final long index, @Valid Student student) {
        for (int i=0; i<Model.getInstance().getStudents().size(); i++) {
            Student tempStudent = Model.getInstance().getStudents().get(i);
            if(tempStudent.getIndex() == student.getIndex()){
                Model.getInstance().getStudents().set(i, student);
            }
        }

        return Response.status(Response.Status.OK).entity(student).build();
    }

    @Path("/{index}")
    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteStudent(@PathParam("index") final long index) {
        Response response = null;
        for (Student student : Model.getInstance().getStudents()) {
            if (student.getIndex() == index) {
                Model.getInstance().getStudents().remove(student);
                response = Response.ok("Student  " + index + " removed").build();
                break;
            }
        }

        if (response == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("404 Not found").build());
        }

        return response;
    }
}
