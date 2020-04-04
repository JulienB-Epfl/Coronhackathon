package coronhackathon.backend.entity;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Challenge {
    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long Id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String description;

    private String logo;     //the link to the logo

    /* ---- Relations ---- */

    @ManyToMany(mappedBy = "has_completed")
    Set<User> has_completed;

    @ManyToMany
    @JoinTable(
            name = "is_A",
            joinColumns = @JoinColumn(name = "challenge_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    Set<Tag> is_A;

    /* ----Getters and Setters---- */

    public long getId() {
        return Id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getLogo() {
        return logo;
    }

    public void setId(long id) {
        Id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }
}