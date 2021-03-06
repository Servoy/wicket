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
package org.apache.wicket.markup.html;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;
import org.apache.wicket.util.time.Time;


/**
 * An WebResource subclass for dynamic resources (resources created programmatically).
 * <p>
 * This class caches the generated resource in memory, and is thus very useful for things you
 * generate dynamically, but reuse for a while after that. If you need resources that stream
 * directly and are not cached, extend {@link WebResource} directly and implement
 * {@link WebResource#getResourceStream()} yourself.
 * </p>
 * 
 * @author Jonathan Locke
 * @author Johan Compagner
 * @author Gili Tzabari
 */
public abstract class DynamicWebResource extends WebResource
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The resource state returned by the getResourceState() method. This state needs to be
	 * thread-safe and its methods must return the same values no matter how many times they are
	 * invoked. A ResourceState may assume getParameters() will remain unchanged during its
	 * lifetime.
	 * 
	 * @author jcompagner
	 */
	public static abstract class ResourceState
	{
		protected Time lastModifiedTime;

		/**
		 * @return The Byte array for this resource
		 */
		public abstract byte[] getData();

		/**
		 * @return The content type of this resource
		 */
		public abstract String getContentType();

		/**
		 * @return The last modified time of this resource
		 */
		public Time lastModifiedTime()
		{
			if (lastModifiedTime == null)
			{
				lastModifiedTime = Time.now();
			}
			return lastModifiedTime;
		}

		/**
		 * @return The length of the data
		 */
		public int getLength()
		{
			byte[] data = getData();
			return data != null ? data.length : 0;
		}
	}

	/**
	 * The resource locale.
	 */
	private final Locale locale;

	/** The filename that will be set as the Content-Disposition header. */
	private final String filename;

	/**
	 * Creates a dynamic resource.
	 */
	public DynamicWebResource()
	{
		this(null, null);
	}

	/**
	 * Creates a dynamic resource.
	 * 
	 * @param filename
	 *            The filename that will be set as the Content-Disposition header.
	 */
	public DynamicWebResource(String filename)
	{
		this(null, filename);
	}

	/**
	 * Creates a dynamic resource from for the given locale
	 * 
	 * @param locale
	 *            The locale of this resource
	 */
	public DynamicWebResource(Locale locale)
	{
		this(locale, null);
	}

	/**
	 * Creates a dynamic resource from for the given locale
	 * 
	 * @param locale
	 *            The locale of this resource
	 * @param filename
	 *            The filename that will be set as the Content-Disposition header.
	 */
	public DynamicWebResource(Locale locale, String filename)
	{
		this.locale = locale;
		this.filename = filename;
		setCacheable(false);
	}

	/**
	 * @see org.apache.wicket.markup.html.WebResource#setHeaders(org.apache.wicket.protocol.http.WebResponse)
	 */
	@Override
	protected void setHeaders(WebResponse response)
	{
		super.setHeaders(response);
		if (filename != null)
		{
			response.setAttachmentHeader(filename);
		}
	}

	/**
	 * Returns the resource locale.
	 * 
	 * @return The locale of this resource
	 */
	public Locale getLocale()
	{
		return locale;
	}

	/**
	 * @return Gets the resource to attach to the component.
	 */
	// this method is deliberately non-final. some users depend on it
	@Override
	public IResourceStream getResourceStream()
	{
		return new IResourceStream()
		{
			private static final long serialVersionUID = 1L;

			private Locale locale = DynamicWebResource.this.getLocale();

			/** Transient input stream to resource */
			private transient InputStream inputStream = null;

			/**
			 * Transient ResourceState of the resources, will always be deleted in the close
			 */
			private transient ResourceState data = null;

			/**
			 * @see org.apache.wicket.util.resource.IResourceStream#close()
			 */
			public void close() throws IOException
			{
				if (inputStream != null)
				{
					inputStream.close();
					inputStream = null;
				}
				data = null;
			}

			/**
			 * @see org.apache.wicket.util.resource.IResourceStream#getContentType()
			 */
			public String getContentType()
			{
				checkLoadData();
				return data.getContentType();
			}

			/**
			 * @see org.apache.wicket.util.resource.IResourceStream#getInputStream()
			 */
			public InputStream getInputStream() throws ResourceStreamNotFoundException
			{
				checkLoadData();
				if (inputStream == null)
				{
					inputStream = new ByteArrayInputStream(data.getData());
				}
				return inputStream;
			}

			/**
			 * @see org.apache.wicket.util.watch.IModifiable#lastModifiedTime()
			 */
			public Time lastModifiedTime()
			{
				checkLoadData();
				return data.lastModifiedTime();
			}

			/**
			 * @see org.apache.wicket.util.resource.IResourceStream#length()
			 */
			public long length()
			{
				checkLoadData();
				return data.getLength();
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
			public void setLocale(Locale loc)
			{
				locale = loc;
			}

			/**
			 * Check whether the data was loaded yet. If not, load it now.
			 */
			private void checkLoadData()
			{
				if (data == null)
				{
					data = getResourceState();
				}
			}
		};
	}

	/**
	 * Gets the byte array for our dynamic resource. If the subclass regenerates the data, it should
	 * set the lastModifiedTime too. This ensures that resource caching works correctly.
	 * 
	 * @return The byte array for this dynamic resource.
	 */
	protected abstract ResourceState getResourceState();
}
