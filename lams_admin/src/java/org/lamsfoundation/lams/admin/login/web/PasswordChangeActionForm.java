package org.lamsfoundation.lams.admin.login.web;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * @author fmalikoff
 * @Modified by Fei Yang	
 * 
 * @struts:form name="PasswordChangeActionForm"
 * 		include-pk="true"
 * 		include-all="true"
 */
public class PasswordChangeActionForm extends ActionForm {

	public static final String formName = "PasswordChangeActionForm"; // must match name in @struts:action section above

	private static Logger log = Logger.getLogger(PasswordChangeActionForm.class);

	private String oldPassword;

	private String password;

	private String passwordConfirm;

	private String login;

	public PasswordChangeActionForm() {
	}

	/**
	 * Returns the login.
	 * @return String
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * Returns the password.
	 * @return String
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Returns the passwordConfirm.
	 * @return String
	 */
	public String getPasswordConfirm() {
		return passwordConfirm;
	}

	/**
	 * Sets the login.
	 * @param login The login to set
	 */
	public void setLogin(String login) {
		this.login = StringUtils.trimToEmpty(login);
	}

	/**
	 * Sets the password.
	 * @param password The password to set
	 */
	public void setPassword(String password) {
		this.password = StringUtils.trimToEmpty(password);
	}

	/**
	 * Sets the passwordConfirm.
	 * @param passwordConfirm The passwordConfirm to set
	 */
	public void setPasswordConfirm(String passwordConfirm) {
		this.passwordConfirm = StringUtils.trimToEmpty(passwordConfirm);
	}

	/**
	 * @return Returns the oldPassword.
	 */
	public String getOldPassword() {
		return oldPassword;
	}

	/**
	 * @param oldPassword The oldPassword to set.
	 */
	public void setOldPassword(String oldPassword) {
		this.oldPassword = StringUtils.trimToEmpty(oldPassword);
	}

	/**
	 * Reset all properties to their default values.
	 *
	 * @param mapping The mapping used to select this instance
	 * @param request The servlet request we are processing
	 */
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		setOldPassword(null);
		setPassword(null);
		setPasswordConfirm(null);
	}

	/**
	 * Validate the properties that have been set from this HTTP request,
	 * and return an <code>ActionErrors</code> object that encapsulates any
	 * validation errors that have been found.  If no errors are found, return
	 * <code>null</code> or an <code>ActionErrors</code> object with no
	 * recorded error messages.
	 * <p>
	 * @param mapping The mapping used to select this instance
	 * @param request The servlet request we are processing
	 */
	public ActionErrors validate(ActionMapping mapping,
			HttpServletRequest request) {
		ActionErrors errors = super.validate(mapping, request);

		if (errors == null)
			errors = new ActionErrors();

		if (getPassword() == null || getPassword().length() == 0
				|| !getPassword().equals(getPasswordConfirm())) {
			errors.add("password", new ActionMessage("error.newpassword.mismatch"));
		}

		if (errors.isEmpty())
			return null;
		else {
			// don't want to pass back what they had as the password
			setOldPassword(null);
			setPassword(null);
			setPasswordConfirm(null);
			return errors;
		}

	}

}