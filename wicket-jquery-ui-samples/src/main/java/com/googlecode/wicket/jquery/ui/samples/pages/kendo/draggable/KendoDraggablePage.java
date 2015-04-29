package com.googlecode.wicket.jquery.ui.samples.pages.kendo.draggable;

import org.apache.wicket.ajax.AjaxRequestTarget;

import com.googlecode.wicket.kendo.ui.interaction.draggable.DraggableBehavior;

public class KendoDraggablePage extends AbstractDraggablePage
{
	private static final long serialVersionUID = 1L;

	public KendoDraggablePage()
	{
		// Draggable //
		this.add(new DraggableBehavior("#draggable") {

			private static final long serialVersionUID = 1L;

			@Override
			protected boolean hideOnDrag()
			{
				// hides the original element while dragging
				return true;
			}

			@Override
			public boolean isCancelEventEnabled()
			{
				// not enabled by default to prevent unnecessary server round-trips.
				return false;
			}

			@Override
			public void onDragStart(AjaxRequestTarget target, int top, int left)
			{
				// noop
			}

			@Override
			public void onDragStop(AjaxRequestTarget target, int top, int left)
			{
				// noop
			}

			@Override
			public void onDragCancel(AjaxRequestTarget target, int top, int left)
			{
				// noop
			}
		});
	}
}
