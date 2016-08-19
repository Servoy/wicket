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
package org.apache.wicket.protocol.http;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.Application;
import org.apache.wicket.Response;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.util.string.AppendingStringBuffer;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.util.time.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Implements responses over the HTTP protocol by holding an underlying HttpServletResponse object
 * and providing convenience methods for using that object. Convenience methods include methods
 * which: add a cookie, close the stream, encode a URL, redirect a request to another resource,
 * determine if a redirect has been issued, set the content type, set the locale and, most
 * importantly, write a String to the response output.
 *
 * @author Jonathan Locke
 */
public class WebResponse extends Response
{
	private static final Pattern LEADING_DOT_DOT_SLASH = Pattern.compile("^((\\.\\./)+)");

	/** Log. */
	private static final Logger log = LoggerFactory.getLogger(WebResponse.class);

	/** True if response is a redirect. */
	protected boolean redirect;

	/** The underlying response object. */
	private final HttpServletResponse httpServletResponse;

	/** Is the request an ajax request? */
	private boolean ajax;

	/**
	 * Constructor for testing harness.
	 */
	public WebResponse()
	{
		httpServletResponse = null;
	}

	/**
	 * Package private constructor.
	 *
	 * @param httpServletResponse
	 *            The servlet response object
	 */
	public WebResponse(final HttpServletResponse httpServletResponse)
	{
		this.httpServletResponse = httpServletResponse;
	}

	/**
	 * Add a cookie to the web response
	 *
	 * @param cookie
	 */
	public void addCookie(final Cookie cookie)
	{
		if (httpServletResponse != null)
		{
			httpServletResponse.addCookie(cookie);
		}
	}

	/**
	 * Convenience method for clearing a cookie.
	 *
	 * @param cookie
	 *            The cookie to set
	 * @see WebResponse#addCookie(Cookie)
	 */
	public void clearCookie(final Cookie cookie)
	{
		if (httpServletResponse != null)
		{
			cookie.setMaxAge(0);
			cookie.setValue(null);
			addCookie(cookie);
		}
	}

	/**
	 * Closes response output.
	 */
	@Override
	public void close()
	{
		// NOTE: Servlet container will close the response output stream
		// automatically, so we do nothing here.
	}

	/**
	 * Returns the given url encoded.
	 *
	 * @param url
	 *            The URL to encode
	 * @return The encoded url
	 */
	@Override
	public CharSequence encodeURL(CharSequence url)
	{
		if (httpServletResponse != null && url != null)
		{
			if (url.length() > 0 && url.charAt(0) == '?')
			{
				// there is a bug in apache tomcat 5.5 where tomcat doesn't put sessionid to url
				// when the URL starts with '?'. So we prepend the URL with ./ and remove it
				// afterwards (unless some container prepends session id before './' or mangles
				// the URL otherwise

				String encoded = httpServletResponse.encodeURL("./" + url.toString());
				if (encoded.startsWith("./"))
				{
					return encoded.substring(2);
				}
				else
				{
					return encoded;
				}
			}
			else
			{
				return httpServletResponse.encodeURL(url.toString());
			}
		}
		return url;
	}

	/**
	 * Gets the wrapped http servlet response object.
	 *
	 * @return The wrapped http servlet response object
	 */
	public final HttpServletResponse getHttpServletResponse()
	{
		return httpServletResponse;
	}

	/**
	 * @see org.apache.wicket.Response#getOutputStream()
	 */
	@Override
	public OutputStream getOutputStream()
	{
		try
		{
			return httpServletResponse.getOutputStream();
		}
		catch (IOException e)
		{
			throw new WicketRuntimeException("Error while getting output stream.", e);
		}
	}

	/**
	 * Whether this response is going to redirect the user agent.
	 *
	 * @return True if this response is going to redirect the user agent
	 */
	@Override
	public final boolean isRedirect()
	{
		return redirect;
	}

	private String removeLeadingDotDotSlashes(String url, StringBuilder removed)
	{
		Matcher matcher = LEADING_DOT_DOT_SLASH.matcher(url);
		if (!matcher.find())
		{
			return url;
		}

		removed.append(matcher.group(1));
		StringBuffer sb = new StringBuffer();
		return matcher.appendReplacement(sb, "").appendTail(sb).toString();
	}

