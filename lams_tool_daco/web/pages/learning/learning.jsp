<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
        "http://www.w3.org/TR/html4/strict.dtd">

<%@ include file="/common/taglibs.jsp"%>
<%@ page import="org.lamsfoundation.lams.tool.daco.DacoConstants"%>
<lams:html>
<lams:head>
	<title><fmt:message key="label.learning.title" /></title>
	
	<%@ include file="/common/tabbedheader.jsp"%>
	<c:if test="${not empty param.sessionMapID}">
		<c:set var="sessionMapID" value="${param.sessionMapID}" />
	</c:if>
	
	<c:set var="sessionMap" value="${sessionScope[sessionMapID]}" />
	<c:set var="mode" value="${sessionMap.mode}" />
	<c:set var="toolSessionID" value="${sessionMap.toolSessionID}" />
	<c:set var="daco" value="${sessionMap.daco}" />
	
	<c:if test="${empty displayedRecordNumber}">
		<c:set var="displayedRecordNumber" value="1" />
	</c:if>
	
	<c:choose>
		<c:when test="${not empty sessionMap.learningCurrentTab}">
			<c:set var="learningCurrentTab" value="${sessionMap.learningCurrentTab}" />
		</c:when>
		<c:otherwise>
			<c:set var="learningCurrentTab" value="1" />
		</c:otherwise>
	</c:choose>
	
	<script type="text/javascript">
	 	var currentTab = ${learningCurrentTab};
	 	var changeViewUrl = "<c:url value='/learning/changeView.do' />";
	 	var finishUrl = '<c:url value="/learning/finish.do?sessionMapID=${sessionMapID}"/>';
	 	var continueReflectUrl = '<c:url value="/learning/startReflection.do?sessionMapID=${sessionMapID}&displayedRecordNumber=${displayedRecordNumber}"/>';
	 	var refreshQuestionSummariesUrl = '<c:url value="/learning/refreshQuestionSummaries.do"/>';
    </script>
    <script type="text/javascript" src="<html:rewrite page='/includes/javascript/dacoLearning.js'/>"></script>
</lams:head>

<body class="stripes" onLoad="init()" id="body">
<div id="page-learner">
<h1><fmt:message key="label.learning.title" /></h1>
<div id="header-learner">
<lams:Tabs useKey="true" control="true">
	<lams:Tab id="1" key="label.learning.heading.add" />
	<lams:Tab id="2" key="label.learning.heading.list" />
	<lams:Tab id="3" key="label.learning.heading.summary" />
</lams:Tabs></div>

<div id="content-learner">
<div style="float: right; margin-left: 10px; padding-top: 4px" class="help">
	<img src="${tool}includes/images/uparrow.gif" title="<fmt:message key="label.common.view.change" />"
	 onclick="javascript:changeView('${sessionMapID}',${displayedRecordNumber})" />
</div>

<lams:help toolSignature="<%= DacoConstants.TOOL_SIGNATURE %>" module="learning" />



<c:if test="${daco.lockOnFinished and mode != 'teacher'}">
	<div class="info"><c:choose>
		<c:when test="${sessionMap.userFinished}">
			<fmt:message key="message.learning.activityLocked" />
		</c:when>
		<c:otherwise>
			<fmt:message key="message.learning.warnLockOnFinish" />
		</c:otherwise>
	</c:choose></div>
</c:if>

<lams:TabBody id="1" titleKey="label.learning.heading.add" page="addRecord.jsp?displayedRecordNumber=${displayedRecordNumber}" />
<lams:TabBody id="2" titleKey="label.learning.heading.list" page="listRecords.jsp?includeMode=learning" />
<lams:TabBody id="3" titleKey="label.learning.heading.summary" page="questionSummaries.jsp" />
		
<c:if test="${mode != 'teacher'}">
	<div class="space-top align-right space-right" style="padding-right: 20px;">
		<c:choose>
			<c:when test="${daco.reflectOnActivity && (not sessionMap.userFinished)}">
				<html:button property="FinishButton" onclick="javascript:continueReflect()" styleClass="button">
					<fmt:message key="label.learning.continue" />
				</html:button>
			</c:when>
			<c:otherwise>
				<html:button property="FinishButton" styleId="finishButton" onclick="javascript:finishSession()" styleClass="button">
					<fmt:message key="label.learning.finished" />
				</html:button>
			</c:otherwise>
		</c:choose>
	</div>
</c:if>

</div>

<div id="footer-learner"></div>
<!-- end page div --></div>
</body>
</lams:html>