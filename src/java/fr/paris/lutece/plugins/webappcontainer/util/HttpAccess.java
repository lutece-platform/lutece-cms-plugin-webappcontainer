/*
 * Copyright (c) 2002-2017, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.webappcontainer.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.NTCredentials;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.ByteArrayPartSource;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.PartSource;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.webappcontainer.business.Site;
import fr.paris.lutece.plugins.webappcontainer.business.WebappResponse;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

/**
 * Http net Object Accessor <br/>
 */
public class HttpAccess
{
	// proxy authentication settings
	private static final String PROPERTY_PROXY_HOST = "webappcontainer.httpAccess.proxyHost";

	private static final String PROPERTY_PROXY_PORT = "webappcontainer.httpAccess.proxyPort";

	private static final String PROPERTY_PROXY_USERNAME = "webappcontainer.httpAccess.proxyUserName";

	private static final String PROPERTY_PROXY_PASSWORD = "webappcontainer.httpAccess.proxyPassword";

	private static final String PROPERTY_HOST_NAME = "webappcontainer.httpAccess.hostName";

	private static final String PROPERTY_DOMAIN_NAME = "webappcontainer.httpAccess.domainName";

	private static final String PROPERTY_REALM = "webappcontainer.httpAccess.realm";

	private static final String PROPERTY_TIMEOUT_SOCKET = "webappcontainer.httpAccess.timeoutSocket";

	private static final String PROPERTY_TIMEOUT_CONNECTION = "webappcontainer.httpAccess.timeoutConnection";

	private static final String PROPERTY_PROTOCOL_CONTENT_CHARSET = "webappcontainer.site.default.encoding";

	private static final String HTTP_SOCKET_TIMEOUT = "http.socket.timeout";

	private static final String HTTP_CONNECTION_TIMEOUT = "http.connection.timeout";

	private static final String HTTP_PROTOCOL_CONTENT_CHARSET = "http.protocol.content-charset";

	private static final String RESPONSE_HEADER_LOCATION = "location";

	private static final String REGEX_INT = "^[\\d]+$";

	private static final int DEFAULT_TIMEOUT_SOCKET = 10000;

	private static final int DEFAULT_TIMEOUT_CONNECTION = 10000;

	private static final boolean FOLLOW_REDIRECT = true;

	private Map<String, String[]> _parametersMap;

	private Map<String, FileItem> _filesMap;

	private HttpClient _client;

	private Cookie[] _cookies = new Cookie[]
	{};

	private Site _site = new Site();

	/**
	 * Initialize the Http state with the previous state informations (cookies)
	 * 
	 * @param cookies The cookie array
	 * @param site The site requested
	 */
	public void initializeState( Cookie[] cookies, Site site )
	{
		// Save a copy to cookies to avoid malicious code
		if ( cookies != null )
		{
			_cookies = cookies.clone();
		}

		_site = site;
	}

	/**
	 * Send a GET HTTP request to an Url and return the response content.
	 * 
	 * @param strUrl The Url to access
	 * @return The response content of the Get request to the given Url
	 * @throws HttpAccessException if there is a problem to access to the given Url
	 */
	public WebappResponse doGet( String strUrl ) throws HttpAccessException
	{
		WebappResponse webappResponse = null;

		try
		{
			AppLogService.debug( "HttpAccess.goGet( strUrl = " + strUrl + ")" );

			HttpMethodBase method = new GetMethod( strUrl );

			method.setFollowRedirects( FOLLOW_REDIRECT );

			webappResponse = doRequest( method );
		}
		catch ( Exception e )
		{
			AppLogService.error( e.getMessage(), e );
			throw new HttpAccessException( e.getMessage(), e );
		}

		return webappResponse;
	}

