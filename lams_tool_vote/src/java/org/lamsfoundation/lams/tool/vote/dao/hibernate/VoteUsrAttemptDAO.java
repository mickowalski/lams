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
 * ***********************************************************************/

package org.lamsfoundation.lams.tool.vote.dao.hibernate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.FlushMode;
import org.lamsfoundation.lams.tool.vote.dao.IVoteUserDAO;
import org.lamsfoundation.lams.tool.vote.dao.IVoteUsrAttemptDAO;
import org.lamsfoundation.lams.tool.vote.pojos.VoteQueUsr;
import org.lamsfoundation.lams.tool.vote.pojos.VoteUsrAttempt;
import org.lamsfoundation.lams.tool.vote.service.IVoteService;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * @author Ozgur Demirtas repaired by lfoxton
 *         <p>
 *         Hibernate implementation for database access to VoteUsrAttemptDAO for
 *         the voting tool.
 *         </p>
 */
public class VoteUsrAttemptDAO extends HibernateDaoSupport implements IVoteUsrAttemptDAO {
    static Logger logger = Logger.getLogger(VoteUsrAttemptDAO.class.getName());

    private IVoteUserDAO voteUserDAO;

    private static final String LOAD_ATTEMPT_FOR_QUE_CONTENT = "from voteUsrAttempt in class VoteUsrAttempt where voteUsrAttempt.queUsrId=:queUsrId and voteUsrAttempt.voteQueContentId=:voteQueContentId";

    private static final String LOAD_ATTEMPT_FOR_USER = "from voteUsrAttempt in class VoteUsrAttempt where voteUsrAttempt.queUsrId=:queUsrId";

    private static final String LOAD_ATTEMPT_FOR_QUESTION_CONTENT = "from voteUsrAttempt in class VoteUsrAttempt where voteUsrAttempt.voteQueContentId=:voteQueContentId";

    private static final String LOAD_ATTEMPT_FOR_USER_AND_QUESTION_CONTENT = "from voteUsrAttempt in class VoteUsrAttempt where voteUsrAttempt.queUsrId=:queUsrId and voteUsrAttempt.voteQueContentId=:voteQueContentId";

    private static final String LOAD_ATTEMPT_FOR_USER_AND_QUESTION_CONTENT_AND_SESSION = "from voteUsrAttempt in class VoteUsrAttempt where voteUsrAttempt.queUsrId=:queUsrId and voteUsrAttempt.voteQueContentId=:voteQueContentId";

    private static final String LOAD_ATTEMPT_FOR_USER_AND_SESSION = "from voteUsrAttempt in class VoteUsrAttempt where voteUsrAttempt.queUsrId=:queUsrId";

    private static final String LOAD_USER_ENTRIES = "select distinct voteUsrAttempt.userEntry from VoteUsrAttempt voteUsrAttempt";

    private static final String LOAD_USER_ENTRY_RECORDS = "from voteUsrAttempt in class VoteUsrAttempt where voteUsrAttempt.userEntry=:userEntry and voteUsrAttempt.voteQueContentId=1 ";

    private static final String LOAD_DISTINCT_USER_ENTRY_RECORDS = "select distinct voteUsrAttempt.queUsrId from VoteUsrAttempt voteUsrAttempt where voteUsrAttempt.userEntry=:userEntry";

    // lfoxton about below:
    // No! Very Bad programmer!!! Go to your room and think about what you did!
    //private static final String LOAD_ALL_ENTRIES = "from voteUsrAttempt in class VoteUsrAttempt";

    private static final String LOAD_DISTINCT_USER_ENTRIES = "select distinct voteUsrAttempt.queUsrId from VoteUsrAttempt voteUsrAttempt";

    private static final String COUNT_ATTEMPTS_BY_CONTENT_ID = "select count(*) from VoteUsrAttempt att, VoteQueUsr user, VoteSession ses where "
	    + "att.voteQueUsr=user and user.voteSession=ses and " + "ses.voteContentId=:voteContentId";
    
    private static final String LOAD_ENTRIES_BY_SESSION_ID = "select att from VoteUsrAttempt att, VoteQueUsr user, VoteSession ses where "
	    + "att.voteQueUsr=user and user.voteSession=ses and ses.uid=:voteSessionUid";
    
    private static final String COUNT_ENTRIES_BY_SESSION_ID = "select count(*) from VoteUsrAttempt att, VoteQueUsr user, VoteSession ses where "
	    + "att.voteQueUsr=user and user.voteSession=ses and ses.uid=:voteSessionUid";


