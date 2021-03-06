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
package org.apache.wicket.markup.html.form.border;

import junit.framework.TestCase;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;

/**
 * Simple test using the WicketTester
 */
public class TestHomePage extends TestCase
{
	private WicketTester tester;

	private FormTester formTester;

	@Override
	public void setUp()
	{
		tester = new WicketTester();

		// Start and render the test page
		tester.startPage(HomePage.class);
		tester.assertRenderedPage(HomePage.class);
	}

	/**
	 * 
	 */
	public void testWithBorder2()
	{
		formTester = tester.newFormTester("border:form");

		// formTester.setValue("..:textfield1", "testxxx");
		TextField<String> textfield = (TextField<String>)tester.getLastRenderedPage().get(
			"border:textfield");
		tester.getServletRequest().setParameter(textfield.getInputName(), "abcde");

		MyTextField datefield = (MyTextField)tester.getLastRenderedPage().get("border:datefield");
		tester.getServletRequest().setParameter(datefield.getInputName(), "aaabbb");

		MyDateField datefield2 = (MyDateField)tester.getLastRenderedPage().get("border:datefield2");
		TextField<String> date = (TextField<String>)datefield2.get("date");
		tester.getServletRequest().setParameter(date.getInputName(), "abcdef");

		formTester.submit();
		tester.assertNoErrorMessage();

		HomePage page = (HomePage)tester.getLastRenderedPage();
		assertEquals("abcde", page.getTextfield());
		assertEquals("aaabbb-converted", page.getDatefield());
		assertEquals("abcdef-converted", page.getDatefield2());

		assertEquals("abcde", page.get("border:lbltextfield").getDefaultModelObjectAsString());
		assertEquals("aaabbb-converted", page.get("border:lbldatefield")
			.getDefaultModelObjectAsString());
		assertEquals("abcdef-converted", page.get("border:lbldatefield2")
			.getDefaultModelObjectAsString());
	}
}
