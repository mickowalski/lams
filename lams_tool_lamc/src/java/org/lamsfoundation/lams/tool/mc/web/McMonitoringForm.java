/***************************************************************************
 * Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)
 * =============================================================
 * License Information: http://lamsfoundation.org/licensing/lams/2.0/
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation.
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
/* $$Id$$ */
package org.lamsfoundation.lams.tool.mc.web;


import org.lamsfoundation.lams.tool.mc.McAppConstants;

/**
 * @author Ozgur Demirtas
 *
 * ActionForm for the Monitoring environment
 */
public class McMonitoringForm extends McAuthoringForm implements McAppConstants {
	protected String method;
	
	protected String selectedToolSessionId;
	
	protected String isToolSessionChanged;
	
	private String currentUid;

	private String editResponse;

	
	/**
	 * @return Returns the isToolSessionChanged.
	 */
	public String getIsToolSessionChanged() {
		return isToolSessionChanged;
	}
	/**
	 * @param isToolSessionChanged The isToolSessionChanged to set.
	 */
	public void setIsToolSessionChanged(String isToolSessionChanged) {
		this.isToolSessionChanged = isToolSessionChanged;
	}
	/**
	 * @return Returns the selectedToolSessionId.
	 */
	public String getSelectedToolSessionId() {
		return selectedToolSessionId;
	}
	/**
	 * @param selectedToolSessionId The selectedToolSessionId to set.
	 */
	public void setSelectedToolSessionId(String selectedToolSessionId) {
		this.selectedToolSessionId = selectedToolSessionId;
	}
	
	/**
	 * @return Returns the method.
	 */
	public String getMethod() {
		return method;
	}
	/**
	 * @param method The method to set.
	 */
	public void setMethod(String method) {
		this.method = method;
	}
    /**
     * @return Returns the currentUid.
     */
    public String getCurrentUid() {
        return currentUid;
    }
    /**
     * @param currentUid The currentUid to set.
     */
    public void setCurrentUid(String currentUid) {
        this.currentUid = currentUid;
    }
    /**
     * @return Returns the editResponse.
     */
    public String getEditResponse() {
        return editResponse;
    }
    /**
     * @param editResponse The editResponse to set.
     */
    public void setEditResponse(String editResponse) {
        this.editResponse = editResponse;
    }
}
