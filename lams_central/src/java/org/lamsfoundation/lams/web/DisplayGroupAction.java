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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 * USA
 *
 * http://www.gnu.org/licenses/gpl.txt
 * ****************************************************************
 */ 
 
/* $Id$ */ 
package org.lamsfoundation.lams.web; 

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.lamsfoundation.lams.index.IndexLessonBean;
import org.lamsfoundation.lams.index.IndexLinkBean;
import org.lamsfoundation.lams.index.IndexOrgBean;
import org.lamsfoundation.lams.lesson.Lesson;
import org.lamsfoundation.lams.lesson.service.LessonService;
import org.lamsfoundation.lams.usermanagement.Organisation;
import org.lamsfoundation.lams.usermanagement.OrganisationState;
import org.lamsfoundation.lams.usermanagement.OrganisationType;
import org.lamsfoundation.lams.usermanagement.Role;
import org.lamsfoundation.lams.usermanagement.User;
import org.lamsfoundation.lams.usermanagement.UserOrganisationRole;
import org.lamsfoundation.lams.usermanagement.service.IUserManagementService;
import org.lamsfoundation.lams.util.Configuration;
import org.lamsfoundation.lams.util.ConfigurationKeys;
import org.lamsfoundation.lams.util.IndexUtils;
import org.lamsfoundation.lams.util.WebUtil;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
 
/**
 * @author jliew
 * 
 * @struts.action path="/displayGroup" validate="false"
 * @struts.action-forward name="groupHeader" path="/groupHeader.jsp"
 * @struts.action-forward name="groupContents" path="/groupContents.jsp"
 * @struts.action-forward name="group" path="/group.jsp"
 */
public class DisplayGroupAction extends Action {

	private static Logger log = Logger.getLogger(DisplayGroupAction.class);
	private static IUserManagementService service;
	private static LessonService lessonService;
	private Integer stateId = OrganisationState.ACTIVE;
	
	@SuppressWarnings({"unchecked"})
	public ActionForward execute(ActionMapping mapping, 
			ActionForm form, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		
		String display = WebUtil.readStrParam(request, "display", false);
		stateId = WebUtil.readIntParam(request, "stateId", false);
		Integer orgId = WebUtil.readIntParam(request, "orgId", false);
		
		Organisation org = null;
		if (orgId != null) {
			org = (Organisation)getService().findById(Organisation.class, orgId);
		}
		
		String forwardPath = "group";
		if (org != null) {
			boolean allowSorting = false;
			List<Integer> roles = new ArrayList<Integer>();
			List<UserOrganisationRole> userOrganisationRoles = getService().getUserOrganisationRoles(orgId,request.getRemoteUser());
			for(UserOrganisationRole userOrganisationRole:userOrganisationRoles){
				Integer roleId = userOrganisationRole.getRole().getRoleId();
				roles.add(roleId);
				if (roleId.equals(Role.ROLE_GROUP_MANAGER) || roleId.equals(Role.ROLE_MONITOR)) {
					allowSorting = true;
				}
			}
			
			IndexOrgBean iob;
			if (StringUtils.equals(display, "contents")) {
				iob = new IndexOrgBean(org.getOrganisationId(), org.getName(), org.getOrganisationType().getOrganisationTypeId());
				iob = populateContentsOrgBean(iob, org, roles, request.getRemoteUser(), request.isUserInRole(Role.SYSADMIN));
				forwardPath = "groupContents";
			} else if (StringUtils.equals(display, "header")) {
				iob = createHeaderOrgBean(org, roles, request.getRemoteUser(), request.isUserInRole(Role.SYSADMIN), false);
				forwardPath = "groupHeader";
			} else {
				iob = createHeaderOrgBean(org, roles, request.getRemoteUser(), request.isUserInRole(Role.SYSADMIN), true);
			}
			
			request.setAttribute("orgBean", iob);
			request.setAttribute("allowSorting", allowSorting);
		}
		
		return mapping.findForward(forwardPath);
	}