    public VoteUsrAttempt getVoteUserAttemptByUID(Long uid) {
	return (VoteUsrAttempt) this.getHibernateTemplate().get(VoteUsrAttempt.class, uid);
    }

    public VoteUsrAttempt getAttemptByUID(Long uid) {
	String query = "from VoteUsrAttempt attempt where attempt.uid=?";

	HibernateTemplate templ = this.getHibernateTemplate();
	List list = getSession().createQuery(query).setLong(0, uid.longValue()).list();

	if (list != null && list.size() > 0) {
	    VoteUsrAttempt attempt = (VoteUsrAttempt) list.get(0);
	    return attempt;
	}
	return null;
    }

    public void saveVoteUsrAttempt(VoteUsrAttempt voteUsrAttempt) {
	this.getHibernateTemplate().save(voteUsrAttempt);
    }

    public List getAttemptsForUser(final Long queUsrId) {
	HibernateTemplate templ = this.getHibernateTemplate();
	List list = getSession().createQuery(LOAD_ATTEMPT_FOR_USER).setLong("queUsrId", queUsrId.longValue()).list();
	return list;
    }

    public int getUserEnteredVotesCountForContent(final Long voteContentUid) {
	List result = getSession().createQuery(COUNT_ATTEMPTS_BY_CONTENT_ID).setLong("voteContentId", voteContentUid)
		.list();
        Integer resultInt = (result.get(0) != null) ? (Integer) result.get(0) : new Integer(0);
        return resultInt.intValue();
    }

    public Set getUserEntries() {
	HibernateTemplate templ = this.getHibernateTemplate();
	List list = getSession().createQuery(LOAD_USER_ENTRIES).list();

	Set set = new HashSet();

	Set userEntries = new HashSet();
	if (list != null && list.size() > 0) {
	    Iterator listIterator = list.iterator();
	    while (listIterator.hasNext()) {
		String entry = (String) listIterator.next();
		logger.debug("entry: " + entry);
		if ((entry != null) && (entry.length() > 0))
		    userEntries.add(entry);
	    }
	}
	return userEntries;
    }

    public List getSessionUserEntries(final Long voteSessionUid) {
	
	return getSession().createQuery(LOAD_ENTRIES_BY_SESSION_ID).setLong("voteSessionUid", voteSessionUid).list();
    }

    public Set getSessionUserEntriesSet(final Long voteSessionUid) {
	List<VoteUsrAttempt> list = (List<VoteUsrAttempt>)getSessionUserEntries(voteSessionUid);
	Set<VoteUsrAttempt> sessionUserEntries = new HashSet();
	for (VoteUsrAttempt att :  list)
	{
	    sessionUserEntries.add(att);
	}
	return sessionUserEntries;
    }

    public int getUserRecordsEntryCount(final String userEntry) {
	HibernateTemplate templ = this.getHibernateTemplate();
	List list = getSession().createQuery(LOAD_DISTINCT_USER_ENTRY_RECORDS).setString("userEntry", userEntry).list();

	if (list != null && list.size() > 0) {
	    return list.size();
	}

	return 0;
    }

    public int getSessionUserRecordsEntryCount(final String userEntry, final Long voteSessionUid,
	    IVoteService voteService) {
	HibernateTemplate templ = this.getHibernateTemplate();
	List list = getSession().createQuery(LOAD_DISTINCT_USER_ENTRY_RECORDS).setString("userEntry", userEntry).list();

	int entryCount = 0;

	if (list != null && list.size() > 0) {
	    Iterator listIterator = list.iterator();
	    while (listIterator.hasNext()) {
		Long userId = (Long) listIterator.next();
		logger.debug("userId: " + userId);
		logger.debug("voteService: " + voteService);
		VoteQueUsr voteQueUsr = voteService.getVoteUserByUID(userId);
		logger.debug("voteQueUsr: " + voteQueUsr);

		if (voteQueUsr.getVoteSession().getUid().toString().equals(voteSessionUid.toString())) {
		    ++entryCount;
		}
	    }
	}
	return entryCount;
    }

    public void removeAttemptsForUser(final Long queUsrId) {
	HibernateTemplate templ = this.getHibernateTemplate();
	List list = getSession().createQuery(LOAD_ATTEMPT_FOR_USER).setLong("queUsrId", queUsrId.longValue()).list();

	if (list != null && list.size() > 0) {
	    Iterator listIterator = list.iterator();
	    while (listIterator.hasNext()) {
		VoteUsrAttempt attempt = (VoteUsrAttempt) listIterator.next();
		this.getSession().setFlushMode(FlushMode.AUTO);
		templ.delete(attempt);
		templ.flush();
	    }
	}
    }

