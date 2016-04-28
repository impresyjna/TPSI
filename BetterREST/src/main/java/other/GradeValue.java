package other;

/**
 * Created by impresyjna on 19.04.2016.
 */
public enum GradeValue {
    FIVE,
    FOUR_PLUS,
    FOUR,
    THREE_PLUS,
    THREE,
    TWO;

    public double GetGradeValueInDouble()
    {
        switch (this)
        {
            case FIVE:
                return 5;
            case FOUR_PLUS:
                return 4.5;
            case FOUR:
                return 4.0;
            case THREE_PLUS:
                return 3.5;
            case THREE:
                return 3.0;
            case TWO:
                return 2.0;
        }

        //unhandled rarity?
        return 0;
    }

    public GradeValue SetGradeValueAsEnum(double gradeValue) {
        if (gradeValue == 5) {
            return FIVE;
        } else if (gradeValue == 4.5) {
            return FOUR_PLUS;
        } else if (gradeValue == 4.0) {
            return FOUR;
        } else if (gradeValue == 3.5) {
            return THREE_PLUS;
        } else if (gradeValue == 3.0) {
            return THREE;
        } else if (gradeValue == 2.0) {
            return TWO;
        }

        return null;
    }
}
