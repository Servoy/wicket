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
package org.apache.wicket;

/**
 * <p>
 * A request target is the base entity that is the subject of a request. Different types of request
 * have different request targets. For instance a request for a bookmarkable page differs from a
 * request for a link on a previously rendered page, which in turn differs from a request for a
 * shared resource.
 * </p>
 * <p>
 * It is very important (for mounting) that implementations implement
 * {@link java.lang.Object#equals(java.lang.Object)} and {@link java.lang.Object#hashCode()} in a
 * consistent way.
 * </p>
 * Typically, implementations of IRequestTarget are not meant to be used more than once.
 * 
 * @author Eelco Hillenius
 */
public interface IRequestTarget
{
	/**
	 * Generates a response.
	 * 
	 * @param requestCycle
	 *            the current request cycle
	 */
	void respond(RequestCycle requestCycle);

	/**
	 * This method is called at the end of a request cycle to indicate that processing is done and
	 * that cleaning up of the subject(s) of this target may be done.
	 * 
	 * @param requestCycle
	 *            the current request cycle
	 */
	void detach(RequestCycle requestCycle);
}
