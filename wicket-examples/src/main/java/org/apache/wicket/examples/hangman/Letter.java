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
package org.apache.wicket.examples.hangman;

import java.awt.Color;

import org.apache.wicket.IClusterable;
import org.apache.wicket.Resource;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.html.image.resource.DefaultButtonImageResource;
import org.apache.wicket.util.lang.Primitives;


/**
 * Model for a letter in the game of hangman
 * 
 * @author Jonathan Locke
 */
public class Letter implements IClusterable
{
	/** True if the letter has been guessed */
	private boolean guessed;

	/** The letter */
	private char letter;

	/**
	 * Constructor
	 * 
	 * @param letter
	 *            The letter
	 */
	public Letter(final char letter)
	{
		this.letter = letter;
	}

	/**
	 * @return This letter as a string
	 */
	public String asString()
	{
		return Character.toString(letter);
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(final Object object)
	{
		if (object instanceof Letter)
		{
			final Letter that = (Letter)object;
			return that.letter == this.letter && that.guessed == this.guessed;
		}
		return false;
	}

	/**
	 * @return ResourceReference token for this letter
	 */
	public ResourceReference getSharedImageResource()
	{
		return new ResourceReference(Letter.class, asString() +
				(isGuessed() ? "_enabled" : "_disabled"))
		{
			protected Resource newResource()
			{
				// Lazy loading of shared resource
				final DefaultButtonImageResource buttonResource = new DefaultButtonImageResource(
						30, 30, asString());
				if (!isGuessed())
				{
					buttonResource.setColor(Color.GRAY);
				}
				return buttonResource;
			}
		};
	}

	/**
	 * Guess this letter
	 */
	public void guess()
	{
		this.guessed = true;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode()
	{
		return Primitives.hashCode(letter << (guessed ? 1 : 0));
	}

	/**
	 * @return Returns the isGuessed.
	 */
	public boolean isGuessed()
	{
		return guessed;
	}

	/**
	 * Resets this letter into the default state
	 */
	public void reset()
	{
		this.guessed = false;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return "[Letter letter = " + letter + ", guessed = " + guessed + "]";
	}
}
