/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.test.mock;

import org.apache.commons.io.IOUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class MockResponseProvider {

  Integer errorCode = null;
  String errorMsg = null;
  Integer statusCode = null;
  String redirectUrl = null;
  Map<String,String> headers = null;
  Set<Cookie> cookies = null;
  byte[] entity = null;
  String contentType = null;
  String characterEncoding = null;
  Integer contentLength = null;
  Locale locale = null;

  public MockResponseProvider status( int statusCode ) {
    this.statusCode = statusCode;
    return this;
  }

  public MockResponseProvider error( int code, String message ) {
    errorCode = code;
    errorMsg = message;
    return this;
  }

  public MockResponseProvider redirect( String location ) {
    redirectUrl = location;
    return this;
  }

  public MockResponseProvider header( String name, String value ) {
    if( headers == null ) {
      headers = new HashMap<String, String>();
    }
    headers.put( name, value );
    return this;
  }

  public MockResponseProvider cookie( Cookie cookie ) {
    if( cookies == null ) {
      cookies = new HashSet<Cookie>();
    }
    cookies.add( cookie );
    return this;
  }

  public MockResponseProvider content( byte[] entity ) {
    this.entity = entity;
    return this;
  }

  public MockResponseProvider content( String string, Charset charset ) {
    this.entity = string.getBytes( charset );
    return this;
  }

  public MockResponseProvider content( URL url ) throws IOException {
    content( url.openStream() );
    return this;
  }

  public MockResponseProvider content( InputStream stream ) throws IOException {
    content( IOUtils.toByteArray( stream ) );
    return this;
  }

  public MockResponseProvider contentType( String contentType ) {
    this.contentType = contentType;
    return this;
  }

  public MockResponseProvider contentLength( int contentLength ) {
    this.contentLength = contentLength;
    return this;
  }

  public MockResponseProvider characterEncoding( String charset ) {
    this.characterEncoding = charset;
    return this;
  }

  public void apply( HttpServletResponse response ) throws IOException {
    if( statusCode != null ) {
      response.setStatus( statusCode );
    }
    if( errorCode != null ) {
      if( errorMsg != null ) {
        response.sendError( errorCode, errorMsg );
      } else {
        response.sendError( errorCode );
      }
    }
    if( redirectUrl != null ) {
      response.sendRedirect( redirectUrl );
    }
    if( headers != null ) {
      for( String name: headers.keySet() ) {
        response.addHeader( name, headers.get( name ) );
      }
    }
    if( cookies != null ) {
      for( Cookie cookie: cookies ) {
        response.addCookie( cookie );
      }
    }
    if( locale != null ) {
      response.setLocale( locale );
    }
    if( contentType != null ) {
      response.setContentType( contentType );
    }
    if( characterEncoding != null ) {
      response.setCharacterEncoding( characterEncoding );
    }
    if( contentLength != null ) {
      response.setContentLength( contentLength );
    }
    if( entity != null ) {
      response.getOutputStream().write( entity );
      response.getOutputStream().close();
    }
  }

}