	/**
	 * Send a POST HTTP request to an Url and return the response content.
	 * 
	 * @param strUrl The Url to access
	 * @param parametersMap The list of post parameters
	 * @param fileMap The file list
	 * @return The response content of the Post request to the given Url
	 * @throws HttpAccessException if there is a problem to access to the given Url
	 */
	public WebappResponse doPost( String strUrl, Map<String, String[]> parametersMap, Map<String, FileItem> fileMap ) throws HttpAccessException
	{
		WebappResponse webappResponse = null;
		_parametersMap = parametersMap;
		_filesMap = fileMap;

		try
		{
			AppLogService.debug( "HttpAccess.goPost( strUrl = " + strUrl + ")" );

			PostMethod method = new PostMethod( strUrl );

			// If the file map is not null, then there is file to send to external site. So create the multi part request
			if ( fileMap != null )
			{
				setMultipartRequest( method, parametersMap, fileMap );
			}
			else
			{
				Set<String> setParametersString = parametersMap.keySet();
				Iterator<String> iteratorParametersString = setParametersString.iterator();

				while (iteratorParametersString.hasNext())
				{
					String strKey = ( String ) iteratorParametersString.next();
					for ( String strValue : parametersMap.get( strKey ) )
					{
						method.addParameter( strKey, strValue );
					}
					// method.addParameter( strKey, ( String ) parametersMap.get( strKey )[0] );
				}
			}

			webappResponse = doRequest( method );
		}
		catch ( Exception e )
		{
			AppLogService.error( e.getMessage(), e );
			throw new HttpAccessException( e.getMessage(), e );
		}

		return webappResponse;
	}

	/**
	 * In case of multipart request, set the multi parts with file list and String parameter list
	 * @param method The {@link PostMethod}
	 * @param parametersMap The list of String parameters
	 * @param fileMap The file list
	 */
	private void setMultipartRequest( PostMethod method, Map<String, String[]> parametersMap, Map<String, FileItem> fileMap )
	{
		Set<String> setParametersString = parametersMap.keySet();
		Iterator<String> iteratorParametersString = setParametersString.iterator();
		Set<String> setParametersFile = fileMap.keySet();
		Part[] parts = new Part[setParametersString.size() + setParametersFile.size()];
		int i = 0;

		// Set the String parameters list
		while (iteratorParametersString.hasNext())
		{
			String strKey = ( String ) iteratorParametersString.next();
			parts[i++] = new StringPart( strKey, ( String ) parametersMap.get( strKey )[0] );
		}

		method.getParams().setBooleanParameter( HttpMethodParams.USE_EXPECT_CONTINUE, true );

		Iterator<String> iteratorParemetersFile = setParametersFile.iterator();

		// Set the File parameters list
		while (iteratorParemetersFile.hasNext())
		{
			String strKey = ( String ) iteratorParemetersFile.next();
			FileItem fileItem = fileMap.get( strKey );
			PartSource partSource = new ByteArrayPartSource( fileItem.getName(), fileItem.get() );
			parts[i++] = new FilePart( strKey, partSource, fileItem.getContentType(), method.getRequestCharSet() );
		}

		method.setRequestEntity( new MultipartRequestEntity( parts, method.getParams() ) );
	}

