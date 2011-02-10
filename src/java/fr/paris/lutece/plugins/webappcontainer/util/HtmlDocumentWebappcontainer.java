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

import fr.paris.lutece.plugins.webappcontainer.business.Site;
import fr.paris.lutece.plugins.webappcontainer.service.WebappcontainerPlugin;
import fr.paris.lutece.plugins.webappcontainer.web.WebappcontainerApp;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.w3c.tidy.Tidy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


/**
 * This classes provides implementation to retrieve urls from specified tags
 * on an HTML page.
 */
public class HtmlDocumentWebappcontainer
{
    public static final String CONSTANT_STATIC_URL = "https?://[^/]+/";
    public static final String CONSTANT_PROTOCOL_DELIMITER = ":";
    private static final String PROPERTY_PARSING_STOP_WHEN_ERROR = "webappcontainer.jtidy.parsing.stopWhenError";
    private static final String TAG_INPUT = "input";
    private static final String TAG_INPUT_ATTRIBUTE_TYPE = "type";
    private static final String TAG_INPUT_ATTRIBUTE_NAME = "name";
    private static final String TAG_INPUT_ATTRIBUTE_VALUE = "value";
    private static final String TAG_INPUT_ATTRIBUTE_TYPE_VALUE_HIDDEN = "hidden";
    private static final String EMPTY_STRING = "";
    private static final String OMIT_XML_DECLARATION_TRUE = "yes";
    private static final String METHOD_HTML = "html";

    // Definition of some basic html elements
    /**
     *  To define a CSS link, html element must have:
     *  <ul>
     *  <li>"link" tag name</li>
     *  <li>"rel" attribute equal to "stylesheet"</li>
     *  </ul>
     *  The url is contained in the attributed named "href"
     */
    public static final ElementUrl ELEMENT_CSS_LINK;

    /**
     *  To define a CSS style, html element must have:
     *  <ul>
     *  <li>"style" tag name</li>
     *  <li>"type" attribute equal to "text/css"</li>
     *  </ul>
     */
    public static final ElementUrl ELEMENT_CSS_STYLE;

    /**
     *  To define a RSS/XML, link element must have:
     *  <ul>
     *  <li>"link" tag name</li>
     *  <li>"rel" attribute equal to "alternate"</li>
     *  </ul>
     *  The url is contained in the attributed named "href"
     */
    public static final ElementUrl ELEMENT_ALTERNATE;

    /**
     *  To define a javascript, html element must have:
     *  <ul>
     *  <li>"script" tag name</li>
     *  <li>"type" attribute equal to "text/javascript"</li>
     *  </ul>
     *  The url is contained in the attributed named "src"
     */
    public static final ElementUrl ELEMENT_JAVASCRIPT;

    /**
         *  To define an image, html element must have:
         *  <ul>
         *  <li>"img" tag name</li>
         *  </ul>
         *  The url is contained in the attributed named "src"
         */
    public static final ElementUrl ELEMENT_IMG;

    /**
     *  To define a anchor, a element must have:
     *  <ul>
     *  <li>"a" tag name</li>
     *  </ul>
     *  The url is contained in the attributed named "href"
     */
    public static final ElementUrl ELEMENT_A;

    /**
     *  To define a form, form element must have:
     *  <ul>
     *  <li>"form" tag name</li>
     *  </ul>
     *  The url is contained in the attributed named "action"
     */
    public static final ElementUrl ELEMENT_FORM;

    /**
     *  To define a head, head element must have:
     *  <ul>
     *  <li>"head" tag name</li>
     *  </ul>
     */
    public static final ElementUrl ELEMENT_HEAD;

    /**
     *  To define a body, body element must have:
     *  <ul>
     *  <li>"body" tag name</li>
     *  </ul>
     */
    public static final ElementUrl ELEMENT_BODY;

    /**
     *  To define a base, base element must have:
     *  <ul>
     *  <li>"base" tag name</li>
     *  </ul>
     */
    public static final ElementUrl ELEMENT_BASE;

    /**
     *  To define an input. input element must have:
     *  <ul>
     *  <li>"type" = "hidden" tag</li>
     *  </ul>
     */
    public static final ElementUrl ELEMENT_INPUT;

