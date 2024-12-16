package tn.cot.smarthydro.entities;

import jakarta.nosql.Column;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;
import tn.cot.smarthydro.enums.Role;
import tn.cot.smarthydro.utils.Argon2Utils;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;


@Entity
public class User implements Serializable {
    @Id
    @Column("id")
    private String id;
    @Column("email")
    private String email;
    @Column("password")
    private String password;
    @Column("creationDate")
    private String  creationDate;
    @Column("role")
    private Set<Role> roles ;
    @Column("isAccountActivated")
    private boolean isAccountActivated;

    public String getId() {return id;}

    public String getEmail() {return email;}

    public String getPassword() {return password;}

    public String getCreationDate() {return creationDate;}

    public Set<Role> getRoles() {return roles;}

    public boolean getAccountActivated() {return isAccountActivated;}

    public void setEmail(String email) {this.email = email;}

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCreationDate(String creationDate) {this.creationDate = creationDate;}

    public void setRoles(Set<Role> roles) {this.roles = roles;}

    public void setAccountActivated(boolean accountActivated) {this.isAccountActivated = accountActivated;}

    public User() {
        this.id = UUID.randomUUID().toString();
        this.isAccountActivated = false;
    }

    public User(String id, String email, String password, String creationDate, Set<Role> roles, boolean isAccountActivated) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.creationDate = creationDate;
        this.roles = roles;
        this.isAccountActivated = isAccountActivated;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                "email='" + email + '\'' +
                "password='" + password + '\'' +
                "creationDate=" + creationDate +
                "roles=" + roles +
                "accoutActivated=" + isAccountActivated +
                '}';
    }

    public void hashPassword(String password, Argon2Utils argonUtility) {
        this.password = argonUtility.hash(password.toCharArray());
    }
}
