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

package org.lamsfoundation.lams.admin.web.controller;

import java.util.ArrayList;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.lamsfoundation.lams.admin.service.AdminServiceProxy;
import org.lamsfoundation.lams.admin.web.dto.UserBean;
import org.lamsfoundation.lams.admin.web.form.UserOrgRoleForm;
import org.lamsfoundation.lams.usermanagement.User;
import org.lamsfoundation.lams.usermanagement.service.IUserManagementService;
import org.lamsfoundation.lams.util.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author jliew
 *
 * Saves roles for users that were just added.
 * Uses session scope because using request scope doesn't copy the form data
 * into UserOrgRoleForm's userBeans ArrayList (the list becomes empty).
 *
 */

/**
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
@Controller
public class UserOrgRoleSaveController {

    private static Logger log = Logger.getLogger(UserOrgRoleSaveController.class);
    private static IUserManagementService service;

    @Autowired
    private WebApplicationContext applicationContext;

    @Autowired
    @Qualifier("adminMessageService")
    private MessageService adminMessageService;

    @RequestMapping("/userorgrolesave")
    public String execute(@ModelAttribute UserOrgRoleForm userOrgRoleForm, HttpServletRequest request)
	    throws Exception {

	service = AdminServiceProxy.getService(applicationContext.getServletContext());

	ArrayList userBeans = userOrgRoleForm.getUserBeans();
	log.debug("userBeans is null? " + userBeans == null);
	Integer orgId = userOrgRoleForm.getOrgId();
	log.debug("orgId: " + orgId);

	request.setAttribute("org", orgId);
	request.getSession().removeAttribute("UserOrgRoleForm");

	if (request.getAttribute("CANCEL") != null) {
	    return "redirect:/usermanage.do";
	}

	// save UserOrganisation memberships, and the associated roles;
	// for subgroups, if user is not a member of the parent group then add to that as well.
	for (int i = 0; i < userBeans.size(); i++) {
	    UserBean bean = (UserBean) userBeans.get(i);
	    User user = (User) service.findById(User.class, bean.getUserId());
	    log.debug("userId: " + bean.getUserId());
	    String[] roleIds = bean.getRoleIds();
	    if (roleIds.length == 0) {
		// TODO forward to userorgrole.do, not userorg.do
		MultiValueMap<String, String> errorMap = new LinkedMultiValueMap<>();
		errorMap.add("roles", adminMessageService.getMessage("error.roles.empty"));
		request.setAttribute("errorMap", errorMap);
		request.setAttribute("orgId", orgId);
		return "redirect:/userorg.do";
	    }
	    service.setRolesForUserOrganisation(user, orgId, Arrays.asList(roleIds));
	    // FMALIKOFF 5/7/7 Commented out the following code that set the roles in the course if the current org is a class, as the logic
	    // is done in service.setRolesForUserOrganisation()
	    //if (organisation.getOrganisationType().getOrganisationTypeId().equals(OrganisationType.CLASS_TYPE)) {
	    //	if (service.getUserOrganisation(bean.getUserId(), organisation.getParentOrganisation().getOrganisationId())==null) {
	    //		service.setRolesForUserOrganisation(user, organisation.getParentOrganisation(), (List<String>)Arrays.asList(roleIds));
	    //	}
	    //}
	}
	return "redirect:/usermanage.do";
    }

}
