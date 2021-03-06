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
package org.apache.wicket.util.resource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Locale;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.util.io.IOUtils;
import org.apache.wicket.util.io.Streams;
import org.apache.wicket.util.time.Time;


/**
 * @see org.apache.wicket.util.resource.IResourceStream
 * 
 * @author Jonathan Locke
 */
public abstract class AbstractResourceStream implements IStringResourceStream
{
	private static final long serialVersionUID = 1L;

	/** Charset for resource */
	private Charset charset;
	private Locale locale;

	/**
	 * Sets the character set used for reading this resource.
	 * 
	 * @param charset
	 *            Charset for component
	 */
	public void setCharset(final Charset charset)
	{
		this.charset = charset;
	}

	/**
	 * @return This resource as a String.
	 */
	public String asString()
	{
		Reader reader = null;
		try
		{
			if (charset == null)
			{
				reader = new InputStreamReader(getInputStream());
			}
			else
			{
				reader = new InputStreamReader(getInputStream(), charset);
			}
			return Streams.readString(reader);
		}
		catch (IOException e)
		{
			throw new WicketRuntimeException("Unable to read resource as String", e);
		}
		catch (ResourceStreamNotFoundException e)
		{
			throw new WicketRuntimeException("Unable to read resource as String", e);
		}
		finally
		{
			IOUtils.closeQuietly(reader);
			try
			{
				close();
			}
			catch (IOException e)
			{
				// ignore
			}
		}
	}

	/**
	 * @return Charset for resource
	 */
	protected Charset getCharset()
	{
		return charset;
	}

	/**
	 * @see org.apache.wicket.util.resource.IResourceStream#getLocale()
	 */
	public Locale getLocale()
	{
		return locale;
	}

	/**
	 * @see org.apache.wicket.util.resource.IResourceStream#setLocale(java.util.Locale)
	 */
	public void setLocale(Locale locale)
	{
		this.locale = locale;
	}

	/**
	 * @see org.apache.wicket.util.resource.IResourceStream#length()
	 */
	public long length()
	{
		return -1;
	}

	/**
	 * @see org.apache.wicket.util.resource.IResourceStream#getContentType()
	 */
	public String getContentType()
	{
		return null;
	}

	/**
	 * @see org.apache.wicket.util.watch.IModifiable#lastModifiedTime()
	 */
	public Time lastModifiedTime()
	{
		return null;
	}
}
