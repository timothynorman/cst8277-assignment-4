/********************************************************************************************************2*4*w*
 * File:  Student.java Course materials CST 8277
 * 
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * 
 * Updated by:  Group 45
 *  Timothy Norman
 *  Camryn Collis
 *  Fereshteh Rohani
 *   
 */
package acmecollege.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.OneToOne;

/**
 * The persistent class for the student database table.
 */
@SuppressWarnings("unused")
@Entity
@Table(name = "student")
@NamedQuery(name = Student.ALL_STUDENTS_QUERY_NAME, query = "SELECT s FROM Student s LEFT JOIN FETCH s.membershipCards LEFT JOIN FETCH s.peerTutorRegistrations")
@NamedQuery(name = Student.QUERY_STUDENT_BY_ID, query = "SELECT s FROM Student s LEFT JOIN FETCH s.membershipCards LEFT JOIN FETCH s.peerTutorRegistrations where s.id = :param1")
//No need for AttributeOverride as student id column is called id as well.
public class Student extends PojoBase implements Serializable {
	private static final long serialVersionUID = 1L;

    public static final String ALL_STUDENTS_QUERY_NAME = "Student.findAll";
    public static final String QUERY_STUDENT_BY_ID = "Student.findAllByID";

    public Student() {
    	super();
    }
    
	@Basic(optional = false)
    @Column(name = "first_name", nullable = false, length = 50)
	private String firstName;

	@Basic(optional = false)
    @Column(name = "last_name", nullable = false, length = 50)
	private String lastName;

	@OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY, mappedBy = "owner")
	private Set<MembershipCard> membershipCards = new HashSet<>();

	@OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY, mappedBy = "student")
	private Set<PeerTutorRegistration> peerTutorRegistrations = new HashSet<>();
        
        @OneToOne(mappedBy = "student")
        private SecurityUser securityUser;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

    public SecurityUser getSecurityUser() {
        return securityUser;
    }

    public void setSecurityUser(SecurityUser securityUser) {
        this.securityUser = securityUser;
    }

    // Simplify JSON body, skip MembershipCards
    @JsonIgnore
    public Set<MembershipCard> getMembershipCards() {
		return membershipCards;
	}

	public void setMembershipCards(Set<MembershipCard> membershipCards) {
		this.membershipCards = membershipCards;
	}

    // Simplify JSON body, skip PeerTutorRegistrations
    @JsonIgnore
    public Set<PeerTutorRegistration> getPeerTutorRegistrations() {
		return peerTutorRegistrations;
	}

	public void setPeerTutorRegistrations(Set<PeerTutorRegistration> peerTutorRegistrations) {
		this.peerTutorRegistrations = peerTutorRegistrations;
	}

	public void setFullName(String firstName, String lastName) {
		setFirstName(firstName);
		setLastName(lastName);
	}
	
	//Inherited hashCode/equals is sufficient for this entity class

}
