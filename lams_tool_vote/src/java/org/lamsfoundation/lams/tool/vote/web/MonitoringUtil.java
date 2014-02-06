/***************************************************************************
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
 * ***********************************************************************/

package org.lamsfoundation.lams.tool.vote.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.lamsfoundation.lams.notebook.model.NotebookEntry;
import org.lamsfoundation.lams.notebook.service.CoreNotebookConstants;
import org.lamsfoundation.lams.tool.vote.VoteAllGroupsDTO;
import org.lamsfoundation.lams.tool.vote.SessionDTO;
import org.lamsfoundation.lams.tool.vote.VoteAppConstants;
import org.lamsfoundation.lams.tool.vote.VoteComparator;
import org.lamsfoundation.lams.tool.vote.VoteGeneralLearnerFlowDTO;
import org.lamsfoundation.lams.tool.vote.VoteGeneralMonitoringDTO;
import org.lamsfoundation.lams.tool.vote.VoteMonitoredAnswersDTO;
import org.lamsfoundation.lams.tool.vote.VoteMonitoredUserDTO;
import org.lamsfoundation.lams.tool.vote.VoteStatsDTO;
import org.lamsfoundation.lams.tool.vote.VoteStringComparator;
import org.lamsfoundation.lams.tool.vote.VoteUtils;
import org.lamsfoundation.lams.tool.vote.pojos.VoteContent;
import org.lamsfoundation.lams.tool.vote.pojos.VoteQueContent;
import org.lamsfoundation.lams.tool.vote.pojos.VoteQueUsr;
import org.lamsfoundation.lams.tool.vote.pojos.VoteSession;
import org.lamsfoundation.lams.tool.vote.pojos.VoteUsrAttempt;
import org.lamsfoundation.lams.tool.vote.service.IVoteService;
import org.lamsfoundation.lams.usermanagement.dto.UserDTO;
import org.lamsfoundation.lams.util.DateUtil;
import org.lamsfoundation.lams.util.MessageService;
import org.lamsfoundation.lams.web.session.SessionManager;
import org.lamsfoundation.lams.web.util.AttributeNames;

/**
 * 
 * <p>
 * More generic monitoring mode functions live here
 * </p>
 * 
 * @author Ozgur Demirtas
 * 
 */
public class MonitoringUtil implements VoteAppConstants {

    public static Map<String, String> populateToolSessionsId(VoteContent voteContent, IVoteService voteService) {
	List<Long> sessionIds = voteService.getSessionsFromContent(voteContent);

	Map<String, String> sessionsMap = new TreeMap(new VoteComparator());
	int mapIndex = 1;
	for (Long sessionId : sessionIds) {
	    sessionsMap.put("" + mapIndex, sessionId.toString());
	    mapIndex++;
	}

	if (sessionsMap.isEmpty()) {
	    sessionsMap.put(new Long(1).toString(), "None");
	} else {
	    sessionsMap.put(new Long(sessionsMap.size() + 1).toString(), "All");
	}

	return sessionsMap;
    }

    public static Map<String, VoteMonitoredUserDTO> convertToVoteMonitoredUserDTOMap(List<VoteMonitoredUserDTO> list) {
	Map<String, VoteMonitoredUserDTO> map = new TreeMap<String, VoteMonitoredUserDTO>(new VoteComparator());

	Iterator<VoteMonitoredUserDTO> listIterator = list.iterator();
	Long mapIndex = new Long(1);

	while (listIterator.hasNext()) {
	    VoteMonitoredUserDTO data = listIterator.next();

	    map.put(mapIndex.toString(), data);
	    mapIndex = new Long(mapIndex.longValue() + 1);
	}
	return map;
    }

    public static Map<String, Map> convertToMap(List list) {
	Map<String, Map> map = new TreeMap<String, Map>(new VoteComparator());

	Iterator listIterator = list.iterator();
	Long mapIndex = new Long(1);

	while (listIterator.hasNext()) {
	    Map data = (Map) listIterator.next();
	    map.put(mapIndex.toString(), data);
	    mapIndex = new Long(mapIndex.longValue() + 1);
	}
	return map;
    }

