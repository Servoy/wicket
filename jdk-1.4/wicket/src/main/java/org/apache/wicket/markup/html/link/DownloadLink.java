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
package org.apache.wicket.markup.html.link;

import java.io.File;

import org.apache.wicket.request.target.resource.ResourceStreamRequestTarget;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.string.Strings;


/**
 * A link that streams a file to the client. When clicked this link will prompt
 * the save as dialog in the browser.
 * 
 * @author Igor Vaynberg (ivaynberg)
 */
public class DownloadLink extends Link
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * File to stream
	 */
	private final File file;

	/**
	 * File name to stream
	 */
	private final String fileName;


	/**
	 * Constructor. File name used will be the result of
	 * <code>file.getName()</code>
	 * 
	 * @param id
	 *            component id
	 * @param file
	 *            file to stream to client
	 */
	public DownloadLink(String id, File file)
	{
		super(id);
		if (file == null)
		{
			throw new IllegalArgumentException("file cannot be null");
		}
		this.file = file;
		this.fileName = file.getName();
	}

	/**
	 * Constructor
	 * 
	 * @param id
	 *            component id
	 * @param file
	 *            file to stream to client
	 * @param fileName
	 *            name of the file
	 */
	public DownloadLink(String id, File file, String fileName)
	{
		super(id);
		if (file == null)
		{
			throw new IllegalArgumentException("file cannot be null");
		}
		if (Strings.isEmpty(fileName))
		{
			throw new IllegalArgumentException("fileName cannot be an empty string");
		}
		this.file = file;
		this.fileName = fileName;
	}


	/**
	 * 
	 * @see org.apache.wicket.markup.html.link.Link#onClick()
	 */
	public void onClick()
	{
		IResourceStream resourceStream = new FileResourceStream(new org.apache.wicket.util.file.File(file));
		getRequestCycle().setRequestTarget(new ResourceStreamRequestTarget(resourceStream) {
			public String getFileName()
			{
				return fileName;
			}
		});
	}
}