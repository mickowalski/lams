<%@ taglib uri="/WEB-INF/struts-html-el.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic-el.tld" prefix="logic-el" %>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/fmt.tld" prefix="fmt" %>
<%@ taglib uri="fck-editor" prefix="FCK" %>

<script language="JavaScript" type="text/JavaScript">
<!--

function submitDeleteFile(uuid, actionMethod) {
	document.McAuthoringForm.uuid.value=uuid; 
	submitMethod(actionMethod);
}

//-->     
</script>

				<table class="forms">
				<tr> 
					<td NOWRAP class="formlabel" valign=top>
          				<bean:message key="label.offlineInstructions" />:
          			</td>
					<td NOWRAP colspan=3 class="formcontrol" valign=top>
					<FCK:editor id="richTextOfflineInstructions" basePath="/lams/fckeditor/">
						  <c:out value="${richTextOfflineInstructions}" escapeXml="false" />						  
					</FCK:editor>
					</td> 
				</tr>

				<tr> 
					<td NOWRAP class="formlabel" valign=top>
          				<bean:message key="label.offlineFiles" />
          			</td>
          			<td colspan=3 NOWRAP valign=top> 
						<html:file  property="theOfflineFile"></html:file>
					 	<html:submit styleClass="a.button" onclick="javascript:submitMethod('submitOfflineFiles');">
								<bean:message key="button.upload"/>
						</html:submit>
					</td> 

				</tr>
				<tr> 
					<td NOWRAP align="right" valign=top>
          				<bean:message key="label.uploadedOfflineFiles" />
          			</td>
					<td NOWRAP colspan=3 align=left valign=top width="100%">
						<table align="left" border="1">
									<c:forEach var='file' items='${sessionScope.listOfflineFilesMetadata}'>
											<tr>
												<td NOWRAP valign=top>
													<c:out value="${file.filename}"/>
												</td>
												<td NOWRAP valign=top>
													<c:set var="viewURL">
														<html:rewrite page="/download/?uuid=${file.uuid}&preferDownload=false"/>
													</c:set>
													<a href="javascript:launchInstructionsPopup('<c:out value='${viewURL}' escapeXml='false'/>')">
														<bean:message key="label.view"/>
													</a>
												</td>
												<td NOWRAP valign=top>
													<c:set var="downloadURL">
															<html:rewrite page="/download/?uuid=${file.uuid}&preferDownload=true"/>
													</c:set>
													<a href="<c:out value='${downloadURL}' escapeXml='false'/>">
														<bean:message key="label.download"/>
													</a>
												</td>
												<td NOWRAP valign=top> 
													<img src="images/delete.gif" align=left onclick="javascript:submitDeleteFile('<c:out value="${file.uuid}"/>','deleteOfflineFile');">													
												</td>
											</tr>
				         			</c:forEach>
	         			</table>
					</td> 
				</tr>
          		<tr> 
					<td NOWRAP align="right" valign=top>
          				<bean:message key="label.onlineInstructions" />
          			</td>
					<td NOWRAP colspan=3 class="formcontrol" valign=top>
					<FCK:editor id="richTextOnlineInstructions" basePath="/lams/fckeditor/">
						  <c:out value="${sessionScope.richTextOnlineInstructions}" escapeXml="false" />						  
					</FCK:editor>
					</td> 
				</tr>
				
				<tr> 
					<td NOWRAP class="formlabel" valign=top>
          				<bean:message key="label.onlineFiles" />
          			</td>
          			<td colspan=3 NOWRAP valign=top> 
						<html:file  property="theOnlineFile"></html:file>
					 	<html:submit onclick="javascript:submitMethod('submitOnlineFiles');">
								<bean:message key="button.upload"/>
						</html:submit>
					</td> 
				</tr>

				<tr>
					<td NOWRAP align="right" valign=top>
          				<bean:message key="label.uploadedOnlineFiles" />
          			</td>
          			
					<td NOWRAP colspan=3 align=left valign=top width="100%">
						<table align="left" border="1">
									<c:forEach var='file' items='${sessionScope.listOnlineFilesMetadata}'>
											<tr>
												<td NOWRAP valign=top>
													<c:out value="${file.filename}"/>
												</td>
												<td NOWRAP valign=top>
													<c:set var="viewURL">
														<html:rewrite page="/download/?uuid=${file.uuid}&preferDownload=false"/>
													</c:set>
													<a href="javascript:launchInstructionsPopup('<c:out value='${viewURL}' escapeXml='false'/>')">
														<bean:message key="label.view"/>
													</a>
												</td>
												<td NOWRAP valign=top>
													<c:set var="downloadURL">
															<html:rewrite page="/download/?uuid=${file.uuid}&preferDownload=true"/>
													</c:set>
													<a href="<c:out value='${downloadURL}' escapeXml='false'/>">
														<bean:message key="label.download"/>
													</a>
												</td>
												<td NOWRAP valign=top> 
													<img src="images/delete.gif" align=left onclick="javascript:submitDeleteFile('<c:out value="${file.uuid}"/>','deleteOnlineFile');">													
												</td>
											</tr>
				         			</c:forEach>
	         			</table>
					</td> 
				</tr>

				<html:hidden property="fileItem"/>
				<html:hidden property="offlineFile"/>				
				<html:hidden property="uuid"/>				
				
		  		<tr>
 				 	<td NOWRAP colspan=4 align=center valign=top>								
						&nbsp
				  	</td>
				</tr>
 				 
 				 <tr>
				 	<td NOWRAP valign=top>								
				  	</td>
					 <td NOWRAP colspan=3 valign=top> 
						 <html:submit onclick="javascript:submitMethod('doneInstructionsTab');">
							<bean:message key="button.done"/>
						</html:submit>
					</td> 
 				 </tr>
 				 
 				 <tr>
 				 	<td NOWRAP colspan=4 align=center valign=top>								
						&nbsp
				  	</td>
				</tr>
				
				<c:if test="${requestScope.fileContentReady == 1}"> 			
					<tr>
						<td NOWRAP align="right" valign=top>
	          				 <bean:message key="label.fileContent" /> : 
	          			</td>
					
	 				 	<td NOWRAP colspan=3 align=left valign=top>								
	 				 		<table>
	 				 			<tr> 
	 				 				<td NOWRAP align=center> <font size=2> <b>
	 				 					 <c:out value="${sessionScope.fileName}"/>  
	 				 				</b> </font> </td>
				 				 </tr>
				 				 <tr>
			 				 		<td NOWRAP> <textarea ROWS=20 COLS=80><c:out value="${sessionScope.fileContent}"/> </textarea> </td>				 				 
				 				 </tr>
	 				 		</table>
					  	</td>
					</tr>
				</c:if> 			
 				 
			</table>	  	
