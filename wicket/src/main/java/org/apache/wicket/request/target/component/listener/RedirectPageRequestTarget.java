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
package org.apache.wicket.request.target.component.listener;

import org.apache.wicket.IRedirectListener;
import org.apache.wicket.Page;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.request.target.basic.RedirectRequestTarget;

/**
 * Specialization of page request that denotes that we are actually handling a redirect request of a
 * page. It does not actually cause a redirect itself.
 * 
 * <p>
 * FIXME javadoc - how is this used? What is it's relationship to {@link RedirectRequestTarget}?
 * 
 * @see RedirectRequestTarget
 * @author Eelco Hillenius
 */
public class RedirectPageRequestTarget extends AbstractListenerInterfaceRequestTarget
{
	/**
	 * Construct.
	 * 
	 * @param page
	 *            the target of the redirect handling
	 */
	public RedirectPageRequestTarget(final Page page)
	{
		super(page, page, IRedirectListener.INTERFACE);
	}

	/**
	 * FIXME javadoc - why does this not call onProcessEvents like ListenerInterfaceRequestTarget
	 * does?
	 * 
	 * @see org.apache.wicket.request.target.IEventProcessor#processEvents(org.apache.wicket.RequestCycle)
	 */
	public final void processEvents(final RequestCycle requestCycle)
	{
		// onProcessEvents(requestCycle);
		getRequestListenerInterface().invoke(getPage(), getTarget());
	}
}
