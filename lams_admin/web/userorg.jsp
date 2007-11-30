<%@ include file="/taglibs.jsp"%>

<script language="JavaScript" type="text/javascript" src="<lams:LAMSURL/>/includes/javascript/jquery-1.1.4.pack.js"></script>
<script language="JavaScript" type="text/javascript" src="<lams:LAMSURL/>/includes/javascript/jquery.tablesorter.pack.js"></script>
<script language="JavaScript" type="text/javascript" src="<lams:LAMSURL/>/includes/javascript/jquery.tablesorter.pager.js"></script>
<script>
	<!--
	jQuery(document).ready(function() {
		jQuery("table").tablesorter({widthFixed:true, sortList:[[0,0]]}).tablesorterPager({container: jQuery("#pager")});
	});
	
	function toggleCheckboxes(){
		<logic:iterate id="user" name="userlist" indexId="index" >
		document.UserOrgForm.userIds[<bean:write name="index" />].checked=document.UserOrgForm.allCheckboxes.checked;
		</logic:iterate>
	}
	//-->
</script>

<html-el:form action="/userorgsave.do" method="post">
<html-el:hidden property="orgId" />
<h2 class="align-left">
	<a href="orgmanage.do?org=1"><fmt:message key="admin.course.manage" /></a>
    <logic:notEmpty name="pOrgId">
        : <a href="orgmanage.do?org=<bean:write name="pOrgId" />"><bean:write name="pOrgName"/></a>
    </logic:notEmpty>
    <logic:notEqual name="UserOrgForm" property="orgId" value="1">
		: <a href="<logic:equal name="orgType" value="3">user</logic:equal><logic:notEqual name="orgType" value="3">org</logic:notEqual>manage.do?org=<bean:write name="UserOrgForm" property="orgId" />">
		<bean:write name="UserOrgForm" property="orgName"/></a>
	</logic:notEqual>
	<logic:equal name="UserOrgForm" property="orgId" value="1">
		: <a href="usermanage.do?org=<bean:write name="UserOrgForm" property="orgId" />"><fmt:message key="admin.global.roles.manage" /></a>
	</logic:equal>
    : <fmt:message key="admin.user.add"/>
</h2>

<p>&nbsp;</p>

<logic:equal name="orgType" value="2">
	<p><fmt:message key="msg.remove.from.subgroups"/></p>
</logic:equal>

<div align="center"><html-el:errors/></div>
<input type="checkbox" name="allCheckboxes" onclick="toggleCheckboxes();" onkeyup="toggleCheckboxes();" />
check/uncheck all boxes
<table class="alternative-color" width=100% cellspacing="0">
<thead>
<tr>
	<th><fmt:message key="admin.user.login"/></th>
	<th><fmt:message key="admin.user.title"/></th>
	<th><fmt:message key="admin.user.first_name"/></th>
	<th><fmt:message key="admin.user.last_name"/></th>
	<th></th>
</tr>
</thead>
<tbody>
<logic:iterate id="user" name="userlist">
	<tr>
		<td>
			<bean:write name="user" property="login" />
		</td>
		<td>
			<bean:write name="user" property="title" />
		</td>
		<td>
			<bean:write name="user" property="firstName" />
		</td>
		<td>
			<bean:write name="user" property="lastName" />
		</td>
		<td>
            <html-el:multibox name="UserOrgForm" property="userIds" value="${user.userId}"/>
		</td>		
	</tr>
</logic:iterate>
</tbody>
</table>

<table>
<tr>
	<td class="align-right">
		<html-el:submit styleClass="button"><fmt:message key="admin.save"/></html-el:submit>
		<html-el:reset styleClass="button"><fmt:message key="admin.reset"/></html-el:reset>
		<html-el:cancel styleClass="button"><fmt:message key="admin.cancel"/></html-el:cancel>
	</td>
</tr>
</table>
</html-el:form>

<div id="pager" class="pager">
	<form>
		<img src="<lams:LAMSURL/>/images/first.png" class="first"/>
		<img src="<lams:LAMSURL/>/images/prev.png" class="prev">
		<input type="text" class="pagedisplay" disabled="disabled"/>
		<img src="<lams:LAMSURL/>/images/next.png" class="next">
		<img src="<lams:LAMSURL/>/images/last.png" class="last">
		<select class="pagesize">
			<option selected="selected"  value="10">10&nbsp;&nbsp;</option>
			<option value="20">20</option>
			<option value="30">30</option>
			<option  value="40">40</option>
			<option  value="50">50</option>
			<option  value="100">100</option>
		</select>
	</form>
</div>