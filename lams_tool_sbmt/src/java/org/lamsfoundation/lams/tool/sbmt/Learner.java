/****************************************************************
 * Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)
 * =============================================================
 * License Information: http://lamsfoundation.org/licensing/lams/2.0/
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2.0 
 * as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 * 
 * http://www.gnu.org/licenses/gpl.txt
 * ****************************************************************
 */

/* $$Id$$ */	
package org.lamsfoundation.lams.tool.sbmt;

import java.io.Serializable;
import java.util.Set;

import org.apache.log4j.Logger;
/**
 * 
 * @hibernate.class table="tl_lasbmt11_session_learners"
 * @author Steve.Ni
 * 
 * @version $Revision$
 * @serial 4951104689120529660L;
 */
public class Learner implements Serializable,Cloneable{

	private static final long serialVersionUID = 4951104689120529660L;
	private static Logger log = Logger.getLogger(Learner.class);
	
	//key
    private Long learnerID;
    //lams User ID
	private Integer userID;
	private String firstName;
	private String lastName;
	private String loginName;
	private Long sessionID;
	private boolean finished;
	private Set submissionDetails;
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {
		
		Object obj = null;
		try {
			obj = super.clone();
		} catch (CloneNotSupportedException e) {
			log.error("When clone " + Learner.class + " failed");
		}
		return obj;
	}
	
	//***********************************************************
	// Get / Set methods
	//***********************************************************
	/**
	 * @hibernate.id generator-class="identity" type="java.lang.Long" column="learner_id"
	 * @return Returns the learnerID.
	 */
	public Long getLearnerID() {
		return learnerID;
	}
	/**
	 * @param learnerID The learnerID to set.
	 */
	public void setLearnerID(Long learnerID) {
		this.learnerID = learnerID;
	}

	/**
	 * @hibernate.property column="user_id" length="20"
	 * @return Returns the userID.
	 */
	public Integer getUserID() {
		return userID;
	}
	/**
	 * @param userID
	 *            The userID to set.
	 */
	public void setUserID(Integer userID) {
		this.userID = userID;
	}
	/**
	 * @hibernate.property column="finished" length="1"
	 * @return Returns the finished.
	 */
	public boolean isFinished() {
		return finished;
	}
	/**
	 * @param finished The finished to set.
	 */
	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	/**
	 * @hibernate.property column="session_id" length="20"
	 * @return Returns the sessionID.
	 */
	public Long getSessionID() {
		return sessionID;
	}
	/**
	 * @param sessionID The sessionID to set.
	 */
	public void setSessionID(Long sessionID) {
		this.sessionID = sessionID;
	}
	
	/**
	 * @hibernate.set lazy="true" inverse="true" cascade="all-delete-orphan"
	 * @hibernate.collection-key column="learner_id"
	 * @hibernate.collection-one-to-many class="org.lamsfoundation.lams.tool.sbmt.SubmissionDetails"
	 *  
	 * @return Returns the submissionDetails.
	 */
	public Set getSubmissionDetails() {
		return submissionDetails;
	}
	/**
	 * @param submissionDetails The submissionDetails to set.
	 */
	public void setSubmissionDetails(Set submissionDetails) {
		this.submissionDetails = submissionDetails;
	}
	/**
	 * @hibernate.property column="first_name" 
	 */
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @hibernate.property column="login_name" 
	 */
	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	/**
	 * @hibernate.property column="last_name" 
	 */
	public String getLastName() {
		return lastName;
	}

	public void setLastName(String secondName) {
		this.lastName = secondName;
	}
}
