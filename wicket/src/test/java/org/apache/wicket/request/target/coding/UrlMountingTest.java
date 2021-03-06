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
package org.apache.wicket.request.target.coding;

import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.apache.wicket.IRequestTarget;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.protocol.http.WebRequestCycle;
import org.apache.wicket.protocol.http.WebRequestCycleProcessor;
import org.apache.wicket.protocol.http.request.WebErrorCodeResponseTarget;
import org.apache.wicket.settings.ISecuritySettings;
import org.apache.wicket.util.lang.PackageName;
import org.apache.wicket.util.tester.WicketTester;

/**
 * Tests package resources.
 * 
 * @author <a href="mailto:jbq@apache.org">Jean-Baptiste Quenot</a>
 */
public class UrlMountingTest extends TestCase
{
	private WicketTester tester;

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		tester = new WicketTester();
		tester.getApplication().mount("/mount/point", PackageName.forClass(TestPage.class));
		tester.setupRequestAndResponse();
	}

	/**
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception
	{
		tester.destroy();
	}

	/**
	 * Tests mounting.
	 */
	public void testBadRequest1()
	{
		tester.getServletRequest().setPath("/mount/XXXpoint");
		assertNull(getRequestCodingStrategy());
	}

	/**
	 * Tests mounting.
	 */
	public void testBadRequest2()
	{
		tester.getServletRequest().setPath("/mount/pointXXX");
		assertNull(getRequestCodingStrategy());
	}

	/**
	 * Tests mounting.
	 */
	public void testBadRequest3()
	{
		tester.getServletRequest()
			.setURL(
				"/WicketTester$DummyWebApplication/WicketTester$DummyWebApplication/mount/point/nonexistent.TestPage");
		WebRequestCycle requestCycle = tester.createRequestCycle();
		tester.processRequestCycle(requestCycle);
		assertEquals(HttpServletResponse.SC_NOT_FOUND, getErrorCode(requestCycle));
	}

	/**
	 * Test direct access (with wicket parameters) to a mounted page that should be allowed. By
	 * default, enforcement is not turned on, so we don't set it as a setting here.
	 */
	public void testDirectAccessToMountedPageAllowed()
	{
		tester.setupRequestAndResponse();
		tester.getServletRequest()
			.setURL(
				"/WicketTester$DummyWebApplication/WicketTester$DummyWebApplication?wicket:bookmarkablePage=:" +
					TestPage.class.getName() + "");
		tester.processRequestCycle();
		tester.assertRenderedPage(TestPage.class);
	}

	int getErrorCode(RequestCycle requestCycle)
	{
		IRequestTarget requestTarget = requestCycle.getRequestTarget();
		assertTrue(requestTarget instanceof WebErrorCodeResponseTarget);
		WebErrorCodeResponseTarget error = (WebErrorCodeResponseTarget)requestTarget;
		return error.getErrorCode();
	}

	/**
	 * Test direct access (with wicket parameters) to a mounted page that should NOT be allowed due
	 * to the {@link ISecuritySettings#getEnforceMounts()} setting being set to true.
	 */
	public void testDirectAccessToMountedPageNotAllowed()
	{
		tester.getApplication().getSecuritySettings().setEnforceMounts(true);

		tester.setupRequestAndResponse();
		tester.getServletRequest().setURL(
			"?wicket:bookmarkablePage=:" + TestPage.class.getName() + "");
		try
		{
			WebRequestCycle requestCycle = tester.createRequestCycle();
			tester.processRequestCycle(requestCycle);
			assertEquals(HttpServletResponse.SC_FORBIDDEN, getErrorCode(requestCycle));
		}
		finally
		{
			tester.getApplication().getSecuritySettings().setEnforceMounts(false);
		}
	}

	/**
	 * Test direct access (with wicket parameters) to a mounted page including (part of the) mount
	 * path.
	 * 
	 * @see WebRequestCycleProcessor#resolve(org.apache.wicket.RequestCycle,
	 *      org.apache.wicket.request.RequestParameters) for an explanation of this test
	 */
	public void testDirectAccessToMountedPageWithExtraPath()
	{
		tester.setupRequestAndResponse();
		tester.getServletRequest()
			.setURL(
				"/WicketTester$DummyWebApplication/WicketTester$DummyWebApplication/foo/bar/?wicket:bookmarkablePage=:" +
					TestPage.class.getName() + "");
		tester.processRequestCycle();
		tester.assertRenderedPage(TestPage.class);
	}

	/**
	 * Test mount access to a mounted page that should be allowed.
	 */
	public void testMountAccessToMountedPageAllowed()
	{
		tester.getApplication().getSecuritySettings().setEnforceMounts(false);

		tester.setupRequestAndResponse();
		tester.getServletRequest()
			.setURL(
				"/WicketTester$DummyWebApplication/WicketTester$DummyWebApplication/mount/point/TestPage");
		tester.processRequestCycle();
		tester.assertRenderedPage(TestPage.class);
	}

	/**
	 * Tests mounting.
	 */
	public void testValidMount1()
	{
		tester.getServletRequest().setURL(
			"/WicketTester$DummyWebApplication/WicketTester$DummyWebApplication/mount/point");
		IRequestTargetUrlCodingStrategy ucs = getRequestCodingStrategy();
		assertNotNull(ucs);
		assertNull(ucs.decode(tester.getWicketRequest().getRequestParameters()));
	}

	/**
	 * Tests mounting.
	 */
	public void testValidMount2()
	{
		tester.getServletRequest()
			.setURL(
				"/WicketTester$DummyWebApplication/WicketTester$DummyWebApplication/mount/point/TestPage");
		IRequestTargetUrlCodingStrategy ucs = getRequestCodingStrategy();
		assertNotNull(ucs);
		assertNotNull(ucs.decode(tester.getWicketRequest().getRequestParameters()));
	}

	/**
	 * @return request coding strategy for this test.
	 */
	private IRequestTargetUrlCodingStrategy getRequestCodingStrategy()
	{
		String relativePath = tester.getApplication().getWicketFilter().getRelativePath(
			tester.getServletRequest());
		return tester.getApplication()
			.getRequestCycleProcessor()
			.getRequestCodingStrategy()
			.urlCodingStrategyForPath(relativePath);
	}
}
