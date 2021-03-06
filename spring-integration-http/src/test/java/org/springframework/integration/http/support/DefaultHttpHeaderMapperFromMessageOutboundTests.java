/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.integration.http.support;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.junit.Ignore;
import org.junit.Test;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.integration.mapping.HeaderMapper;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.util.StopWatch;

/**
 * @author Oleg Zhurakousky
 * @author Mark Fisher
 * @author Gunnar Hillert
 * @author Artem Bilan
 * @author Gary Russell
 * @since 2.0.1
 */
public class DefaultHttpHeaderMapperFromMessageOutboundTests {

	// ACCEPT tests
	@Test(expected = IllegalArgumentException.class)
	public void validateAcceptHeaderWithNoSlash() {
		HeaderMapper<HttpHeaders> mapper = DefaultHttpHeaderMapper.outboundMapper();
		Map<String, Object> messageHeaders = new HashMap<String, Object>();
		messageHeaders.put("Accept", "bar");
		HttpHeaders headers = new HttpHeaders();

		mapper.fromHeaders(new MessageHeaders(messageHeaders), headers);
	}

	@Test
	public void validateAcceptHeaderSingleString() {
		HeaderMapper<HttpHeaders> mapper = DefaultHttpHeaderMapper.outboundMapper();
		Map<String, Object> messageHeaders = new HashMap<String, Object>();
		messageHeaders.put("Accept", "bar/foo");

		HttpHeaders headers = new HttpHeaders();

		mapper.fromHeaders(new MessageHeaders(messageHeaders), headers);
		assertEquals("bar", headers.getAccept().get(0).getType());
		assertEquals("foo", headers.getAccept().get(0).getSubtype());
	}

	@Test
	public void validateAcceptHeaderSingleMediaType() {
		HeaderMapper<HttpHeaders> mapper = DefaultHttpHeaderMapper.outboundMapper();
		Map<String, Object> messageHeaders = new HashMap<String, Object>();
		messageHeaders.put("Accept", new MediaType("bar", "foo"));

		HttpHeaders headers = new HttpHeaders();

		mapper.fromHeaders(new MessageHeaders(messageHeaders), headers);
		assertEquals("bar", headers.getAccept().get(0).getType());
		assertEquals("foo", headers.getAccept().get(0).getSubtype());
	}

	@Test
	public void validateAcceptHeaderMultipleAsDelimitedString() {
		HeaderMapper<HttpHeaders> mapper = DefaultHttpHeaderMapper.outboundMapper();
		Map<String, Object> messageHeaders = new HashMap<String, Object>();
		messageHeaders.put("Accept", "bar/foo, text/xml");
		HttpHeaders headers = new HttpHeaders();

		mapper.fromHeaders(new MessageHeaders(messageHeaders), headers);
		assertEquals(2, headers.getAccept().size());
		assertEquals("bar/foo", headers.getAccept().get(0).toString());
		assertEquals("text/xml", headers.getAccept().get(1).toString());
	}

	@Test
	public void validateAcceptHeaderMultipleAsStringArray() {
		HeaderMapper<HttpHeaders> mapper = DefaultHttpHeaderMapper.outboundMapper();
		Map<String, Object> messageHeaders = new HashMap<String, Object>();
		messageHeaders.put("Accept", new String[] {"bar/foo", "text/xml"});
		HttpHeaders headers = new HttpHeaders();

		mapper.fromHeaders(new MessageHeaders(messageHeaders), headers);
		assertEquals(2, headers.getAccept().size());
		assertEquals("bar/foo", headers.getAccept().get(0).toString());
		assertEquals("text/xml", headers.getAccept().get(1).toString());
	}

	@Test
	public void validateAcceptHeaderMultipleAsStringCollection() {
		HeaderMapper<HttpHeaders> mapper = DefaultHttpHeaderMapper.outboundMapper();
		Map<String, Object> messageHeaders = new HashMap<String, Object>();
		messageHeaders.put("Accept", Arrays.asList("bar/foo", "text/xml"));
		HttpHeaders headers = new HttpHeaders();

		mapper.fromHeaders(new MessageHeaders(messageHeaders), headers);
		assertEquals(2, headers.getAccept().size());
		assertEquals("bar/foo", headers.getAccept().get(0).toString());
		assertEquals("text/xml", headers.getAccept().get(1).toString());
	}

