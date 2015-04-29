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
package com.googlecode.wicket.kendo.ui.interaction.draggable;

import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.CallbackParameter;

import com.googlecode.wicket.jquery.core.JQueryEvent;
import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.core.ajax.IJQueryAjaxAware;
import com.googlecode.wicket.jquery.core.ajax.JQueryAjaxBehavior;
import com.googlecode.wicket.kendo.ui.KendoUIBehavior;

/**
 * Provides a Kendo UI draggable behavior<br/>
 * <br/>
 * <b>Warning:</b> not thread-safe: the instance of this behavior should only be used once
 *
 * @author Sebastien Briquet - sebfz1
 */
public abstract class DraggableBehavior extends KendoUIBehavior implements IJQueryAjaxAware, IDraggableListener
{
	private static final long serialVersionUID = 1L;
	private static final String METHOD = "kendoDraggable";

	private JQueryAjaxBehavior onDragStartBehavior;
	private JQueryAjaxBehavior onDragStopBehavior = null;
	private JQueryAjaxBehavior onDragCancelBehavior = null;
	private Component component = null;

	/**
	 * Constructor
	 * 
	 * @param selector the html selector (ie: "#myId")
	 */
	public DraggableBehavior(String selector)
	{
		this(selector, new Options());
	}

	/**
	 * Constructor
	 * 
	 * @param selector the html selector (ie: "#myId")
	 * @param options the {@link Options}
	 */
	public DraggableBehavior(String selector, Options options)
	{
		super(selector, METHOD, options);
	}

	// Methods //

	@Override
	public void bind(Component component)
	{
		super.bind(component);

		if (this.component != null)
		{
			throw new WicketRuntimeException("Behavior is already bound to another component.");
		}

		this.component = component; // warning, not thread-safe: the instance of this behavior should only be used once
		this.component.add(this.onDragStartBehavior = this.newOnDragStartBehavior());
		this.component.add(this.onDragStopBehavior = this.newOnDragStopBehavior());

		// this event is not enabled by default to prevent unnecessary server round-trips.
		if (this.isCancelEventEnabled())
		{
			this.component.add(this.onDragCancelBehavior = this.newOnDragCancelBehavior());
		}
	}

	// Properties //

	/**
	 * Hides the original element while dragging
	 * 
	 * @return false by default
	 */
	protected boolean hideOnDrag()
	{
		return false;
	}

	// Events //

	@Override
	public void onConfigure(Component component)
	{
		super.onConfigure(component);

		// options //
		this.setOption("hint", "function(element) { return element.clone(); }");

		// behaviors //

		this.setOption("dragstart", this.onDragStartBehavior.getCallbackFunction());

		if (this.onDragStopBehavior != null)
		{
			this.setOption("dragend", this.onDragStopBehavior.getCallbackFunction());
		}

		if (this.onDragCancelBehavior != null)
		{
			this.setOption("dragcancel", this.onDragCancelBehavior.getCallbackFunction());
		}
	}

	@Override
	public void onAjax(AjaxRequestTarget target, JQueryEvent event)
	{
		if (event instanceof DraggableEvent)
		{
			DraggableEvent ev = (DraggableEvent) event;

			if (ev instanceof DragStartEvent)
			{
				this.onDragStart(target, 0, 0); // TODO ev.getTop(), ev.getLeft()

				// register to all DroppableBehavior(s) //
				// target.getPage().visitChildren(this.newDroppableBehaviorVisitor());
			}

			else if (ev instanceof DragStopEvent)
			{
				this.onDragStop(target, 0, 0); // TODO ev.getTop(), ev.getLeft()
			}

			else if (ev instanceof DragCancelEvent)
			{
				this.onDragCancel(target, 0, 0); // TODO ev.getTop(), ev.getLeft()
			}
		}
	}

	// Factories //

	// /**
	// * Gets a new {@link DroppableBehavior} visitor.<br/>
	// * This {@link IVisitor} is responsible for register the {@link DroppableBehavior} to this {@link DraggableBehavior}
	// *
	// * @return a {@link IVisitor}
	// */
	// private IVisitor<Component, ?> newDroppableBehaviorVisitor()
	// {
	// return new IVisitor<Component, Void>() {
	//
	// @Override
	// public void component(Component component, IVisit<Void> visit)
	// {
	// for (DroppableBehavior behavior : component.getBehaviors(DroppableBehavior.class))
	// {
	// behavior.setDraggable(DraggableBehavior.this.component);
	// }
	// }
	// };
	// }

	/**
	 * Gets a new {@link JQueryAjaxBehavior} that will be called on 'dragstart' javascript event
	 * 
	 * @return the {@link JQueryAjaxBehavior}
	 */
	protected JQueryAjaxBehavior newOnDragStartBehavior()
	{
		return new JQueryAjaxBehavior(this) {

			private static final long serialVersionUID = 1L;

			@Override
			protected CallbackParameter[] getCallbackParameters()
			{
				return new CallbackParameter[] { CallbackParameter.context("e") // lf
				// CallbackParameter.context("ui"), // lf
				// CallbackParameter.resolved("top", "ui.position.top"), // lf
				// CallbackParameter.resolved("left", "ui.position.left"), //lf
				// CallbackParameter.resolved("offsetTop", "ui.offset.top | 0"), // cast to int, no rounding
				// CallbackParameter.resolved("offsetLeft", "ui.offset.left | 0") // cast to int, no rounding

				// left: e.draggable.hintOffset.left,
				// top: e.draggable.hintOffset.top
				};
			}

			@Override
			public CharSequence getCallbackFunctionBody(CallbackParameter... parameters)
			{
				if (DraggableBehavior.this.hideOnDrag())
				{
					String statement = "this.element.hide();";
					return statement + super.getCallbackFunctionBody(parameters);
				}

				return super.getCallbackFunctionBody(parameters);
			}

			@Override
			protected JQueryEvent newEvent()
			{
				return new DragStartEvent();
			}
		};
	}

