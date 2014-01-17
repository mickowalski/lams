package org.lamsfoundation.lams.tool.mc.service;

import org.lamsfoundation.lams.learningdesign.service.ToolContentVersionFilter;
import org.lamsfoundation.lams.tool.mc.pojos.McContent;
import org.lamsfoundation.lams.tool.mc.pojos.McOptsContent;
import org.lamsfoundation.lams.tool.mc.pojos.McQueContent;
import org.lamsfoundation.lams.tool.mc.pojos.McUsrAttempt;

/**
 * Import filter class for different version of MC content.
 * 
 * @author steven
 * 
 */
public class McImportContentVersionFilter extends ToolContentVersionFilter {

    /**
     * Import 1.0 version content to 1.1 version tool server.
     * 
     */
    public void up10To20061015() {
	this.removeField(McQueContent.class, "weight");
	this.removeField(McUsrAttempt.class, "timeZone");

    }

    public void up20061015To20061113() {
	// Change name to suit the version you give the tool.

	this.addField(McContent.class, "showMarks", new Boolean(false));
	this.addField(McContent.class, "randomize", new Boolean(false));
	this.addField(McOptsContent.class, "displayOrder", new Integer(0));
    }

    public void up20061113To20070820() {
	// Adds displayAnswers LDEV-1156

	this.addField(McContent.class, "displayAnswers", new Boolean(true));
    }
    
    /**
     * Import 20140101 version content to 20140102 version tool server.
     */
    public void up20140101To20140102() {
	this.removeField(McContent.class, "runOffline");
	this.removeField(McContent.class, "onlineInstructions");
	this.removeField(McContent.class, "offlineInstructions");
	this.removeField(McContent.class, "mcAttachments");
    }
}
