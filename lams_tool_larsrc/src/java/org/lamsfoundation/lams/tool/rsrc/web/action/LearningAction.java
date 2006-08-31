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

/* $Id$ */
package org.lamsfoundation.lams.tool.rsrc.web.action;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.lamsfoundation.lams.notebook.service.CoreNotebookConstants;
import org.lamsfoundation.lams.tool.ToolAccessMode;
import org.lamsfoundation.lams.tool.rsrc.ResourceConstants;
import org.lamsfoundation.lams.tool.rsrc.model.Resource;
import org.lamsfoundation.lams.tool.rsrc.model.ResourceItem;
import org.lamsfoundation.lams.tool.rsrc.model.ResourceSession;
import org.lamsfoundation.lams.tool.rsrc.model.ResourceUser;
import org.lamsfoundation.lams.tool.rsrc.service.IResourceService;
import org.lamsfoundation.lams.tool.rsrc.service.ResourceApplicationException;
import org.lamsfoundation.lams.tool.rsrc.service.UploadResourceFileException;
import org.lamsfoundation.lams.tool.rsrc.web.form.ReflectionForm;
import org.lamsfoundation.lams.tool.rsrc.web.form.ResourceItemForm;
import org.lamsfoundation.lams.usermanagement.dto.UserDTO;
import org.lamsfoundation.lams.util.WebUtil;
import org.lamsfoundation.lams.web.session.SessionManager;
import org.lamsfoundation.lams.web.util.AttributeNames;
import org.lamsfoundation.lams.web.util.SessionMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
/**
 * 
 * @author Steve.Ni
 * 
 * @version $Revision$
 */
public class LearningAction extends Action {

	private static Logger log = Logger.getLogger(LearningAction.class);
	
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		
		String param = mapping.getParameter();
		//-----------------------Resource Learner function ---------------------------
		if(param.equals("start")){
			return start(mapping, form, request, response);
		}
		if(param.equals("complete")){
			return complete(mapping, form, request, response);
		}

		if(param.equals("finish")){
			return finish(mapping, form, request, response);
		}
		if (param.equals("addfile")) {
			return addItem(mapping, form, request, response);
		}
		if (param.equals("addurl")) {
			return addItem(mapping, form, request, response);
		}
        if (param.equals("saveOrUpdateItem")) {
        	return saveOrUpdateItem(mapping, form, request, response);
        }
        
		//================ Reflection =======================
		if (param.equals("newReflection")) {
			return newReflection(mapping, form, request, response);
		}
		if (param.equals("submitReflection")) {
			return submitReflection(mapping, form, request, response);
		}
		
		return  mapping.findForward(ResourceConstants.ERROR);
	}
	/**
	 * Initial page for add resource item (single file or URL).
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	private ActionForward addItem(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		ResourceItemForm itemForm = (ResourceItemForm) form;
		itemForm.setMode(WebUtil.readStrParam(request, AttributeNames.ATTR_MODE));
		itemForm.setSessionMapID(WebUtil.readStrParam(request, ResourceConstants.ATTR_SESSION_MAP_ID));
		return mapping.findForward(ResourceConstants.SUCCESS);
	}
	/**
	 * Read resource data from database and put them into HttpSession. It will redirect to init.do directly after this
	 * method run successfully. 
	 *  
	 * This method will avoid read database again and lost un-saved resouce item lost when user "refresh page",
	 * 
	 */
	private ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		
		//initial Session Map 
		SessionMap sessionMap = new SessionMap();
		request.getSession().setAttribute(sessionMap.getSessionID(), sessionMap);
		
		//save toolContentID into HTTPSession
		ToolAccessMode mode = WebUtil.readToolAccessModeParam(request,AttributeNames.PARAM_MODE, true);
		
		Long sessionId =  new Long(request.getParameter(ResourceConstants.PARAM_TOOL_SESSION_ID));
		
		request.setAttribute(ResourceConstants.ATTR_SESSION_MAP_ID, sessionMap.getSessionID());
		request.setAttribute(AttributeNames.ATTR_MODE,mode);
		request.setAttribute(AttributeNames.PARAM_TOOL_SESSION_ID,sessionId);
		
