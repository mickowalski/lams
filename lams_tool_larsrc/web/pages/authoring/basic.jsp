<%@ include file="/common/taglibs.jsp" %>
<script lang="javascript">
<!-- Common Javascript functions for LAMS -->

	/**
	 * Launches the popup window for the instruction files
	 */
	function showMessage(url) {
		var area=document.getElementById("reourceInputArea");
		area.style.width="100%";
		area.style.height="100%";
		area.src=url;
		area.style.display="block";
	}
	function hideMessage(){
		var area=document.getElementById("reourceInputArea");
		area.style.width="0px";
		area.style.height="0px";
		area.style.display="none";
	}
	function launchPopup(url,title) {
		var wd = null;
		if(wd && wd.open && !wd.closed){
			wd.close();
		}
		wd = window.open(url,title,'resizable,width=796,height=570,scrollbars');
		wd.window.focus();
	}
	function verifyUrl(myUrl, title){
		launchPopup(myUrl,title);
	}
	
	function previewItem(idx,uuid,versionId){
		//2:file, 3:website,4:learning object
		if(idx == 2){
		}else if(idx ==3){
			var myUrl = "<c:url value='/download/?uuid='/>"+uuid+"&preferDownload=false";
			launchPopup(myUrl,"Website");
		}else if(idx ==4){
			var myUrl = "<c:url value='/download/?uuid='/>"+uuid+"&preferDownload=false";
			launchPopup(myUrl,"LearningObject");
		}
		
	}
	
	function editItem(idx){
		 var reqIDVar = new Date();
		var url = "<c:url value="/authoring/editItemInit.do?itemIndex="/>" + idx +"&reqID="+reqIDVar.getTime();
		showMessage(url);
	}
	//The panel of resource list panel
	var resourceListTargetDiv = "resourceListArea";
	function deleteItem(idx){
		var url = "<c:url value="/authoring/removeItem.do"/>";
	    var reqIDVar = new Date();
		var param = "itemIndex=" + idx +"&reqID="+reqIDVar.getTime();
		deleteItemLoading();
	    var myAjax = new Ajax.Updater(
		    	resourceListTargetDiv,
		    	url,
		    	{
		    		method:'get',
		    		parameters:param,
		    		onComplete:deleteItemComplete,
		    		evalScripts:true
		    	}
	    );
	}
	function deleteItemLoading(){
		showBusy(resourceListTargetDiv);
	}
	function deleteItemComplete(){
		hideBusy(resourceListTargetDiv);
	
	}
	function showBusy(targetDiv){
		if($(targetDiv+"_Busy") != null){
			Element.show(targetDiv+"_Busy");
		}
	}
	function hideBusy(targetDiv){
		if($(targetDiv+"_Busy") != null){
			Element.hide(targetDiv+"_Busy");
		}				
	}
</script>
	<!---------------------------Basic Tab Content ------------------------>
	<table class="forms">
		<tr>
			<td class="formlabel"><fmt:message key="label.authoring.basic.title" />:</td>
			<td NOWRAP width="700"><lams:SetEditor id="Title" text="${authoring.title}" small="true"/></td>
		</tr>
		<tr>
			<td class="formlabel"><fmt:message key="label.authoring.basic.instruction" />:</td>
			<td NOWRAP width="700">
				<lams:SetEditor id="Instructions" text="${authoring.instruction}"/>
			</td>
		</tr>
		<tr>
			<td colspan="2">
				<div id="resourceListArea">
						<%@ include file="/pages/authoring/parts/itemlist.jsp"%>
				</div>
			</td>
		</tr>
		<tr>
			<td colspan="2">
				<table class="forms">
					<tr>
						<td>
							<a href="javascript:showMessage('<html:rewrite page="/authoring/newItemInit.do?itemType=1"/>');">
								<fmt:message key="label.authoring.basic.add.url" />
							</a>
						</td>
						<td>
							<a href="javascript:showMessage('<html:rewrite page="/authoring/newItemInit.do?itemType=2"/>');">
								<fmt:message key="label.authoring.basic.add.file" />
							</a>
						</td>
						<td>
							<a href="javascript:showMessage('<html:rewrite page="/authoring/newItemInit.do?itemType=3"/>');">
								<fmt:message key="label.authoring.basic.add.website" />
							</a>
						</td>
						<td>
							<a href="javascript:showMessage('<html:rewrite page="/authoring/newItemInit.do?itemType=4"/>');">
								<fmt:message key="label.authoring.basic.add.learning.object"/>
							</a>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td  colspan="2">
				<iframe onload="javascript:this.style.height=this.contentWindow.document.body.scrollHeight+'px'" 
					id="reourceInputArea" name="reourceInputArea" style="width:0px;height:0px;border:0px;display:none" frameborder="no" scrolling="no">
				</iframe>
			</td>
		</tr>
	</table>
