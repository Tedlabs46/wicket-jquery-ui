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
package com.googlecode.wicket.jquery.ui.form.datepicker;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.CallbackParameter;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.form.FormComponent;

import com.googlecode.wicket.jquery.core.JQueryEvent;
import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.core.ajax.IJQueryAjaxAware;
import com.googlecode.wicket.jquery.core.ajax.JQueryAjaxBehavior;
import com.googlecode.wicket.jquery.core.ajax.JQueryAjaxPostBehavior;
import com.googlecode.wicket.jquery.core.utils.JQueryUtils;
import com.googlecode.wicket.jquery.core.utils.RequestCycleUtils;
import com.googlecode.wicket.jquery.ui.JQueryUIBehavior;

/**
 * Provides a jQuery datepicker behavior
 *
 * @author Sebastien Briquet - sebfz1
 */
public abstract class DatePickerBehavior extends JQueryUIBehavior implements IJQueryAjaxAware, IDatePickerListener
{
	private static final long serialVersionUID = 1L;
	public static final String METHOD = "datepicker";

	private JQueryAjaxBehavior onSelectAjaxBehavior = null;

	/**
	 * Constructor
	 * 
	 * @param selector the html selector (ie: "#myId")
	 */
	public DatePickerBehavior(String selector)
	{
		this(selector, new Options());
	}

	/**
	 * Constructor
	 * 
	 * @param selector the html selector (ie: "#myId")
	 * @param options the {@link Options}
	 */
	public DatePickerBehavior(String selector, Options options)
	{
		super(selector, METHOD, options);
	}

	// Methods //

	@Override
	public void bind(Component component)
	{
		super.bind(component);

		if (this.isOnSelectEventEnabled())
		{
			this.onSelectAjaxBehavior = this.newOnSelectAjaxBehavior(this);
			component.add(this.onSelectAjaxBehavior);
		}
	}

	@Override
	public void destroy(IPartialPageRequestHandler handler)
	{
		// FIXME: workaround, will be removed when fixed in jquery-ui
		handler.prependJavaScript(JQueryUtils.trycatch(this.$(Options.asString("destroy"))));

		this.onDestroy(handler);
	}

	// Events //

	@Override
	public void onConfigure(Component component)
	{
		super.onConfigure(component);

		if (this.onSelectAjaxBehavior != null)
		{
			this.setOption("onSelect", this.onSelectAjaxBehavior.getCallbackFunction());
		}
	}

	@Override
	public void onAjax(AjaxRequestTarget target, JQueryEvent event)
	{
		if (event instanceof SelectEvent)
		{
			this.onSelect(target, ((SelectEvent) event).getDateText());
		}
	}

	// Factories //

	/**
	 * Gets a new {@link JQueryAjaxPostBehavior} that will be wired to the 'onSelect' event
	 *
	 * @param source the {@link IJQueryAjaxAware}
	 * @return a new {@link OnSelectAjaxBehavior} by default
	 */
	protected abstract JQueryAjaxPostBehavior newOnSelectAjaxBehavior(IJQueryAjaxAware source);

	// Ajax classes //

	/**
	 * Provides a {@link JQueryAjaxPostBehavior} that aims to be wired to the 'onSelect' event
	 */
	protected static class OnSelectAjaxBehavior extends JQueryAjaxPostBehavior
	{
		private static final long serialVersionUID = 1L;

		public OnSelectAjaxBehavior(final IJQueryAjaxAware source, final FormComponent<?> component)
		{
			super(source, component);
		}

		@Override
		protected CallbackParameter[] getCallbackParameters()
		{
			// function( dateText, inst ) { ... }
			return new CallbackParameter[] { CallbackParameter.explicit("dateText"), CallbackParameter.context("inst") };
		}

		@Override
		protected JQueryEvent newEvent()
		{
			return new SelectEvent();
		}
	}

	// Event objects //

	/**
	 * Provides an event object that will be broadcasted by the {@link OnSelectAjaxBehavior} callback
	 */
	protected static class SelectEvent extends JQueryEvent
	{
		private final String date;

		public SelectEvent()
		{
			this.date = RequestCycleUtils.getPostParameterValue("dateText").toString();
		}

		public String getDateText()
		{
			return this.date;
		}
	}
}