	/**
	 * CLIENTS SHOULD NEVER CALL THIS METHOD FOR DAY TO DAY USE!
	 * <p>
	 * Redirects to the given url. Implementations should encode the URL to make sure cookie-less
	 * operation is supported in case clients forgot.
	 * </p>
	 *
	 * @param url
	 *            The URL to redirect to
	 */
	@Override
	public void redirect(String url)
	{
		if (!redirect)
		{
			if (httpServletResponse != null)
			{
				// encode to make sure no caller forgot this
				try
				{
					if (isAjax())
					{
						// Remove leading '../' sequences for encoding, some webserver
						// implementations (tomcat) may fail because in case of an a ajax call the
						// redirect is relative to the page url and not the current ajax http
						// request.
						StringBuilder dotsRemoved = new StringBuilder();
						url = httpServletResponse.encodeRedirectURL(removeLeadingDotDotSlashes(url,
							dotsRemoved));
						url = dotsRemoved.toString() + url;

						if (httpServletResponse.isCommitted())
						{
							log.error("Unable to redirect to: " + url +
								", HTTP Response has already been committed.");
						}

						if (log.isDebugEnabled())
						{
							log.debug("Redirecting to " + url);
						}

						/*
						 * By reaching this point, make sure the HTTP response status code is set to
						 * 200, otherwise wicket-ajax.js will not process the Ajax-Location header
						 */
						httpServletResponse.addHeader("Ajax-Location", url);

						// safari chokes on empty response. so we should always output at least a
						// "-"

						/*
						 * usually the Ajax-Location header is enough and we do not need to the
						 * redirect url into the response, but sometimes the response is processed
						 * via an iframe (eg using multipart ajax handling) and the headers are not
						 * available because XHR is not used and that is the only way javascript has
						 * access to response headers.
						 */
						httpServletResponse.getWriter().write(
							"<ajax-response><redirect><![CDATA[" + url +
								"]]></redirect></ajax-response>");

						configureAjaxRedirect();
					}
					else
					{

						url = httpServletResponse.encodeRedirectURL(url);
						if (httpServletResponse.isCommitted())
						{
							log.error("Unable to redirect to: " + url +
								", HTTP Response has already been committed.");
						}

						if (log.isDebugEnabled())
						{
							log.debug("Redirecting to " + url);
						}

						sendRedirect(url);
					}
					redirect = true;
				}
				catch (IOException e)
				{
					log.warn("redirect to " + url + " failed: " + e.getMessage());
				}
			}
		}
		else
		{
			log.info("Already redirecting to an url current one ignored: " + url);
		}
	}

	/**
	 * Called when Wicket wants to send a redirect to the servlet response. By default, WebResponse
	 * just calls <code>httpServletResponse.sendRedirect(url)</code>. However, certain servlet
	 * containers do not treat relative URL redirects correctly (i.e. WebSphere). If you are using
	 * one of these containers, you can override this method and convert the relative URL to an
	 * absolute URL before sending the redirect to the servlet container.
	 *
	 * Example of how to fix this for your buggy container (in your application):
	 *
	 * <pre>
	 * &#064;Override
	 * protected WebResponse newWebResponse(HttpServletResponse servletResponse)
	 * {
	 * 	return new WebResponse(servletResponse)
	 * 	{
	 * 		&#064;Override
	 * 		public void sendRedirect(String url) throws IOException
	 * 		{
	 * 			String reqUrl = ((WebRequest)RequestCycle.get().getRequest()).getHttpServletRequest()
	 * 				.getRequestURI();
	 * 			String absUrl = RequestUtils.toAbsolutePath(reqUrl, url);
	 * 			getHttpServletResponse().sendRedirect(absUrl);
	 * 		}
	 * 	};
	 * }
	 * </pre>
	 *
	 * @param url
	 *            the URL to redirect to
	 * @throws IOException
	 */
	protected void sendRedirect(String url) throws IOException
	{
		httpServletResponse.sendRedirect(url);
	}

	/**
	 * Additional header configs for ajax redirects
	 */
	protected void configureAjaxRedirect()
	{
		// Set the encoding (see Wicket-2348)
		final String encoding = Application.get()
			.getRequestCycleSettings()
			.getResponseRequestEncoding();

		// Set content type based on markup type for page
		setCharacterEncoding(encoding);
		setContentType("text/xml; charset=" + encoding);
		setHeader("Expires", "Mon, 26 Jul 1997 05:00:00 GMT");
		setHeader("Cache-Control", "no-cache, must-revalidate");
		setHeader("Pragma", "no-cache");
	}

	/**
	 * Set the content type on the response.
	 *
	 * @param mimeType
	 *            The mime type
	 */
	@Override
	public final void setContentType(final String mimeType)
	{
		if (httpServletResponse != null)
		{
			httpServletResponse.setContentType(mimeType);
		}
	}

