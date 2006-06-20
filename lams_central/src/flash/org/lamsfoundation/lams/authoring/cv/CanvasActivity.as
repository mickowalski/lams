﻿/***************************************************************************
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
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 * 
 * http://www.gnu.org/licenses/gpl.txt
 * ************************************************************************
 */

import org.lamsfoundation.lams.common.*;
import org.lamsfoundation.lams.common.util.*;
import org.lamsfoundation.lams.common.util.ui.*;
import org.lamsfoundation.lams.authoring.*;
import org.lamsfoundation.lams.authoring.cv.*;
import org.lamsfoundation.lams.monitoring.mv.*;
import org.lamsfoundation.lams.monitoring.mv.tabviews.LearnerTabView;
import org.lamsfoundation.lams.common.style.*

import com.polymercode.Draw;
import mx.managers.*
import mx.containers.*;
import mx.events.*
import mx.utils.*

/**  
* CanvasActivity - 
*/  
class org.lamsfoundation.lams.authoring.cv.CanvasActivity extends MovieClip implements ICanvasActivity{  
//class org.lamsfoundation.lams.authoring.cv.CanvasActivity extends MovieClip{  
  
	public static var TOOL_ACTIVITY_WIDTH:Number = 123.1;
	public static var TOOL_ACTIVITY_HEIGHT:Number = 50.5;
	public static var GATE_ACTIVITY_HEIGHT:Number =30;
	public static var GATE_ACTIVITY_WIDTH:Number = 30;
	public static var ICON_WIDTH:Number = 30;
	public static var ICON_HEIGHT:Number = 30;
	
	//this is set by the init object
	private var _canvasController:CanvasController;
	private var _canvasView:CanvasView;
	private var _monitorController:MonitorController;
	private var _monitorView;
	private var _tm:ThemeManager;
	//TODO:This should be ToolActivity
	private var _activity:Activity;
	
	private var _isSelected:Boolean;
	private var app:Application;
	//locals
	private var learnerOffset_X:Number = 4
	private var learnerOffset_Y:Number = 3
	private var onActivityLearnerArray:Array;
	private var _module:String;
	private var icon_mc:MovieClip;
	private var icon_mcl:MovieClipLoader;
	private var bkg_pnl:MovieClip;
	private var act_pnl:MovieClip;
	private var title_lbl:MovieClip;
	private var groupIcon_mc:MovieClip;
	private var stopSign_mc:MovieClip;	
	private var clickTarget_mc:MovieClip;
	private var canvasActivity_mc:MovieClip;
	private var canvasActivityGrouped_mc:MovieClip;	private var _dcStartTime:Number = 0;
	private var _doubleClicking:Boolean;
	private var _visibleWidth:Number;
	private var _visibleHeight:Number;
	private var _base_mc:MovieClip;
	private var _selected_mc:MovieClip;
	
	
	
	function CanvasActivity(){
		//Debugger.log("_activity:"+_activity.title,4,'Constructor','CanvasActivity');
		_tm = ThemeManager.getInstance();
		//Get reference to application and design data model
		app = Application.getInstance();
		//let it wait one frame to set up the components.
		//this has to be set b4 the do later :)
		if(_activity.isGateActivity()){
			_visibleHeight = CanvasActivity.GATE_ACTIVITY_HEIGHT;
			_visibleWidth = CanvasActivity.GATE_ACTIVITY_WIDTH;
		}else if(_activity.isGroupActivity()){
			_visibleHeight = CanvasActivity.TOOL_ACTIVITY_HEIGHT;
			_visibleWidth = CanvasActivity.TOOL_ACTIVITY_WIDTH;
		}else{
			_visibleHeight = CanvasActivity.TOOL_ACTIVITY_HEIGHT;
			_visibleWidth = CanvasActivity.TOOL_ACTIVITY_WIDTH;
		}
		_base_mc = this;
		//call init if we have passed in the _activity as an initObj in the attach movie,
		//otherwise wait as the class outside will call it
		if(_activity != undefined){
			init();
		}
		
	}
	
