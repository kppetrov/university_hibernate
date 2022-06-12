package ua.com.foxminded.university.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.JoinColumn;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "courses")
@NamedQuery(name = Course.FIND_COURSE_BY_ID_WITH_GROUPS, 
            query = "select distinct c from Course c "
                    + "left join fetch c.groups g " 
                    + "where c.id = :id")
public class Course {    
    public static final String FIND_COURSE_BY_ID_WITH_GROUPS = "Course.findByIdWithGroups";  
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "name")
    private String name;
    
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "course", fetch = FetchType.LAZY)
    private Set<Lesson> lessons = new HashSet<>();
    
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "course_group",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id"))
    private Set<Group> groups = new HashSet<>();
    
    public boolean addGroup(Group group) {
        return groups.add(group);
    }

    public boolean removeGroup(Group group) {
        return groups.remove(group);
    }
    
    public boolean addLesson(Lesson lesson) {
        return lessons.add(lesson);        
    }

    public Course(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
