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
package com.googlecode.wicket.kendo.ui.form.localdatetime;

import java.util.Locale;

import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.util.string.Strings;
import org.threeten.bp.LocalTime;
import org.threeten.bp.format.DateTimeFormatter;

import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.core.utils.LocaleUtils;

/**
 * Provides a Kendo UI time-picker<br/>
 * It should be created on a HTML &lt;input type="text" /&gt; element
 *
 * @author Sebastien Briquet - sebfz1
 */
public class LocalTimePicker extends LocalTextField<LocalTime>
{
	private static final long serialVersionUID = 1L;

	protected static final String METHOD = "kendoTimePicker";
	protected static final String DEFAULT_PATTERN = "h:mm a"; // default java time pattern

	/**
	 * Constructor
	 * @param id the markup id
	 */
	public LocalTimePicker(String id)
	{
		this(id, DEFAULT_PATTERN, new Options());
	}

	/**
	 * Constructor
	 * @param id the markup id
	 * @param options {@link Options}
	 */
	public LocalTimePicker(String id, Options options)
	{
		this(id, DEFAULT_PATTERN, options);
	}

	/**
	 * Constructor
	 * @param id the markup id
	 * @param pattern a <code>SimpleDateFormat</code> pattern
	 */
	public LocalTimePicker(String id, String pattern)
	{
		this(id, pattern, new Options());
	}

	/**
	 * Main constructor
	 * @param id the markup id
	 * @param pattern a <code>SimpleDateFormat</code> pattern
	 * @param options {@link Options}
	 */
	public LocalTimePicker(String id, String pattern, Options options)
	{
		this(id, null, pattern, options);
	}

	/**
	 * Constructor, which use {@link Locale} and Kengo UI Globalization
	 * @param id the markup id
	 * @param locale the {@link Locale}
	 */
	public LocalTimePicker(String id, Locale locale)
	{
		this(id, locale, new Options());
	}

	/**
	 * Constructor, which use {@link Locale} and Kengo UI Globalization
	 * @param id the markup id
	 * @param locale the {@link Locale}
	 * @param options the {@link Options}
	 */
	public LocalTimePicker(String id, Locale locale, Options options)
	{
		this(id, LocaleUtils.getLocaleTimePattern(locale, DEFAULT_PATTERN), options.set("culture", Options.asString(LocaleUtils.getLangageCode(locale))));
	}


	/**
	 * Constructor
	 * @param id the markup id
	 * @param model the {@link IModel}
	 */
	public LocalTimePicker(String id, IModel<LocalTime> model)
	{
		this(id, model, DEFAULT_PATTERN, new Options());
	}

	/**
	 * Constructor
	 * @param id the markup id
	 * @param model the {@link IModel}
	 * @param options {@link Options}
	 */
	public LocalTimePicker(String id, IModel<LocalTime> model, Options options)
	{
		this(id, model, DEFAULT_PATTERN, options);
	}

	/**
	 * Constructor
	 * @param id the markup id
	 * @param model the {@link IModel}
	 * @param pattern a <code>SimpleDateFormat</code> pattern
	 */
	public LocalTimePicker(String id, IModel<LocalTime> model, String pattern)
	{
		this(id, model, pattern, new Options());
	}

	/**
	 * Main constructor
	 * @param id the markup id
	 * @param model the {@link IModel}
	 * @param pattern a <code>SimpleDateFormat</code> pattern.
	 * @param options {@link Options}
	 */
	public LocalTimePicker(String id, IModel<LocalTime> model, final String pattern, Options options)
	{
		super(id, model, pattern, options, LocalTime.class, new IConverter<LocalTime>() {
			private static final long serialVersionUID = 1L;

			@Override
			public LocalTime convertToObject(String value, Locale locale) throws ConversionException {
				return Strings.isEmpty(value) ? null : LocalTime.parse(value, DateTimeFormatter.ofPattern(pattern));
			}

			@Override
			public String convertToString(LocalTime date, Locale locale) {
				return date == null ? null : date.format(DateTimeFormatter.ofPattern(pattern));
			}
		});
	}

	/**
	 * Constructor, which use {@link Locale} and Kengo UI Globalization
	 * @param id the markup id
	 * @param model the {@link IModel}
	 * @param locale the {@link Locale}
	 */
	public LocalTimePicker(String id, IModel<LocalTime> model, Locale locale)
	{
		this(id, model, locale, new Options());
	}

	/**
	 * Constructor, which use {@link Locale} and Kengo UI Globalization
	 * @param id the markup id
	 * @param model the {@link IModel}
	 * @param locale the {@link Locale}
	 * @param options the {@link Options}
	 */
	public LocalTimePicker(String id, IModel<LocalTime> model, Locale locale, Options options)
	{
		this(id, model, LocaleUtils.getLocaleTimePattern(locale, DEFAULT_PATTERN), options.set("culture", Options.asString(LocaleUtils.getLangageCode(locale))));
	}
	
	@Override
	protected String getMethod() {
		return METHOD;
	}
}
