/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.wicket.devutils.inspector;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.IPageMap;
import org.apache.wicket.Session;
import org.apache.wicket.devutils.DevUtilsPanel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;


/**
 * A Wicket panel that shows interesting information about a given Wicket session.
 * 
 * @author Jonathan Locke
 */
public final class SessionView extends DevUtilsPanel
{
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 * 
	 * @param id
	 *            See Component
	 * @param session
	 * @see Component#Component(String)
	 */
	public SessionView(final String id, final Session session)
	{
		super(id);

		// Basic attributes
		add(new Label("id", session.getId()));
		add(new Label("locale", session.getLocale().toString()));
		add(new Label("style", session.getStyle() == null ? "[None]" : session.getStyle()));
		add(new Label("size", new SessionSizeModel(session)));
		add(new Label("totalSize", new SessionTotalSizeModel(session)));

		// Get pagemaps
		final List<IPageMap> pagemaps = session.getPageMaps();

		// Create the table containing the list the components
		add(new ListView<IPageMap>("pagemaps", pagemaps)
		{
			private static final long serialVersionUID = 1L;

			/**
			 * Populate the table with Wicket elements
			 */
			@Override
			protected void populateItem(final ListItem<IPageMap> listItem)
			{
				IPageMap p = listItem.getModelObject();
				listItem.add(new PageMapView("pagemap", p));
			}
		});
	}
	
}
