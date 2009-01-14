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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301
 * USA
 * 
 * http://www.gnu.org/licenses/gpl.txt
 * ****************************************************************
 */

/* $Id$ */

package org.lamsfoundation.lams.tool.mdchat.dto;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.lamsfoundation.lams.tool.mdchat.model.MdlChatSession;
import org.lamsfoundation.lams.tool.mdchat.model.MdlChatUser;

public class MdlChatSessionDTO implements Comparable {

    Long sessionID;

    Long extSessionID;

    String sessionName;

    Set<MdlChatUserDTO> userDTOs = new TreeSet<MdlChatUserDTO>();

    int numberOfLearners;

    int numberOfFinishedLearners;

    String runTimeUrl;

    public MdlChatSessionDTO(MdlChatSession session) {
	this.sessionID = session.getSessionId();
	this.sessionName = session.getSessionName();
	this.extSessionID = session.getExtSessionId();
	numberOfFinishedLearners = 0;

	/*
	for (Iterator iterator = session.getMdlChatUsers().iterator(); iterator.hasNext();) {
		MdlChatUser user = (MdlChatUser) iterator.next();
		MdlChatUserDTO userDTO = new MdlChatUserDTO(user);
		userDTOs.add(userDTO);
	}
	numberOfLearners = userDTOs.size();	
	*/
    }

    public MdlChatSessionDTO() {
    }

    public Long getSessionID() {
	return sessionID;
    }

    public void setSessionID(Long sessionID) {
	this.sessionID = sessionID;
    }

    public String getSessionName() {
	return sessionName;
    }

    public void setSessionName(String sessionName) {
	this.sessionName = sessionName;
    }

    public int compareTo(Object o) {
	int returnValue;
	MdlChatSessionDTO toSession = (MdlChatSessionDTO) o;
	returnValue = this.sessionName.compareTo(toSession.sessionName);
	if (returnValue == 0) {
	    returnValue = this.sessionID.compareTo(toSession.sessionID);
	}
	return returnValue;
    }

    public Set<MdlChatUserDTO> getUserDTOs() {
	return userDTOs;
    }

    public void setUserDTOs(Set<MdlChatUserDTO> mdlChatUsers) {
	this.userDTOs = mdlChatUsers;
    }

    public int getNumberOfLearners() {
	return numberOfLearners;
    }

    public void setNumberOfLearners(int numberOfLearners) {
	this.numberOfLearners = numberOfLearners;
    }

    public int getNumberOfFinishedLearners() {
	return numberOfFinishedLearners;
    }

    public void setNumberOfFinishedLearners(int numberOfFinishedLearners) {
	this.numberOfFinishedLearners = numberOfFinishedLearners;
    }

    public Long getExtSessionID() {
	return extSessionID;
    }

    public void setExtSessionID(Long extSessionID) {
	this.extSessionID = extSessionID;
    }

    public String getRunTimeUrl() {
	return runTimeUrl;
    }

    public void setRunTimeUrl(String runTimeUrl) {
	this.runTimeUrl = runTimeUrl;
    }
}