	public function init(initObj):Void{
		
		clickTarget_mc.onPress = Proxy.create (this, localOnPress);
		clickTarget_mc.onRelease = Proxy.create (this, localOnRelease);
		clickTarget_mc.onReleaseOutside = Proxy.create (this, localOnReleaseOutside);
		
		if(initObj){
			
			if (_module == "monitoring"){
				_monitorView = initObj._monitorView;
				_monitorController = initObj._monitorController;
			}else {
				_canvasView = initObj._canvasView;
				_canvasController = initObj._canvasController;
			}
			_activity = initObj.activity;
		}
		
		showAssets(false);
		
		

		
		if(!_activity.isGateActivity() && !_activity.isGroupActivity()){
			loadIcon();
		}
		setStyles();
		MovieClipUtils.doLater(Proxy.create(this,draw));

	}
	
	private function showAssets(isVisible:Boolean){
		groupIcon_mc._visible = isVisible;
		title_lbl._visible = isVisible;
		act_pnl._visible = isVisible;
		icon_mc._visible = isVisible;
		stopSign_mc._visible = isVisible;
		canvasActivity_mc._visible = isVisible;
		clickTarget_mc._visible = isVisible;
		canvasActivityGrouped_mc._visible = isVisible;
	}
	
	/**
	 * Updates the CanvasActivity display fields with the current data
	 * @usage   
	 * @return  
	 */
	public function refresh():Void{
		draw();
		setSelected(_isSelected);
	}
	
	public function setSelected(isSelected){
		Debugger.log(_activity.title+" isSelected:"+isSelected,4,'setSelected','CanvasActivity');
		var MARGIN = 5;
		if(isSelected){
			//draw a selected border
			var tgt_mc;
			if(_activity.isGateActivity()){
				tgt_mc = stopSign_mc;			
			}else if(_activity.groupingUIID > 0){
				tgt_mc = canvasActivityGrouped_mc;
			}else{
				tgt_mc = canvasActivity_mc;
			}
			Debugger.log("tgt_mc:"+tgt_mc,4,'setSelected','CanvasActivity');
				//vars
				var tl_x = tgt_mc._x - MARGIN; 							//top left x
				var tl_y = tgt_mc._y - MARGIN;							//top left y
				var tr_x = tgt_mc._x + tgt_mc._width + MARGIN;//top right x
				var tr_y = tl_y;														//top right y
				var br_x = tr_x;														//bottom right x
				var br_y = tgt_mc._y + tgt_mc._height + MARGIN;//bottom right y
				var bl_x = tl_x;														//biottom left x															
				var bl_y = br_y;														//bottom left y
				
				
				//dashTo(target:MovieClip, x1:Number, y1:Number,x2:Number, y2:Number, dashLength:Number, spaceLength:Number )
				if(_selected_mc){
					_selected_mc.removeMovieClip();
				}
				_selected_mc = _base_mc.createEmptyMovieClip('_selected_mc',_base_mc.getNextHighestDepth());
				Draw.dashTo(_selected_mc,tl_x,tl_y,tr_x,tr_y,2,3,2,0x266DEE);
				Draw.dashTo(_selected_mc,tr_x,tr_y,br_x,br_y,2,3,2,0x266DEE);
				Draw.dashTo(_selected_mc,br_x,br_y,bl_x,bl_y,2,3,2,0x266DEE);
				Draw.dashTo(_selected_mc,bl_x,bl_y,tl_x,tl_y,2,3,2,0x266DEE);
				/*				Draw.dashTo(_base_mc,tl_x,tl_y,tr_x,tr_y,2,3,2,0x266DEE);
				Draw.dashTo(_base_mc,tr_x,tr_y,br_x,br_y,2,3,2,0x266DEE);
				Draw.dashTo(_base_mc,br_x,br_y,bl_x,bl_y,2,3,2,0x266DEE);
				Draw.dashTo(_base_mc,bl_x,bl_y,tl_x,tl_y,2,3,2,0x266DEE);
				*/
				
				_isSelected = isSelected;
			
			
		}else{
			//hide the selected border
			_selected_mc.removeMovieClip();
		}
		
	}
	
	
	private function loadIcon():Void{
		//Debugger.log('Running, _activity.libraryActivityUIImage:'+_activity.libraryActivityUIImage,4,'loadIcon','CanvasActivity');
		trace("Icon_MC is: "+Config.getInstance().serverUrl+_activity.libraryActivityUIImage)
		icon_mc = this.createEmptyMovieClip("icon_mc", this.getNextHighestDepth());
		var ml = new MovieLoader(Config.getInstance().serverUrl+_activity.libraryActivityUIImage,setUpActIcon,this,icon_mc);	
		//icon_mc = MovieLoader.movieCache[Config.getInstance().serverUrl+_activity.libraryActivityUIImage];
		//Debugger.log('icon_mc:'+icon_mc,4,'loadIcon','CanvasActivity');
		//setUpActIcon(icon_mc);
	}
	
	
	
	
	
	
	private function setUpActIcon(icon_mc):Void{
		icon_mc._x = (CanvasActivity.TOOL_ACTIVITY_WIDTH / 2) - (icon_mc._width / 2);
		icon_mc._y = (CanvasActivity.TOOL_ACTIVITY_HEIGHT / 2) - (icon_mc._height / 2) - 5;
	}
	