//		get back the resource and item list and display them on page
		IResourceService service = getResourceService();
		ResourceUser resourceUser = getCurrentUser(service,sessionId);

		List<ResourceItem> items = null;
		Resource resource;
		items = service.getResourceItemsBySessionId(sessionId);
		resource = service.getResourceBySessionId(sessionId);

		//check whehter finish lock is on/off
		boolean lock = resource.getLockWhenFinished() && resourceUser.isSessionFinished();
		
		
		//check whether there is only one resource item and run auto flag is true or not.
		boolean runAuto = false;
		int itemsNumber = 0;
		if(resource.getResourceItems() != null){
			itemsNumber = resource.getResourceItems().size();
			if(resource.isRunAuto() && itemsNumber == 1){
				ResourceItem item = (ResourceItem) resource.getResourceItems().iterator().next();
				//only visible item can be run auto.
				if(!item.isHide()){
					runAuto = true;
					request.setAttribute(ResourceConstants.ATTR_RESOURCE_ITEM_UID,item.getUid());
				}
			}
		}
		
		//basic information
		sessionMap.put(ResourceConstants.ATTR_TITLE,resource.getTitle());
		sessionMap.put(ResourceConstants.ATTR_RESOURCE_INSTRUCTION,resource.getInstructions());
		sessionMap.put(ResourceConstants.ATTR_FINISH_LOCK,lock);
		
		sessionMap.put(AttributeNames.PARAM_TOOL_SESSION_ID,sessionId);
		sessionMap.put(AttributeNames.ATTR_MODE,mode);
		//reflection information
		sessionMap.put(ResourceConstants.ATTR_REFLECTION_ON,resource.isReflectOnActivity());
		sessionMap.put(ResourceConstants.ATTR_REFLECTION_INSTRUCTION,resource.getReflectInstructions());
		sessionMap.put(ResourceConstants.ATTR_RUN_AUTO,new Boolean(runAuto));
		
		//add define later support
		if(resource.isDefineLater()){
			return mapping.findForward("defineLater");
		}
		//add run offline support
		if(resource.getRunOffline()){
			sessionMap.put(ResourceConstants.PARAM_RUN_OFFLINE, true);
			return mapping.findForward("runOffline");
		}else
			sessionMap.put(ResourceConstants.PARAM_RUN_OFFLINE, false);
				
		//init resource item list
		List<ResourceItem> resourceItemList = getResourceItemList(sessionMap);
		resourceItemList.clear();
		if(items != null){
			//remove hidden items.
			for(ResourceItem item : items){
				//becuase in webpage will use this login name. Here is just 
				//initial it to avoid session close error in proxy object. 
				item.getCreateBy().getLoginName();
				if(!item.isHide()){
					resourceItemList.add(item);
				}
			}
		}
		
		//set complete flag for display purpose
		service.retrieveComplete(resourceItemList, resourceUser);
		
		sessionMap.put(ResourceConstants.ATTR_RESOURCE,resource);
		
		//set contentInUse flag to true!
		resource.setContentInUse(true);
		resource.setDefineLater(false);
		service.saveOrUpdateResource(resource);

		
		return mapping.findForward(ResourceConstants.SUCCESS);
	}
	/**
	 * Mark resource item as complete status. 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	private ActionForward complete(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		String mode = request.getParameter(AttributeNames.ATTR_MODE);
		String sessionMapID = request.getParameter(ResourceConstants.ATTR_SESSION_MAP_ID);
		
		doComplete(request);
		
		request.setAttribute(AttributeNames.ATTR_MODE,mode);
		request.setAttribute(ResourceConstants.ATTR_SESSION_MAP_ID,sessionMapID);
		return  mapping.findForward(ResourceConstants.SUCCESS);
	}
	/**
	 * Finish learning session. 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	private ActionForward finish(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		
		//get back SessionMap
		String sessionMapID = request.getParameter(ResourceConstants.ATTR_SESSION_MAP_ID);
		SessionMap sessionMap = (SessionMap) request.getSession().getAttribute(sessionMapID);
		
		//get mode and ToolSessionID from sessionMAP
		ToolAccessMode mode = (ToolAccessMode) sessionMap.get(AttributeNames.ATTR_MODE);
		Long sessionId = (Long) sessionMap.get(AttributeNames.PARAM_TOOL_SESSION_ID);
		
		//auto run mode, when use finish the only one resource item, mark it as complete then finish this activity as well.
		String resourceItemUid = request.getParameter(ResourceConstants.PARAM_RESOURCE_ITEM_UID);
		if(resourceItemUid != null){
			doComplete(request);
			//NOTE:So far this flag is useless(31/08/2006).
			//set flag, then finish page can know redir target is parent(AUTO_RUN) or self(normal)
			request.setAttribute(ResourceConstants.ATTR_RUN_AUTO,true);
		}else
			request.setAttribute(ResourceConstants.ATTR_RUN_AUTO,false);
		
		if(!validateBeforeFinish(request, sessionMapID))
			return mapping.getInputForward();
		
		IResourceService service = getResourceService();
		// get sessionId from HttpServletRequest
		String nextActivityUrl = null ;
		try {
			HttpSession ss = SessionManager.getSession();
			UserDTO user = (UserDTO) ss.getAttribute(AttributeNames.USER);
			Long userID = new Long(user.getUserID().longValue());
			
			nextActivityUrl = service.finishToolSession(sessionId,userID);
			request.setAttribute(ResourceConstants.ATTR_NEXT_ACTIVITY_URL,nextActivityUrl);
		} catch (ResourceApplicationException e) {
			log.error("Failed get next activity url:" + e.getMessage());
		}
		
		return  mapping.findForward(ResourceConstants.SUCCESS);
	}

	
	/**
	 * Save file or url resource item into database.
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	private ActionForward saveOrUpdateItem(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		//get back SessionMap
		String sessionMapID = request.getParameter(ResourceConstants.ATTR_SESSION_MAP_ID);
		SessionMap sessionMap = (SessionMap) request.getSession().getAttribute(sessionMapID);
		request.setAttribute(ResourceConstants.ATTR_SESSION_MAP_ID, sessionMapID);
		
		Long sessionId = (Long) sessionMap.get(ResourceConstants.ATTR_TOOL_SESSION_ID);
		
		String mode = request.getParameter(AttributeNames.ATTR_MODE);
		ResourceItemForm itemForm = (ResourceItemForm)form;
		ActionErrors errors = validateResourceItem(itemForm);
		
		if(!errors.isEmpty()){
			this.addErrors(request,errors);
			return findForward(itemForm.getItemType(),mapping);
		}
		short type = itemForm.getItemType();
		
		//create a new ResourceItem
		ResourceItem item = new ResourceItem(); 
		IResourceService service = getResourceService();
		ResourceUser resourceUser = getCurrentUser(service,sessionId);
		item.setType(type);
		item.setTitle(itemForm.getTitle());
		item.setDescription(itemForm.getDescription());
		item.setCreateDate(new Timestamp(new Date().getTime()));
		item.setCreateByAuthor(false);
		item.setCreateBy(resourceUser);
		
		//special attribute for URL or FILE
		if(type == ResourceConstants.RESOURCE_TYPE_FILE){
			try {
				service.uploadResourceItemFile(item, itemForm.getFile());
			} catch (UploadResourceFileException e) {
				log.error("Failed upload Resource File " + e.toString());
				return  mapping.findForward(ResourceConstants.ERROR);
			}
		}else if(type == ResourceConstants.RESOURCE_TYPE_URL){
			item.setUrl(itemForm.getUrl());
			item.setOpenUrlNewWindow(itemForm.isOpenUrlNewWindow());
		}
		//save and update session
		
		ResourceSession resSession = service.getResourceSessionBySessionId(sessionId);
		if(resSession == null){
			log.error("Failed update ResourceSession by ID[" + sessionId + "]");
			return  mapping.findForward(ResourceConstants.ERROR);
		}
		Set<ResourceItem> items = resSession.getResourceItems();
		if(items == null){
			items = new HashSet<ResourceItem>();
			resSession.setResourceItems(items);
		}
		items.add(item);
		service.saveOrUpdateResourceSession(resSession);
		
		//update session value
		List<ResourceItem> resourceItemList = getResourceItemList(sessionMap);
		resourceItemList.add(item);
		
		//URL or file upload
		request.setAttribute(ResourceConstants.ATTR_ADD_RESOURCE_TYPE,new Short(type));
		request.setAttribute(AttributeNames.ATTR_MODE,mode);
		return  mapping.findForward(ResourceConstants.SUCCESS);
	}
	/**
	 * Display empty reflection form.
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	private ActionForward newReflection(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		
		//get session value
		String sessionMapID = WebUtil.readStrParam(request, ResourceConstants.ATTR_SESSION_MAP_ID);
		if(!validateBeforeFinish(request, sessionMapID))
			return mapping.getInputForward();

		ReflectionForm refForm = (ReflectionForm) form;
		HttpSession ss = SessionManager.getSession();
		UserDTO user = (UserDTO) ss.getAttribute(AttributeNames.USER);
		
		refForm.setUserID(user.getUserID());
		refForm.setSessionMapID(sessionMapID);
		
		return mapping.findForward(ResourceConstants.SUCCESS);
	}
	/**
	 * Submit reflection form input database.
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	private ActionForward submitReflection(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		ReflectionForm refForm = (ReflectionForm) form;
		Integer userId = refForm.getUserID();
		
		String sessionMapID = WebUtil.readStrParam(request, ResourceConstants.ATTR_SESSION_MAP_ID);
		SessionMap sessionMap = (SessionMap) request.getSession().getAttribute(sessionMapID);
		Long sessionId = (Long) sessionMap.get(AttributeNames.PARAM_TOOL_SESSION_ID);
		
		IResourceService service = getResourceService();
		service.createNotebookEntry(sessionId, 
				CoreNotebookConstants.NOTEBOOK_TOOL,
				ResourceConstants.TOOL_SIGNATURE, 
				userId,
				refForm.getEntryText());

		return finish(mapping, form, request, response);
	}
	

	//*************************************************************************************
	// Private method 
	//*************************************************************************************
	private boolean validateBeforeFinish(HttpServletRequest request, String sessionMapID) {
		SessionMap sessionMap = (SessionMap) request.getSession().getAttribute(sessionMapID);
		Long sessionId = (Long) sessionMap.get(AttributeNames.PARAM_TOOL_SESSION_ID);
		
		HttpSession ss = SessionManager.getSession();
		UserDTO user = (UserDTO) ss.getAttribute(AttributeNames.USER);
		Long userID = new Long(user.getUserID().longValue());
		
		IResourceService service = getResourceService();
		int miniViewFlag = service.checkMiniView(sessionId,userID);
		//if current user view less than reqired view count number, then just return error message.
		//if it is runOffline content, then need not check minimum view count
		Boolean runOffline = (Boolean) sessionMap.get(ResourceConstants.PARAM_RUN_OFFLINE);
		if(miniViewFlag > 0 && !runOffline){
			ActionErrors errors = new ActionErrors();
			errors.add(ActionMessages.GLOBAL_MESSAGE,new ActionMessage("lable.learning.minimum.view.number.less",miniViewFlag));
			this.addErrors(request,errors);
			return false;
		}
		
		return true;
	}
	private IResourceService getResourceService() {
	      WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(getServlet().getServletContext());
	      return (IResourceService) wac.getBean(ResourceConstants.RESOURCE_SERVICE);
	}
	/**
	 * List save current resource items.
	 * @param request
	 * @return
	 */
	private List getResourceItemList(SessionMap sessionMap) {
		return getListFromSession(sessionMap,ResourceConstants.ATTR_RESOURCE_ITEM_LIST);
	}	
	/**
	 * Get <code>java.util.List</code> from HttpSession by given name.
	 * 
	 * @param request
	 * @param name
	 * @return
	 */
	private List getListFromSession(SessionMap sessionMap,String name) {
		List list = (List) sessionMap.get(name);
		if(list == null){
			list = new ArrayList();
			sessionMap.put(name,list);
		}
		return list;
	}

	/**
	 * Return <code>ActionForward</code> according to resource item type.
	 * @param type
	 * @param mapping
	 * @return
	 */
	private ActionForward findForward(short type, ActionMapping mapping) {
		ActionForward forward;
		switch (type) {
		case ResourceConstants.RESOURCE_TYPE_URL:
			forward = mapping.findForward("url");
			break;
		case ResourceConstants.RESOURCE_TYPE_FILE:
			forward = mapping.findForward("file");
			break;
		case ResourceConstants.RESOURCE_TYPE_WEBSITE:
			forward = mapping.findForward("website");
			break;
		case ResourceConstants.RESOURCE_TYPE_LEARNING_OBJECT:
			forward = mapping.findForward("learningobject");
			break;
		default:
			forward = null;
			break;
		}
		return forward;
	}

	private ResourceUser getCurrentUser(IResourceService service, Long sessionId) {
		//try to get form system session
		HttpSession ss = SessionManager.getSession();
		//get back login user DTO
		UserDTO user = (UserDTO) ss.getAttribute(AttributeNames.USER);
		ResourceUser resourceUser = service.getUserByIDAndSession(new Long(user.getUserID().intValue()),sessionId);
		
		if(resourceUser == null){
			ResourceSession session = service.getResourceSessionBySessionId(sessionId);
			resourceUser = new ResourceUser(user,session);
			service.createUser(resourceUser);
		}
		return resourceUser;
	}
	/**
	 * @param itemForm
	 * @return
	 */
	private ActionErrors validateResourceItem(ResourceItemForm itemForm) {
		ActionErrors errors = new ActionErrors();
		if(StringUtils.isBlank(itemForm.getTitle()))
			errors.add(ActionMessages.GLOBAL_MESSAGE,new ActionMessage(ResourceConstants.ERROR_MSG_TITLE_BLANK));
		
		if(itemForm.getItemType() == ResourceConstants.RESOURCE_TYPE_URL){
			if(StringUtils.isBlank(itemForm.getUrl()))
				errors.add(ActionMessages.GLOBAL_MESSAGE,new ActionMessage(ResourceConstants.ERROR_MSG_URL_BLANK));
			//URL validation: Commom URL validate(1.3.0) work not very well: it can not support http://address:port format!!!
//			UrlValidator validator = new UrlValidator();
//			if(!validator.isValid(itemForm.getUrl()))
//				errors.add(ActionMessages.GLOBAL_MESSAGE,new ActionMessage(ResourceConstants.ERROR_MSG_INVALID_URL));
		}
//		if(itemForm.getItemType() == ResourceConstants.RESOURCE_TYPE_WEBSITE 
//				||itemForm.getItemType() == ResourceConstants.RESOURCE_TYPE_LEARNING_OBJECT){
			if(StringUtils.isBlank(itemForm.getDescription()))
				errors.add(ActionMessages.GLOBAL_MESSAGE,new ActionMessage(ResourceConstants.ERROR_MSG_DESC_BLANK));
//		}
		if(itemForm.getItemType() == ResourceConstants.RESOURCE_TYPE_WEBSITE 
				||itemForm.getItemType() == ResourceConstants.RESOURCE_TYPE_LEARNING_OBJECT
				||itemForm.getItemType() == ResourceConstants.RESOURCE_TYPE_FILE){
			//for edit validate: file already exist
			if(!itemForm.isHasFile() &&
				(itemForm.getFile() == null || StringUtils.isEmpty(itemForm.getFile().getFileName())))
				errors.add(ActionMessages.GLOBAL_MESSAGE,new ActionMessage(ResourceConstants.ERROR_MSG_FILE_BLANK));
		}
		return errors;
	}
	/**
	 * Set complete flag for given resource item.
	 * @param request
	 * @param sessionId 
	 */
	private void doComplete(HttpServletRequest request) {
		//get back sessionMap
		String sessionMapID = request.getParameter(ResourceConstants.ATTR_SESSION_MAP_ID);
		SessionMap sessionMap = (SessionMap) request.getSession().getAttribute(sessionMapID);
		
		Long resourceItemUid = new Long(request.getParameter(ResourceConstants.PARAM_RESOURCE_ITEM_UID));
		IResourceService service = getResourceService();
		HttpSession ss = SessionManager.getSession();
		//get back login user DTO
		UserDTO user = (UserDTO) ss.getAttribute(AttributeNames.USER);
		
		Long sessionId =  (Long) sessionMap.get(ResourceConstants.ATTR_TOOL_SESSION_ID);
		service.setItemComplete(resourceItemUid,new Long(user.getUserID().intValue()),sessionId);
		
		//set resource item complete tag
		List<ResourceItem> resourceItemList = getResourceItemList(sessionMap);
		for(ResourceItem item:resourceItemList){
			if(item.getUid().equals(resourceItemUid)){
				item.setComplete(true);
				break;
			}
		}
	}

}