	@SuppressWarnings({"unchecked"})
	private IndexOrgBean createHeaderOrgBean(Organisation org, List<Integer> roles, String username, boolean isSysAdmin, boolean includeContents)
		throws SQLException, NamingException {
		IndexOrgBean orgBean = new IndexOrgBean(org.getOrganisationId(), org.getName(), org.getOrganisationType().getOrganisationTypeId());

		// set org links
		List<IndexLinkBean> links = new ArrayList<IndexLinkBean>();
		if(isSysAdmin && stateId.equals(OrganisationState.ACTIVE)){
			if (orgBean.getType().equals(OrganisationType.COURSE_TYPE)) {
				links.add(new IndexLinkBean("index.classman", "javascript:openOrgManagement(" + org.getOrganisationId()+")", "manage-group-button", null));
			}
		}
		if ((contains(roles, Role.ROLE_GROUP_ADMIN) || contains(roles, Role.ROLE_GROUP_MANAGER) || contains(roles,Role.ROLE_MONITOR))
				&& stateId.equals(OrganisationState.ACTIVE)) {
			if (orgBean.getType().equals(OrganisationType.COURSE_TYPE)) {
				if((!isSysAdmin)&&(contains(roles, Role.ROLE_GROUP_ADMIN) || contains(roles, Role.ROLE_GROUP_MANAGER))){
					links.add(new IndexLinkBean("index.classman", "javascript:openOrgManagement(" + org.getOrganisationId()+")", "manage-group-button", null));
				}
				if(contains(roles, Role.ROLE_GROUP_MANAGER) || contains(roles,Role.ROLE_MONITOR))
					links.add(new IndexLinkBean("index.addlesson", "javascript:openAddLesson(" + org.getOrganisationId()+",'')", "add-lesson-button", null));
					links.add(new IndexLinkBean("index.searchlesson",  Configuration.get(ConfigurationKeys.SERVER_URL) + "/findUserLessons.do?dispatch=getResults&courseID=" + org.getOrganisationId()+"&KeepThis=true&TB_iframe=true&height=400&width=600", "thickbox"+org.getOrganisationId(), "index.searchlesson.tooltip")); 
			}else{//CLASS_TYPE
				if(contains(roles, Role.ROLE_GROUP_MANAGER) || contains(roles,Role.ROLE_MONITOR))
					links.add(new IndexLinkBean("index.addlesson","javascript:openAddLesson("+org.getParentOrganisation().getOrganisationId()+","+org.getOrganisationId()+")", "add-lesson-button", null));
			}
		}
		orgBean.setLinks(links);
		
		// set archived date if archived
		if (stateId.equals(OrganisationState.ARCHIVED) && org.getOrganisationState().getOrganisationStateId().equals(OrganisationState.ARCHIVED)) {
			orgBean.setArchivedDate(org.getArchivedDate());
		}
		
		if (includeContents) {
			orgBean = populateContentsOrgBean(orgBean, org, roles, username, isSysAdmin);
		}
		
		return orgBean;
	}
	
	private IndexOrgBean populateContentsOrgBean(IndexOrgBean orgBean, Organisation org, List<Integer> roles, String username, boolean isSysAdmin)
		throws SQLException, NamingException {
		User user = (User)getService().findByProperty(User.class, "login", username).get(0);
		
		//	set lesson beans
		List<IndexLessonBean> lessonBeans = null;
		try {
			Map<Long, IndexLessonBean> map = populateLessonBeans(user.getUserId(), org.getOrganisationId(), roles);
			lessonBeans = IndexUtils.sortLessonBeans(org.getOrderedLessonIds(), map);
		} catch (Exception e) {
			log.error("Failed retrieving user's lessons from database: " + e, e);
		}
		orgBean.setLessons(lessonBeans);

		// create subgroup beans
		if(orgBean.getType().equals(OrganisationType.COURSE_TYPE)){
			Set<Organisation> children = org.getChildOrganisations();

			List<IndexOrgBean> childOrgBeans = new ArrayList<IndexOrgBean>();
			for(Organisation organisation:children){
				if(organisation.getOrganisationState().getOrganisationStateId().equals(stateId)){
					List<Integer> classRoles = new ArrayList<Integer>();
					List<UserOrganisationRole> userOrganisationRoles = getService().getUserOrganisationRoles(organisation.getOrganisationId(),username);
					// don't list the subgroup if user is not a member, and not a group admin/manager
					if (userOrganisationRoles==null || userOrganisationRoles.isEmpty()) {
						if (!contains(roles,Role.ROLE_GROUP_ADMIN) && 
								!contains(roles,Role.ROLE_GROUP_MANAGER) &&
								!isSysAdmin) {
							continue;
						}
					}
					for(UserOrganisationRole userOrganisationRole:userOrganisationRoles){
						classRoles.add(userOrganisationRole.getRole().getRoleId());
					}
					if(contains(roles,Role.ROLE_GROUP_MANAGER)) classRoles.add(Role.ROLE_GROUP_MANAGER);
					childOrgBeans.add(createHeaderOrgBean(organisation,classRoles,username,isSysAdmin,true));
				}
			}
			Collections.sort(childOrgBeans);
			orgBean.setChildIndexOrgBeans(childOrgBeans);
		}
		return orgBean;
	}
	
