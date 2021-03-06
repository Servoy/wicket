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
package org.apache.wicket.markup.html.resources;

import java.util.Locale;

import org.apache.wicket.Resource;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.html.CompressedPackageResource;
import org.apache.wicket.markup.html.PackageResource;


/**
 * A static resource reference which can be transferred to the browser using the gzip compression.
 * Reduces the download size of for example javascript resources.
 * 
 * see {@link ResourceReference} and {@link CompressedPackageResource}
 * 
 * @author Janne Hietam&auml;ki
 */
public class CompressedResourceReference extends ResourceReference
{
	private static final long serialVersionUID = 1L;

	/**
	 * @see ResourceReference#ResourceReference(Class, String)
	 */
	public CompressedResourceReference(Class<?> scope, String name)
	{
		super(scope, name);
		setStateless(true);
	}

	/**
	 * @see ResourceReference#ResourceReference(Class scope, String name, Locale locale, String
	 *      style)
	 */
	public CompressedResourceReference(Class<?> scope, String name, Locale locale, String style)
	{
		super(scope, name, locale, style);
		setStateless(true);
	}

	/**
	 * @see ResourceReference#ResourceReference(String)
	 */
	public CompressedResourceReference(String name)
	{
		super(name);
		setStateless(true);
	}

	/**
	 * @see org.apache.wicket.ResourceReference#newResource()
	 */
	@Override
	protected Resource newResource()
	{
		PackageResource packageResource = CompressedPackageResource.newPackageResource(getScope(),
			getName(), getLocale(), getStyle());
		if (packageResource != null)
		{
			locale = packageResource.getLocale();
		}
		else
		{
			throw new IllegalArgumentException("package resource [scope=" + getScope() + ",name=" +
				getName() + ",locale=" + getLocale() + "style=" + getStyle() + "] not found");
		}
		return packageResource;
	}
}
