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
package org.lamsfoundation.lams.tool.sbmt.web.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.TreeSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.lamsfoundation.lams.tool.sbmt.SbmtConstants;
import org.lamsfoundation.lams.tool.sbmt.SubmissionDetails;
import org.lamsfoundation.lams.tool.sbmt.SubmitFilesContent;
import org.lamsfoundation.lams.tool.sbmt.SubmitFilesSession;
import org.lamsfoundation.lams.tool.sbmt.SubmitUser;
import org.lamsfoundation.lams.tool.sbmt.dto.AuthoringDTO;
import org.lamsfoundation.lams.tool.sbmt.dto.FileDetailsDTO;
import org.lamsfoundation.lams.tool.sbmt.dto.SessionDTO;
import org.lamsfoundation.lams.tool.sbmt.dto.StatisticDTO;
import org.lamsfoundation.lams.tool.sbmt.service.ISubmitFilesService;
import org.lamsfoundation.lams.usermanagement.dto.UserDTO;
import org.lamsfoundation.lams.util.DateUtil;
import org.lamsfoundation.lams.util.MessageService;
import org.lamsfoundation.lams.util.WebUtil;
import org.lamsfoundation.lams.web.session.SessionManager;
import org.lamsfoundation.lams.web.util.AttributeNames;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author Manpreet Minhas
 */
@Controller
@RequestMapping("/monitoring")
public class MonitoringController {

    public static Logger logger = Logger.getLogger(MonitoringController.class);

    @Autowired
    @Qualifier("submitFilesService")
    private ISubmitFilesService submitFilesService;

    @Autowired
    @Qualifier("sbmtMessageService")
    private MessageService messageService;

    private class SessionComparator implements Comparator<SessionDTO> {
	@Override
	public int compare(SessionDTO o1, SessionDTO o2) {
	    if (o1 != null && o2 != null) {
		return o1.getSessionName().compareTo(o2.getSessionName());
	    } else if (o1 != null) {
		return 1;
	    } else {
		return -1;
	    }
	}
    }

    private class StatisticComparator implements Comparator<StatisticDTO> {
	@Override
	public int compare(StatisticDTO o1, StatisticDTO o2) {
	    if (o1 != null && o2 != null) {
		return o1.getSessionName().compareTo(o2.getSessionName());
	    } else if (o1 != null) {
		return 1;
	    } else {
		return -1;
	    }
	}
    }

    /**
     * Default ActionForward for Monitor
     */
    @RequestMapping("/monitoring")
    public String unspecified(HttpServletRequest request) {
	String contentFolderID = WebUtil.readStrParam(request, AttributeNames.PARAM_CONTENT_FOLDER_ID);
	Long contentID = new Long(WebUtil.readLongParam(request, AttributeNames.PARAM_TOOL_CONTENT_ID));

	request.setAttribute(AttributeNames.PARAM_CONTENT_FOLDER_ID, contentFolderID);
	request.setAttribute(AttributeNames.PARAM_TOOL_CONTENT_ID, contentID);

	List submitFilesSessionList = submitFilesService.getSubmitFilesSessionByContentID(contentID);
	summary(request, submitFilesSessionList);
	statistic(request, contentID);

	// instruction
	SubmitFilesContent persistContent = submitFilesService.getSubmitFilesContent(contentID);
	// if this content does not exist, then reset the contentID to current value to keep it on HTML page.
	persistContent.setContentID(contentID);

	AuthoringDTO authorDto = new AuthoringDTO(persistContent);
	request.setAttribute(SbmtConstants.AUTHORING_DTO, authorDto);
	request.setAttribute(SbmtConstants.CONTENT_IN_USE, persistContent.isContentInUse());
	request.setAttribute(SbmtConstants.ATTR_IS_GROUPED_ACTIVITY, submitFilesService.isGroupedActivity(contentID));
	request.setAttribute(SbmtConstants.ATTR_REFLECTION_ON, persistContent.isReflectOnActivity());

	// set SubmissionDeadline, if any
	if (persistContent.getSubmissionDeadline() != null) {
	    Date submissionDeadline = persistContent.getSubmissionDeadline();
	    HttpSession ss = SessionManager.getSession();
	    UserDTO teacher = (UserDTO) ss.getAttribute(AttributeNames.USER);
	    TimeZone teacherTimeZone = teacher.getTimeZone();
	    Date tzSubmissionDeadline = DateUtil.convertToTimeZoneFromDefault(teacherTimeZone, submissionDeadline);
	    request.setAttribute(SbmtConstants.ATTR_SUBMISSION_DEADLINE, tzSubmissionDeadline.getTime());
	    request.setAttribute(SbmtConstants.ATTR_SUBMISSION_DEADLINE_DATESTRING,
		    DateUtil.convertToStringForJSON(submissionDeadline, request.getLocale()));
	}

	// smbtMonitoringForm.set("currentTab", WebUtil.readStrParam(request, AttributeNames.PARAM_CURRENT_TAB,true));

	return "monitoring/monitoring";
    }

