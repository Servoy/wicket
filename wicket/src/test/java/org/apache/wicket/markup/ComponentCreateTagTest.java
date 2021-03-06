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
package org.apache.wicket.markup;

import java.util.Locale;

import org.apache.wicket.Request;
import org.apache.wicket.Response;
import org.apache.wicket.Session;
import org.apache.wicket.WicketTestCase;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.tester.WicketTester;
import org.apache.wicket.util.tester.WicketTester.DummyWebApplication;


/**
 * Simple application that demonstrates the mock http application code (and checks that it is
 * working)
 * 
 * @author Chris Turner
 */
public class ComponentCreateTagTest extends WicketTestCase
{
	/**
	 * Create the test.
	 * 
	 * @param name
	 *            The test name
	 */
	public ComponentCreateTagTest(String name)
	{
		super(name);
	}

	/**
	 * Simple Label
	 * 
	 * @throws Exception
	 */
	public void testRenderHomePage_1() throws Exception
	{
		tester.getApplication().getMarkupSettings().setStripWicketTags(true);
		executeTest(ComponentCreateTag_1.class, "ComponentCreateTagExpectedResult_1.html");
	}

	/**
	 * A Table with X rows and a label inside
	 * 
	 * @throws Exception
	 */
	public void testRenderHomePage_2() throws Exception
	{
		tester.getApplication().getMarkupSettings().setStripWicketTags(true);
		executeTest(ComponentCreateTag_2.class, "ComponentCreateTagExpectedResult_2.html");
	}

	/**
	 * A Border
	 * 
	 * @throws Exception
	 */
	public void testRenderHomePage_3() throws Exception
	{
		tester.getApplication().getMarkupSettings().setStripWicketTags(true);
		executeTest(ComponentCreateTag_3.class, "ComponentCreateTagExpectedResult_3.html");
	}

	/**
	 * A Border inside another Border (nested <wicket:components>)
	 * 
	 * @throws Exception
	 */
	public void testRenderHomePage_4() throws Exception
	{
		tester.getApplication().getMarkupSettings().setStripWicketTags(true);
		executeTest(ComponentCreateTag_4.class, "ComponentCreateTagExpectedResult_4.html");
	}

	/**
	 * 
	 * 
	 * @throws Exception
	 */
	public void testRenderHomePage_6() throws Exception
	{
		WebApplication myApplication = new DummyWebApplication()
		{
			/**
			 * @see org.apache.wicket.protocol.http.WebApplication#newSession(org.apache.wicket.Request,
			 *      org.apache.wicket.Response)
			 */
			@Override
			public Session newSession(Request request, Response response)
			{
				Session session = super.newSession(request, response);
				session.setLocale(Locale.ENGLISH);
				return session;
			}
		};

		tester = new WicketTester(myApplication);

		tester.getApplication().getMarkupSettings().setStripWicketTags(true);
		executeTest(ComponentCreateTag_6.class, "ComponentCreateTagExpectedResult_6.html");
	}
}
