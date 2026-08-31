package at.daniel.phishingprototype.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "employee")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String role;

    private String department;

    @Column(length = 1000)
    private String publicInterests;

    @Column(length = 2000)
    private String recentPost;

    private String visibilitySource;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;


    public Employee() {
    }


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPublicInterests() {
        return publicInterests;
    }

    public void setPublicInterests(String publicInterests) {
        this.publicInterests = publicInterests;
    }

    public String getRecentPost() {
        return recentPost;
    }

    public void setRecentPost(String recentPost) {
        this.recentPost = recentPost;
    }

    public String getVisibilitySource() {
        return visibilitySource;
    }

    public void setVisibilitySource(String visibilitySource) {
        this.visibilitySource = visibilitySource;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
}