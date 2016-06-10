package model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Id;

/**
 * Created by impresyjna on 10.06.2016.
 */
public class StudentIterator {
    @Id
    ObjectId id;
    int value;

    public StudentIterator() {
    }

    public StudentIterator(int value) {
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
