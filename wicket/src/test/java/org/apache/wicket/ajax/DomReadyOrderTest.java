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
package org.apache.wicket.ajax;

import org.apache.wicket.WicketTestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jcompagner
 */
public class DomReadyOrderTest extends WicketTestCase
{
	/** log. */
	private static final Logger log = LoggerFactory.getLogger(DomReadyOrderTest.class);

	/**
	 * @throws Exception
	 */
	public void testDomReadyOrder() throws Exception
	{
		tester.processRequestCycle(DomReadyOrderPage.class);
		tester.assertResultPage(DomReadyOrderPage.class, "DomReadyOrderPage_expected.html");

		tester.clickLink("test", true);
		tester.assertResultPage(DomReadyOrderPage.class, "DomReadyOrderPage_ajax_expected.html");
	}

	/**
	 * @throws Exception
	 */
	public void testDomReadyOrder2() throws Exception
	{
		tester.processRequestCycle(DomReadyOrderPage.class);
		tester.assertResultPage(DomReadyOrderPage.class, "DomReadyOrderPage_expected.html");

		tester.executeAjaxEvent("test", "onclick");
		tester.assertResultPage(DomReadyOrderPage.class, "DomReadyOrderPage_ajax_expected.html");
	}

	/**
	 * 
	 */
	public void testAjaxSubmitWhileAnotherButtonIsNotVisible()
	{
		// start and render the test page
		tester.startPage(HomePage.class);
		// assert rendered page class
		tester.assertRenderedPage(HomePage.class);
		// assert rendered label component
		tester.assertLabel("message",
			"If you see this message wicket is properly configured and running");

		// assert rendered row element
		tester.assertLabel("form:listViewContainer:listView:0:label", "0");

		// add a row, execute ajax
		tester.executeAjaxEvent("form:addButton", "onclick");

		// assert rendered page class
		tester.assertRenderedPage(HomePage.class);

		String doc = tester.getServletResponse().getDocument();
		log.error("'" + doc + "'");

		// assert rendered row elements
		tester.assertLabel("form:listViewContainer:listView:0:label", "0");
		tester.assertLabel("form:listViewContainer:listView:1:label", "1");
	}
}
