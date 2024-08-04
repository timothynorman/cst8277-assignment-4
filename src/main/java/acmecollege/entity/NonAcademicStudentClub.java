/********************************************************************************************************2*4*w*
 * File:  NonAcademicStudentClub.java Course materials CST 8277
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
@DiscriminatorValue("0")
public class NonAcademicStudentClub extends StudentClub implements Serializable {
	private static final long serialVersionUID = 1L;

	public NonAcademicStudentClub() {
		super(false);

	}
}