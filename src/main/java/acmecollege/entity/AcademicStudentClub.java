/********************************************************************************************************2*4*w*
 * File:  AcademicStudentClub.java Course materials CST 8277
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

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(value = "1")
public class AcademicStudentClub extends StudentClub implements Serializable {
	private static final long serialVersionUID = 1L;

	public AcademicStudentClub() {
		super(true);
	}
}
