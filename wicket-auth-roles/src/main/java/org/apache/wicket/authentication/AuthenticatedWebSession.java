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
package org.apache.wicket.authentication;

import org.apache.wicket.Request;
import org.apache.wicket.Session;
import org.apache.wicket.authorization.strategies.role.Roles;
import org.apache.wicket.protocol.http.WebSession;


/**
 * Basic authenticated web session. Subclasses must provide a method that authenticates the session
 * based on a username and password, and a method implementation that gets the Roles
 * 
 * @author Jonathan Locke
 */
public abstract class AuthenticatedWebSession extends WebSession
{
	private static final long serialVersionUID = 1L;

	/**
	 * @return Current authenticated web session
	 */
	public static AuthenticatedWebSession get()
	{
		return (AuthenticatedWebSession)Session.get();
	}

	/** True when the user is signed in */
	private volatile boolean signedIn;

	/**
	 * Construct.
	 * 
	 * @param application
	 *            The web application
	 * @param request
	 *            The current request object
	 * @deprecated Use {@link #AuthenticatedWebSession(Request)}
	 */
	@Deprecated
	public AuthenticatedWebSession(final AuthenticatedWebApplication application, Request request)
	{
		super(application, request);
	}

	/**
	 * Construct.
	 * 
	 * @param request
	 *            The current request object
	 */
	public AuthenticatedWebSession(Request request)
	{
		super(request);
	}

	/**
	 * Authenticates this session using the given username and password
	 * 
	 * @param username
	 *            The username
	 * @param password
	 *            The password
	 * @return True if the user was authenticated successfully
	 */
	public abstract boolean authenticate(final String username, final String password);

	/**
	 * @return Get the roles that this session can play
	 */
	public abstract Roles getRoles();

	/**
	 * @return True if the user is signed in to this session
	 */
	public final boolean isSignedIn()
	{
		return signedIn;
	}

	/**
	 * Signs user in by authenticating them with a username and password
	 * 
	 * @param username
	 *            The username
	 * @param password
	 *            The password
	 * @return True if the user was signed in successfully
	 */
	public final boolean signIn(final String username, final String password)
	{
		return signedIn = authenticate(username, password);
	}

	/**
	 * Sign the user out.
	 */
	public void signOut()
	{
		signedIn = false;
	}

	/**
	 * Cookie based logins (remember me) may not rely on putting username and password into the
	 * cookie but something else that safely identifies the user. This method is meant to support
	 * these use cases.
	 * 
	 * It is protected (and not public) to enforce that cookie based authentication gets implemented
	 * in a subclass (like you need to subclass authenticate() for 'normal' authentication).
	 * 
	 * @see #authenticate(String, String)
	 * 
	 * @param value
	 */
	protected final void signIn(boolean value)
	{
		signedIn = value;
	}
}