	// create lesson beans
	private Map<Long, IndexLessonBean> populateLessonBeans(Integer userId, Integer orgId, List<Integer> roles) 
		throws SQLException, NamingException {
		
		// iterate through user's lessons where they are learner
		Map<Long, IndexLessonBean> map = getLessonService().getLessonsByOrgAndUserWithCompletedFlag(userId, orgId, false);
		for (IndexLessonBean bean : map.values()) {
			List<IndexLinkBean> lessonLinks = new ArrayList<IndexLinkBean>();
			String url = null;
			Integer lessonStateId = bean.getState();
			if (stateId.equals(OrganisationState.ACTIVE)) {
				if (contains(roles, Role.ROLE_LEARNER)) {
					if (lessonStateId.equals(Lesson.STARTED_STATE) || lessonStateId.equals(Lesson.FINISHED_STATE)) {
						url = "javascript:openLearner("+bean.getId()+")";
					}
				}
			} else if (stateId.equals(OrganisationState.ARCHIVED)) {
				if (contains(roles, Role.ROLE_LEARNER)) {
					if (lessonStateId.equals(Lesson.STARTED_STATE) || lessonStateId.equals(Lesson.FINISHED_STATE)) {
						lessonLinks.add(new IndexLinkBean("label.export.portfolio","javascript:openExportPortfolio("+bean.getId()+")"));
					}
				}
			}
			if (lessonLinks.size()>0 || url!=null) {
				bean.setUrl(url);
				bean.setLinks(lessonLinks);
			}
		}
		
		// iterate through user's lessons where they are staff, and add staff links to the beans in the map.
		Map<Long, IndexLessonBean> staffMap = getLessonService().getLessonsByOrgAndUserWithCompletedFlag(userId, orgId, true);
		for (IndexLessonBean bean : staffMap.values()) {
			if (map.containsKey(bean.getId())) {
				bean = map.get(bean.getId());
			}
			List<IndexLinkBean> lessonLinks = bean.getLinks();
			if (lessonLinks == null) lessonLinks = new ArrayList<IndexLinkBean>();
			if (stateId.equals(OrganisationState.ACTIVE)) {
				if (contains(roles, Role.ROLE_GROUP_MANAGER) || contains(roles, Role.ROLE_MONITOR)){
					lessonLinks.add(new IndexLinkBean("index.monitor", "javascript:openMonitorLesson(" + bean.getId()+")"));
				}
			} else if (stateId.equals(OrganisationState.ARCHIVED)) {
				if (contains(roles, Role.ROLE_GROUP_MANAGER)) {
					lessonLinks.add(new IndexLinkBean("index.monitor", "javascript:openMonitorLesson(" + bean.getId()+")"));
				}
			}
			if (lessonLinks.size() > 0) {
				bean.setLinks(lessonLinks);
			}
			map.put(bean.getId(), bean);
		}
		
		return map;
	}

	private boolean contains(List<Integer> roles, Integer roleId) {
		for (int i = 0; i < roles.size(); i++) {
			if (roleId.equals(roles.get(i)))
				return true;
		}
		return false;
	}
	
	private IUserManagementService getService(){
		if(service==null){
			WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(getServlet().getServletContext());
			service = (IUserManagementService) ctx.getBean("userManagementService");
		}
		return service;
	}
	
	private LessonService getLessonService(){
		if(lessonService==null){
			WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(getServlet().getServletContext());
			lessonService = (LessonService) ctx.getBean("lessonService");
		}
		return lessonService;
	}
}
 