    static
    {
        ELEMENT_CSS_LINK = new ElementUrl( "link", "href", "rel", "stylesheet" );
        ELEMENT_CSS_STYLE = new ElementUrl( "style", null, "type", "text/css" );
        ELEMENT_ALTERNATE = new ElementUrl( "link", "href", "rel", "alternate" );
        ELEMENT_JAVASCRIPT = new ElementUrl( "script", "src", "type", "text/javascript" );
        ELEMENT_IMG = new ElementUrl( "img", "src", null, null );
        ELEMENT_A = new ElementUrl( "a", "href", null, null );
        ELEMENT_FORM = new ElementUrl( "form", "action", null, null );
        ELEMENT_HEAD = new ElementUrl( "head", null, null, null );
        ELEMENT_BODY = new ElementUrl( "body", null, null, null );
        ELEMENT_BASE = new ElementUrl( "base", "href", null, null );
        ELEMENT_INPUT = new ElementUrl( "input", null, null, null );
    }

    private Document _content;

    /**
     * Instantiates an HtmlDocumentWebappcontainer after having built the DOM tree.
     *
     * @param byteHtml The Html code to be parsed.
     * @param strEncoding The encoding used to retrieve urls.
     * @throws HtmlDocumentWebappcontainerException if errors when parsing HTML
     */
    public HtmlDocumentWebappcontainer( byte[] byteHtml, String strEncoding )
        throws HtmlDocumentWebappcontainerException
    {
        // use of tidy to retrieve the DOM tree
        Tidy tidy = new Tidy(  );
        tidy.setQuiet( true );
        tidy.setShowWarnings( false );

        ByteArrayOutputStream baErrors = new ByteArrayOutputStream(  );
        PrintWriter pw = new PrintWriter( baErrors, true );
        tidy.setErrout( pw );
        tidy.setTidyMark( false );
        tidy.setInputEncoding( strEncoding );
        _content = tidy.parseDOM( new ByteArrayInputStream( byteHtml ), null );

        boolean bStopWhenError = Boolean.parseBoolean( AppPropertiesService.getProperty( 
                    PROPERTY_PARSING_STOP_WHEN_ERROR, Boolean.toString( false ) ) );

        if ( bStopWhenError && !baErrors.toString(  ).equals( EMPTY_STRING ) )
        {
            throw new HtmlDocumentWebappcontainerException( baErrors.toString(  ), null );
        }
    }

    /**
     * Get the urls of all html elements specified by elementType and convert its to absolutes urls
     *
     * @param elementType the type of element to get
     * @param strBaseUrlSite The base url of the external site
     * @param site The external site concerned by url conversion
     * @param strReplaceUrl The prefix of the new url (ie : link to webappcontainer servlet or webappcontainer Xpage)
     * @param bEncodeUrl true = encode (Base64) the url
     *
     */
    public void convertUrls( ElementUrl elementType, String strBaseUrlSite, Site site, String strReplaceUrl,
        boolean bEncodeUrl )
    {
        if ( elementType == ELEMENT_FORM )
        {
            changeInputs(  );
        }

        NodeList nodes = _content.getElementsByTagName( elementType.getTagName(  ) );

        for ( int i = 0; i < nodes.getLength(  ); i++ )
        {
            Node node = nodes.item( i );
            NamedNodeMap attributes = node.getAttributes(  );

            // Test if the element matches the required attribute
            if ( ( elementType.getTestedAttributeName(  ) != null ) &&
                    ( attributes.getNamedItem( elementType.getTestedAttributeName(  ) ) != null ) )
            {
                String strRel = attributes.getNamedItem( elementType.getTestedAttributeName(  ) ).getNodeValue(  );

                if ( !elementType.getTestedAttributeValue(  ).equalsIgnoreCase( strRel ) )
                {
                    continue;
                }
            }

            // Retrieve the url, then test if it matches the base url
            Node nodeAttribute = attributes.getNamedItem( elementType.getAttributeName(  ) );

            if ( nodeAttribute == null )
            {
                continue;
            }

            String strSrc = nodeAttribute.getNodeValue(  ).trim(  );
            String strAbsoluteUrl = UrlUtils.convertRelativeToAbsoluteUrl( strSrc, strBaseUrlSite );
            boolean isExternalUrl = !UrlUtils.hostsEquals( strAbsoluteUrl, site.getUrl(  ) );

            if ( bEncodeUrl && !isExternalUrl )
            {
                strAbsoluteUrl = UrlUtils.encodeUrl( strAbsoluteUrl );
            }

            if ( elementType == ELEMENT_FORM )
            {
                Element elementHiddenXPageName = _content.createElement( TAG_INPUT );
                elementHiddenXPageName.setAttribute( TAG_INPUT_ATTRIBUTE_TYPE, TAG_INPUT_ATTRIBUTE_TYPE_VALUE_HIDDEN );
                elementHiddenXPageName.setAttribute( TAG_INPUT_ATTRIBUTE_NAME, WebappcontainerApp.PARAMETER_PAGE );
                elementHiddenXPageName.setAttribute( TAG_INPUT_ATTRIBUTE_VALUE, WebappcontainerPlugin.PLUGIN_NAME );

                node.appendChild( elementHiddenXPageName );

                Element elementHiddenSiteCode = _content.createElement( TAG_INPUT );
                elementHiddenSiteCode.setAttribute( TAG_INPUT_ATTRIBUTE_TYPE, TAG_INPUT_ATTRIBUTE_TYPE_VALUE_HIDDEN );
                elementHiddenSiteCode.setAttribute( TAG_INPUT_ATTRIBUTE_NAME, WebappcontainerApp.PARAMETER_CODE );
                elementHiddenSiteCode.setAttribute( TAG_INPUT_ATTRIBUTE_VALUE, site.getCode(  ) );

                node.appendChild( elementHiddenSiteCode );

                Element elementHiddenWebappUrl = _content.createElement( TAG_INPUT );
                elementHiddenWebappUrl.setAttribute( TAG_INPUT_ATTRIBUTE_TYPE, TAG_INPUT_ATTRIBUTE_TYPE_VALUE_HIDDEN );
                elementHiddenWebappUrl.setAttribute( TAG_INPUT_ATTRIBUTE_NAME, WebappcontainerApp.PARAMETER_WEBAPP_URL );
                elementHiddenWebappUrl.setAttribute( TAG_INPUT_ATTRIBUTE_VALUE, strAbsoluteUrl );

                node.appendChild( elementHiddenWebappUrl );

                nodeAttribute.setNodeValue( AppPathService.getPortalUrl(  ) );
            }
            else
            {
                // Concat the "webappcontainer" part of the url with the external site url (encoded)
                nodeAttribute.setNodeValue( ( !isExternalUrl ) ? ( strReplaceUrl + strAbsoluteUrl ) : strAbsoluteUrl );
            }
        }
    }