	@Test
	public void validateAcceptHeaderMultipleAsStringCollectionCaseInsensitive() {
		HeaderMapper<HttpHeaders> mapper = DefaultHttpHeaderMapper.outboundMapper();
		Map<String, Object> messageHeaders = new HashMap<String, Object>();
		messageHeaders.put("acCePt", Arrays.asList("bar/foo", "text/xml"));
		HttpHeaders headers = new HttpHeaders();

		mapper.fromHeaders(new MessageHeaders(messageHeaders), headers);
		assertEquals(2, headers.getAccept().size());
		assertEquals("bar/foo", headers.getAccept().get(0).toString());
		assertEquals("text/xml", headers.getAccept().get(1).toString());
	}

	@Test
	public void validateAcceptHeaderMultipleAsMediatypeCollection() {
		HeaderMapper<HttpHeaders> mapper = DefaultHttpHeaderMapper.outboundMapper();
		Map<String, Object> messageHeaders = new HashMap<String, Object>();
		messageHeaders.put("Accept", Arrays.asList(new MediaType("bar", "foo"), new MediaType("text", "xml")));
		HttpHeaders headers = new HttpHeaders();

		mapper.fromHeaders(new MessageHeaders(messageHeaders), headers);
		assertEquals(2, headers.getAccept().size());
		assertEquals("bar/foo", headers.getAccept().get(0).toString());
		assertEquals("text/xml", headers.getAccept().get(1).toString());
	}

	// ACCEPT_CHARSET tests

	@Test(expected = UnsupportedCharsetException.class)
	public void validateAcceptCharsetHeaderWithWrongCharset() {
		HeaderMapper<HttpHeaders> mapper = DefaultHttpHeaderMapper.outboundMapper();
		Map<String, Object> messageHeaders = new HashMap<String, Object>();
		messageHeaders.put("Accept-Charset", "foo");
		HttpHeaders headers = new HttpHeaders();
		mapper.fromHeaders(new MessageHeaders(messageHeaders), headers);
	}

	@Test
	public void validateAcceptCharsetHeaderSingleString() {
		HeaderMapper<HttpHeaders> mapper = DefaultHttpHeaderMapper.outboundMapper();
		Map<String, Object> messageHeaders = new HashMap<String, Object>();
		messageHeaders.put("Accept-Charset", "UTF-8");
		HttpHeaders headers = new HttpHeaders();

		mapper.fromHeaders(new MessageHeaders(messageHeaders), headers);
		assertEquals(1, headers.getAcceptCharset().size());
		assertEquals("UTF-8", headers.getAcceptCharset().get(0).displayName());
	}

	@Test
	public void validateAcceptCharsetHeaderSingleCharset() {
		HeaderMapper<HttpHeaders> mapper = DefaultHttpHeaderMapper.outboundMapper();
		Map<String, Object> messageHeaders = new HashMap<String, Object>();
		messageHeaders.put("Accept-Charset", Charset.forName("UTF-8"));
		HttpHeaders headers = new HttpHeaders();

		mapper.fromHeaders(new MessageHeaders(messageHeaders), headers);
		assertEquals(1, headers.getAcceptCharset().size());
		assertEquals("UTF-8", headers.getAcceptCharset().get(0).displayName());
	}

	@Test
	public void validateAcceptCharsetHeaderMultipleAsDelimitedString() {
		HeaderMapper<HttpHeaders> mapper = DefaultHttpHeaderMapper.outboundMapper();
		Map<String, Object> messageHeaders = new HashMap<String, Object>();
		messageHeaders.put("Accept-Charset", "UTF-8, ISO-8859-1");
		HttpHeaders headers = new HttpHeaders();

		mapper.fromHeaders(new MessageHeaders(messageHeaders), headers);
		assertEquals(2, headers.getAcceptCharset().size());
		// validate contains since the order is not enforced
		assertTrue(headers.getAcceptCharset().contains(Charset.forName("UTF-8")));
		assertTrue(headers.getAcceptCharset().contains(Charset.forName("ISO-8859-1")));
	}

