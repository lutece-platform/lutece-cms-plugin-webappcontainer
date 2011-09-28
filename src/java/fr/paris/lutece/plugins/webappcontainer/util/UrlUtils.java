/*
 * Copyright (c) 2002-2009, Mairie de Marseille
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
 *  3. Neither the name of 'Mairie de Marseille' nor 'Lutece' nor the names of its
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
import java.net.MalformedURLException;
import java.net.URL;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import fr.paris.lutece.portal.service.util.AppLogService;

/**
 * This class provide tools for Urls
 * @author ELY
 * 
 */
public final class UrlUtils
{
	/**
	 * Private constructor - this class does not need to be instantiated
	 */
	private UrlUtils()
	{
	}

	/**
	 * Check if the host of the two url are the same
	 * @param strFirstUrl The first url
	 * @param strSecondUrl The second url
	 * @return true if the host are equals, false else or if an url is malformed (MalformedURLException).
	 */
	public static boolean hostsEquals( String strFirstUrl, String strSecondUrl )
	{
		URL urlFirstUrl = null;
		URL urlSecondUrl = null;

		try
		{
			urlFirstUrl = new URL( strFirstUrl );
			urlSecondUrl = new URL( strSecondUrl );
		}
		catch ( MalformedURLException e )
		{
			return false;
		}

		return urlFirstUrl.getHost().equalsIgnoreCase( urlSecondUrl.getHost() );
	}

	/**
	 * Convert the specified url
	 * @param strUrl The url to convert
	 * @param strBaseUrlSite The webapp url
	 * @return The converted url
	 */
	public static String convertRelativeToAbsoluteUrl( String strUrl, String strBaseUrlSite )
	{
		String strConvertedUrl = null;
		URL baseUrlSite = null;
		AppLogService.debug( "[ConvertUrl] Webapp url = " + strBaseUrlSite );

		try
		{
			baseUrlSite = new URL( strBaseUrlSite );

			URL convertedUrl = new URL( baseUrlSite, strUrl );

			strConvertedUrl = convertedUrl.toExternalForm();
		}
		catch ( MalformedURLException e )
		{
			AppLogService.error( "[convertUrl] Malformed Url Exception with url = " + strUrl, e );

			return null;
		}

		AppLogService.debug( "[ConvertUrl] " + strUrl + "   ->   " + strConvertedUrl );

		return strConvertedUrl;
	}

	/**
	 * Encode (Base64) the specified Url
	 * @param strUrl The url to encode
	 * @return The encoded url
	 */
	public static String encodeUrl( String strUrl )
	{
		BASE64Encoder encoder = new BASE64Encoder();

		return encoder.encode( strUrl.getBytes() );
	}

	/**
	 * Decode (Base64) the specified url
	 * 
	 * @param strUrl the url to decode
	 * @return The decoded url or null if error (IOException)
	 */
	public static String decodeUrl( String strUrl )
	{
		BASE64Decoder decoder = new BASE64Decoder();
		String strReturnUrl = null;

		try
		{
			if ( strUrl != null )
			{
				strReturnUrl = new String( decoder.decodeBuffer( strUrl ) );
			}
		}
		catch ( IOException e )
		{
			return null;
		}

		return strReturnUrl;
	}
}
