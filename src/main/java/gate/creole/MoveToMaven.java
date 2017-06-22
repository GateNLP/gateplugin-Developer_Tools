/*
 *  MoveToMaven.java
 *
 *  Copyright (c) 2017, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 3, June 2007 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  Mark A. Greenwood, 2nd February 2017
 */
package gate.creole;

import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;

import gate.creole.metadata.AutoInstance;
import gate.creole.metadata.CreoleResource;
import gate.gui.ActionsPublisher;

@CreoleResource(tool = true, isPrivate = true, autoinstances = @AutoInstance, name = "Upgrade App to new Plugin format", comment = "Upgrade the plugins used within an app to the new Maven based format")
public class MoveToMaven extends AbstractResource implements ActionsPublisher {

  @Override
  public List<Action> getActions() {
	return null;

	//choose a file

	//read the plugins out of the XML (not using persistence manager)
	
	//for each URL try and find a new plugin

	//replace the URL with the new plugin entry

	//scan the rest of the app for any URLs that go inside the plugin base URL and replace with creole:// ResourceReference instances
  }
}