	/**
	 * Send a HTTP request to an Url and return the response content.
	 * 
	 * @param method the request method
	 * @return The response content of the request in a {@link WebappResponse} object
	 * @throws HttpAccessException if there is a problem to access to the given Url
	 */
	private WebappResponse doRequest( HttpMethodBase method ) throws HttpAccessException
	{
		WebappResponse webappResponse = null;
		String strUrl = null;

		try
		{
			strUrl = method.getURI().toString();

			// Initialize the state with the previous cookies
			HttpState state = new HttpState();
			// get Cookies
			state.addCookies( _cookies );

			HttpClient client = getHttpClient( method );

			client.setState( state );

			int nResponse = client.executeMethod( method );

			// Show debug if debug mode is enabled
			if ( AppLogService.isDebugEnabled() )
			{
				showRequestDebugLogs( state );

				AppLogService.debug( "[doRequest] Get content from " + strUrl );
				AppLogService.debug( "[doRequest] HTTP code : " + nResponse );
			}

			// Redirections :
			if ( ( nResponse >= HttpURLConnection.HTTP_MULT_CHOICE ) && ( nResponse < HttpURLConnection.HTTP_BAD_REQUEST ) )
			{
				String strNewLocation = method.getResponseHeader( RESPONSE_HEADER_LOCATION ).getValue();
				AppLogService.debug( "[doRequest] Redirection -> " + strNewLocation );

				// The redirection can only be caused by a postMethod (the get method have the followRedirect to true)
				return doPost( strNewLocation, _parametersMap, _filesMap );
			}

			// Errors :
			if ( nResponse != HttpURLConnection.HTTP_OK )
			{
				String strError = "HttpAccess - Error getting URL : " + strUrl + " - return code : " + nResponse;
				throw new HttpAccessException( strError, null );
			}

			// Check if the content is empty
			int nResponseBodyLength = method.getResponseBody().length;

			if ( nResponseBodyLength <= 0 )
			{
				String strError = "HttpAccess - Error getting URL : No content received (" + nResponseBodyLength + " byte)";
				throw new HttpAccessException( strError, null );
			}

			// Set the webappResponse with the response informations
			webappResponse = new WebappResponse();

			// Set the content
			webappResponse.setContent( method.getResponseBody() );

			// Set the content charset
			webappResponse.setContentCharset( _site.getEncoding() );

			// Set the cookies list
			if ( client.getState().getCookies().length > 0 )
			{
				webappResponse.setCookies( client.getState().getCookies() );
			}

			// Set the location
			if ( method.getURI() != null )
			{
				webappResponse.setLocation( method.getURI().getURI() );
			}
			else
			{
				webappResponse.setLocation( strUrl );
			}

			// Show debug if debug mode is enabled
			if ( AppLogService.isDebugEnabled() )
			{
				showResponseDebugLogs( client, method );
			}
		}
		catch ( HttpException e )
		{
			String strError = "HttpAccess - Error connecting to '" + strUrl + "' : ";
			AppLogService.error( strError + e.getMessage(), e );
			throw new HttpAccessException( strError + e.getMessage(), e );
		}
		catch ( IOException e )
		{
			String strError = "HttpAccess - Error downloading '" + strUrl + "' : ";
			AppLogService.error( strError + e.getMessage(), e );
			throw new HttpAccessException( strError + e.getMessage(), e );
		}
		finally
		{
			// Release the connection.
			method.releaseConnection();
		}

		return webappResponse;
	}

	/**
	 * Create an HTTP client object using current configuration
	 * 
	 * @param method The method
	 * @return An HTTP client authenticated
	 * @throws HttpAccessException if there is a problem to access to the given Url
	 */
	private HttpClient getHttpClient( HttpMethodBase method ) throws HttpAccessException
	{
		String strProxyHost = AppPropertiesService.getProperty( PROPERTY_PROXY_HOST );
		String strProxyPort = AppPropertiesService.getProperty( PROPERTY_PROXY_PORT );
		String strProxyUserName = AppPropertiesService.getProperty( PROPERTY_PROXY_USERNAME );
		String strProxyPassword = AppPropertiesService.getProperty( PROPERTY_PROXY_PASSWORD );
		String strHostName = AppPropertiesService.getProperty( PROPERTY_HOST_NAME );
		String strDomainName = AppPropertiesService.getProperty( PROPERTY_DOMAIN_NAME );
		String strRealm = AppPropertiesService.getProperty( PROPERTY_REALM );
		String strContentCharset = _site.getEncoding();
		int nTimeoutSocket = getPropertyInt( PROPERTY_TIMEOUT_SOCKET, DEFAULT_TIMEOUT_SOCKET );
		int nTimeoutConnection = getPropertyInt( PROPERTY_TIMEOUT_CONNECTION, DEFAULT_TIMEOUT_CONNECTION );

		if ( _client != null )
		{
			return _client;
		}

		// Create an instance of HttpClient.
		HttpClient client = new HttpClient();

		// Set socket and connection timeout
		client.getParams().setParameter( HTTP_SOCKET_TIMEOUT, nTimeoutSocket );
		client.getParams().setParameter( HTTP_CONNECTION_TIMEOUT, nTimeoutConnection );

		// Set the encoding
		if ( StringUtils.isBlank( strContentCharset ) )
		{
			strContentCharset = AppPropertiesService.getProperty( PROPERTY_PROTOCOL_CONTENT_CHARSET );
		}
		client.getParams().setParameter( HTTP_PROTOCOL_CONTENT_CHARSET, strContentCharset );

		// If proxy host and port are defined and site use proxy, set the corresponding elements
		if ( ( strProxyHost != null ) && ( !strProxyHost.equals( "" ) ) && ( strProxyPort != null ) && ( !strProxyPort.equals( "" ) ) && _site.isUseProxy() )
		{
			client.getHostConfiguration().setProxy( strProxyHost, Integer.parseInt( strProxyPort ) );
		}

		Credentials cred = null;

		// if hostname and domain name found, consider we are in NTLM authentication scheme
		// else if only username and password found, use simple UsernamePasswordCredentials
		if ( ( strHostName != null ) && ( strDomainName != null ) )
		{
			cred = new NTCredentials( strProxyUserName, strProxyPassword, strHostName, strDomainName );
		}
		else if ( ( strProxyUserName != null ) && ( strProxyPassword != null ) )
		{
			cred = new UsernamePasswordCredentials( strProxyUserName, strProxyPassword );
		}

		if ( cred != null )
		{
			AuthScope authScope = new AuthScope( null, -1, strRealm, null );
			client.getState().setProxyCredentials( authScope, cred );

			client.getParams().setAuthenticationPreemptive( true );

			method.setDoAuthentication( true );
		}

		client.getParams().setCookiePolicy( CookiePolicy.BROWSER_COMPATIBILITY );
		_client = client;

		return client;
	}