    /** Ajax call to populate the tablesorter */
    @RequestMapping(path = "/getUsers", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String getUsers(HttpServletRequest request, HttpServletResponse response) {

	Long sessionID = new Long(WebUtil.readLongParam(request, AttributeNames.PARAM_TOOL_SESSION_ID));
	Long contentId = WebUtil.readLongParam(request, AttributeNames.PARAM_TOOL_CONTENT_ID);

	// paging parameters of tablesorter
	int size = WebUtil.readIntParam(request, "size");
	int page = WebUtil.readIntParam(request, "page");
	Integer sortByName = WebUtil.readIntParam(request, "column[0]", true);
	Integer sortByNumFiles = WebUtil.readIntParam(request, "column[1]", true);
	Integer sortByMarked = WebUtil.readIntParam(request, "column[2]", true);
	String searchString = request.getParameter("fcol[0]");

	int sorting = SbmtConstants.SORT_BY_NO;
	if (sortByName != null) {
	    sorting = sortByName.equals(0) ? SbmtConstants.SORT_BY_USERNAME_ASC : SbmtConstants.SORT_BY_USERNAME_DESC;
	} else if (sortByNumFiles != null) {
	    sorting = sortByNumFiles.equals(0) ? SbmtConstants.SORT_BY_NUM_FILES_ASC
		    : SbmtConstants.SORT_BY_NUM_FILES_DESC;
	} else if (sortByMarked != null) {
	    sorting = sortByMarked.equals(0) ? SbmtConstants.SORT_BY_MARKED_ASC : SbmtConstants.SORT_BY_MARKED_DESC;
	}

	// return user list according to the given sessionID
	SubmitFilesContent spreadsheet = submitFilesService.getSubmitFilesContent(contentId);
	List<Object[]> users = submitFilesService.getUsersForTablesorter(sessionID, page, size, sorting, searchString,
		spreadsheet.isReflectOnActivity());

	ArrayNode rows = JsonNodeFactory.instance.arrayNode();
	ObjectNode responsedata = JsonNodeFactory.instance.objectNode();
	responsedata.put("total_rows", submitFilesService.getCountUsersBySession(sessionID, searchString));
	SubmitUser groupLeader = new SubmitUser();
	if (spreadsheet.isUseSelectLeaderToolOuput()) {
	    SubmitFilesSession session = submitFilesService.getSessionById(sessionID);
	    groupLeader = session.getGroupLeader();
	}
	for (Object[] userAndReflection : users) {

	    ObjectNode responseRow = JsonNodeFactory.instance.objectNode();

	    SubmitUser user = (SubmitUser) userAndReflection[0];
	    responseRow.put(SbmtConstants.ATTR_USER_UID, user.getUid());
	    responseRow.put(SbmtConstants.USER_ID, user.getUserID());
	    responseRow.put(SbmtConstants.ATTR_USER_FULLNAME, HtmlUtils.htmlEscape(user.getFullName()));

	    if (userAndReflection.length > 2) {
		responseRow.put(SbmtConstants.ATTR_PORTRAIT_ID, (Integer) userAndReflection[1]);
	    }

	    if (userAndReflection.length > 3) {
		responseRow.put(SbmtConstants.ATTR_USER_NUM_FILE,
			(Integer) userAndReflection[2] - (Integer) userAndReflection[3]);
	    }

	    if (userAndReflection.length > 4) {
		responseRow.put(SbmtConstants.ATTR_USER_FILE_MARKED, (Integer) userAndReflection[4] > 0);
	    }

	    if (userAndReflection.length > 5) {
		responseRow.put(SbmtConstants.ATTR_USER_REFLECTION, (String) userAndReflection[5]);
	    }
	    if (!spreadsheet.isUseSelectLeaderToolOuput()
		    || (spreadsheet.isUseSelectLeaderToolOuput() && groupLeader == user)) {
		rows.add(responseRow);
	    }
	}

	responsedata.set("rows", rows);

	return responsedata.toString();

    }

    /**
     * AJAX call to refresh statistic page.
     */
    @RequestMapping("/doStatistic")
    public String doStatistic(HttpServletRequest request) {

	Long contentID = new Long(WebUtil.readLongParam(request, AttributeNames.PARAM_TOOL_CONTENT_ID));
	statistic(request, contentID);
	request.setAttribute(SbmtConstants.ATTR_IS_GROUPED_ACTIVITY, submitFilesService.isGroupedActivity(contentID));
	return "monitoring/parts/statisticpart";
    }

    private void statistic(HttpServletRequest request, Long contentID) {
	SortedSet<StatisticDTO> statistics = new TreeSet<>(new StatisticComparator());
	SubmitFilesContent spreadsheet = submitFilesService.getSubmitFilesContent(contentID);
	if (spreadsheet.isUseSelectLeaderToolOuput()) {
	    statistics.addAll(submitFilesService.getLeaderStatisticsBySession(contentID));
	    request.setAttribute("statisticList", statistics);
	} else {
	    statistics.addAll(submitFilesService.getStatisticsBySession(contentID));
	    request.setAttribute("statisticList", statistics);
	}
    }

    /**
     * Release mark
     */
    @RequestMapping("/releaseMarks")
    @ResponseBody
    public void releaseMarks(HttpServletRequest request, HttpServletResponse response) {

	// get service then update report table
	Long sessionID = new Long(WebUtil.readLongParam(request, AttributeNames.PARAM_TOOL_SESSION_ID));
	submitFilesService.releaseMarksForSession(sessionID);

	try {
	    response.setContentType("text/html;charset=utf-8");
	    PrintWriter out = response.getWriter();
	    SubmitFilesSession session = submitFilesService.getSessionById(sessionID);
	    String sessionName = "";
	    if (session != null) {
		sessionName = session.getSessionName();
	    }
	    out.write(messageService.getMessage("msg.mark.released", new String[] { sessionName }));
	    out.flush();
	} catch (IOException e) {
	}
    }

    /**
     * Download submit file marks by MS Excel file format.
     */
    @RequestMapping("/downloadMarks")
    public void downloadMarks(HttpServletRequest request, HttpServletResponse response) {

	Long sessionID = new Long(WebUtil.readLongParam(request, AttributeNames.PARAM_TOOL_SESSION_ID));
	// return FileDetailsDTO list according to the given sessionID
	Map userFilesMap = submitFilesService.getFilesUploadedBySession(sessionID, request.getLocale());
	// construct Excel file format and download
	MultiValueMap<String, String> errorMap = new LinkedMultiValueMap<>();
	try {
	    // create an empty excel file
	    HSSFWorkbook wb = new HSSFWorkbook();
	    HSSFSheet sheet = wb.createSheet("Marks");
	    sheet.setColumnWidth(0, 5000);
	    HSSFRow row;
	    HSSFCell cell;

	    Iterator iter = userFilesMap.values().iterator();
	    Iterator dtoIter;

	    int idx = 0;

	    row = sheet.createRow(idx++);
	    cell = row.createCell(2);
	    cell.setCellValue(messageService.getMessage("label.learner.fileName"));

	    cell = row.createCell(3);
	    cell.setCellValue(messageService.getMessage("label.learner.fileDescription"));

	    cell = row.createCell(4);
	    cell.setCellValue(messageService.getMessage("label.learner.marks"));

	    cell = row.createCell(5);
	    cell.setCellValue(messageService.getMessage("label.learner.comments"));

	    while (iter.hasNext()) {
		List list = (List) iter.next();
		dtoIter = list.iterator();

		while (dtoIter.hasNext()) {
		    FileDetailsDTO dto = (FileDetailsDTO) dtoIter.next();
		    if (!dto.isRemoved()) {
			row = sheet.createRow(idx++);

			int count = 0;

			cell = row.createCell(count++);
			cell.setCellValue(dto.getOwner().getFullName());

			++count;

			sheet.setColumnWidth(count, 8000);

			cell = row.createCell(count++);
			cell.setCellValue(dto.getFilePath());

			cell = row.createCell(count++);
			cell.setCellValue(dto.getFileDescription());

			cell = row.createCell(count++);

			String marks = dto.getMarks();
			cell.setCellValue(marks != null ? marks : "");

			cell = row.createCell(count++);
			cell.setCellValue(dto.getComments());
		    }
		}
	    }

	    ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    wb.write(bos);

	    // construct download file response header
	    String fileName = "marks" + sessionID + ".xls";
	    String mineType = "application/vnd.ms-excel";
	    String header = "attachment; filename=\"" + fileName + "\";";
	    response.setContentType(mineType);
	    response.setHeader("Content-Disposition", header);

	    byte[] data = bos.toByteArray();
	    response.getOutputStream().write(data, 0, data.length);
	    response.getOutputStream().flush();
	} catch (Exception e) {
	    logger.error(e);
	    errorMap.add(messageService.getMessage("monitoring.download.error"), e.toString());
	}

	if (!errorMap.isEmpty()) {
	    try {
		PrintWriter out = response.getWriter();
		Iterator<String> it = errorMap.keySet().iterator();
		while (it.hasNext()) {
		    String theKey = it.next();
		    out.write(theKey);
		}
		out.flush();
	    } catch (IOException e) {
	    }
	}
    }

    /**
     * Set Submission Deadline
     */
    @RequestMapping(path = "setSubmissionDeadline", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String setSubmissionDeadline(HttpServletRequest request, HttpServletResponse response) throws IOException {

	Long contentID = WebUtil.readLongParam(request, AttributeNames.PARAM_TOOL_CONTENT_ID);
	SubmitFilesContent content = submitFilesService.getSubmitFilesContent(contentID);

	Long dateParameter = WebUtil.readLongParam(request, SbmtConstants.ATTR_SUBMISSION_DEADLINE, true);
	Date tzSubmissionDeadline = null;
	String formattedDate = "";
	if (dateParameter != null) {
	    Date submissionDeadline = new Date(dateParameter);
	    HttpSession ss = SessionManager.getSession();
	    UserDTO teacher = (UserDTO) ss.getAttribute(AttributeNames.USER);
	    TimeZone teacherTimeZone = teacher.getTimeZone();
	    tzSubmissionDeadline = DateUtil.convertFromTimeZoneToDefault(teacherTimeZone, submissionDeadline);
	    formattedDate = DateUtil.convertToStringForJSON(tzSubmissionDeadline, request.getLocale());
	}
	content.setSubmissionDeadline(tzSubmissionDeadline);
	submitFilesService.saveOrUpdateContent(content);

	return formattedDate;
    }

    // **********************************************************
    // Mark udpate/view methods
    // **********************************************************
    /**
     * Display special user's marks information.
     */
    @RequestMapping("/listMark")
    public String listMark(HttpServletRequest request) {
	Long sessionID = new Long(WebUtil.readLongParam(request, AttributeNames.PARAM_TOOL_SESSION_ID));
	Integer userID = WebUtil.readIntParam(request, "userID");

	// return FileDetailsDTO list according to the given userID and sessionID
	List files = submitFilesService.getFilesUploadedByUser(userID, sessionID, request.getLocale(), true);

	request.setAttribute(AttributeNames.PARAM_TOOL_SESSION_ID, sessionID);
	request.setAttribute("report", files);
	return "monitoring/mark/mark";
    }

    /**
     * View mark of all learner from same tool content ID.
     */
    @RequestMapping("/listAllMarks")
    public String listAllMarks(HttpServletRequest request) {

	Long sessionID = new Long(WebUtil.readLongParam(request, AttributeNames.PARAM_TOOL_SESSION_ID));
	// return FileDetailsDTO list according to the given sessionID
	Map userFilesMap = submitFilesService.getFilesUploadedBySession(sessionID, request.getLocale());
	request.setAttribute(AttributeNames.PARAM_TOOL_SESSION_ID, sessionID);
	// request.setAttribute("user",submitFilesService.getUserDetails(userID));
	request.setAttribute("reports", userFilesMap);

	return "monitoring/mark/allmarks";

    }

    /**
     * Remove the original file created by the learner. Does not actually remove it from the content repository - merely
     * makes it as removed.
     */
    @RequestMapping("/removeLearnerFile")
    public String removeLearnerFile(HttpServletRequest request) throws ServletException {
	return removeRestoreLearnerFile(request, true);
    }

    /**
     * Remove the original file created by the learner. Does not actually remove it from the content repository - merely
     * makes it as removed.
     */
    @RequestMapping("/restoreLearnerFile")
    public String restoreLearnerFile(HttpServletRequest request) throws ServletException {
	return removeRestoreLearnerFile(request, false);
    }

    @RequestMapping("/removeRestoreLearnerFile")
    public String removeRestoreLearnerFile(HttpServletRequest request, boolean remove) throws ServletException {

	UserDTO currentUser = (UserDTO) SessionManager.getSession().getAttribute(AttributeNames.USER);

	Long sessionID = WebUtil.readLongParam(request, AttributeNames.PARAM_TOOL_SESSION_ID);
	Integer learnerUserID = WebUtil.readIntParam(request, AttributeNames.PARAM_USER_ID);
	Long detailID = WebUtil.readLongParam(request, "detailID");

	SubmissionDetails fileToProcess = submitFilesService.getSubmissionDetail(detailID);

	if (fileToProcess == null) {
	    StringBuilder builder = new StringBuilder("Unable to ").append(remove ? "remove" : "restore")
		    .append("file as file does not exist. Requested by user ").append(currentUser.getUserID())
		    .append(" for file ").append(detailID).append(" for user ").append(learnerUserID);
	    logger.error(builder.toString());
	    throw new ServletException("Invalid call to " + (remove ? "remove" : "restore")
		    + " file. See the server log for more details.");
	} else {

	    if (!fileToProcess.getSubmitFileSession().getSessionID().equals(sessionID)
		    || !fileToProcess.getLearner().getUserID().equals(learnerUserID)) {
		StringBuilder builder = new StringBuilder("Unable to ").append(remove ? "remove" : "restore")
			.append("file as values in database do not match values in request. Requested by user ")
			.append(currentUser.getUserID()).append(" for file ").append(detailID).append(" for user ")
			.append(learnerUserID).append(" in session ").append(sessionID);
		logger.error(builder.toString());
		throw new ServletException("Invalid call to " + (remove ? "remove" : "restore")
			+ " file. See the server log for more details.");
	    } else {

		if (remove) {
		    submitFilesService.removeLearnerFile(detailID, currentUser);
		    notifyRemoveRestore(fileToProcess, "event.file.restore.subject", "event.file.restore.body",
			    "restore file");

		} else {
		    submitFilesService.restoreLearnerFile(detailID, currentUser);
		    notifyRemoveRestore(fileToProcess, "event.file.delete.subject", "event.file.delete.body",
			    "delete file");
		}

	    }
	}

	List files = submitFilesService.getFilesUploadedByUser(learnerUserID, sessionID, request.getLocale(), true);

	request.setAttribute(AttributeNames.PARAM_TOOL_SESSION_ID, sessionID);
	request.setAttribute("report", files);
	return "monitoring/mark/mark";
    }

    /**
     * Notify the user by email of the file change. Need to do it here rather than in the service so that any issues are
     * caught and logged
     * without stuffing up the transaction.
     */
    public void notifyRemoveRestore(SubmissionDetails detail, String i18nSubjectKey, String i18nBodyKey,
	    String errorSubject) {
	Long contentID = detail.getSubmitFileSession().getContent().getContentID();
	Integer learnerID = detail.getLearner().getUserID();

	// Can't just create a new subscription then call triggerForSingleUser() as
	// it needs a subscription id, which doesn't exist for a subscription created in the same
	// transaction. So reuse the existing RELEASE MARKS event and subscription (created when
	// a file is uploaded) and override both the subject and the message.

	try {
	    boolean eventExists = submitFilesService.getEventNotificationService().eventExists(
		    SbmtConstants.TOOL_SIGNATURE, SbmtConstants.EVENT_NAME_NOTIFY_LEARNERS_ON_MARK_RELEASE, contentID);

	    if (eventExists) {
		submitFilesService.getEventNotificationService().triggerForSingleUser(SbmtConstants.TOOL_SIGNATURE,
			SbmtConstants.EVENT_NAME_NOTIFY_LEARNERS_ON_MARK_RELEASE, contentID, learnerID,
			submitFilesService.getLocalisedMessage(i18nSubjectKey, null),
			submitFilesService.getLocalisedMessage(i18nBodyKey, new Object[] { detail.getFilePath() }));
	    } else {
		logger.error("Unable to notify user of " + errorSubject + ". contentID=" + contentID + " learner="
			+ learnerID + " file " + detail.getFilePath() + " as "
			+ SbmtConstants.EVENT_NAME_NOTIFY_LEARNERS_ON_MARK_RELEASE + " event is missing");
	    }
	} catch (Exception e) {
	    logger.error("Unable to notify user of " + errorSubject + ". contentID=" + contentID + " learner="
		    + learnerID + " file " + detail.getFilePath() + " due to exception " + e.getMessage(), e);
	}
    }

    // **********************************************************
    // Private methods
    // **********************************************************

    /**
     * Save file mark information into HttpRequest
     */
    private void setMarkPage(HttpServletRequest request, Long sessionID, Long userID, Long detailID,
	    String updateMode) {

    }

    /**
     * Save Summary information into HttpRequest.
     */
    private void summary(HttpServletRequest request, List submitFilesSessionList) {
	SortedSet<SessionDTO> sessions = new TreeSet<>(this.new SessionComparator());

	// build a map with all users in the submitFilesSessionList
	Iterator it = submitFilesSessionList.iterator();
	while (it.hasNext()) {
	    SessionDTO sessionDto = new SessionDTO();
	    SubmitFilesSession sfs = (SubmitFilesSession) it.next();

	    Long sessionID = sfs.getSessionID();
	    sessionDto.setSessionID(sessionID);
	    sessionDto.setSessionName(sfs.getSessionName());
	    sessions.add(sessionDto);
	}

	// request.setAttribute(AttributeNames.PARAM_TOOL_SESSION_ID,sessionID);
	request.setAttribute("sessions", sessions);
    }

}