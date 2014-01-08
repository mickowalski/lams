<%@ include file="/common/taglibs.jsp"%>
<c:set scope="request" var="lams"><lams:LAMSURL/></c:set>
<c:set scope="request" var="tool"><lams:WebAppURL/></c:set>

<c:set var="dto" value="${leaderselectionDTO}" />

<c:forEach var="session" items="${dto.sessionDTOs}">

	<c:if test="${isGroupedActivity}">
		<h2>
			${session.sessionName}
		</h2>
	</c:if>

	<table cellpadding="0">

		<tr>
			<th>
				<fmt:message key="heading.learner" />
			</th>
			<th>
				<fmt:message key="heading.leader" />
			</th>
		</tr>

		<c:forEach var="user" items="${session.userDTOs}">
			<tr>
				<td width="30%" style="padding: 5px 0;">
					${user.firstName} ${user.lastName}
				</td>
				<td width="70%">
					<c:choose>
						<c:when test="${session.groupLeader != null && session.groupLeader.uid == user.uid}">
							<img src="<lams:LAMSURL />images/tick.png">
						</c:when>

						<c:otherwise>
							-
						</c:otherwise>
					</c:choose>
				</td>
			</tr>
		</c:forEach>
		
	</table>
</c:forEach>

<div class="bottom-buttons">
	<a href="<c:url value='/monitoring.do'/>?dispatch=manageLeaders&sessionMapID=${sessionMapID}&KeepThis=true&TB_iframe=true&height=400&width=650" class="float-right button thickbox" title="<fmt:message key="label.manage.leaders" />">
		<fmt:message key="label.manage.leaders" />
	</a>
</div>

