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
package wicket.protocol.http;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

import wicket.application.ReloadingClassLoader;
import wicket.util.listener.IChangeListener;

/**
 * Custom WicketFilter that reloads the web applications when classes are
 * modified. In order to reload your own classes, use include and exclude
 * patterns using wildcards. And in web.xml, point to the reloading wicket
 * filter instead of the original one.
 * 
 * <p>
 * <b>Example denoting the built-in patterns:</b>
 * 
 * <pre>
 * public class MyFilter extends ReloadingWicketFilter
 * {
 * 	static
 * 	{
 * 		ReloadingClassLoader.excludePattern(&quot;wicket.*&quot;);
 * 		ReloadingClassLoader.includePattern(&quot;wicket.examples.*&quot;);
 * 	}
 * }
 * </pre>
 * 
 * <p>
 * It is also possible to add an URL to watch for changes using
 * <tt>ReloadingClassLoader.addLocation()</tt>. By default, all the URL
 * locations we can find for the provided class loader are registered.
 * </p>
 * 
 * @see WicketFilter
 * 
 * @author <a href="mailto:jbq@apache.org">Jean-Baptiste Quenot</a>
 */
public class ReloadingWicketFilter extends WicketFilter
{
	private ReloadingClassLoader reloadingClassLoader;

	/**
	 * Instantiate the reloading class loader
	 */
	public ReloadingWicketFilter()
	{
		// Create a reloading classloader
		reloadingClassLoader = new ReloadingClassLoader(getClass().getClassLoader());
	}

	/**
	 * @see wicket.protocol.http.WicketFilter#getClassLoader()
	 */
	protected ClassLoader getClassLoader()
	{
		return reloadingClassLoader;
	}

	/**
	 * @see wicket.protocol.http.WicketFilter#init(javax.servlet.FilterConfig)
	 */
	public void init(final FilterConfig filterConfig) throws ServletException
	{
		reloadingClassLoader.setListener(new IChangeListener()
		{
			public void onChange()
			{
				reloadingClassLoader = new ReloadingClassLoader(getClass().getClassLoader());
				try
				{
					init(filterConfig);
				}
				catch (ServletException e)
				{
					throw new RuntimeException(e);
				}
			}
		});

		super.init(filterConfig);
	}
}