	@Test
	public void validateAcceptCharsetHeaderMultipleAsStringArray() {
		HeaderMapper<HttpHeaders> mapper = DefaultHttpHeaderMapper.outboundMapper();
		Map<String, Object> messageHeaders = new HashMap<String, Object>();
		messageHeaders.put("Accept-Charset", new String[] {"UTF-8", "ISO-8859-1"});
		HttpHeaders headers = new HttpHeaders();

		mapper.fromHeaders(new MessageHeaders(messageHeaders), headers);
		assertEquals(2, headers.getAcceptCharset().size());
		// validate contains since the order is not enforced
		assertTrue(headers.getAcceptCharset().contains(Charset.forName("UTF-8")));
		assertTrue(headers.getAcceptCharset().contains(Charset.forName("ISO-8859-1")));
	}

	@Test
	public void validateAcceptCharsetHeaderMultipleAsCharsetArray() {
		HeaderMapper<HttpHeaders> mapper = DefaultHttpHeaderMapper.outboundMapper();
		Map<String, Object> messageHeaders = new HashMap<String, Object>();
		messageHeaders.put("Accept-Charset", new Charset[] {Charset.forName("UTF-8"), Charset.forName("ISO-8859-1")});
		HttpHeaders headers = new HttpHeaders();

		mapper.fromHeaders(new MessageHeaders(messageHeaders), headers);
		assertEquals(2, headers.getAcceptCharset().size());
		// validate contains since the order is not enforced
		assertTrue(headers.getAcceptCharset().contains(Charset.forName("UTF-8")));
		assertTrue(headers.getAcceptCharset().contains(Charset.forName("ISO-8859-1")));
	}

	@Test
	public void validateAcceptCharsetHeaderMultipleAsCollectionOfStrings() {
		HeaderMapper<HttpHeaders> mapper = DefaultHttpHeaderMapper.outboundMapper();
		Map<String, Object> messageHeaders = new HashMap<String, Object>();
		messageHeaders.put("Accept-Charset", Arrays.asList("UTF-8", "ISO-8859-1"));
		HttpHeaders headers = new HttpHeaders();

		mapper.fromHeaders(new MessageHeaders(messageHeaders), headers);
		assertEquals(2, headers.getAcceptCharset().size());
		// validate contains since the order is not enforced
		assertTrue(headers.getAcceptCharset().contains(Charset.forName("UTF-8")));
		assertTrue(headers.getAcceptCharset().contains(Charset.forName("ISO-8859-1")));
	}

	@Test
	public void validateAcceptCharsetHeaderMultipleAsCollectionOfCharsets() {
		HeaderMapper<HttpHeaders> mapper = DefaultHttpHeaderMapper.outboundMapper();
		Map<String, Object> messageHeaders = new HashMap<String, Object>();
		messageHeaders.put("Accept-Charset", Arrays.asList(Charset.forName("UTF-8"), Charset.forName("ISO-8859-1")));
		HttpHeaders headers = new HttpHeaders();

		mapper.fromHeaders(new MessageHeaders(messageHeaders), headers);
		assertEquals(2, headers.getAcceptCharset().size());
		// validate contains since the order is not enforced
		assertTrue(headers.getAcceptCharset().contains(Charset.forName("UTF-8")));
		assertTrue(headers.getAcceptCharset().contains(Charset.forName("ISO-8859-1")));
	}

	// Cache-Control tests

	@Test
	public void validateCacheControl() {
		HeaderMapper<HttpHeaders> mapper = DefaultHttpHeaderMapper.outboundMapper();
		Map<String, Object> messageHeaders = new HashMap<String, Object>();
		messageHeaders.put("Cache-Control", "foo");
		HttpHeaders headers = new HttpHeaders();

		mapper.fromHeaders(new MessageHeaders(messageHeaders), headers);
		assertEquals("foo", headers.getCacheControl());
	}

	// Content-Length tests

	@Test
	public void validateContentLengthAsString() {
		HeaderMapper<HttpHeaders> mapper = DefaultHttpHeaderMapper.outboundMapper();
		Map<String, Object> messageHeaders = new HashMap<String, Object>();
		messageHeaders.put("Content-Length", "1");
		HttpHeaders headers = new HttpHeaders();

		mapper.fromHeaders(new MessageHeaders(messageHeaders), headers);
		assertEquals(1, headers.getContentLength());
	}

