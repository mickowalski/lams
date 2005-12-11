<%@ taglib uri="/WEB-INF/struts-html-el.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic-el.tld" prefix="logic-el" %>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/fmt.tld" prefix="fmt" %>

	<!--options content goes here-->
				<table align=center bgcolor="#FFFFFF">
					  <tr>
					  	<td NOWRAP align=left class="input" valign=top bgColor="#333366" colspan=2> 
						  	<font size=2 color="#FFFFFF"> <b>  <bean:message key="label.assessment"/> </b> </font>
					  	</td>
					  </tr>
					  

			 		<c:if test="${sessionScope.isRetries == 'true'}"> 		
					  <tr>
					  	<td NOWRAP align=center class="input" valign=top colspan=2> 
						  	<font size=3> <b>  <bean:message key="label.withRetries"/> </b> </font>
					  	</td>
					  </tr>
					</c:if> 			
				
					<c:if test="${sessionScope.isRetries == 'false'}"> 		
					  <tr>
					  	<td NOWRAP align=center class="input" valign=top colspan=2> 
						  	<font size=3> <b>  <bean:message key="label.withoutRetries"/> </b> </font>
					  	</td>
					  </tr>
					</c:if> 			

			 		<c:if test="${sessionScope.isRetries == 'true' && sessionScope.passMark > 0}"> 		
					  <tr>
					  	<td NOWRAP align=left class="input" valign=top colspan=2> 
						  	<font size=2> <b>  <bean:message key="label.learner.message"/> (<c:out value="${sessionScope.passMark}"/><bean:message key="label.percent"/> ) 
						  	</b> </font>
					  	</td>
					  </tr>
					</c:if> 								  
				
  		  	 		<c:set var="mainQueIndex" scope="session" value="0"/>
					<c:forEach var="questionEntry" items="${sessionScope.mapQuestionContentLearner}">
					<c:set var="mainQueIndex" scope="session" value="${mainQueIndex +1}"/>
						  <tr>
						  	<td NOWRAP align=left class="input" valign=top bgColor="#999966" colspan=2> 
							  	<font color="#FFFFFF"> 
							  		<c:out value="${questionEntry.value}"/> 
							  	</font> 
						  	</td>
						  </tr>

								  								  
						  <tr>						 
							<td NOWRAP align=left>
							<table align=left>
			  		  	 		<c:set var="queIndex" scope="session" value="0"/>
								<c:forEach var="mainEntry" items="${sessionScope.mapGeneralOptionsContent}">
									<c:set var="queIndex" scope="session" value="${queIndex +1}"/>
										<c:if test="${sessionScope.mainQueIndex == sessionScope.queIndex}"> 		
									  		<c:forEach var="subEntry" items="${mainEntry.value}">
									  		

							  		  	 		<c:set var="checkedOptionFound" scope="request" value="0"/>
												<!-- traverse the selected option from here --> 									  		
	  											<c:forEach var="selectedMainEntry" items="${sessionScope.mapGeneralCheckedOptionsContent}">
														<c:if test="${selectedMainEntry.key == sessionScope.queIndex}"> 		
													  		<c:forEach var="selectedSubEntry" items="${selectedMainEntry.value}">

																<c:if test="${subEntry.key == selectedSubEntry.key}"> 		
									  							
																	<tr> 
																		<td NOWRAP align=left class="input" valign=top> 
																			<input type="checkbox" 
																			name=optionCheckBox<c:out value="${sessionScope.queIndex}"/>-<c:out value="${subEntry.key}"/>
																			onclick="javascript:document.forms[0].optionCheckBoxSelected.value=1; 
																			document.forms[0].questionIndex.value=<c:out value="${sessionScope.queIndex}"/>; 
																			document.forms[0].optionIndex.value=<c:out value="${subEntry.key}"/>;
																			document.forms[0].optionValue.value='<c:out value="${subEntry.value}"/>';
																			
																			if (this.checked == 1)
																			{
																				document.forms[0].checked.value=true;
																			}
																			else
																			{
																				document.forms[0].checked.value=false;
																			}
																			document.forms[0].submit();" CHECKED> 
																		</td> 
																		<td NOWRAP align=left class="input" valign=top> 
																			<font color="#CCCC99"> 	<c:out value="${subEntry.value}"/> </font>
																		</td>
																	</tr>	
												  		  	 		<c:set var="checkedOptionFound" scope="request" value="1"/>
				  												</c:if> 			

														</c:forEach>																						
	  												</c:if> 			
												</c:forEach>									
												<!-- till  here --> 									  					

												<c:if test="${requestScope.checkedOptionFound == 0}"> 		
																	<tr> 
																		<td NOWRAP align=left class="input" valign=top> 
																			<input type="checkbox" 
																			name=optionCheckBox<c:out value="${sessionScope.queIndex}"/>-<c:out value="${subEntry.key}"/>
																			onclick="javascript:document.forms[0].optionCheckBoxSelected.value=1; 
																			document.forms[0].questionIndex.value=<c:out value="${sessionScope.queIndex}"/>; 
																			document.forms[0].optionIndex.value=<c:out value="${subEntry.key}"/>;
																			document.forms[0].optionValue.value='<c:out value="${subEntry.value}"/>';																			

																			if (this.checked == 1)
																			{
																				document.forms[0].checked.value=true;
																			}
																			else
																			{
																				document.forms[0].checked.value=false;
																			}
																			document.forms[0].submit();"> 
																		</td> 
																		<td NOWRAP align=left class="input" valign=top> 
																			<font color="#CCCC99"> <c:out value="${subEntry.value}"/> </font>
																		</td>
																	</tr>	
  												</c:if> 			

											</c:forEach>
										</c:if> 			
								</c:forEach>
							</table>
							</td>
						</tr>
					</c:forEach>

			  	   	<tr> 
				  	   	<html:hidden property="optionCheckBoxSelected"/>
						<html:hidden property="questionIndex"/>
						<html:hidden property="optionIndex"/>
						<html:hidden property="optionValue"/>						
						<html:hidden property="checked"/>
				 		<td NOWRAP colspan=2 class="input" valign=top> 
				 		&nbsp
				 		</td>
			  	   </tr>
			  	   
	  	   		  <tr>
				  	<td NOWRAP colspan=2 align=right class="input" valign=top> 
			  			<html:submit property="continueOptionsCombined" styleClass="a.button">
							<bean:message key="button.continue"/>
						</html:submit>	 				 		  					
				  	 </td>
				  </tr>
				</table>
	<!--options content ends here-->