	/**
	 * Gets a new {@link JQueryAjaxBehavior} that will be called on 'dragend' javascript event
	 * 
	 * @return the {@link JQueryAjaxBehavior}
	 */
	protected JQueryAjaxBehavior newOnDragStopBehavior()
	{
		return new JQueryAjaxBehavior(this) {

			private static final long serialVersionUID = 1L;

			@Override
			protected CallbackParameter[] getCallbackParameters()
			{
				return new CallbackParameter[] { CallbackParameter.context("e"), // lf
				// CallbackParameter.context("ui"), // lf
				// CallbackParameter.resolved("top", "ui.position.top"), // lf
				// CallbackParameter.resolved("left", "ui.position.left"), //lf
				// CallbackParameter.resolved("offsetTop", "ui.offset.top | 0"), // cast to int, no rounding
				// CallbackParameter.resolved("offsetLeft", "ui.offset.left | 0") // cast to int, no rounding
				};
			}

			@Override
			public CharSequence getCallbackFunctionBody(CallbackParameter... parameters)
			{
				if (DraggableBehavior.this.hideOnDrag())
				{
					String statement = "this.element.show();";
					return statement + super.getCallbackFunctionBody(parameters);
				}

				return super.getCallbackFunctionBody(parameters);
			}

			@Override
			protected JQueryEvent newEvent()
			{
				return new DragStopEvent();
			}
		};
	}

	/**
	 * Gets a new {@link JQueryAjaxBehavior} that will be called on 'dragend' javascript event
	 * 
	 * @return the {@link JQueryAjaxBehavior}
	 */
	protected JQueryAjaxBehavior newOnDragCancelBehavior()
	{
		return new JQueryAjaxBehavior(this) {

			private static final long serialVersionUID = 1L;

			@Override
			protected CallbackParameter[] getCallbackParameters()
			{
				return new CallbackParameter[] { CallbackParameter.context("e"), // lf
				// CallbackParameter.context("ui"), // lf
				// CallbackParameter.resolved("top", "ui.position.top"), // lf
				// CallbackParameter.resolved("left", "ui.position.left"), //lf
				// CallbackParameter.resolved("offsetTop", "ui.offset.top | 0"), // cast to int, no rounding
				// CallbackParameter.resolved("offsetLeft", "ui.offset.left | 0") // cast to int, no rounding
				};
			}

			@Override
			protected JQueryEvent newEvent()
			{
				return new DragCancelEvent();
			}
		};
	}

	// Events classes //

	/**
	 * Provides a base class for draggable event object
	 */
	protected static class DraggableEvent extends JQueryEvent
	{
		// private final int top;
		// private final int left;
		// private final int offsetTop;
		// private final int offsetLeft;

		/**
		 * Constructor.
		 */
		public DraggableEvent()
		{
			// this.top = RequestCycleUtils.getQueryParameterValue("top").toInt(-1);
			// this.left = RequestCycleUtils.getQueryParameterValue("left").toInt(-1);
			// this.offsetTop = RequestCycleUtils.getQueryParameterValue("offsetTop").toInt(-1);
			// this.offsetLeft = RequestCycleUtils.getQueryParameterValue("offsetLeft").toInt(-1);
		}

		// /**
		// * Gets the position's top value
		// *
		// * @return the position's top value
		// */
		// public int getTop()
		// {
		// return this.top;
		// }
		//
		// /**
		// * Gets the position's left value
		// *
		// * @return the position's left value
		// */
		// public int getLeft()
		// {
		// return this.left;
		// }
		//
		// /**
		// * Gets the offset's top value
		// *
		// * @return the offset's top value
		// */
		// public int getOffsetTop()
		// {
		// return this.offsetTop;
		// }
		//
		// /**
		// * Gets the offset's left value
		// *
		// * @return the offset's left value
		// */
		// public int getOffsetLeft()
		// {
		// return this.offsetLeft;
		// }
	}

	/**
	 * Provides an event object that will be broadcasted by the {@link JQueryAjaxBehavior} 'dragstart' callback
	 */
	protected static class DragStartEvent extends DraggableEvent
	{
	}

	/**
	 * Provides an event object that will be broadcasted by the {@link JQueryAjaxBehavior} 'dragend' callback
	 */
	protected static class DragStopEvent extends DraggableEvent
	{
	}

	/**
	 * Provides an event object that will be broadcasted by the {@link JQueryAjaxBehavior} 'dragcancel' callback
	 */
	protected static class DragCancelEvent extends DraggableEvent
	{
	}
}