	/**
	 * @see org.apache.wicket.Response#setContentLength(long)
	 */
	@Override
	public void setContentLength(long length)
	{
		if (httpServletResponse != null)
		{
			httpServletResponse.addHeader("Content-Length", Long.toString(length));
		}
	}

	/**
	 * @see org.apache.wicket.Response#setLastModifiedTime(org.apache.wicket.util.time.Time)
	 */
	@Override
	public void setLastModifiedTime(Time time)
	{
		if (httpServletResponse != null)
		{
			if (time != null && time.getMilliseconds() != -1)
			{
				httpServletResponse.setDateHeader("Last-Modified", time.getMilliseconds());
			}
		}
	}

	/**
	 * Output stream encoding. If the deployment descriptor contains a locale-encoding-mapping-list
	 * element, and that element provides a mapping for the given locale, that mapping is used.
	 * Otherwise, the mapping from locale to character encoding is container dependent. Default is
	 * ISO-8859-1.
	 *
	 * @see javax.servlet.ServletResponse#setLocale(java.util.Locale)
	 *
	 * @param locale
	 *            The locale use for mapping the character encoding
	 */
	@Override
	public final void setLocale(final Locale locale)
	{
		if (httpServletResponse != null)
		{
			httpServletResponse.setLocale(locale);
		}
	}

	/**
	 * Writes string to response output.
	 *
	 * @param string
	 *            The string to write
	 */
	@Override
	public void write(final CharSequence string)
	{
		if (string instanceof AppendingStringBuffer)
		{
			write((AppendingStringBuffer)string);
		}
		else if (string instanceof StringBuffer)
		{
			try
			{
				StringBuffer sb = (StringBuffer)string;
				char[] array = new char[sb.length()];
				sb.getChars(0, sb.length(), array, 0);
				httpServletResponse.getWriter().write(array, 0, array.length);
			}
			catch (IOException e)
			{
				throw new WicketRuntimeException("Error while writing to servlet output writer.", e);
			}
		}
		else
		{
			try
			{
				httpServletResponse.getWriter().write(string.toString());
			}
			catch (IOException e)
			{
				throw new WicketRuntimeException("Error while writing to servlet output writer.", e);
			}
		}
	}

	/**
	 * Writes AppendingStringBuffer to response output.
	 *
	 * @param asb
	 *            The AppendingStringBuffer to write to the stream
	 */
	public void write(AppendingStringBuffer asb)
	{
		try
		{
			httpServletResponse.getWriter().write(asb.getValue(), 0, asb.length());
		}
		catch (IOException e)
		{
			throw new WicketRuntimeException("Error while writing to servlet output writer.", e);
		}
	}

	/**
	 * Set a header to the date value in the servlet response stream.
	 *
	 * @param header
	 * @param date
	 */
	public void setDateHeader(String header, long date)
	{
		if (httpServletResponse != null)
		{
			httpServletResponse.setDateHeader(header, date);
		}
	}


	/**
	 * Set a header to the string value in the servlet response stream.
	 *
	 * @param header
	 * @param value
	 */
	public void setHeader(String header, String value)
	{
		if (httpServletResponse != null)
		{
			httpServletResponse.setHeader(header, value);
		}
	}

	/**
	 * Convenience method for setting the content-disposition:attachment header. This header is used
	 * if the response should prompt the user to download it as a file instead of opening in a
	 * browser.
	 *
	 * @param filename
	 *            file name of the attachment
	 */
	public void setAttachmentHeader(String filename)
	{
		setHeader("Content-Disposition", "attachment" +
			((!Strings.isEmpty(filename)) ? ("; filename=\"" + filename + "\"") : ""));
	}

	/**
	 * Is the request, which matches this response an ajax request.
	 *
	 * @return True if the request is an ajax request.
	 */
	public boolean isAjax()
	{
		return ajax;
	}

	/**
	 * Set that the request which matches this response is an ajax request.
	 *
	 * @param ajax
	 *            True if the request is an ajax request.
	 */
	public void setAjax(boolean ajax)
	{
		this.ajax = ajax;
	}

	/**
	 * Make this response non-cacheable
	 */
	public void disableCaching()
	{
		setDateHeader("Date", System.currentTimeMillis());
		setDateHeader("Expires", 0);
		setHeader("Pragma", "no-cache");
		setHeader("Cache-Control", "no-cache, no-store");
	}
}
