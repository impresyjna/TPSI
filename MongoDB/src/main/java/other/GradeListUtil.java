package other;

import model.Grade;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by impresyjna on 16.06.2016.
 */
public abstract class GradeListUtil {
    public static List<Grade> getGradesByGradeValue(List<Grade> grades, double gradeValue, int direction) {
        List<Grade> result = grades;
        switch(direction) {
            case -1:
                result = result.stream().filter(grade -> grade.getGradeValue() < gradeValue).collect(Collectors.toList());
                break;
            case 0:
                result = result.stream().filter(grade -> grade.getGradeValue() == gradeValue).collect(Collectors.toList());
                break;
            case 1:
                result = result.stream().filter(grade -> grade.getGradeValue() > gradeValue).collect(Collectors.toList());
                break;
            default:
                break;
        }

        return result;
    }

    public static List<Grade> getGradesByDate(List<Grade> grades, Date date) {
        return grades.stream().filter(grade -> grade.getDate().equals(date)).collect(Collectors.toList());
    }
}
