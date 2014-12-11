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

import org.apache.wicket.markup.html.form.AbstractTextComponent.ITextFormatProvider;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.IConverter;

import com.googlecode.wicket.jquery.core.IJQueryWidget;
import com.googlecode.wicket.jquery.core.JQueryBehavior;
import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.kendo.ui.KendoUIBehavior;
import com.googlecode.wicket.kendo.ui.utils.KendoDateTimeUtils;

public abstract class LocalTextField<T> extends TextField<T> implements ITextFormatProvider, IJQueryWidget
{
	private static final long serialVersionUID = 1L;

	/**
	 * The date pattern of the text field
	 */
	private final String pattern;

	/**
	 * The converter for the TextField
	 */
	private IConverter<T> converter;

	final Options options;

	/**
	 * Main constructor
	 *
	 * @param id the markup id
	 * @param model the {@link IModel}
	 * @param pattern a <code>SimpleDateFormat</code> pattern
	 * @param options {@link Options}
	 */
	public LocalTextField(String id, IModel<T> model, String pattern, Options options, Class<T> clazz, IConverter<T> converter)
	{
		super(id, model, clazz);
		this.pattern = pattern;
		this.converter = converter;

		this.options = options;
	}

	/**
	 * Returns the date pattern.
	 * 
	 * Marked as final. It is - probably - not consistent to have a pattern different from the display
	 * 
	 * @see org.apache.wicket.markup.html.form.AbstractTextComponent.ITextFormatProvider#getTextFormat()
	 */
	@Override
	public final String getTextFormat()
	{
		return pattern;
	}

	/**
	 * Gets a string representation given the time pattern in use.
	 *
	 * @return the model object as string
	 */
	public String getModelObjectAsString()
	{
		T date = this.getModelObject();

		if (date != null)
		{
			return converter.convertToString(date, getLocale());
		}

		return "";
	}

	/**
	 * Returns the default converter if created without pattern; otherwise it returns a
	 * pattern-specific converter.
	 * 
	 * @param type
	 *            The type for which the convertor should work
	 * 
	 * @return A pattern-specific converter
	 * 
	 * @see org.apache.wicket.markup.html.form.TextField
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <C> IConverter<C> getConverter(final Class<C> type)
	{
		if (getType().isAssignableFrom(type))
		{
			return (IConverter<C>)converter;
		}
		else
		{
			return super.getConverter(type);
		}
	}

	protected abstract String getMethod();
	
	// Events //
	@Override
	protected void onInitialize()
	{
		super.onInitialize();

		this.add(JQueryWidget.newWidgetBehavior(this)); // cannot be in ctor as the markupId may be set manually afterward
	}

	@Override
	public void onConfigure(JQueryBehavior behavior)
	{
		if (behavior.getOption("format") == null)
		{
			behavior.setOption("format", Options.asString(KendoDateTimeUtils.toPattern(this.getTextFormat())));
		}
	}

	@Override
	public void onBeforeRender(JQueryBehavior behavior)
	{
		// noop
	}

	// IJQueryWidget //
	@Override
	public JQueryBehavior newWidgetBehavior(String selector)
	{
		return new KendoUIBehavior(selector, getMethod(), this.options);
	}
}