	@Test
	public void validateContentLengthAsNumber() {
		HeaderMapper<HttpHeaders> mapper = DefaultHttpHeaderMapper.outboundMapper();
		Map<String, Object> messageHeaders = new HashMap<String, Object>();
		messageHeaders.put("Content-Length", 1);
		HttpHeaders headers = new HttpHeaders();

		mapper.fromHeaders(new MessageHeaders(messageHeaders), headers);
		assertEquals(1, headers.getContentLength());
	}

	@Test(expected = NumberFormatException.class)
	public void validateContentLengthAsNonNumericString() {
		HeaderMapper<HttpHeaders> mapper = DefaultHttpHeaderMapper.outboundMapper();
		Map<String, Object> messageHeaders = new HashMap<String, Object>();
		messageHeaders.put("Content-Length", "foo");
		HttpHeaders headers = new HttpHeaders();

		mapper.fromHeaders(new MessageHeaders(messageHeaders), headers);
	}

	// Content-Type test

	@Test(expected = IllegalArgumentException.class)
	public void validateContentTypeWrongValue() {
		HeaderMapper<HttpHeaders> mapper = DefaultHttpHeaderMapper.outboundMapper();
		Map<String, Object> messageHeaders = new HashMap<String, Object>();
		messageHeaders.put(MessageHeaders.CONTENT_TYPE, "foo");
		HttpHeaders headers = new HttpHeaders();

		mapper.fromHeaders(new MessageHeaders(messageHeaders), headers);
	}

	@Test
	public void validateContentTypeAsString() {
		HeaderMapper<HttpHeaders> mapper = DefaultHttpHeaderMapper.outboundMapper();
		Map<String, Object> messageHeaders = new HashMap<String, Object>();
		messageHeaders.put("Content-Type", "text/html");
		HttpHeaders headers = new HttpHeaders();

		mapper.fromHeaders(new MessageHeaders(messageHeaders), headers);
		assertEquals("text", headers.getContentType().getType());
		assertEquals("html", headers.getContentType().getSubtype());
	}

	@Test
	public void validateContentTypeAsMediaType() {
		HeaderMapper<HttpHeaders> mapper = DefaultHttpHeaderMapper.outboundMapper();
		Map<String, Object> messageHeaders = new HashMap<String, Object>();
		messageHeaders.put(MessageHeaders.CONTENT_TYPE, new MediaType("text", "html"));
		HttpHeaders headers = new HttpHeaders();

		mapper.fromHeaders(new MessageHeaders(messageHeaders), headers);
		assertEquals("text", headers.getContentType().getType());
		assertEquals("html", headers.getContentType().getSubtype());
	}

	// Date test

	@Test
	public void validateDateAsNumber() throws ParseException {
		HeaderMapper<HttpHeaders> mapper = DefaultHttpHeaderMapper.outboundMapper();
		Map<String, Object> messageHeaders = new HashMap<String, Object>();
		messageHeaders.put("Date", 12345678);
		HttpHeaders headers = new HttpHeaders();
		mapper.fromHeaders(new MessageHeaders(messageHeaders), headers);

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);

