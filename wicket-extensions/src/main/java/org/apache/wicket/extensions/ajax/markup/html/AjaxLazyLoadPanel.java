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
package org.apache.wicket.extensions.ajax.markup.html;

import org.apache.wicket.Component;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.version.undo.Change;

/**
 * A panel where you can lazy load another panel. This can be used if you have a panel/component
 * that is pretty heavy in creation and you first want to show the user the page and then replace
 * the panel when it is ready.
 * 
 * @author jcompagner
 * 
 * @since 1.3
 */
public abstract class AjaxLazyLoadPanel extends Panel
{
	private static final String LAZY_LOAD_COMPONENT_ID = "content";

	private static final long serialVersionUID = 1L;

	// state,
	// 0:add loading component
	// 1:loading component added, waiting for ajax replace
	// 2:ajax replacement completed
	private byte state = 0;

	/**
	 * Constructor
	 * 
	 * @param id
	 */
	public AjaxLazyLoadPanel(final String id)
	{
		this(id, null);
	}

	/**
	 * Constructor
	 * 
	 * @param id
	 * @param model
	 */
	public AjaxLazyLoadPanel(final String id, final IModel<?> model)
	{
		super(id, model);

		setOutputMarkupId(true);

		add(new AbstractDefaultAjaxBehavior()
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void respond(AjaxRequestTarget target)
			{
				if (state < 2)
				{
					Component component = getLazyLoadComponent(LAZY_LOAD_COMPONENT_ID);
					AjaxLazyLoadPanel.this.replace(component);
					setState((byte)2);
				}
				target.addComponent(AjaxLazyLoadPanel.this);
			}

			@Override
			public void renderHead(IHeaderResponse response)
			{
				super.renderHead(response);
				if (state < 2)
				{
					handleCallbackScript(response, getCallbackScript().toString());
				}
			}

		});
	}

	/**
	 * Allows subclasses to change the callback script if needed.
	 * 
	 * @param response
	 * @param callbackScript
	 */
	protected void handleCallbackScript(final IHeaderResponse response, final String callbackScript)
	{
		response.renderOnDomReadyJavascript(callbackScript);
	}

	/**
	 * @see org.apache.wicket.Component#onBeforeRender()
	 */
	@Override
	protected void onBeforeRender()
	{
		if (state == 0)
		{
			add(getLoadingComponent(LAZY_LOAD_COMPONENT_ID));
			setState((byte)1);
		}
		super.onBeforeRender();
	}

	/**
	 * 
	 * @param state
	 */
	private void setState(byte state)
	{
		if (this.state != state)
		{
			addStateChange(new StateChange(this.state));
		}
		this.state = state;
	}

	/**
	 * 
	 * @param markupId
	 *            The components markupid.
	 * @return The component that must be lazy created. You may call setRenderBodyOnly(true) on this
	 *         component if you need the body only.
	 */
	public abstract Component getLazyLoadComponent(String markupId);

	/**
	 * @param markupId
	 *            The components markupid.
	 * @return The component to show while the real component is being created.
	 */
	public Component getLoadingComponent(final String markupId)
	{
		return new Label(markupId, "<img alt=\"Loading...\" src=\"" +
			RequestCycle.get().urlFor(AbstractDefaultAjaxBehavior.INDICATOR) + "\"/>").setEscapeModelStrings(false);
	}

	/**
	 * 
	 */
	private final class StateChange extends Change
	{
		private static final long serialVersionUID = 1L;

		private final byte state;

		/**
		 * Construct.
		 * 
		 * @param state
		 */
		public StateChange(byte state)
		{
			this.state = state;
		}

		/**
		 * @see org.apache.wicket.version.undo.Change#undo()
		 */
		@Override
		public void undo()
		{
			AjaxLazyLoadPanel.this.state = state;
		}
	}
}
