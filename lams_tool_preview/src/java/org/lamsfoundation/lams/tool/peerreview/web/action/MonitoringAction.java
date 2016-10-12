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


package org.lamsfoundation.lams.tool.peerreview.web.action;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.tomcat.util.json.JSONArray;
import org.apache.tomcat.util.json.JSONException;
import org.apache.tomcat.util.json.JSONObject;
import org.lamsfoundation.lams.rating.dto.ItemRatingCriteriaDTO;
import org.lamsfoundation.lams.rating.dto.ItemRatingDTO;
import org.lamsfoundation.lams.rating.dto.RatingCommentDTO;
import org.lamsfoundation.lams.rating.dto.RatingDTO;
import org.lamsfoundation.lams.rating.dto.StyledCriteriaRatingDTO;
import org.lamsfoundation.lams.rating.dto.StyledRatingDTO;
import org.lamsfoundation.lams.rating.model.RatingCriteria;
import org.lamsfoundation.lams.tool.peerreview.PeerreviewConstants;
import org.lamsfoundation.lams.tool.peerreview.dto.GroupSummary;
import org.lamsfoundation.lams.tool.peerreview.dto.ReflectDTO;
import org.lamsfoundation.lams.tool.peerreview.model.Peerreview;
import org.lamsfoundation.lams.tool.peerreview.model.PeerreviewUser;
import org.lamsfoundation.lams.tool.peerreview.service.IPeerreviewService;
import org.lamsfoundation.lams.util.WebUtil;
import org.lamsfoundation.lams.web.util.AttributeNames;
import org.lamsfoundation.lams.web.util.SessionMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class MonitoringAction extends Action {
    public static Logger log = Logger.getLogger(MonitoringAction.class);

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws IOException, ServletException, JSONException {
	String param = mapping.getParameter();

	request.setAttribute("initialTabId", WebUtil.readLongParam(request, AttributeNames.PARAM_CURRENT_TAB, true));

	if (param.equals("summary")) {
	    return summary(mapping, form, request, response);
	}
	if (param.equals("criteria")) {
	    return criteria(mapping, form, request, response);
	}
	if (param.equals("getUsers")) {
	    return getUsers(mapping, form, request, response);
	}
	if (param.equals("getSubgridData")) {
	    return getSubgridData(mapping, form, request, response);
	}

	return mapping.findForward(PeerreviewConstants.ERROR);
    }

    private ActionForward summary(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) {
	// initial Session Map
	SessionMap<String, Object> sessionMap = new SessionMap<String, Object>();
	request.getSession().setAttribute(sessionMap.getSessionID(), sessionMap);
	request.setAttribute(PeerreviewConstants.ATTR_SESSION_MAP_ID, sessionMap.getSessionID());
	// save contentFolderID into session
	sessionMap.put(AttributeNames.PARAM_CONTENT_FOLDER_ID,
		WebUtil.readStrParam(request, AttributeNames.PARAM_CONTENT_FOLDER_ID));

	Long contentId = WebUtil.readLongParam(request, AttributeNames.PARAM_TOOL_CONTENT_ID);
	IPeerreviewService service = getPeerreviewService();
	List<GroupSummary> groupList = service.getGroupSummaries(contentId);

	Peerreview peerreview = service.getPeerreviewByContentId(contentId);

	// Create reflectList if reflection is enabled.
	if (peerreview.isReflectOnActivity()) {
	    List<ReflectDTO> relectList = service.getReflectList(contentId);
	    sessionMap.put(PeerreviewConstants.ATTR_REFLECT_LIST, relectList);
	}

	// user name map
	List<PeerreviewUser> sessionUsers = service.getUsersByContent(contentId);
	HashMap<Long, String> userNameMap = new HashMap<Long, String>();
	for (PeerreviewUser userIter : sessionUsers) {
	    userNameMap.put(userIter.getUserId(), userIter.getFirstName() + " " + userIter.getLastName());
	}
	sessionMap.put("userNameMap", userNameMap);

	// cache into sessionMap
	sessionMap.put(PeerreviewConstants.ATTR_SUMMARY_LIST, groupList);
	sessionMap.put(PeerreviewConstants.PAGE_EDITABLE, peerreview.isContentInUse());
	sessionMap.put(PeerreviewConstants.ATTR_PEERREVIEW, peerreview);
	sessionMap.put(PeerreviewConstants.ATTR_TOOL_CONTENT_ID, contentId);
	sessionMap.put(PeerreviewConstants.ATTR_IS_GROUPED_ACTIVITY, service.isGroupedActivity(contentId));
	
	List<RatingCriteria> criterias = service.getRatingCriterias(contentId);
	request.setAttribute(PeerreviewConstants.ATTR_CRITERIAS, criterias);
	return mapping.findForward(PeerreviewConstants.SUCCESS);
    }

//    private void setupRankHedgeData(HttpServletRequest request, Long contentId, IPeerreviewService service, RatingCriteria criteria) {
//	int sorting = criteria.isHedgeStyleRating() ? PeerreviewConstants.SORT_BY_AVERAGE_RESULT_DESC : (criteria
//		.isRankingStyleRating() ? PeerreviewConstants.SORT_BY_AVERAGE_RESULT_ASC
//		: PeerreviewConstants.SORT_BY_USERNAME_ASC);
//	StyledCriteriaRatingDTO dto = service.getUsersRatingsCommentsByCriteriaIdDTO(contentId, criteria, -1L,
//		false, sorting, true, true);
//	request.setAttribute("criteriaRatings", dto);
//    }

    private ActionForward criteria(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) {

	String sessionMapID = request.getParameter(PeerreviewConstants.ATTR_SESSION_MAP_ID);
	SessionMap<String, Object> sessionMap = (SessionMap<String, Object>) request.getSession()
		.getAttribute(sessionMapID);
	request.setAttribute(PeerreviewConstants.ATTR_SESSION_MAP_ID, sessionMap.getSessionID());

	Long contentId = (Long) sessionMap.get(PeerreviewConstants.ATTR_TOOL_CONTENT_ID);
	Long toolSessionId = WebUtil.readLongParam(request, "toolSessionId");

	IPeerreviewService service = getPeerreviewService();
	Long criteriaId = WebUtil.readLongParam(request, "criteriaId");
	RatingCriteria criteria = service.getCriteriaByCriteriaId(criteriaId);

	request.setAttribute("criteria", criteria);
	request.setAttribute("toolSessionId", toolSessionId);
	return mapping.findForward(PeerreviewConstants.SUCCESS);
    }

    /**
     * Refreshes user list.
     */
    public ActionForward getUsers(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse res) throws IOException, ServletException, JSONException {
	IPeerreviewService service = getPeerreviewService();

	Long toolContentId = WebUtil.readLongParam(request, "toolContentId");
	Long toolSessionId = WebUtil.readLongParam(request, "toolSessionId");
	Long criteriaId = WebUtil.readLongParam(request, "criteriaId");

	RatingCriteria criteria = service.getCriteriaByCriteriaId(criteriaId);

	// Getting the params passed in from the jqGrid
	int page = WebUtil.readIntParam(request, PeerreviewConstants.PARAM_PAGE) - 1;
	int size = WebUtil.readIntParam(request, PeerreviewConstants.PARAM_ROWS);
	String sortOrder = WebUtil.readStrParam(request, PeerreviewConstants.PARAM_SORD);
	String sortBy = WebUtil.readStrParam(request, PeerreviewConstants.PARAM_SIDX, true);

	int sorting = PeerreviewConstants.SORT_BY_AVERAGE_RESULT_DESC;
	if ( criteria.isRankingStyleRating() )
	    sorting = PeerreviewConstants.SORT_BY_AVERAGE_RESULT_ASC;
	else if ( criteria.isCommentRating() )
	    sorting = PeerreviewConstants.SORT_BY_USERNAME_ASC;
	    
	if (sortBy != null && sortBy.equals(PeerreviewConstants.PARAM_SORT_NAME)) {
	    if (sortOrder != null && sortOrder.equals(PeerreviewConstants.SORT_DESC)) {
		sorting = PeerreviewConstants.SORT_BY_USERNAME_DESC;
	    } else {
		sorting = PeerreviewConstants.SORT_BY_USERNAME_ASC;
	    }
	} else if (sortBy != null && sortBy.equals(PeerreviewConstants.PARAM_SORT_RATING)) {
	    if (sortOrder != null && sortOrder.equals(PeerreviewConstants.SORT_DESC)) {
		sorting = PeerreviewConstants.SORT_BY_AVERAGE_RESULT_DESC;
	    } else {
		sorting = PeerreviewConstants.SORT_BY_AVERAGE_RESULT_ASC;
	    }
	}

	// in case of monitoring we show all results. in case of learning - don't show results from the current user
	Long dummyUserId = -1L;
	
	JSONObject responcedata = new JSONObject();

	responcedata.put("page", page + 1);
	responcedata.put("total", Math.ceil((float) service.getCountUsersBySession(toolSessionId, dummyUserId) / size));
	responcedata.put("records", service.getCountUsersBySession(toolSessionId, dummyUserId));

	JSONArray rows = new JSONArray();

	if (criteria.isCommentRating()) {
	    // special db lookup just for this - gets the user's & how many comments left for them
	    List<Object[]> rawRows = service.getCommentsCounts(toolContentId, toolSessionId, criteria, page, size,
		    sorting);

	    for (int i = 0; i < rawRows.size(); i++) {
		Object[] rawRow = rawRows.get(i);
		JSONObject cell = new JSONObject();
		cell.put("itemId", rawRow[0]);
		cell.put("itemDescription", rawRow[3]);

		Number numCommentsNumber = (Number) rawRow[1];
		int numComments = numCommentsNumber != null ? numCommentsNumber.intValue() : 0;
		if (numComments > 0) {
		    cell.put("rating", service.getLocalisedMessage("label.monitoring.num.of.comments", new Object[] { numComments }));
		} else {
		    cell.put("rating", "");
		}

		JSONObject row = new JSONObject();
		row.put("id", "" + rawRow[0]);
		row.put("cell", cell);
		rows.put(row);
	    }
	} else {
	    // all other styles can use the "normal" routine and munge the JSON to suit jqgrid
	    JSONArray rawRows = service.getUsersRatingsCommentsByCriteriaIdJSON(toolContentId, toolSessionId, criteria,
		    dummyUserId, page, size, sorting, true, true, false);

	    for (int i = 0; i < rawRows.length(); i++) {

		JSONObject rawRow = rawRows.getJSONObject(i);

		String averageRating = (String) rawRow.get("averageRating");
		Object numberOfVotes = rawRow.get("numberOfVotes");

		if (averageRating == null || averageRating.length() == 0) {
		    rawRow.put("rating", "");
		} else if (criteria.isStarStyleRating()) {
		    String starString = "<div class='rating-stars-holder'>";
		    starString += "<div class='rating-stars-disabled rating-stars-new' data-average='" + averageRating
			    + "' data-id='" + criteriaId + "'>";
		    starString += "</div>";
		    starString += "<div class='rating-stars-caption' id='rating-stars-caption-" + criteriaId + "' >";
		    String msg = service.getLocalisedMessage("label.average.rating", new Object[] { averageRating,
			    numberOfVotes });
		    starString += msg;
		    starString += "</div>";
		    rawRow.put("rating", starString);
		} else {
		    rawRow.put("rating", averageRating);
		}

		JSONObject row = new JSONObject();
		row.put("id", "" + rawRow.get("itemId"));
		row.put("cell", rawRow);
		rows.put(row);
	    }
	}
	responcedata.put("rows", rows);

	res.setContentType("application/json;charset=utf-8");
	res.getWriter().print(new String(responcedata.toString()));
	return null;
    }

    private ActionForward getSubgridData(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws JSONException, IOException {
	IPeerreviewService service = getPeerreviewService();
	String sessionMapID = request.getParameter(PeerreviewConstants.ATTR_SESSION_MAP_ID);

	Long itemId = WebUtil.readLongParam(request, "itemId");
	Long toolContentId = WebUtil.readLongParam(request, "toolContentId");
	Long toolSessionId = WebUtil.readLongParam(request, "toolSessionId");
	Long criteriaId = WebUtil.readLongParam(request, "criteriaId");

	// ratings left by others for this user
	List<Object[]> ratings = service.getDetailedRatingsComments(toolContentId, toolSessionId, criteriaId, itemId );
	RatingCriteria criteria = service.getCriteriaByCriteriaId(criteriaId);
	String title = StringEscapeUtils.escapeHtml(criteria.getTitle());
	
	// processed data from db is userId, comment, rating, first_name, escaped( firstname + last_name)
	// if no rating or comment, then the entries will be null and not an empty string
	JSONArray rows = new JSONArray();
	int i = 0;
	
	for (Object[] ratingDetails : ratings) {
	    if ( ratingDetails[2] != null ) {
   		JSONArray userData = new JSONArray();
    		userData.put(i);
    		userData.put(ratingDetails[4]);
    		userData.put(ratingDetails[2]);
    		userData.put(title);

    		JSONObject userRow = new JSONObject();
    		userRow.put("id", i++);
    		userRow.put("cell", userData);
    
    		rows.put(userRow);
	    }
	}

	if ( criteria.isCommentsEnabled() ) {
	    for (Object[] ratingDetails : ratings) {

		// Show comment if comment has been left by user. Exclude the special case where it is a hedging rating
		//  and the rating is not null - otherwise we end up putting the justification comment against entries that were not rated.
		String comment = (String) ratingDetails[1];
		if ( comment != null && ( ! criteria.isHedgeStyleRating() || ( criteria.isHedgeStyleRating() && ratingDetails[2] != null ) ) ) {
		    JSONArray userData = new JSONArray();
		    userData.put(i);
		    userData.put(ratingDetails[4]);
		    userData.put(StringEscapeUtils.escapeHtml(comment));
		    userData.put("Comments");

		    JSONObject userRow = new JSONObject();
		    userRow.put("id", i++);
		    userRow.put("cell", userData);

		    rows.put(userRow); 
		}
	    }
	}
	
	JSONObject responseJSON = new JSONObject();
	responseJSON.put("total", 1);
	responseJSON.put("page", 1);
	responseJSON.put("records", rows.length());
	responseJSON.put("rows", rows);

	response.setContentType("application/json;charset=utf-8");
	response.getWriter().write(responseJSON.toString());
	return null;
    }

    // *************************************************************************************
    // Private method
    // *************************************************************************************
    private IPeerreviewService getPeerreviewService() {
	WebApplicationContext wac = WebApplicationContextUtils
		.getRequiredWebApplicationContext(getServlet().getServletContext());
	return (IPeerreviewService) wac.getBean(PeerreviewConstants.PEERREVIEW_SERVICE);
    }
}
