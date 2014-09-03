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

/* $$Id$$ */
package org.lamsfoundation.lams.tool.noticeboard.dao.hibernate;

import java.util.List;

import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.lamsfoundation.lams.tool.noticeboard.NoticeboardSession;
import org.lamsfoundation.lams.tool.noticeboard.NoticeboardUser;
import org.lamsfoundation.lams.tool.noticeboard.dao.INoticeboardSessionDAO;
import org.springframework.orm.hibernate4.HibernateCallback;
import org.springframework.orm.hibernate4.HibernateTemplate;
import org.springframework.orm.hibernate4.support.HibernateDaoSupport;

/**
 * @author mtruong
 *         <p>
 *         Hibernate implementation for database access to Noticeboard sessions for the noticeboard tool.
 *         </p>
 */

public class NoticeboardSessionDAO extends HibernateDaoSupport implements INoticeboardSessionDAO {
	
    private static final String FIND_NB_SESSION = "from " + NoticeboardSession.class.getName()
	    + " as nb where nb.nbSessionId=?";
	
    private static final String LOAD_NBSESSION_BY_USER = "select ns from NoticeboardSession ns left join fetch "
        + "ns.nbUsers user where user.userId=:userId";
    
    /** @see org.lamsfoundation.lams.tool.noticeboard.dao.INoticeboardSessionDAO#findNbSessionById(java.lang.Long) */
    @SuppressWarnings("unchecked")
    @Override
    public NoticeboardSession findNbSessionById(Long nbSessionId) {
	    String query = "from NoticeboardSession nbS where nbS.nbSessionId=?";
	List<NoticeboardSession> session = (List<NoticeboardSession>) getHibernateTemplate().find(query, nbSessionId);
		
	if ((session != null) && (session.size() == 0)) {
			return null;
	} else {
	    return session.get(0);
		}
	}
	
    /** @see org.lamsfoundation.lams.tool.noticeboard.dao.INoticeboardSessionDAO#saveNbSession(org.lamsfoundation.lams.tool.noticeboard.NoticeboardSession) */
    @Override
    public void saveNbSession(NoticeboardSession nbSession) {
    	this.getHibernateTemplate().save(nbSession);
    }
    
	
    /** @see org.lamsfoundation.lams.tool.noticeboard.dao.INoticeboardSessionDAO#updateNbSession(org.lamsfoundation.lams.tool.noticeboard.NoticeboardSession) */
    @Override
    public void updateNbSession(NoticeboardSession nbSession) {
    	this.getHibernateTemplate().update(nbSession);
    }

 
    /** @see org.lamsfoundation.lams.tool.noticeboard.dao.INoticeboardSessionDAO#removeNbSession(java.lang.Long) */
    @SuppressWarnings("unchecked")
    @Override
    public void removeNbSession(Long nbSessionId) {
       
    	HibernateTemplate templ = this.getHibernateTemplate();
		if ( nbSessionId != null) {
			//String query = "from org.lamsfoundation.lams.tool.noticeboard.NoticeboardContent as nb where nb.nbContentId=?";
			List<NoticeboardSession> list = getSessionFactory().getCurrentSession().createQuery(FIND_NB_SESSION)
				.setLong(0,nbSessionId.longValue())
				.list();
			
	    if ((list != null) && (list.size() > 0)) {
				NoticeboardSession nb = (NoticeboardSession) list.get(0);
				getSessionFactory().getCurrentSession().setFlushMode(FlushMode.AUTO);
				templ.delete(nb);
				templ.flush();
			}
		}
      
    }
    
    /** @see org.lamsfoundation.lams.tool.noticeboard.dao.INoticeboardSessionDAO#removeNbSession(org.lamsfoundation.lams.tool.noticeboard.NoticeboardSession) */
    @Override
    public void removeNbSession(NoticeboardSession nbSession) {
    	removeNbSession(nbSession.getNbSessionId());
        //this.getHibernateTemplate().delete(nbSession);
    }

    
    /** @see org.lamsfoundation.lams.tool.noticeboard.dao.INoticeboardSessionDAO#getNbSessionByUser(java.lang.Long) */
    @Override
    public NoticeboardSession getNbSessionByUser(final Long userId) {
	return (NoticeboardSession) getHibernateTemplate().execute(new HibernateCallback() {

	    @Override
	    public Object doInHibernate(Session session) throws HibernateException {
		return session.createQuery(NoticeboardSessionDAO.LOAD_NBSESSION_BY_USER)
			.setLong("userId", userId.longValue()).uniqueResult();
                    }
                });
	}
	
	 
    /** @see org.lamsfoundation.lams.tool.noticeboard.dao.INoticeboardSessionDAO#removeNbUsers(org.lamsfoundation.lams.tool.noticeboard.NoticeboardSession) */
    @Override
    public void removeNbUsers(NoticeboardSession nbSession) {
    	this.getHibernateTemplate().deleteAll(nbSession.getNbUsers());
    }
	
    /**
     * @see org.lamsfoundation.lams.tool.noticeboard.dao.INoticeboardSessionDAO#addNbUsers(java.lang.Long,
     *      org.lamsfoundation.lams.tool.noticeboard.NoticeboardSession)
     */
    @Override
    public void addNbUsers(Long nbSessionId, NoticeboardUser user) {
	    NoticeboardSession session = findNbSessionById(nbSessionId);
	    user.setNbSession(session);
	    session.getNbUsers().add(user);
	    this.getHibernateTemplate().saveOrUpdate(user);
	    this.getHibernateTemplate().merge(session);	    
	}
}
