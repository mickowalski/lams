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

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.lamsfoundation.lams.authoring.service.IAuthoringService;
import org.lamsfoundation.lams.learningdesign.service.ILearningDesignService;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * this is an action where all lams client environments launch. initial configuration of the individual environment
 * setting is done here.
 * 
 * @struts:action path="/authoring" validate="false" parameter="method"
 * @struts:action-forward name="openAutoring" path="/author2.jsp"
 */
public class AuthoringAction extends DispatchAction {
    private static Logger log = Logger.getLogger(AuthoringAction.class);

    private static IAuthoringService authoringService;
    private static ILearningDesignService learningDesignService;

    public ActionForward openAuthoring(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws IOException {
	request.setAttribute("tools", getLearningDesignService().getToolDTOs(request.getRemoteUser()));
	return mapping.findForward("openAutoring");
    }

    private IAuthoringService getAuthoringService() {
	if (AuthoringAction.authoringService == null) {
	    WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(getServlet()
		    .getServletContext());
	    AuthoringAction.authoringService = (IAuthoringService) ctx.getBean("authoringService");
	}
	return AuthoringAction.authoringService;
    }

    private ILearningDesignService getLearningDesignService() {
	if (AuthoringAction.learningDesignService == null) {
	    WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(getServlet()
		    .getServletContext());
	    AuthoringAction.learningDesignService = (ILearningDesignService) ctx.getBean("learningDesignService");
	}
	return AuthoringAction.learningDesignService;
    }
}