/****************************************************************
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301
 * USA
 *
 * http://www.gnu.org/licenses/gpl.txt
 * ****************************************************************
 */

package org.lamsfoundation.lams.tool.mindmap.dto;

import java.util.Iterator;

import org.apache.log4j.Logger;
import org.apache.tomcat.util.json.JSONException;
import org.apache.tomcat.util.json.JSONObject;
import org.lamsfoundation.lams.tool.mindmap.util.xmlmodel.NodeModel;

/** A JSONObject in the MapJs v3 format as per https://github.com/mindmup/mapjs/wiki/Data-Format.
 * Converts from our existing XML Model to JSON and back again. 
 * 
 * If matchUserId is not null then check if this user can edit. If it is null, any user can edit. */

//    aggregate root
//    
//    {
//    formatVersion:3, /*numeric, only applicable to root idea*/
//    id: _aggregate idea id_, /* alphanumeric */
//    attr: {}, /* aggregate attributes, such as the map theme */
//    ideas: { _rank_: {_root idea_}, _rank2_: {_root idea 2_} ... }, /* key-value map of root nodes */ 
//    }
//    

public class RootJSON extends JSONObject {

    public static final String MAPJS_JSON_ROOT_ID_VALUE = "root";


    private static Logger logger = Logger.getLogger(RootJSON.class);
    
    // TODO support more than one main node. mindmup does it, our database code does not
    /** Create JSON objects from the database model */
    public RootJSON(NodeModel node, boolean includeCreator) throws JSONException {
	super();
	
	// create special root level JSON object
	this.put(IdeaJSON.MAPJS_JSON_ID_KEY, MAPJS_JSON_ROOT_ID_VALUE);
	this.put("formatVersion", "3");

	if ( node != null) {
	    this.put(IdeaJSON.MAPJS_JSON_TITLE_KEY, node.getConcept().getText());
	}

	// start the recursion to create the ideas objects
	JSONObject ideas = new JSONObject();
	ideas.put("1", new IdeaJSON(node, 0, includeCreator) );
	this.put(IdeaJSON.MAPJS_JSON_IDEAS_KEY, ideas);
    }

    /** Deserialise the JSON from the client  */
    public static NodeModel toNodeModel(String jsonContent) throws JSONException {

	JSONObject rootModel = new JSONObject(jsonContent);
	if ( ! MAPJS_JSON_ROOT_ID_VALUE.equals(rootModel.get(IdeaJSON.MAPJS_JSON_ID_KEY)) )
	    throw new JSONException("Root idea missing. Unable to parse mindmap. "+jsonContent);

	JSONObject ideasObject = rootModel.getJSONObject(IdeaJSON.MAPJS_JSON_IDEAS_KEY); 
	
	if ( ideasObject.length() == 0 ) {
	    logger.error("No ideas to save!!!!!");
	    return null;
	} 

	if ( ideasObject.length() > 1 ) {
	    logger.warn("More than node found at top level. Saving random node and children only."+jsonContent);
	}

	Iterator<?> keys = ideasObject.sortedKeys();
	if ( keys.hasNext() ) {
	    String key = (String)keys.next();
	    return IdeaJSON.toNodeModel((JSONObject) ideasObject.get(key));
	}
	return null;
    }	

}
