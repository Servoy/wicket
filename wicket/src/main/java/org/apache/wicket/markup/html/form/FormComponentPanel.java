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

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.ContainerWithAssociatedMarkupHelper;
import org.apache.wicket.markup.html.HeaderPartContainer;
import org.apache.wicket.markup.html.IHeaderPartContainerProvider;
import org.apache.wicket.markup.html.internal.HtmlHeaderContainer;
import org.apache.wicket.markup.parser.XmlTag;
import org.apache.wicket.markup.parser.filter.WicketTagIdentifier;
import org.apache.wicket.model.IModel;

/**
 * Panel (has it's own markup, defined between <wicket:panel> tags), that can act as a form
 * component. It typically wouldn't receive any input yourself, and often you can get by with
 * nesting form components in panels proper. However, using this panel can help you with building
 * components act to the outside world as one component, but internally uses separate components.
 * This component would then use these nested components to handle it's internal state, and would
 * use that internal state to get to one model object.
 * <p>
 * It is recommended that you override {@link #convertInput()} and let it set the value that
 * represents the compound value of the nested components. Often, this goes hand-in-hand with
 * overriding {@link #onBeforeRender()}, where you would analyze the model value, break it up and
 * distribute the appropriate values over the child components.
 * </p>
 * 
 * <p>
 * Here is a simple example of a panel with two components that multiplies and sets that as the
 * master model object. Note that for this simple example, setting the model value wouldn't make
 * sense, as the lhs and rhs cannot be known. For more complete examples of using this class, see
 * the wicket-datetime project.
 * </p>
 * 
 * <pre>
 * public class Multiply extends FormComponentPanel
 * {
 * 	private TextField left;
 * 	private int lhs = 0;
 * 	private int rhs = 0;
 * 	private TextField right;
 * 
 * 	public Multiply(String id)
 * 	{
 * 		super(id);
 * 		init();
 * 	}
 * 
 * 	public Multiply(String id, IModel model)
 * 	{
 * 		super(id, model);
 * 		init();
 * 	}
 * 
 * 	protected void convertInput()
 * 	{
 * 		Integer lhs = (Integer)left.getConvertedInput();
 * 		Integer rhs = (Integer)right.getConvertedInput();
 * 		setConvertedInput(lhs * rhs);
 * 	}
 * 
 * 	private void init()
 * 	{
 * 		add(left = new TextField(&quot;left&quot;, new PropertyModel(this, &quot;lhs&quot;), Integer.class));
 * 		add(right = new TextField(&quot;right&quot;, new PropertyModel(this, &quot;rhs&quot;), Integer.class));
 * 		left.setRequired(true);
 * 		right.setRequired(true);
 * 	}
 * }
 * </pre>
 * 
 * With this markup:
 * 
 * <pre>
 *   &lt;wicket:panel&gt;
 *     &lt;input type=&quot;text&quot; wicket:id=&quot;left&quot; size=&quot;2&quot; /&gt; * &lt;input type=&quot;text&quot; wicket:id=&quot;right&quot; size=&quot;2&quot; /&gt;
 *   &lt;/wicket:panel&gt;
 * </pre>
 * 
 * Which could be used, for example as:
 * 
 * <pre>
 *   add(new Multiply(&quot;multiply&quot;), new PropertyModel(m, &quot;multiply&quot;)));
 *   add(new Label(&quot;multiplyLabel&quot;, new PropertyModel(m, &quot;multiply&quot;)));
 * </pre>
 * 
 * and:
 * 
 * <pre>
 *   &lt;span wicket:id=&quot;multiply&quot;&gt;[multiply]&lt;/span&gt;
 *   = &lt;span wicket:id=&quot;multiplyLabel&quot;&gt;[result]&lt;/span&gt;
 * </pre>
 * 
 * </p>
 * 
 * @author eelcohillenius
 * 
 * @param <T>
 *            The model object type
 */
public abstract class FormComponentPanel<T> extends FormComponent<T>
	implements
		IHeaderPartContainerProvider
{
	private static final long serialVersionUID = 1L;

	static
	{
		// register "wicket:panel"
		WicketTagIdentifier.registerWellKnownTagName("panel");
	}


	private ContainerWithAssociatedMarkupHelper markupHelper;

	/** If if tag was an open-close tag */
	private boolean wasOpenCloseTag = false;

	/**
	 * Construct.
	 * 
	 * @param id
	 */
	public FormComponentPanel(String id)
	{
		super(id);
	}

	/**
	 * Construct.
	 * 
	 * @param id
	 * @param model
	 */
	public FormComponentPanel(String id, IModel<T> model)
	{
		super(id, model);
	}

	/**
	 * @see org.apache.wicket.markup.html.form.FormComponent#checkRequired()
	 */
	@Override
	public boolean checkRequired()
	{
		return true;
	}

	/**
	 * @see org.apache.wicket.markup.html.IHeaderPartContainerProvider#newHeaderPartContainer(java.lang.String,
	 *      java.lang.String)
	 */
	public HeaderPartContainer newHeaderPartContainer(final String id, final String scope)
	{
		return new HeaderPartContainer(id, this, scope);
	}

	/**
	 * Check the associated markup file for a wicket header tag
	 * 
	 * @see org.apache.wicket.Component#renderHead(org.apache.wicket.markup.html.internal.HtmlHeaderContainer)
	 */
	@Override
	public void renderHead(HtmlHeaderContainer container)
	{
		if (markupHelper == null)
		{
			markupHelper = new ContainerWithAssociatedMarkupHelper(this);
		}

		markupHelper.renderHeadFromAssociatedMarkupFile(container);

		super.renderHead(container);
	}

	/**
	 * 
	 * @see org.apache.wicket.Component#onComponentTag(org.apache.wicket.markup.ComponentTag)
	 */
	@Override
	protected void onComponentTag(final ComponentTag tag)
	{
		if (tag.isOpenClose())
		{
			wasOpenCloseTag = true;

			// Convert <span wicket:id="myPanel" /> into
			// <span wicket:id="myPanel">...</span>
			tag.setType(XmlTag.OPEN);
		}
		super.onComponentTag(tag);

		// remove unapplicable attributes that might have been set by the call to super
		tag.remove("name");
		tag.remove("disabled");

	}

	/**
	 * 
	 * @see org.apache.wicket.Component#onComponentTagBody(org.apache.wicket.markup.MarkupStream,
	 *      org.apache.wicket.markup.ComponentTag)
	 */
	@Override
	protected void onComponentTagBody(final MarkupStream markupStream, final ComponentTag openTag)
	{
		// Render the associated markup
		renderAssociatedMarkup("panel",
			"Markup for a panel component has to contain part '<wicket:panel>'");

		if (wasOpenCloseTag == false)
		{
			// Skip any raw markup in the body
			markupStream.skipRawMarkup();
		}
	}
}