    public void removeAttemptsForUserandSession(final Long queUsrId, final Long voteSessionId) {
	String strGetUser = "from voteUsrAttempt in class VoteUsrAttempt where voteUsrAttempt.queUsrId=:queUsrId";
	HibernateTemplate templ = this.getHibernateTemplate();
	List list = getSession().createQuery(strGetUser).setLong("queUsrId", queUsrId.longValue()).list();

	if (list != null && list.size() > 0) {
	    Iterator listIterator = list.iterator();
	    while (listIterator.hasNext()) {
		VoteUsrAttempt attempt = (VoteUsrAttempt) listIterator.next();
		if (attempt.getVoteQueUsr().getVoteSession().getUid().toString().equals(voteSessionId.toString())) {
		    this.getSession().setFlushMode(FlushMode.AUTO);
		    templ.delete(attempt);
		    templ.flush();

		}
	    }
	}
    }

    public int getAttemptsForQuestionContent(final Long voteQueContentId) {
	HibernateTemplate templ = this.getHibernateTemplate();
	List list = getSession().createQuery(LOAD_ATTEMPT_FOR_QUESTION_CONTENT).setLong("voteQueContentId",
		voteQueContentId.longValue()).list();

	if (list != null && list.size() > 0) {
	    return list.size();
	}

	return 0;
    }

    public int getStandardAttemptsForQuestionContentAndSessionUid(final Long voteQueContentId, final Long voteSessionUid) {
	HibernateTemplate templ = this.getHibernateTemplate();
	List list = getSession().createQuery(LOAD_ATTEMPT_FOR_QUESTION_CONTENT).setLong("voteQueContentId",
		voteQueContentId.longValue()).list();

	List userEntries = new ArrayList();
	if (list != null && list.size() > 0) {
	    Iterator listIterator = list.iterator();
	    while (listIterator.hasNext()) {
		VoteUsrAttempt attempt = (VoteUsrAttempt) listIterator.next();
		if (attempt.getVoteQueUsr().getVoteSession().getUid().toString().equals(voteSessionUid.toString())) {
		    userEntries.add(attempt);
		}
	    }
	}
	return userEntries.size();

    }

    public List getStandardAttemptUsersForQuestionContentAndSessionUid(final Long voteQueContentId,
	    final Long voteSessionUid) {
	HibernateTemplate templ = this.getHibernateTemplate();
	List list = getSession().createQuery(LOAD_ATTEMPT_FOR_QUESTION_CONTENT).setLong("voteQueContentId",
		voteQueContentId.longValue()).list();

	List userEntries = new ArrayList();
	if (list != null && list.size() > 0) {
	    Iterator listIterator = list.iterator();
	    while (listIterator.hasNext()) {
		VoteUsrAttempt attempt = (VoteUsrAttempt) listIterator.next();
		if (attempt.getVoteQueUsr().getVoteSession().getUid().toString().equals(voteSessionUid.toString())) {
		    userEntries.add(attempt);
		}
	    }
	}
	return userEntries;

    }

    public boolean isVoteVisibleForSession(final String userEntry, final Long voteSessionUid) {
	HibernateTemplate templ = this.getHibernateTemplate();
	List list = getSession().createQuery(LOAD_USER_ENTRY_RECORDS).setString("userEntry", userEntry).list();

	List sessionUserEntries = new ArrayList();
	if (list != null && list.size() > 0) {
	    Iterator listIterator = list.iterator();
	    while (listIterator.hasNext()) {
		VoteUsrAttempt attempt = (VoteUsrAttempt) listIterator.next();
		logger.debug("attempt: " + attempt);
		if (attempt.getVoteQueUsr().getVoteSession().getUid().toString().equals(voteSessionUid.toString())) {
		    boolean isVoteVisible = attempt.isVisible();
		    logger.debug("isVoteVisible: " + isVoteVisible);
		    if (isVoteVisible == false)
			return false;
		}
	    }
	}

	return true;
    }

