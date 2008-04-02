<%@ include file="/common/taglibs.jsp"%>
<c:set var="sessionMap" value="${sessionScope[sessionMapID]}" />

<script type="text/javascript">
	function refreshTopic(){
		var reqIDVar = new Date();
		location.href= "<html:rewrite page="/learning/viewTopic.do?sessionMapID=${sessionMapID}&topicID=${sessionMap.rootUid}&reqUid=" />"+reqIDVar.getTime();;
	}
</script>


<div id="content">

	<h1>
		${sessionMap.title}
	</h1>
	<div>
		<div class="right-buttons">
			<c:set var="backToWiki">
				<html:rewrite
					page="/learning/viewWiki.do?mode=${sessionMap.mode}&sessionMapID=${sessionMapID}&toolSessionID=${sessionMap.toolSessionID}" />
			</c:set>
			<html:button property="backToWiki"
				onclick="javascript:location.href='${backToWiki}';"
				styleClass="button">
				<fmt:message key="label.back.to.wiki" />
			</html:button>

		</div>
		<h2>
			<fmt:message key="title.message.view.topic" />
		</h2>

	</div>


	<%@ include file="message/topicview.jsp"%>
	<c:set var="refreshTopicURL">
	</c:set>

	<div class="space-bottom-top">

		<div class="right-buttons">
			<c:set var="backToWiki">
				<html:rewrite
					page="/learning/viewWiki.do?mode=${sessionMap.mode}&sessionMapID=${sessionMapID}&toolSessionID=${sessionMap.toolSessionID}" />
			</c:set>
			<html:button property="backToWiki"
				onclick="javascript:location.href='${backToWiki}';"
				styleClass="button">
				<fmt:message key="label.back.to.wiki" />
			</html:button>

		</div>

		<a href="javascript:refreshTopic();" class="button"> <fmt:message
				key="label.refresh" /> </a>




	</div>
</div>
