package ua.com.foxminded.university.model;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "classrooms")
public class Classroom {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    
    @NotBlank(message="{validation.name.NotBlank.message}")
    @Size(min=3, max=25, message="{validation.name.Size.message}")
    @Column(name = "name", unique = true)
    private String name;

    public Classroom() {
    }

    public Classroom(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Classroom other = (Classroom) obj;
        return id == other.id && Objects.equals(name, other.name);
    }

    @Override
    public String toString() {
        return "Classroom [id=" + id + ", name=" + name + "]";
    }
}
