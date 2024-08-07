/********************************************************************************************************2*4*w*
 * File:  SecurityUser.java Course materials CST 8277
 *
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * @author (original) Mike Norman
 * 
 * Updated by:  Group 45
 *  Timothy Norman
 *  Camryn Collis
 *  Fereshteh Rohani
 * 
 */
package acmecollege.entity;

import static acmecollege.entity.SecurityUser.SECURITY_USER_BY_NAME_QUERY;
import acmecollege.rest.serializer.SecurityRoleSerializer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.Serializable;
import java.security.Principal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@SuppressWarnings("unused")

/**
 * User class used for (JSR-375) Java EE Security authorization/authentication
 */

//TODO (DONE TN) - Make this into JPA entity and add all the necessary annotations
@Entity
@Table(name = "security_user")
@AttributeOverride(name = "id", column = @Column(name = "user_id"))
@NamedQuery(name = SecurityUser.SECURITY_USER_BY_NAME_QUERY, query = "SELECT su FROM SecurityUser su LEFT JOIN FETCH su.roles WHERE su.username = :username")
@NamedQuery(name = SecurityUser.IS_DUPLICATE_SECURITY_USER, query = "SELECT COUNT(su) FROM SecurityUser su WHERE su.username = :username")
@NamedQuery(name = SecurityUser.SECURITY_USER_BY_STUDENT_ID_QUERY, query = "SELECT su FROM SecurityUser su WHERE su.student.id = :studentId")
public class SecurityUser implements Serializable, Principal {
    /** Explicit set serialVersionUID */
    private static final long serialVersionUID = 1L;
    
    public static final String SECURITY_USER_BY_NAME_QUERY = "SecurityUser.userByName";
    public static final String IS_DUPLICATE_SECURITY_USER = "SecurityUser.isDuplicate";
    public static final String SECURITY_USER_BY_STUDENT_ID_QUERY = "SecurityUser.byStudentId";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected int id;
    
    @Column(name = "user_name")
    protected String username;
    
    @Column(name = "password_hash")
    protected String pwHash;
    
    @OneToOne(optional = true)
    @JoinColumn(name = "id", referencedColumnName = "id")
    protected Student student;
    
    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinTable(name = "user_has_role",
            joinColumns = @JoinColumn(referencedColumnName = "user_id", name = "user_id"), // SecurityUser entity
            inverseJoinColumns = @JoinColumn(referencedColumnName = "role_id", name = "role_id")) // SecurityRole entity
    protected Set<SecurityRole> roles = new HashSet<SecurityRole>();

    public SecurityUser() {
        super();
    }

    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPwHash() {
        return pwHash;
    }
    
    public void setPwHash(String pwHash) {
        this.pwHash = pwHash;
    }

    // TODO SU01 (DONE TN) - Setup custom JSON serializer -> see Lab 4. 
    @JsonSerialize(using = SecurityRoleSerializer.class)
    public Set<SecurityRole> getRoles() {
        return roles;
    }
    
    public void setRoles(Set<SecurityRole> roles) {
        this.roles = roles;
    }

    public Student getStudent() {
        return student;
    }
    
    public void setStudent(Student student) {
        this.student = student;
    }

    // Principal
    @Override
    public String getName() {
        return getUsername();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        // Only include member variables that really contribute to an object's identity
        // i.e. if variables like version/updated/name/etc. change throughout an object's lifecycle,
        // they shouldn't be part of the hashCode calculation
        return prime * result + Objects.hash(getId());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof SecurityUser otherSecurityUser) {
            // See comment (above) in hashCode():  Compare using only member variables that are
            // truly part of an object's identity
            return Objects.equals(this.getId(), otherSecurityUser.getId());
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SecurityUser [id = ").append(id).append(", username = ").append(username).append("]");
        return builder.toString();
    }
    
}