    public static boolean notebookEntriesExist(IVoteService voteService, VoteContent voteContent) {
	Iterator<VoteSession> iteratorSession = voteContent.getVoteSessions().iterator();
	while (iteratorSession.hasNext()) {
	    VoteSession voteSession = iteratorSession.next();

	    if (voteSession != null) {

		Iterator iteratorUser = voteSession.getVoteQueUsers().iterator();
		while (iteratorUser.hasNext()) {
		    VoteQueUsr voteQueUsr = (VoteQueUsr) iteratorUser.next();

		    if (voteQueUsr != null) {
			NotebookEntry notebookEntry = voteService.getEntry(voteSession.getVoteSessionId(),
				CoreNotebookConstants.NOTEBOOK_TOOL, MY_SIGNATURE, new Integer(voteQueUsr.getQueUsrId()
					.intValue()));

			if (notebookEntry != null) {
			    return true;
			}

		    }
		}
	    }
	}
	return false;
    }

    public static void buildVoteStatsDTO(HttpServletRequest request, IVoteService voteService, VoteContent voteContent) {

	int countSessionComplete = 0;
	int countAllUsers = 0;
	Iterator iteratorSession = voteContent.getVoteSessions().iterator();
	while (iteratorSession.hasNext()) {
	    VoteSession voteSession = (VoteSession) iteratorSession.next();

	    if (voteSession != null) {
		if (voteSession.getSessionStatus().equals(COMPLETED)) {
		    ++countSessionComplete;
		}

		Iterator iteratorUser = voteSession.getVoteQueUsers().iterator();
		while (iteratorUser.hasNext()) {
		    VoteQueUsr voteQueUsr = (VoteQueUsr) iteratorUser.next();
		    if (voteQueUsr != null) {
			++countAllUsers;
		    }
		}
	    }
	}

	VoteStatsDTO voteStatsDTO = new VoteStatsDTO();
	voteStatsDTO.setCountAllUsers(new Integer(countAllUsers).toString());
	voteStatsDTO.setCountSessionComplete(new Integer(countSessionComplete).toString());
	request.setAttribute(VOTE_STATS_DTO, voteStatsDTO);

	// setting up the advanced summary for LDEV-1662
	request.setAttribute("useSelectLeaderToolOuput", voteContent.isUseSelectLeaderToolOuput());
	request.setAttribute("lockOnFinish", voteContent.isLockOnFinish());
	request.setAttribute("allowText", voteContent.isAllowText());
	request.setAttribute("maxNominationCount", voteContent.getMaxNominationCount());
	request.setAttribute("minNominationCount", voteContent.getMinNominationCount());
	request.setAttribute("showResults", voteContent.isShowResults());
	request.setAttribute("reflect", voteContent.isReflect());
	request.setAttribute("reflectionSubject", voteContent.getReflectionSubject());
	request.setAttribute("toolContentID", voteContent.getVoteContentId());

	// setting up the SubmissionDeadline
	if (voteContent.getSubmissionDeadline() != null) {
	    Date submissionDeadline = voteContent.getSubmissionDeadline();
	    HttpSession ss = SessionManager.getSession();
	    UserDTO teacher = (UserDTO) ss.getAttribute(AttributeNames.USER);
	    TimeZone teacherTimeZone = teacher.getTimeZone();
	    Date tzSubmissionDeadline = DateUtil.convertToTimeZoneFromDefault(teacherTimeZone, submissionDeadline);
	    request.setAttribute(VoteAppConstants.ATTR_SUBMISSION_DEADLINE, tzSubmissionDeadline.getTime());
	}
    }

    public static void repopulateRequestParameters(HttpServletRequest request, VoteMonitoringForm voteMonitoringForm,
	    VoteGeneralMonitoringDTO voteGeneralMonitoringDTO) {

	String toolContentID = request.getParameter(VoteAppConstants.TOOL_CONTENT_ID);
	voteMonitoringForm.setToolContentID(toolContentID);
	voteGeneralMonitoringDTO.setToolContentID(toolContentID);

	String activeModule = request.getParameter(VoteAppConstants.ACTIVE_MODULE);
	voteMonitoringForm.setActiveModule(activeModule);
	voteGeneralMonitoringDTO.setActiveModule(activeModule);

	String defineLaterInEditMode = request.getParameter(VoteAppConstants.DEFINE_LATER_IN_EDIT_MODE);
	voteMonitoringForm.setDefineLaterInEditMode(defineLaterInEditMode);
	voteGeneralMonitoringDTO.setDefineLaterInEditMode(defineLaterInEditMode);

	String responseId = request.getParameter(VoteAppConstants.RESPONSE_ID);
	voteMonitoringForm.setResponseId(responseId);
	voteGeneralMonitoringDTO.setResponseId(responseId);

	String currentUid = request.getParameter(VoteAppConstants.CURRENT_UID);
	voteMonitoringForm.setCurrentUid(currentUid);
	voteGeneralMonitoringDTO.setCurrentUid(currentUid);
    }

}
