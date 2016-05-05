package rest;

import model.Course;
import model.Grade;
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
import java.util.stream.Collectors;

/**
 * Created by impresyjna on 20.04.2016.
 */
@Path("/students/{index}/courses/{courseId}/grades")
public class GradeResource {

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List<Grade> getAllGrades(@PathParam("index") final long index, @PathParam("courseId") final long courseId) {
        return Model.getInstance().getCourses().get(0).getGrades().stream().filter(grade -> grade.getStudent().getIndex() == index).collect(Collectors.toList());
    }

    @GET
    @Path("/{gradeId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getOneGrade(@PathParam("index") final long index, @PathParam("courseId") final long courseId, @PathParam("gradeId") final int gradeId) {
        List<Grade> grades = Model.getInstance().getCourses().get(0).getGrades().stream().filter(grade -> grade.getStudent().getIndex() == index).collect(Collectors.toList());
        Grade returnedGrade = null;
        for (Grade grade : grades) {
//            if (grade.getId() == gradeId) {
//                returnedGrade = grade;
//                break;
//            }
        }

        if (returnedGrade == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("404 Not found").build());
        }

        return Response.ok(returnedGrade).build();
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createGrade(@PathParam("index") final long index, @PathParam("courseId") final int courseId, @Valid Grade grade, @Context UriInfo uriInfo) {
        Course choosenCourse = null;
        for (Course course : Model.getInstance().getCourses()) {
//            if (course.getId() == courseId) {
//                choosenCourse = course;
//                break;
//            }
        }
        if (grade.validateNote()) {
//            grade.setId(Model.getInstance().getGradeIndex());
//
//            if (grade.getStudent() == null) {
//                Student studentForGrade = null;
//                for (Student student : Model.getInstance().getStudents()) {
//                    if (student.getIndex() == index) {
//                        studentForGrade = student;
//                        break;
//                    }
//                }
//                grade.setStudent(studentForGrade);
//            }
//            choosenCourse.getGrades().add(grade);
//
//            URI uri = uriInfo.getAbsolutePathBuilder().path(Integer.toString(grade.getId())).build();
//            return Response.created(uri).entity(grade).build();
        }
        return Response.status(Response.Status.NOT_MODIFIED).entity(grade).build();
    }

    @PUT
    @Path("/{gradeId}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updateGrade(@PathParam("index") final long index, @PathParam("courseId") final int courseId, @Valid Grade grade, @PathParam("gradeId") final int gradeId) {
        if (grade.validateNote()) {
            Course choosenCourse = null;
            for (Course course : Model.getInstance().getCourses()) {
//                if (course.getId() == courseId) {
//                    choosenCourse = course;
//                    System.out.println(course.getName());
//                    break;
//                }
            }
            Student choosenStudent = null;
            for (Student student : Model.getInstance().getStudents()) {
                if (student.getIndex() == index) {
                    choosenStudent = student;
                    break;
                }
            }
            for (int i = 0; i < choosenCourse.getGrades().size(); i++) {
                Grade tempGrade = choosenCourse.getGrades().get(i);
//                if (tempGrade.getId() == gradeId) {
//                    if (grade.getStudent() == null) {
//                        grade.setStudent(choosenStudent);
//                    }
//                    choosenCourse.getGrades().set(i, grade);
//                }
            }

            return Response.status(Response.Status.OK).entity(grade).build();
        }
        return Response.status(Response.Status.NOT_MODIFIED).entity(grade).build();
    }

    @Path("/{gradeId}")
    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteGrade(@PathParam("index") final long index, @PathParam("courseId") final long courseId, @PathParam("gradeId") final int gradeId) {
        Response response = null;
        Course choosenCourse = null;
        for (Course course : Model.getInstance().getCourses()) {
//            if (course.getId() == courseId) {
//                choosenCourse = course;
//                break;
//            }
        }
        if (choosenCourse != null) {
            List<Grade> grades = choosenCourse.getGrades();
            for (Grade grade1 : grades) {
//                if (grade1.getId() == gradeId) {
//                    grades.remove(grade1);
//                    response = Response.ok("Grade  " + gradeId + " removed").build();
//                    break;
//                }
            }
        }

        if (response == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("404 Not found").build());
        }

        return response;
    }
}
