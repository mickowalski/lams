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
 * ************************************************************************
 */
/* $$Id$$ */ 
package org.lamsfoundation.lams.notebook.web;

import java.util.Hashtable;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.lamsfoundation.lams.notebook.model.NotebookEntry;
import org.lamsfoundation.lams.usermanagement.User;
import org.lamsfoundation.lams.notebook.service.ICoreNotebookService;
import org.lamsfoundation.lams.util.wddx.FlashMessage;
import org.lamsfoundation.lams.util.wddx.WDDXProcessor;
import org.lamsfoundation.lams.util.wddx.WDDXTAGS;
import org.lamsfoundation.lams.web.servlet.AbstractStoreWDDXPacketServlet;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Store a Notebook entry (private).
 * 
 * @author Mitchell Seaton
 *
 * @web:servlet name="storeNotebookEntry"
 * @web:servlet-mapping url-pattern="/servlet/notebook/storeNotebookEntry"
 */
public class StoreNBEntryServlet extends AbstractStoreWDDXPacketServlet {

	private static Logger log = Logger.getLogger(StoreNBEntryServlet.class);
	public static final String STORE_NBENTRY_MESSAGE_KEY = "storeNotebookEntry";
	
	/** for sending acknowledgment/error messages back to flash */
	private FlashMessage flashMessage;

	public ICoreNotebookService getNotebookService(){
		WebApplicationContext webContext = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
		return (ICoreNotebookService) webContext.getBean("coreNotebookService");		
	}

	protected String process(String entryDetails, HttpServletRequest request) 
		throws Exception
		{
				
		ICoreNotebookService notebookService = getNotebookService();
		

		Hashtable table = (Hashtable)WDDXProcessor.deserialize(entryDetails);
		NotebookEntry notebookEntry = new NotebookEntry();
		
		try {
			
			if (keyExists(table, WDDXTAGS.EXTERNAL_ID)) {
				notebookEntry.setExternalID(WDDXProcessor.convertToLong(table, WDDXTAGS.EXTERNAL_ID));
			}
			if (keyExists(table, WDDXTAGS.EXTERNAL_ID_TYPE)) {
				notebookEntry.setExternalIDType(WDDXProcessor.convertToInteger(table, WDDXTAGS.EXTERNAL_ID_TYPE));
			}
			if (keyExists(table, WDDXTAGS.EXTERNAL_SIG)) {
				notebookEntry.setExternalSignature(WDDXProcessor.convertToString(table, WDDXTAGS.EXTERNAL_SIG));
			}
			if (keyExists(table, WDDXTAGS.USER_ID)) {
				User user = (User) notebookService.getUserManagementService().findById(User.class,WDDXProcessor.convertToInteger(table, WDDXTAGS.USER_ID));
				
				notebookEntry.setUser(user);
			}
			if (keyExists(table, WDDXTAGS.TITLE)) {
				notebookEntry.setTitle(WDDXProcessor.convertToString(table, WDDXTAGS.TITLE));
			}
			if (keyExists(table, WDDXTAGS.ENTRY)) {
				notebookEntry.setEntry(WDDXProcessor.convertToString(table, WDDXTAGS.ENTRY));
			}
			
			// set date fields
			notebookEntry.setCreateDate(new Date());
			
			
			notebookService.saveOrUpdateNotebookEntry(notebookEntry);
			
		} catch ( Exception e ) {
			flashMessage = new FlashMessage(STORE_NBENTRY_MESSAGE_KEY,
											notebookService.getMessageService().getMessage("invalid.wddx.packet",new Object[]{e.getMessage()}),
											FlashMessage.ERROR);
		}
		
		flashMessage = new FlashMessage(STORE_NBENTRY_MESSAGE_KEY, notebookEntry.getUid());			

		return flashMessage.serializeMessage();
		
		}
	
	protected String getMessageKey(String designDetails, HttpServletRequest request) {
		return STORE_NBENTRY_MESSAGE_KEY;
	}
	
	/**
	 * Checks whether the hashtable contains the key specified by <code>key</code>
	 * If the key exists, returns true, otherwise return false.
	 * @param table The hashtable to check
	 * @param key The key to find
	 * @return
	 */
	private boolean keyExists(Hashtable table, String key) 
	{
	    if (table.containsKey(key))
	        return true;
	    else
	        return false;
	}

}
