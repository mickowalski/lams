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


package org.lamsfoundation.lams.policies.service;

import java.util.List;

import org.lamsfoundation.lams.policies.Policy;
import org.lamsfoundation.lams.policies.PolicyConsent;
import org.lamsfoundation.lams.policies.PolicyDTO;

public interface IPolicyService {
    
    void storeUserConsent(String login, Long policyUid);
    
    Policy getPolicyByUid(Long uid);

    /**
     * Return all active policies together with how many users have consented to each of them  
     * 
     * @return
     */
    List<Policy> getAllPoliciesWithUserConsentsCount();
    
    List<Policy> getPreviousVersionsPolicies(Long policyId);
    
    boolean isPolicyConsentRequiredForUser(Integer userId);
    
    /**
     * Return all active policies together with indication which one of them has been consented by the user.
     * 
     * @param userId
     * @return
     */
    List<PolicyDTO> getPolicyDtosByUser(Integer userId);
    
    /**
     * Return all consents user has given.
     * 
     * @param userId
     * @return
     */
    List<PolicyConsent> getConsentsByUserId(Integer userId);
    
    /**
     * Changes policy status as set by the admin. In case of deactivating policy, it also removes all associated
     * consents.
     * 
     * @param policyUid
     * @param newPolicyStatus
     */
    void changePolicyStatus(Long policyUid, Integer newPolicyStatus);
}
