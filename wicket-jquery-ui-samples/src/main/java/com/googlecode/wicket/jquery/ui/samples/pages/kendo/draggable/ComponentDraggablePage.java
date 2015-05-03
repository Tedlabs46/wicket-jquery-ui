package com.googlecode.wicket.jquery.ui.samples.pages.kendo.draggable;

import org.apache.wicket.ajax.AjaxRequestTarget;

import com.googlecode.wicket.jquery.core.utils.RequestCycleUtils;
import com.googlecode.wicket.kendo.ui.interaction.draggable.Draggable;
import com.googlecode.wicket.kendo.ui.panel.KendoFeedbackPanel;

public class ComponentDraggablePage extends AbstractDraggablePage
{
	private static final long serialVersionUID = 1L;
	
	private KendoFeedbackPanel feedback;

	public ComponentDraggablePage()
	{
		// FeedbackPanel //
		this.feedback = new KendoFeedbackPanel("feedback");
		this.add(this.feedback.setOutputMarkupId(true));

		// Draggable //
		Draggable<?> draggable = this.newDraggable("draggable");
		draggable.setContainer("#wrapper-panel-frame");
		this.add(draggable);
	}

	private Draggable<?> newDraggable(String id)
	{
		return new Draggable<Void>(id) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCancelEventEnabled()
			{
				return true;
			}

			@Override
			public void onDragStart(AjaxRequestTarget target, int top, int left)
			{
				this.info(String.format("Drag started - position: {%s, %s}", top, left));

				target.add(feedback);
			}

			@Override
			public void onDragStop(AjaxRequestTarget target, int top, int left)
			{
				double offsetTop = RequestCycleUtils.getQueryParameterValue("offsetTop").toDouble(-1);
				double offsetLeft = RequestCycleUtils.getQueryParameterValue("offsetLeft").toDouble(-1);

				this.info(String.format("Drag stoped - position: {%d, %d}, offset: {%.1f, %.1f}", top, left, offsetTop, offsetLeft));

				target.add(feedback);
			}
		};
	}
}