    public int getStandardAttemptsForQuestionContentAndContentUid(final Long voteQueContentId, final Long voteContentUid) {
	HibernateTemplate templ = this.getHibernateTemplate();
	List list = getSession().createQuery(LOAD_ATTEMPT_FOR_QUESTION_CONTENT).setLong("voteQueContentId",
		voteQueContentId.longValue()).list();

	List userEntries = new ArrayList();
	if (list != null && list.size() > 0) {
	    Iterator listIterator = list.iterator();
	    while (listIterator.hasNext()) {
		VoteUsrAttempt attempt = (VoteUsrAttempt) listIterator.next();

		if (attempt.getVoteQueUsr().getVoteSession().getVoteContent().getUid().toString().equals(
			voteContentUid.toString())) {
		    userEntries.add(attempt);
		}
	    }
	}
	return userEntries.size();

    }

    public List getAttemptsForUserAndQuestionContent(final Long queUsrId, final Long voteQueContentId) {
	HibernateTemplate templ = this.getHibernateTemplate();
	List list = getSession().createQuery(LOAD_ATTEMPT_FOR_USER_AND_QUESTION_CONTENT).setLong("queUsrId",
		queUsrId.longValue()).setLong("voteQueContentId", voteQueContentId.longValue()).list();

	return list;
    }

    public VoteUsrAttempt getAttemptsForUserAndQuestionContentAndSession(final Long queUsrId,
	    final Long voteQueContentId, final Long voteSessionId) {
	HibernateTemplate templ = this.getHibernateTemplate();
	List list = getSession().createQuery(LOAD_ATTEMPT_FOR_USER_AND_QUESTION_CONTENT_AND_SESSION).setLong(
		"queUsrId", queUsrId.longValue()).setLong("voteQueContentId", voteQueContentId.longValue()).list();

	if (list != null && list.size() > 0) {
	    Iterator listIterator = list.iterator();
	    while (listIterator.hasNext()) {
		VoteUsrAttempt attempt = (VoteUsrAttempt) listIterator.next();
		logger.debug("attempt: " + attempt);
		if (attempt.getVoteQueUsr().getVoteSession().getUid().toString().equals(voteSessionId.toString())) {
		    return attempt;
		}
	    }
	}
	return null;
    }

    public Set getAttemptsForUserAndSession(final Long queUsrId, final Long voteSessionId) {
	logger.debug("starting getAttemptsForUserAndSession");
	logger.debug("queUsrId: " + queUsrId);
	logger.debug("voteSessionId: " + voteSessionId);

	HibernateTemplate templ = this.getHibernateTemplate();
	List list = getSession().createQuery(LOAD_ATTEMPT_FOR_USER_AND_SESSION).setLong("queUsrId",
		queUsrId.longValue()).list();
	logger.debug("list: " + list);

	Set userEntries = new HashSet();
	if (list != null && list.size() > 0) {
	    Iterator listIterator = list.iterator();
	    while (listIterator.hasNext()) {
		VoteUsrAttempt attempt = (VoteUsrAttempt) listIterator.next();
		logger.debug("attempt: " + attempt);

		if (attempt.getVoteQueUsr().getVoteSession().getUid().toString().equals(voteSessionId.toString())) {
		    if (!attempt.getVoteQueContentId().toString().equals("1")) {
			logger.debug("adding attempt question : " + attempt.getVoteQueContent().getQuestion());
			userEntries.add(attempt.getVoteQueContent().getQuestion());
		    }
		}
	    }
	}
	logger.debug("returning userEntries: " + userEntries);
	return userEntries;
    }

    public Set getAttemptsForUserAndSessionUseOpenAnswer(final Long queUsrId, final Long voteSessionId) {
	logger.debug("starting getAttemptsForUserAndSession");
	logger.debug("queUsrId: " + queUsrId);
	logger.debug("voteSessionId: " + voteSessionId);

	HibernateTemplate templ = this.getHibernateTemplate();
	List list = getSession().createQuery(LOAD_ATTEMPT_FOR_USER_AND_SESSION).setLong("queUsrId",
		queUsrId.longValue()).list();
	logger.debug("list: " + list);

	String openAnswer = "";
	Set userEntries = new HashSet();
	if (list != null && list.size() > 0) {
	    Iterator listIterator = list.iterator();
	    while (listIterator.hasNext()) {
		VoteUsrAttempt attempt = (VoteUsrAttempt) listIterator.next();
		logger.debug("attempt: " + attempt);

		if (attempt.getVoteQueUsr().getVoteSession().getUid().toString().equals(voteSessionId.toString())) {
		    if (!attempt.getVoteQueContentId().toString().equals("1")) {
			logger.debug("adding attempt question : " + attempt.getVoteQueContent().getQuestion());
			userEntries.add(attempt.getVoteQueContent().getQuestion());
		    } else {
			logger.debug("this is a user entered vote: " + attempt.getUserEntry());
			if (attempt.getUserEntry().length() > 0) {
			    openAnswer = attempt.getUserEntry();
			    logger.debug("adding openAnswer to userEntries: ");
			    userEntries.add(openAnswer);
			}

		    }

		}
	    }
	}

	logger.debug("final userEntries : " + userEntries);
	return userEntries;
    }