    /**
     * Get first element of the specified elementType
     *
     * @param elementType the type of element to get
     * @return the content of the first element specified by the element type
     */
    public StringBuffer getFirstElement( ElementUrl elementType )
    {
        NodeList nodes = _content.getElementsByTagName( elementType.getTagName(  ) );

        if ( nodes.getLength(  ) == 0 )
        {
            return null;
        }

        Node node = nodes.item( 0 );
        NodeList childNodeList = node.getChildNodes(  );
        StringBuffer stringBuffer = new StringBuffer(  );

        for ( int i = 0; i < childNodeList.getLength(  ); i++ )
        {
            stringBuffer.append( getNodeContent( childNodeList.item( i ) ) );
        }

        return stringBuffer;
    }

    /**
     * Get all elements of the specified elementType
     *
     * @param elementType the type of element to get
     * @return the content of all elements specified by the element type
     */
    public StringBuffer getElements( ElementUrl elementType )
    {
        NodeList nodes = _content.getElementsByTagName( elementType.getTagName(  ) );

        if ( nodes.getLength(  ) == 0 )
        {
            return null;
        }

        StringBuffer stringBuffer = new StringBuffer(  );

        for ( int i = 0; i < nodes.getLength(  ); i++ )
        {
            stringBuffer.append( getNodeContent( nodes.item( i ) ) );
        }

        return stringBuffer;
    }

    /**
     * Remove the first element of the specified elementType
     *
     * @param elementType the type of element to remove
     */
    public void removeFirstElement( ElementUrl elementType )
    {
        NodeList nodes = _content.getElementsByTagName( elementType.getTagName(  ) );

        if ( nodes.getLength(  ) != 0 )
        {
            Node node = nodes.item( 0 );
            Node parentNode = node.getParentNode(  );
            parentNode.removeChild( node );
        }
    }

    /**
     * Get first element attribute of the specified elementType
     *
     * @param elementType the type of element to get
     * @return the content of the first element attribute specified by the element type
     */
    public String getFirstElementAttribute( ElementUrl elementType )
    {
        NodeList nodes = _content.getElementsByTagName( elementType.getTagName(  ) );

        if ( nodes.getLength(  ) == 0 )
        {
            return null;
        }

        Node node = nodes.item( 0 );
        NamedNodeMap attributes = node.getAttributes(  );

        // Test if the element matches the required attribute
        if ( elementType.getTestedAttributeName(  ) != null )
        {
            String strRel = attributes.getNamedItem( elementType.getTestedAttributeName(  ) ).getNodeValue(  );

            if ( !elementType.getTestedAttributeValue(  ).equals( strRel ) )
            {
                return null;
            }
        }

        // Retrieve the url, then test if it matches the base url
        Node nodeAttribute = attributes.getNamedItem( elementType.getAttributeName(  ) );

        if ( nodeAttribute == null )
        {
            return null;
        }

        return nodeAttribute.getNodeValue(  );
    }

