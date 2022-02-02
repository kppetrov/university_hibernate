package ua.com.foxminded.university.web.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class CourseModelWithGroups {
    private int id;
    @NotBlank(message="{validation.name.NotBlank.message}")
    @Size(min=3, max=25, message="{validation.name.Size.message}")
    private String name;
    private Set<GroupModel> groups = new HashSet<>();    
    
    public CourseModelWithGroups() {
        
    }
    
    public CourseModelWithGroups(int id, String name, Set<GroupModel> groups) {
        this.id = id;
        this.name = name;
        this.groups = groups;
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

    public Set<GroupModel> getGroups() {
        return groups;
    }

    public void setGroups(Set<GroupModel> groups) {
        this.groups = groups;
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
        CourseModelWithGroups other = (CourseModelWithGroups) obj;
        return id == other.id && Objects.equals(name, other.name);
    }    
}