	/**
	 * Does the work of laying out the screen assets.
	 * Depending on type of Activity different bits will be shown
	 * @usage   
	 * @return  
	 */
	private function draw(){
		
		//Code for Drawing learner on the activty. 
		
		trace("value of _Module in Canvas Activity: "+_module)
		trace("Monitor Model is: "+_monitorController.getModel())
		if (_module == "monitoring"){
			onActivityLearnerArray = new Array();
			var mm:MonitorModel = MonitorModel(_monitorController.getModel());
			trace("all learner progress length in Canvas activity: "+mm.allLearnersProgress.length);
							
			// get the length of learners from the Monitor Model and run a for loop.
			for (var j=0; j<mm.allLearnersProgress.length; j++){
				var learner:Object = new Object();
				learner = mm.allLearnersProgress[j]
				
				//Gets a true if learner's currect activityID matches this activityID else false.
				
				var isLearnerCurrentAct:Boolean = Progress.isLearnerCurrentActivity(learner, _activity.activityID);
				if (isLearnerCurrentAct){
					//onActivityLearnerArray.push(learner)
					
					if (learnerOffset_X > 112){
						learnerOffset_X = 4 
						learnerOffset_Y = 27 
					}
					
					trace(_activity.title+": is the learner's current Activity.")
					 
					this.attachMovie("learnerIcon", "learnerIcon"+learner.getUserName(), this.getNextHighestDepth(),{_activity:_activity, learner:learner, _monitorController:_monitorController, _x:learnerOffset_X, _y:learnerOffset_Y});
					//var learnerIcon = this["learnerIcon"+learner.getUserName()]
					//learnerIcon.click_mc.onRollOver = function (){
					//	trace("I am: "+onActivityLearnerArray[learner.getUserName()])
					//}
					learnerOffset_X = learnerOffset_X+10
					
					
					
				}else {
					trace(_activity.title+": is not the learner's current Activity.")
				}
			}
		}
			
				Debugger.log(_activity.title+',_activity.isGateActivity():'+_activity.isGateActivity(),4,'draw','CanvasActivity');
		setStyles();
		
		var theIcon_mc:MovieClip;
		title_lbl._visible = true;
		
		clickTarget_mc._visible = true;
		
		if(_activity.isGateActivity()){
			stopSign_mc._visible = true;
			stopSign_mc._x = 0;
			stopSign_mc._y = 0;
			
			
		}else{
			//chose the icon:
			if(_activity.isGroupActivity()){
				groupIcon_mc._visible = true;
				icon_mc._visible = false;
				theIcon_mc = groupIcon_mc;
			}else{
				groupIcon_mc._visible = false;
				icon_mc._visible = true;
				theIcon_mc = icon_mc;
			}
			/*
			* some bug here - size always reported as 0x0 and wehn set to ICON_WIDTH it stays at 0, so maybe icon is still loading...
			trace(theIcon_mc._width+'x'+theIcon_mc._height );
			theIcon_mc._width = CanvasActivity.ICON_WIDTH;
			theIcon_mc._height = CanvasActivity.ICON_HEIGHT;
			trace('CanvasActivity.ICON_HEIGHT:'+CanvasActivity.ICON_HEIGHT);
			trace('ICON_HEIGHT:'+ICON_HEIGHT);
			trace(theIcon_mc._width+'x'+theIcon_mc._height );
			*/
			theIcon_mc._visible = true;
			
			
			
			//chose the background mc
			if(_activity.groupingUIID > 0){
				canvasActivityGrouped_mc._visible = true;
				canvasActivity_mc._visible=false;
			}else{
				canvasActivity_mc._visible=true;
				canvasActivityGrouped_mc._visible = false;
			}
			
			title_lbl.visible=true;
			//clickTarget_mc._visible=true;
			stopSign_mc._visible = false;
			
			//write text
			title_lbl.text = _activity.title;
			
			clickTarget_mc._width = TOOL_ACTIVITY_WIDTH;
			clickTarget_mc._height= TOOL_ACTIVITY_HEIGHT;
			
		}
	

		//position
		_x = _activity.xCoord;
		_y = _activity.yCoord;
		

		
		Debugger.log('canvasActivity_mc._visible'+canvasActivity_mc._visible,4,'draw','CanvasActivity');
		_visible = true;
	}
	
	
	private function localOnPress():Void{
		
			
			// check double-click
			var now:Number = new Date().getTime();
			
			if((now - _dcStartTime) <= Config.DOUBLE_CLICK_DELAY){
				trace("Module passed is: "+_module)
				if (app.controlKeyPressed != "transition"){
					_doubleClicking = true;
					if (_module == "monitoring"){
						Debugger.log('DoubleClicking: '+this.activity.activityID,Debugger.GEN,'onPress','CanvasActivity For Monitoring');
						_monitorController.activityDoubleClick(this, "MonitorTabView");
					}else {
						Debugger.log('DoubleClicking: '+this,Debugger.GEN,'onPress','CanvasActivity');
						_canvasController.activityDoubleClick(this);
					}
				}
				/*
				if(workspaceRef.inspecting){
			// click to zoom in / zoom out
			if(workspaceRef.zooming == "in"){
				closeZoomedActivity(this._parent);	
			}else if(workspaceRef.zooming == "out"){
				zoomActivity(this._parent, true);
			}
				*/
				
			}else{
				
				_doubleClicking = false;
				
				//Debugger.log('_canvasController:'+_canvasController,Debugger.GEN,'onPress','CanvasActivity');
				if (_module == "monitoring"){
					Debugger.log('SingleClicking:+'+this,Debugger.GEN,'onPress','CanvasActivity for monitoring');
					_monitorController.activityClick(this);
				}else {
					Debugger.log('SingleClicking:+'+this,Debugger.GEN,'onPress','CanvasActivity');
					_canvasController.activityClick(this);
				}				
				
				/*
				if (userDrawingTransition){
					debug('user drawing transition');
					userDrawTransition(this,true);
				}else if (userDrawingBox){
					workspaceBkg.onPress();
				}else{
					doMove = false;
					//move all the acts with the box
					//Mouse.addListener(moveOpActBoxListner);
					this.onEnterFrame = updateOpActPositions;
					
					draggedOptionalActivity = optActId;
					workspaceRef.selectActivity(this);
					this.startDrag(false);
					this.pickup();
				}
				*/
			}
			
			_dcStartTime = now;
	
	}
	