	/**
	 * Method used to display debug informations before requesting
	 * 
	 * @param state The {@link HttpState} object
	 */
	private void showRequestDebugLogs( HttpState state )
	{
		// [debug] Display the cookies
		for ( Cookie c : state.getCookies() )
		{
			AppLogService.debug( "[doRequest] setCookie = " + c.getName() + "=" + c.getValue() + " - domain : " + c.getDomain() );
		}
	}

	/**
	 * Method used to display response debug informations
	 * 
	 * @param client The {@link HttpClient}
	 * @param method The {@link HttpMethodBase}
	 * @throws IOException Exception occurs when getting response body
	 */
	private void showResponseDebugLogs( HttpClient client, HttpMethodBase method ) throws IOException
	{
		// [debug] Display the cookies
		for ( Cookie cookie : client.getState().getCookies() )
		{
			AppLogService.debug( "[doRequest][response] getCookie = " + cookie.getName() + " " + cookie.getValue() + " - domain : " + cookie.getDomain() );
		}

		AppLogService.debug( "[doRequest] Received : " + method.getResponseBody().length + " bytes" );

		// [debug] Display the headers
		for ( Header header : method.getResponseHeaders() )
		{
			AppLogService.debug( "Method Header" + header.getName() + " -> " + header.getValue() );
		}
	}

	/**
	 * Returns the value of a property defined in the .properties file of the application as an int
	 * 
	 * @param strPropertyName The property name
	 * @param nDefault The default value which is returned if no value is found for the property in the .properties file
	 * @return The property value read in the .properties file
	 * @throws HttpAccessException if there is a problem to access to the given Url
	 */
	private int getPropertyInt( String strPropertyName, int nDefault ) throws HttpAccessException
	{
		String strPropertyValue = AppPropertiesService.getProperty( strPropertyName );
		int nPropertyValue = nDefault;

		try
		{
			if ( ( strPropertyValue != null ) && strPropertyValue.matches( REGEX_INT ) )
			{
				nPropertyValue = Integer.parseInt( strPropertyValue );
			}
		}
		catch ( NumberFormatException e )
		{
			String strError = "HttpAccess - Error retrieving property '" + strPropertyName + "' : ";
			AppLogService.error( strError + e.getMessage(), e );
			throw new HttpAccessException( strError + e.getMessage(), e );
		}

		return nPropertyValue;
	}
}
