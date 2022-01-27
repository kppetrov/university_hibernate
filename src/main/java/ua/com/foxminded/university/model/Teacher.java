package ua.com.foxminded.university.model;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "teachers")
public class Teacher extends AbstractPerson {

    public Teacher() {
        super();
    }

    public Teacher(int id, String firstName, String lastName, Gender gender, LocalDate birthdate) {
        super(id, firstName, lastName, gender, birthdate);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!super.equals(obj)) {
            return false;
        } else if (getClass() != obj.getClass()) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public String toString() {
        return "Teacher [id=" + getId() + ", firstName=" + getFirstName() + ", lastName=" + getLastName() + ", gender="
                + getGender() + ", birthdate=" + getBirthdate() + "]";
    }
}