    public List getAttemptsListForUserAndQuestionContent(final Long queUsrId, final Long voteQueContentId) {
	HibernateTemplate templ = this.getHibernateTemplate();
	List list = getSession().createQuery(LOAD_ATTEMPT_FOR_USER_AND_QUESTION_CONTENT).setLong("queUsrId",
		queUsrId.longValue()).setLong("voteQueContentId", voteQueContentId.longValue()).list();
	return list;
    }

    public int getLastNominationCount(Long userId) {
	return 0;
    }

    public List getAttemptForQueContent(final Long queUsrId, final Long voteQueContentId) {
	HibernateTemplate templ = this.getHibernateTemplate();
	List list = getSession().createQuery(LOAD_ATTEMPT_FOR_QUE_CONTENT).setLong("queUsrId", queUsrId.longValue())
		.setLong("voteQueContentId", voteQueContentId.longValue()).list();
	return list;
    }

    public List getUserRecords(final String userEntry) {
	HibernateTemplate templ = this.getHibernateTemplate();
	List list = getSession().createQuery(LOAD_USER_ENTRY_RECORDS).setString("userEntry", userEntry).list();
	return list;
    }

    public List getUserEnteredVotesForSession(final String userEntry, final Long voteSessionUid) {
	HibernateTemplate templ = this.getHibernateTemplate();
	List list = getSession().createQuery(LOAD_USER_ENTRY_RECORDS).setString("userEntry", userEntry).list();

	List sessionUserEntries = new ArrayList();
	if (list != null && list.size() > 0) {
	    Iterator listIterator = list.iterator();
	    while (listIterator.hasNext()) {
		VoteUsrAttempt attempt = (VoteUsrAttempt) listIterator.next();
		logger.debug("attempt: " + attempt);
		if (attempt.getVoteQueUsr().getVoteSession().getUid().toString().equals(voteSessionUid.toString())) {
		    sessionUserEntries.add(attempt.getUserEntry());
		}
	    }
	}
	return sessionUserEntries;
    }


    public int getSessionEntriesCount(final Long voteSessionUid) {
	List result = getSession().createQuery(COUNT_ENTRIES_BY_SESSION_ID).setLong("voteSessionUid", voteSessionUid)
		.list();
	logger.debug("getSessionEntriesCount: " + result);
	Integer resultInt = (result.get(0) != null) ? (Integer) result.get(0) : new Integer(0);
	return resultInt.intValue();
    }

    public int getCompletedSessionEntriesCount(final Long voteSessionUid) {
	List<VoteUsrAttempt> list = (List<VoteUsrAttempt>)getSessionUserEntries(voteSessionUid);
	int completedSessionCount = 0;
	for(VoteUsrAttempt att : list)
	{
	    String sessionStatus = att.getVoteQueUsr().getVoteSession().getSessionStatus();
	    logger.debug("this is a completed session: " + sessionStatus);
		++completedSessionCount;
	}
	logger.debug("getCompletedSessionEntriesCount" + completedSessionCount);
	return completedSessionCount;
    }

    public void updateVoteUsrAttempt(VoteUsrAttempt voteUsrAttempt) {
	this.getSession().setFlushMode(FlushMode.AUTO);
	this.getHibernateTemplate().update(voteUsrAttempt);
    }

    public void removeVoteUsrAttemptByUID(Long uid) {
	VoteUsrAttempt votea = (VoteUsrAttempt) getHibernateTemplate().get(VoteUsrAttempt.class, uid);
	this.getSession().setFlushMode(FlushMode.AUTO);
	this.getHibernateTemplate().delete(votea);
    }

    public void removeVoteUsrAttempt(VoteUsrAttempt voteUsrAttempt) {
	this.getSession().setFlushMode(FlushMode.AUTO);
	this.getHibernateTemplate().delete(voteUsrAttempt);
    }
}