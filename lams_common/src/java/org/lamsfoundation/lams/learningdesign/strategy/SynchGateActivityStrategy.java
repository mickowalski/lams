/***************************************************************************
 * Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)
 * =============================================================
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
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
 * ***********************************************************************/

package org.lamsfoundation.lams.learningdesign.strategy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.lamsfoundation.lams.learningdesign.Activity;
import org.lamsfoundation.lams.learningdesign.GateActivity;
import org.lamsfoundation.lams.usermanagement.User;


/**
 * Activity strategy that deals with the calculation specific to schedule gate 
 * activity. The major part of this strategy will be overiding the methods that 
 * defined in the abstract level.
 * 
 * @author Jacky Fang
 * @since  2005-4-6
 * @version 1.1
 * 
 */
public class SynchGateActivityStrategy extends GateActivityStrategy
{
    //---------------------------------------------------------------------
    // Overriden methods
    //---------------------------------------------------------------------
    /**
     * <p>Check up the waiting learners list and lesson learner list. If all 
     * lesson learner appears in the waiting list, we assume the open condition
     * for the sync gate is met. </p>
     * 
     * <p>Note, simply compares the size of two list might be proper. Waiting
     * learners might not want to wait any more and exit the lesson, who will
     * be removed from current lesson learner list. Therefore, it is possible
     * that the waiting learner list is larger than the current lesson learner
     * list. </p>
     * 
     * @see org.lamsfoundation.lams.learningdesign.strategy.GateActivityStrategy#isOpenConditionMet()
     */
    protected boolean isOpenConditionMet(GateActivity activity,
                                         List lessonLearners)
    {
        for(Iterator i = lessonLearners.iterator();i.hasNext();)
        {
            User learner = (User)i.next();
            if (!activity.getWaitingLearners().contains(learner))
                return false;
        }
        return true;
    }
    
    /**
     * @see org.lamsfoundation.lams.learningdesign.strategy.SimpleActivityStrategy#setUpContributionType(org.lamsfoundation.lams.learningdesign.Activity, java.util.ArrayList)
     */
    protected void setUpContributionType(Activity activity,
                                         ArrayList contributionTypes)
    {
        contributionTypes.add(SYNC_GATE);   
    }



}