    /**
     * Get the document content
     * @return The StringBuffer content
     */
    public StringBuffer getContent(  )
    {
        DOMSource domSource = new DOMSource( _content );
        StringWriter writer = new StringWriter(  );
        StreamResult result = new StreamResult( writer );
        TransformerFactory tf = TransformerFactory.newInstance(  );
        Transformer transformer;

        try
        {
            transformer = tf.newTransformer(  );
            transformer.setOutputProperty( OutputKeys.OMIT_XML_DECLARATION, OMIT_XML_DECLARATION_TRUE );
            transformer.setOutputProperty( OutputKeys.METHOD, METHOD_HTML );
            transformer.transform( domSource, result );
        }
        catch ( TransformerConfigurationException e )
        {
            AppLogService.error( e.getMessage(  ) );

            return null;
        }
        catch ( TransformerException e )
        {
            AppLogService.error( e.getMessage(  ) );

            return null;
        }

        return writer.getBuffer(  );
    }

    /**
     * Tranform the tags <input name="xxx" ... /> to <input name="page_external_site_xxx" ... />
     */
    private void changeInputs(  )
    {
        NodeList nodes2 = _content.getElementsByTagName( ELEMENT_INPUT.getTagName(  ) );

        for ( int j = 0; j < nodes2.getLength(  ); j++ )
        {
            Element node2 = (Element) nodes2.item( j );

            node2.setAttribute( TAG_INPUT_ATTRIBUTE_NAME,
                WebappcontainerApp.PARAMETER_PAGE_HACK + node2.getAttribute( TAG_INPUT_ATTRIBUTE_NAME ) );
        }
    }

    /**
     * Transform the node content to XML
     *
     * @param node The node
     * @return The node content in XML
     */
    private String getNodeContent( Node node )
    {
        DOMSource domSource = new DOMSource( node );
        StringWriter writer = new StringWriter(  );
        StreamResult result = new StreamResult( writer );
        TransformerFactory tf = TransformerFactory.newInstance(  );
        Transformer transformer;

        try
        {
            transformer = tf.newTransformer(  );
            transformer.setOutputProperty( OutputKeys.OMIT_XML_DECLARATION, OMIT_XML_DECLARATION_TRUE );
            transformer.setOutputProperty( OutputKeys.METHOD, METHOD_HTML );
            transformer.setOutputProperty( OutputKeys.MEDIA_TYPE, "text/html" );
            transformer.transform( domSource, result );
        }
        catch ( TransformerConfigurationException e )
        {
            AppLogService.error( e.getMessage(  ) );

            return null;
        }
        catch ( TransformerException e )
        {
            AppLogService.error( e.getMessage(  ) );

            return null;
        }

        return writer.toString(  );
    }

    /**
     * provide a description for the HTML elements to be parsed
     */
    private static class ElementUrl
    {
        private String _strTagName;
        private String _strAttributeName;
        private String _strTestedAttributeName;
        private String _strTestedAttributeValue;

        /**
         * Instanciates an ElementUrl
         *
         * @param strTagName the tag name to get (example: link, script, img, ...)
         * @param strAttributeName the attribute name to get (example: src, href, ...)
         * @param strTestedAttributeName the attribute name to test
         * @param strTestedAttributeValue the value of the attribute to test :
         * if the value of the attribute strTestedAttributeName equals strTestedAttributeValue,
         * then we get the element's url, else we do nothing.
         */
        public ElementUrl( String strTagName, String strAttributeName, String strTestedAttributeName,
            String strTestedAttributeValue )
        {
            _strTagName = strTagName;
            _strAttributeName = strAttributeName;
            _strTestedAttributeName = strTestedAttributeName;
            _strTestedAttributeValue = strTestedAttributeValue;
        }

        /**
         * Returns the attributeName
         * @return the attributeName
         */
        public String getAttributeName(  )
        {
            return _strAttributeName;
        }

        /**
         * Returns the tagName
         * @return the tagName
         */
        public String getTagName(  )
        {
            return _strTagName;
        }

        /**
         * Returns the testedAttributeName
         * @return the testedAttributeName
         */
        public String getTestedAttributeName(  )
        {
            return _strTestedAttributeName;
        }

        /**
         * Returns the testedAttributeValue
         * @return the testedAttributeValue
         */
        public String getTestedAttributeValue(  )
        {
            return _strTestedAttributeValue;
        }
    }
}
