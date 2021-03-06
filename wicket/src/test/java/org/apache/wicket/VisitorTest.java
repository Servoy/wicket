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
package org.apache.wicket;

import junit.framework.Assert;

import org.apache.wicket.Component.IVisitor;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.FormComponent;

/**
 * <code>
 * A
 * +-B
 * +-C
 * | +-D
 * | +-E
 * |   +-F
 * +-G
 *   +-H
 * </code>
 * 
 * @author igor.vaynberg
 */
public class VisitorTest extends WicketTestCase
{

	public void testContinueTraversal()
	{
		final StringBuilder path = new StringBuilder();

		TestContainer container = new TestContainer();
		container.visitChildren(new IVisitor<Component>()
		{
			public Object component(Component component)
			{
				path.append(component.getId());
				return CONTINUE_TRAVERSAL;
			}
		});

		Assert.assertEquals("BCDEFGH", path.toString());
	}

	public void testContinuePostOrder()
	{
		final StringBuilder path = new StringBuilder();

		TestContainer container = new TestContainer();
		FormComponent.visitComponentsPostOrder(container, new IVisitor<Component>()
		{
			public Object component(Component component)
			{
				path.append(component.getId());
				return CONTINUE_TRAVERSAL;
			}
		});

		Assert.assertEquals("BDFECHGA", path.toString());
	}

	public void testStop()
	{
		final StringBuilder path = new StringBuilder();

		TestContainer container = new TestContainer();
		Object result = container.visitChildren(new IVisitor<Component>()
		{
			public Object component(Component component)
			{
				path.append(component.getId());
				if ("D".equals(component.getId()))
				{
					return "RESULT";
				}
				return CONTINUE_TRAVERSAL;
			}
		});
		Assert.assertEquals("BCD", path.toString());
		Assert.assertEquals("RESULT", result);
	}

	public void testDoNotGoDeeper1()
	{
		final StringBuilder path = new StringBuilder();

		TestContainer container = new TestContainer();
		container.visitChildren(new IVisitor<Component>()
		{
			public Object component(Component component)
			{
				path.append(component.getId());
				if ("C".equals(component.getId()))
				{
					return CONTINUE_TRAVERSAL_BUT_DONT_GO_DEEPER;
				}
				return CONTINUE_TRAVERSAL;
			}
		});
		Assert.assertEquals("BCGH", path.toString());
	}

	public void testDoNotGoDeeper2()
	{
		final StringBuilder path = new StringBuilder();

		TestContainer container = new TestContainer();
		container.visitChildren(new IVisitor<Component>()
		{
			public Object component(Component component)
			{
				path.append(component.getId());
				if ("E".equals(component.getId()))
				{
					return CONTINUE_TRAVERSAL_BUT_DONT_GO_DEEPER;
				}
				return CONTINUE_TRAVERSAL;
			}
		});
		Assert.assertEquals("BCDEGH", path.toString());
	}


	private static class TestContainer extends WebMarkupContainer
	{
		public TestContainer()
		{
			super("A");
			WebMarkupContainer b = new WebMarkupContainer("B");
			WebMarkupContainer c = new WebMarkupContainer("C");
			WebMarkupContainer d = new WebMarkupContainer("D");
			WebMarkupContainer e = new WebMarkupContainer("E");
			WebMarkupContainer f = new WebMarkupContainer("F");
			WebMarkupContainer g = new WebMarkupContainer("G");
			WebMarkupContainer h = new WebMarkupContainer("H");
			add(b);
			add(c);
			c.add(d);
			c.add(e);
			e.add(f);
			add(g);
			g.add(h);
		}

	}
}
