package org.lamsfoundation.lams.learningdesign;

import java.io.Serializable;
import java.util.Date;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.lamsfoundation.lams.learningdesign.strategy.GroupingActivityStrategy;


/**
 * @hibernate.class
 * 
 * A GroupingActivity creates Grouping.
 */
public class GroupingActivity extends SimpleActivity implements Serializable
{
	/** The grouping_ui_id of the Grouping that this activity creates */
	private Integer createGroupingUIID;
	
    /** The grouping that this activity creates */
    public Grouping createGrouping;
    
    /** full constructor */
    public GroupingActivity(Long activityId, 
            Integer id, 
            String description, 
            String title, 
            Integer xcoord, 
            Integer ycoord, 
            Integer orderId, 
            Boolean defineLater, 
            java.util.Date createDateTime, 
            String offlineInstructions, 
			LearningLibrary learningLibrary, 
            Activity parentActivity, 
            Activity libraryActivity,
			Integer parentUIID,
            LearningDesign learningDesign, 
            Grouping grouping, 
            Integer activityTypeId,  
            Transition transitionTo,
            Transition transitionFrom,
            Grouping createGrouping,
			Integer grouping_ui_id,
			Integer create_grouping_ui_id)
    {
        super(activityId, 
                id, 
                description, 
                title, 
                xcoord, 
                ycoord, 
                orderId, 
                defineLater, 
                createDateTime, 
                offlineInstructions,  
                learningLibrary, 
                parentActivity, 
				libraryActivity,
				parentUIID,
                learningDesign, 
                grouping, 
                activityTypeId, 
                transitionTo,
				transitionFrom);
        this.createGrouping = createGrouping;        
        this.createGroupingUIID = create_grouping_ui_id;
        super.simpleActivityStrategy = new GroupingActivityStrategy();
    }
    
    /** default constructor */
    public GroupingActivity()
    {
    	super.simpleActivityStrategy = new GroupingActivityStrategy();
    }
    
    /** minimal constructor */
    public GroupingActivity(Long activityId, 
            Boolean defineLater,
            java.util.Date createDateTime,
            org.lamsfoundation.lams.learningdesign.LearningLibrary learningLibrary,
            org.lamsfoundation.lams.learningdesign.Activity parentActivity,
            org.lamsfoundation.lams.learningdesign.LearningDesign learningDesign,
            org.lamsfoundation.lams.learningdesign.Grouping grouping,
            Integer activityTypeId,
            Transition transitionTo,
            Transition transitionFrom,
            Grouping createGrouping,
			Integer grouping_ui_id,
			Integer create_grouping_ui_id)
    {
        super(activityId,
                defineLater,
                createDateTime,
                learningLibrary,
                parentActivity,
                learningDesign,
                grouping,
                activityTypeId,
                transitionTo,
				transitionFrom);
        this.createGrouping = createGrouping;        
        this.createGroupingUIID = create_grouping_ui_id;
        super.simpleActivityStrategy = new GroupingActivityStrategy();
    }
    /**
     * This function creates a deep copy of the GroupingActivity passed
     * as an argument. However each time a GroupingActivity is deep copied
     * it would result in creation of new Groups as well.
     * @param originalActivity
     * @return
     */
    public static GroupingActivity createCopy(GroupingActivity originalActivity){
    	GroupingActivity groupingActivity = new GroupingActivity();
    	
    	groupingActivity.setActivityUIID(originalActivity.getActivityUIID());
    	groupingActivity.setDescription(originalActivity.getDescription());
    	groupingActivity.setTitle(originalActivity.getTitle());
    	groupingActivity.setHelpText(originalActivity.getHelpText());
    	groupingActivity.setXcoord(originalActivity.getXcoord());
    	groupingActivity.setYcoord(originalActivity.getYcoord());    	    
    	groupingActivity.setActivityTypeId(originalActivity.getActivityTypeId());
    	groupingActivity.setGroupingSupportType(originalActivity.getGroupingSupportType());
    	groupingActivity.setApplyGrouping(originalActivity.getApplyGrouping());
    	groupingActivity.setActivityCategoryID(originalActivity.getActivityCategoryID());
    	groupingActivity.setDefineLater(originalActivity.getDefineLater());    	
    	groupingActivity.setCreateDateTime(new Date()); 
    	groupingActivity.setRunOffline(originalActivity.getRunOffline());
    	groupingActivity.setLibraryActivityUiImage(originalActivity.getLibraryActivityUiImage());    	
    	return groupingActivity;    	
    }
    public String toString()
    {
        return new ToStringBuilder(this)
        .append("activityId", getActivityId())
        .toString();
    }
    
	/**
	 * @return Returns the createGrouping.
	 */
	public Grouping getCreateGrouping() {
		return createGrouping;
	}
	/**
	 * @param createGrouping The createGrouping to set.
	 */
	public void setCreateGrouping(Grouping createGrouping) {
		this.createGrouping = createGrouping;
	}
	public Integer getCreateGroupingUIID() {
		return createGroupingUIID;
	}
	public void setCreateGroupingUIID(Integer create_grouping_ui_id) {
		this.createGroupingUIID = create_grouping_ui_id;
	}

    /**
     * @see org.lamsfoundation.lams.util.Nullable#isNull()
     */
    public boolean isNull()
    {
        return false;
    }
}