	private function localOnRelease():Void{
		if(!_doubleClicking){
			Debugger.log('Releasing:'+this,Debugger.GEN,'onRelease','CanvasActivity');
				trace("Activity ID is: "+this.activity.activityUIID)	
			if (_module == "monitoring"){
				_monitorController.activityRelease(this);
			}else {
				_canvasController.activityRelease(this);
			}
		}
		
	}
	
	private function localOnReleaseOutside():Void{
		Debugger.log('ReleasingOutside:'+this,Debugger.GEN,'onReleaseOutside','CanvasActivity');
		if (_module == "monitoring"){
			_monitorController.activityReleaseOutside(this);
		}else {
			_canvasController.activityReleaseOutside(this);
		}
	}
	
	
	/**
	 * 
	 * @usage   
	 * @return  
	 */
	public function getVisibleWidth ():Number {
		return _visibleWidth;
	}

	
	/**
	 * 
	 * @usage   
	 * @return  
	 */
	public function getVisibleHeight ():Number {
		return _visibleHeight;
	}

	
	
	public function get activity():Activity{
		return getActivity();
	}
	
	public function set activity(a:Activity){
		setActivity(a);
	}
	
	
	public function getActivity():Activity{
		return _activity;

	}
	
	public function setActivity(a:Activity){
		_activity = a;
	}


	/**
	 * Get the CSSStyleDeclaration objects for each component and applies them
	 * directly to the instanced
	 * @usage   
	 * @return  
	 */
	private function setStyles() {
		var styleObj = _tm.getStyleObject('label');
		
		title_lbl.setStyle('styleName',styleObj);
		title_lbl.setStyle('textAlign', 'center');
		
		styleObj = _tm.getStyleObject('ACTPanel')
		act_pnl.setStyle('styleName',styleObj);
			
    }
    

}