		assertEquals(simpleDateFormat.parse("Thu, 01 Jan 1970 03:25:45 GMT").getTime(), headers.getDate());
	}

	@Test
	public void validateDateAsString() throws ParseException {
		HeaderMapper<HttpHeaders> mapper = DefaultHttpHeaderMapper.outboundMapper();
		Map<String, Object> messageHeaders = new HashMap<String, Object>();
		messageHeaders.put("Date", "12345678");
		HttpHeaders headers = new HttpHeaders();
		mapper.fromHeaders(new MessageHeaders(messageHeaders), headers);

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);

		assertEquals(simpleDateFormat.parse("Thu, 01 Jan 1970 03:25:45 GMT").getTime(), headers.getDate());
	}

	@Test
	public void validateDateAsDate() throws ParseException {
		HeaderMapper<HttpHeaders> mapper = DefaultHttpHeaderMapper.outboundMapper();
		Map<String, Object> messageHeaders = new HashMap<String, Object>();
		messageHeaders.put("Date", new Date(12345678));
		HttpHeaders headers = new HttpHeaders();
		mapper.fromHeaders(new MessageHeaders(messageHeaders), headers);

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);

		assertEquals(simpleDateFormat.parse("Thu, 01 Jan 1970 03:25:45 GMT").getTime(), headers.getDate());
	}

	// If-Modified-Since tests

	@Test
	public void validateIfModifiedSinceAsNumber() throws ParseException {
		HeaderMapper<HttpHeaders> mapper = DefaultHttpHeaderMapper.outboundMapper();
		Map<String, Object> messageHeaders = new HashMap<String, Object>();
		messageHeaders.put("If-Modified-Since", 12345678);
		HttpHeaders headers = new HttpHeaders();
		mapper.fromHeaders(new MessageHeaders(messageHeaders), headers);

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);

		assertEquals(simpleDateFormat.parse("Thu, 01 Jan 1970 03:25:45 GMT").getTime(), headers.getIfModifiedSince());
	}

	@Test
	public void validateIfModifiedSinceAsString() throws ParseException {
		HeaderMapper<HttpHeaders> mapper = DefaultHttpHeaderMapper.outboundMapper();
		Map<String, Object> messageHeaders = new HashMap<String, Object>();
		messageHeaders.put("If-Modified-Since", "12345678");
		HttpHeaders headers = new HttpHeaders();
		mapper.fromHeaders(new MessageHeaders(messageHeaders), headers);

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);

		assertEquals(simpleDateFormat.parse("Thu, 01 Jan 1970 03:25:45 GMT").getTime(), headers.getIfModifiedSince());
	}

	@Test
	public void validateIfModifiedSinceAsDate() throws ParseException {
		HeaderMapper<HttpHeaders> mapper = DefaultHttpHeaderMapper.outboundMapper();
		Map<String, Object> messageHeaders = new HashMap<String, Object>();
		messageHeaders.put("If-Modified-Since", new Date(12345678));
		HttpHeaders headers = new HttpHeaders();
		mapper.fromHeaders(new MessageHeaders(messageHeaders), headers);

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);

		assertEquals(simpleDateFormat.parse("Thu, 01 Jan 1970 03:25:45 GMT").getTime(), headers.getIfModifiedSince());
	}

	// If-None-Match

	@Test
	public void validateIfNoneMatch() throws ParseException {
		HeaderMapper<HttpHeaders> mapper = DefaultHttpHeaderMapper.outboundMapper();
		Map<String, Object> messageHeaders = new HashMap<String, Object>();
		messageHeaders.put("If-None-Match", "\"1234567\"");
		HttpHeaders headers = new HttpHeaders();
		mapper.fromHeaders(new MessageHeaders(messageHeaders), headers);

		assertEquals(1, headers.getIfNoneMatch().size());
		assertEquals("\"1234567\"", headers.getIfNoneMatch().get(0));
	}

	@Test
	public void validateIfNoneMatchAsDelimitedString() throws ParseException {
		HeaderMapper<HttpHeaders> mapper = DefaultHttpHeaderMapper.outboundMapper();
		Map<String, Object> messageHeaders = new HashMap<String, Object>();
		messageHeaders.put("If-None-Match", "\"123,4567\", \"123\"");
		HttpHeaders headers = new HttpHeaders();
		mapper.fromHeaders(new MessageHeaders(messageHeaders), headers);

		assertEquals(2, headers.getIfNoneMatch().size());
		assertEquals("\"123,4567\"", headers.getIfNoneMatch().get(0));
		assertEquals("\"123\"", headers.getIfNoneMatch().get(1));
	}

	@Test
	public void validateIfNoneMatchAsStringArray() throws ParseException {
		HeaderMapper<HttpHeaders> mapper = DefaultHttpHeaderMapper.outboundMapper();
		Map<String, Object> messageHeaders = new HashMap<String, Object>();
		messageHeaders.put("If-None-Match", new String[] {"\"1234567\"", "\"123\""});
		HttpHeaders headers = new HttpHeaders();
		mapper.fromHeaders(new MessageHeaders(messageHeaders), headers);

		assertEquals(2, headers.getIfNoneMatch().size());
		assertEquals("\"1234567\"", headers.getIfNoneMatch().get(0));
		assertEquals("\"123\"", headers.getIfNoneMatch().get(1));
	}

	@Test
	public void validateIfNoneMatchAsCommaDelimitedString() throws ParseException {
		HeaderMapper<HttpHeaders> mapper = DefaultHttpHeaderMapper.outboundMapper();
		Map<String, Object> messageHeaders = new HashMap<String, Object>();
		messageHeaders.put("If-None-Match", "\"123.4567\", \"123\"");
		HttpHeaders headers = new HttpHeaders();
		mapper.fromHeaders(new MessageHeaders(messageHeaders), headers);

		assertEquals(2, headers.getIfNoneMatch().size());
		assertEquals("\"123.4567\"", headers.getIfNoneMatch().get(0));
		assertEquals("\"123\"", headers.getIfNoneMatch().get(1));
	}

	@Test
	public void validateIfNoneMatchAsStringCollection() throws ParseException {
		HeaderMapper<HttpHeaders> mapper = DefaultHttpHeaderMapper.outboundMapper();
		Map<String, Object> messageHeaders = new HashMap<String, Object>();
		messageHeaders.put("If-None-Match", Arrays.asList("\"1234567\"", "\"123\""));
		HttpHeaders headers = new HttpHeaders();
		mapper.fromHeaders(new MessageHeaders(messageHeaders), headers);

		assertEquals(2, headers.getIfNoneMatch().size());
		assertEquals("\"1234567\"", headers.getIfNoneMatch().get(0));
		assertEquals("\"123\"", headers.getIfNoneMatch().get(1));
	}

	// Pragma tests
	@Test
	public void validatePragma() throws ParseException {
		HeaderMapper<HttpHeaders> mapper = DefaultHttpHeaderMapper.outboundMapper();
		Map<String, Object> messageHeaders = new HashMap<String, Object>();
		messageHeaders.put("Pragma", "foo");
		HttpHeaders headers = new HttpHeaders();
		mapper.fromHeaders(new MessageHeaders(messageHeaders), headers);
		assertEquals("foo", headers.getPragma());
	}

	// Transfer-Encoding tests
	@Test
	public void validateTransferEncoding() throws ParseException {
		HeaderMapper<HttpHeaders> mapper = DefaultHttpHeaderMapper.outboundMapper();
		Map<String, Object> messageHeaders = new HashMap<String, Object>();
		messageHeaders.put("Transfer-Encoding", "chunked");
		HttpHeaders headers = new HttpHeaders();
		mapper.fromHeaders(new MessageHeaders(messageHeaders), headers);
		assertNull(headers.get("Transfer-Encoding"));
	}

	@Test
	public void validateTransferEncodingToHeaders() throws ParseException {
		HeaderMapper<HttpHeaders> mapper = DefaultHttpHeaderMapper.outboundMapper();

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set("Transfer-Encoding", "chunked");

		Map<String, ?> messageHeaders = mapper.toHeaders(httpHeaders);
		assertEquals(1, messageHeaders.size());

	}

	@Test
	@Ignore
	public void perf() throws ParseException {
		HeaderMapper<HttpHeaders> mapper = DefaultHttpHeaderMapper.outboundMapper();

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set("Transfer-Encoding", "chunked");
		httpHeaders.set("X-Transfer-Encoding1", "chunked");
		httpHeaders.set("X-Transfer-Encoding2", "chunked");
		httpHeaders.set("X-Transfer-Encoding3", "chunked");
		httpHeaders.set("X-Transfer-Encoding4", "chunked");
		httpHeaders.set("X-Transfer-Encoding5", "chunked");
		httpHeaders.set("X-Transfer-Encoding6", "chunked");
		httpHeaders.set("X-Transfer-Encoding7", "chunked");

		StopWatch watch = new StopWatch();
		watch.start();
		for (int i = 0; i < 100000; i++) {
			mapper.toHeaders(httpHeaders);
		}
		watch.stop();

	}

	// Custom headers

	@Test
	public void validateCustomHeaderWithNoHeaderNames() throws ParseException {
		DefaultHttpHeaderMapper mapper = new DefaultHttpHeaderMapper();
		Map<String, Object> messageHeaders = new HashMap<String, Object>();
		messageHeaders.put("foo", "foo");
		HttpHeaders headers = new HttpHeaders();
		mapper.fromHeaders(new MessageHeaders(messageHeaders), headers);
		assertNull(headers.get("foo"));
		assertNull(headers.get("X-foo"));
	}

	@Test
	public void validateCustomHeaderWithHeaderNames() throws ParseException {
		DefaultHttpHeaderMapper mapper = new DefaultHttpHeaderMapper();
		mapper.setOutboundHeaderNames(new String[] {"foo"});
		Map<String, Object> messageHeaders = new HashMap<String, Object>();
		messageHeaders.put("foo", "foo");
		HttpHeaders headers = new HttpHeaders();
		mapper.fromHeaders(new MessageHeaders(messageHeaders), headers);
		assertNull(headers.get("X-foo"));
		assertNotNull(headers.get("foo"));
		assertTrue(headers.get("foo").size() == 1);
		assertEquals("foo", headers.get("foo").get(0));
	}

	@Test
	public void validateCustomHeaderWithHeaderNamePatterns() throws ParseException {
		DefaultHttpHeaderMapper mapper = new DefaultHttpHeaderMapper();
		mapper.setOutboundHeaderNames(new String[] {"x*", "*z", "a*f"});
		Map<String, Object> messageHeaders = new HashMap<String, Object>();
		messageHeaders.put("x1", "x1-value");
		messageHeaders.put("1x", "1x-value");
		messageHeaders.put("z1", "z1-value");
		messageHeaders.put("1z", "1z-value");
		messageHeaders.put("abc", "abc-value");
		messageHeaders.put("def", "def-value");
		messageHeaders.put("abcdef", "abcdef-value");
		HttpHeaders headers = new HttpHeaders();
		mapper.fromHeaders(new MessageHeaders(messageHeaders), headers);
		assertEquals(3, headers.size());
		assertNull(headers.get("1x"));
		assertNull(headers.get("z1"));
		assertNull(headers.get("abc"));
		assertNull(headers.get("def"));
		assertEquals(1, headers.get("x1").size());
		assertEquals("x1-value", headers.getFirst("x1"));
		assertEquals(1, headers.get("1z").size());
		assertEquals("1z-value", headers.getFirst("1z"));
		assertEquals(1, headers.get("abcdef").size());
		assertEquals("abcdef-value", headers.getFirst("abcdef"));
	}

	@Test
	public void validateCustomHeaderWithHeaderNamePatternsAndStandardRequestHeaders() throws ParseException {
		DefaultHttpHeaderMapper mapper = new DefaultHttpHeaderMapper();
		mapper.setOutboundHeaderNames(new String[] {"foo*", "HTTP_REQUEST_HEADERS"});
		Map<String, Object> messageHeaders = new HashMap<String, Object>();
		messageHeaders.put("foobar", "abc");
		messageHeaders.put("Content-Type", "text/html");
		messageHeaders.put("Accept", "text/xml");
		HttpHeaders headers = new HttpHeaders();
		mapper.fromHeaders(new MessageHeaders(messageHeaders), headers);
		assertEquals(3, headers.size());
		assertEquals(1, headers.get("foobar").size());
		assertEquals("abc", headers.getFirst("foobar"));
		assertEquals("text/html", headers.getContentType().toString());
		assertEquals(1, headers.getAccept().size());
		assertEquals(MediaType.TEXT_XML, headers.getAccept().get(0));
	}

	@Test
	public void validateCustomHeaderWithHeaderNamePatternsAndStandardResponseHeaders() throws ParseException {
		DefaultHttpHeaderMapper mapper = new DefaultHttpHeaderMapper();
		mapper.setInboundHeaderNames(new String[] {"foo*", "HTTP_RESPONSE_HEADERS"});
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set("foobar", "abc");
		httpHeaders.setContentType(MediaType.TEXT_HTML);
		httpHeaders.setAccept(Collections.singletonList(MediaType.TEXT_HTML));
		Map<String, ?> messageHeaders = mapper.toHeaders(httpHeaders);
		assertEquals(2, messageHeaders.size());
		assertNull(messageHeaders.get("Accept"));
		assertEquals("abc", messageHeaders.get("foobar"));
		assertEquals("text/html", messageHeaders.get(MessageHeaders.CONTENT_TYPE).toString());
	}

	@Test
	public void validateCustomHeaderWithStandardPrefix() throws Exception {
		DefaultHttpHeaderMapper mapper = new DefaultHttpHeaderMapper();
		mapper.setInboundHeaderNames(new String[] {"X-Foo"});
		HttpHeaders headers = new HttpHeaders();
		headers.set("x-foo", "x-foo-value");
		Map<String, ?> result = mapper.toHeaders(headers);
		assertEquals(1, result.size());
		assertEquals("x-foo-value", result.get("x-foo"));
	}

	@Test
	public void validateCustomHeaderWithStandardPrefixSameCase() throws Exception {
		DefaultHttpHeaderMapper mapper = new DefaultHttpHeaderMapper();
		mapper.setInboundHeaderNames(new String[] {"X-Foo"});
		HttpHeaders headers = new HttpHeaders();
		headers.set("X-Foo", "x-foo-value");
		Map<String, ?> result = mapper.toHeaders(headers);
		assertEquals(1, result.size());
		assertEquals("x-foo-value", result.get("X-Foo"));
	}

	@Test
	public void validateCustomHeaderCaseInsensitivity() throws ParseException {
		DefaultHttpHeaderMapper mapper = new DefaultHttpHeaderMapper();
		mapper.setOutboundHeaderNames(new String[] {"*", "HTTP_REQUEST_HEADERS"});
		mapper.setUserDefinedHeaderPrefix("X-");
		Map<String, Object> messageHeaders = new HashMap<String, Object>();
		messageHeaders.put("foobar", "abc");
		messageHeaders.put("X-bar", "xbar");
		messageHeaders.put("x-baz", "xbaz");
		messageHeaders.put("Content-Type", "text/html");
		messageHeaders.put("Accept", "text/xml");
		HttpHeaders headers = new HttpHeaders();
		mapper.fromHeaders(new MessageHeaders(messageHeaders), headers);
		assertEquals(5, headers.size());
		assertEquals(1, headers.get("X-foobar").size());
		assertEquals(1, headers.get("x-foobar").size());
		assertEquals(1, headers.get("X-bar").size());
		assertEquals(1, headers.get("x-bar").size());
		assertEquals(1, headers.get("X-baz").size());
		assertEquals(1, headers.get("x-baz").size());
	}

	@Test
	public void dontPropagateContentLength() {
		DefaultHttpHeaderMapper mapper = DefaultHttpHeaderMapper.outboundMapper();
		// not suppressed on outbound request, by default
		mapper.setExcludedOutboundStandardRequestHeaderNames("Content-Length");
		Map<String, Object> messageHeaders = new HashMap<String, Object>();
		messageHeaders.put("Content-Length", 4);

		HttpHeaders headers = new HttpHeaders();
		mapper.fromHeaders(new MessageHeaders(messageHeaders), headers);
		assertNull(headers.get("Content-Length"));
	}

	@Test
	public void testInt3063InvalidExpiresHeader() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Expires", "-1");
		Map<String, Object> messageHeaders = DefaultHttpHeaderMapper.outboundMapper().toHeaders(headers);
		assertEquals(0, messageHeaders.size());
	}

	@Test
	public void testInt2995IfModifiedSince() throws Exception {
		Date ifModifiedSince = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy", Locale.US);
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		String value = dateFormat.format(ifModifiedSince);
		Message<?> testMessage = MessageBuilder.withPayload("foo").setHeader("If-Modified-Since", value).build();
		HeaderMapper<HttpHeaders> mapper = DefaultHttpHeaderMapper.outboundMapper();
		HttpHeaders headers = new HttpHeaders();
		mapper.fromHeaders(testMessage.getHeaders(), headers);
		Calendar c = Calendar.getInstance();
		c.setTime(ifModifiedSince);
		c.set(Calendar.MILLISECOND, 0);
		assertEquals(c.getTimeInMillis(), headers.getIfModifiedSince());
	}

	@Test
	public void testContentDispositionHeader() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		String headerValue = "attachment; filename=\"test.txt\"";
		headers.set("Content-Disposition", headerValue);
		Map<String, Object> messageHeaders = DefaultHttpHeaderMapper.outboundMapper().toHeaders(headers);
		assertEquals(1, messageHeaders.size());
		assertEquals(headerValue, messageHeaders.get("Content-Disposition"));
	}

}
