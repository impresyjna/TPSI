package model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * Created by impresyjna on 06.05.2016.
 */
@Entity
public class GradeIterator {
    @Id
    ObjectId id;
    int value;

    public GradeIterator() {
    }

    public GradeIterator(int value) {
        this.id = new ObjectId();
        this.value = value;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
