<%-- 
Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)
License Information: http://lamsfoundation.org/licensing/lams/2.0/

  This program is free software; you can redistribute it and/or modify
  it under the terms of the GNU General Public License version 2 as
  published by the Free Software Foundation.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software
  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
  USA

  http://www.gnu.org/licenses/gpl.txt 
--%>

<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html:html locale="true">
<head>
	<html:base />
	<%@ include file="/common/header.jsp"%>
	<style>
			a, A:link, a:visited, a:active
				{color: #0000aa; text-decoration: none; font-family: Tahoma, Verdana; font-size: 15px}
			A:hover
				{color: #ff0000; text-decoration: none; font-family: Tahoma, Verdana; font-size: 15px}
		</style>
	<script language="JavaScript" type="text/JavaScript">
	        <!--
	      	parent.contentFrame.location = "<c:url value="/download/?uuid=${cpPackage.fileUuid}&preferDownload=false"/>";
	      	parent.contentFrame.focus();
	        //-->
	  </script>

	<!-- for javascript tree -->
	<script language="JavaScript" type="text/JavaScript">
	        <!--
	        <c:set var="downloadUrlPrefix">
	        	<c:url value="/download/${cpPackage.fileUuid}/${cpPackage.fileVersionId}/"/>
	        </c:set>
	        <c:set var="downloadUrlSuffix">
	        	<c:url value="?preferDownload=false"/>
	        </c:set>
			<x:parse xml="${cpPackage.organizationXml}" var="xml"/>
			<c:import var="xsl" url="xmltree.xsl"/>
			<x:transform xml="${xml}" xslt="${xsl}">
  				<x:param name="urlPrefix" value="${downloadUrlPrefix}"/>
  				<x:param name="urlSuffix" value="${downloadUrlSuffix}"/>
  			</x:transform>
  			-->
  	  </script>
	<script language="JavaScript" src="${ctxPath}/includes/javascript/tree.js"></script>
	<script language="JavaScript" src="${ctxPath}/includes/javascript/tree_tpl.js"></script>
</head>

<body>

	<table width="100%" border="0" cellspacing="3" cellpadding="3" align="center" summary="This table is being used for layout purposes only">
		<tr>
			<td>

				<c:choose>
					<c:when test="${empty cpPackage.organizationXml}">
						<p class="body">
							The content package details are missing.
						</p>
					</c:when>
					<c:otherwise>
						<h2>
							<c:out value="${cpPackage.title}" />
						</h2>
						<c:out value="${cpPackage.description}" escapeXml="false" />
						<P>

							<script language="JavaScript">
								<!--//
									new tree (TREE_ITEMS, TREE_TPL);
								//-->
							</script>
						</div>

					</c:otherwise>
				</c:choose>
			</td>
		</tr>
	</table>
</body>
</html:html>
