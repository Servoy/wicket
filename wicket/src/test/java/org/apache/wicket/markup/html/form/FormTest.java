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
package org.apache.wicket.markup.html.form;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.PageParameters;
import org.apache.wicket.WicketTestCase;
import org.apache.wicket.markup.IMarkupResourceStreamProvider;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.validation.IFormValidator;
import org.apache.wicket.model.IDetachable;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;


/**
 * @author Pekka Enberg
 * @author Martijn Dashorst
 */
public class FormTest extends WicketTestCase
{
	private FormComponent.IVisitor visitor;

	/**
	 * Construct.
	 * 
	 * @param name
	 */
	public FormTest(String name)
	{
		super(name);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		visitor = new Form.ValidationVisitor()
		{
			@Override
			public void validate(FormComponent formComponent)
			{
			}
		};
	}

	/**
	 * 
	 */
	public void testShouldContinueTraversalIfListenerAllowsChildProcessing()
	{
		assertTraversalStatus(Component.IVisitor.CONTINUE_TRAVERSAL, true);
	}

	/**
	 * 
	 */
	public void testShouldContinueTraversalButDontGoDeeperIfListenerDisallowsChildProcessing()
	{
		assertTraversalStatus(Component.IVisitor.CONTINUE_TRAVERSAL_BUT_DONT_GO_DEEPER, false);
	}

	private void assertTraversalStatus(Object expected, final boolean processChildren)
	{
		assertEquals(expected, visitor.formComponent(new IFormVisitorParticipant()
		{
			public boolean processChildren()
			{
				return processChildren;
			}
		}));
	}

	/**
	 * @throws Exception
	 */
	public void testFormMethodGet() throws Exception
	{
		executeTest(FormMethodTestPage.class, "FormMethodTestPage_expected.html");
	}

	/**
	 * WICKET-3488
	 */
	public void testFormReplacement()
	{
		tester.startPage(TestPage.class);
		tester.newFormTester("form").submit();
		tester.assertRenderedPage(TestPage.class);
	}

	public void testValidatorsDetach()
	{
		class TestValidator implements IFormValidator, IDetachable
		{
			boolean detached = false;

			public void detach()
			{
				detached = true;
			}

			public FormComponent<?>[] getDependentFormComponents()
			{
				return new FormComponent[] { };
			}

			public void validate(Form<?> form)
			{
			}
		}

		Form<?> form = new Form<Void>("form");
		TestValidator v1 = new TestValidator();
		TestValidator v2 = new TestValidator();
		form.add(v1);
		form.add(v2);
		form.detach();
		assertTrue(v1.detached);
		assertTrue(v2.detached);
	}

	/** */
	public static class TestPage extends WebPage implements IMarkupResourceStreamProvider
	{
		/** */
		public TestPage()
		{
			super(new PageParameters("test=value"));
			add(new Form<Void>("form")
			{
				private static final long serialVersionUID = 1L;

				@Override
				protected void onSubmit()
				{
					replaceWith(new Form<Void>("form"));
				}
			});
		}

		public IResourceStream getMarkupResourceStream(MarkupContainer container,
			Class<?> containerClass)
		{
			return new StringResourceStream(
				"<html><body><form wicket:id=\"form\"></form></body></html>");
		}
	}
}
