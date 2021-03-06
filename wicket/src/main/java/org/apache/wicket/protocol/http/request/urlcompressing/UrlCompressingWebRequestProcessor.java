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
package org.apache.wicket.protocol.http.request.urlcompressing;

import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.IRedirectListener;
import org.apache.wicket.IRequestTarget;
import org.apache.wicket.Page;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.RequestListenerInterface;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.authorization.UnauthorizedActionException;
import org.apache.wicket.markup.html.INewBrowserWindowListener;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebRequestCycleProcessor;
import org.apache.wicket.protocol.http.request.urlcompressing.UrlCompressor.ComponentAndInterface;
import org.apache.wicket.request.IRequestCodingStrategy;
import org.apache.wicket.request.RequestParameters;
import org.apache.wicket.request.target.component.listener.RedirectPageRequestTarget;
import org.apache.wicket.util.string.Strings;

/**
 * Use this processor to minimize the wicket:interface urls. The component path and the interface
 * name will be removed from the url and only an uid will be inserted into the url.
 * 
 * To use this url compressing behavior you must override the {@link Application}'s
 * newRequestCycleProcessor() method and return an instance of this.
 * 
 * @author jcompagner
 * 
 * @since 1.3
 */
public class UrlCompressingWebRequestProcessor extends WebRequestCycleProcessor
{
	/**
	 * Construct.
	 */
	public UrlCompressingWebRequestProcessor()
	{
	}

	/**
	 * @see org.apache.wicket.protocol.http.WebRequestCycleProcessor#newRequestCodingStrategy()
	 */
	@Override
	protected IRequestCodingStrategy newRequestCodingStrategy()
	{
		return new UrlCompressingWebCodingStrategy();
	}

	/**
	 * @see org.apache.wicket.request.AbstractRequestCycleProcessor#resolveListenerInterfaceTarget(org.apache.wicket.RequestCycle,
	 *      org.apache.wicket.Page, java.lang.String, java.lang.String,
	 *      org.apache.wicket.request.RequestParameters)
	 */
	@Override
	protected IRequestTarget resolveListenerInterfaceTarget(final RequestCycle requestCycle,
		final Page page, final String componentPath, String interfaceName,
		final RequestParameters requestParameters)
	{
		String pageRelativeComponentPath = Strings.afterFirstPathComponent(componentPath,
			Component.PATH_SEPARATOR);
		Component component = null;
		if (page instanceof WebPage && !"IResourceListener".equals(interfaceName))
		{
			ComponentAndInterface cai = ((WebPage)page).getUrlCompressor()
				.getComponentAndInterfaceForUID(pageRelativeComponentPath);
			if (cai != null)
			{
				interfaceName = cai.getInterfaceName();
				component = cai.getComponent();
			}
		}

		requestParameters.setInterfaceName(interfaceName);

		if (interfaceName.equals(IRedirectListener.INTERFACE.getName()))
		{
			return new RedirectPageRequestTarget(page);
		}
		else if (interfaceName.equals(INewBrowserWindowListener.INTERFACE.getName()))
		{
			return INewBrowserWindowListener.INTERFACE.newRequestTarget(page, page,
				INewBrowserWindowListener.INTERFACE, requestParameters);
		}
		else
		{
			// Get the listener interface we need to call
			final RequestListenerInterface listener = RequestListenerInterface.forName(interfaceName);
			if (listener == null)
			{
				throw new WicketRuntimeException(
					"Attempt to access unknown request listener interface " + interfaceName);
			}

			// Get component
			if (component == null)
			{
				if (Strings.isEmpty(pageRelativeComponentPath))
				{
					component = page;
				}
				else
				{
					component = page.get(pageRelativeComponentPath);
				}
			}

			if (component == null)
			{
				// still null? that's not right
				throw new WicketRuntimeException("cannot resolve component with path '" +
					pageRelativeComponentPath + "', listener " + listener + " on page " + page);
			}

			if (!component.isEnableAllowed())
			{
				throw new UnauthorizedActionException(component, Component.ENABLE);
			}

			// Ask the request listener interface object to create a request
			// target
			return listener.newRequestTarget(page, component, listener, requestParameters);
		}
	}
}
