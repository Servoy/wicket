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
package org.apache.wicket.examples.stateless;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;

/**
 * Another page of the stateless example.
 * 
 * @author Eelco Hillenius
 */
public class StatelessPage extends WebPage
{
	private TextField tf;

	/**
	 * Constructor
	 */
	public StatelessPage()
	{
		setStatelessHint(true);
		add(new Label("message", new SessionModel()));
		add(new BookmarkablePageLink("indexLink", Index.class));

		StatelessForm statelessForm = new StatelessForm("statelessform")
		{
			/**
			 * @see org.apache.wicket.markup.html.form.Form#onSubmit()
			 */
			protected void onSubmit()
			{
				info("Submitted text: " + tf.getModelObject());
			}
		};
		tf = new TextField("textfield", new Model());
		tf.setRequired(true);
		add(statelessForm);
		statelessForm.add(tf);
		add(new FeedbackPanel("feedback"));
	}
}