package com.chartableminimap;

import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

import javax.inject.Inject;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;

/**
 * Overlay that renders a mask on top of the minimap in fixed mode.
 * The mask appears above the map itself and icons, but below the frame and orbs.
 */
public class MinimapMaskOverlay extends Overlay
{
	private final Client client;

	@Inject
	public MinimapMaskOverlay(Client client)
	{
		this.client = client;
		
		// Position the overlay on the minimap
		setPosition(OverlayPosition.DYNAMIC);
		
		// Set layer to be above the scene (map content) but allow frame/orbs to render on top
		setLayer(OverlayLayer.ABOVE_SCENE);
		
		// Set priority to render at an appropriate level
		// Using HIGHEST to ensure it's above minimap content but the frame/orbs are part of the widget layer
		setPriority(OverlayPriority.HIGHEST);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		// Only render in fixed mode
		// In RuneLite, widget 548 is the fixed mode container
		// If it's not visible, we're not in fixed mode
		Widget fixedViewport = client.getWidget(WidgetInfo.FIXED_VIEWPORT);
		if (fixedViewport == null || fixedViewport.isHidden())
		{
			return null;
		}

		// Get the minimap widget to determine position and size
		Widget minimap = client.getWidget(WidgetInfo.FIXED_VIEWPORT_MINIMAP);
		if (minimap == null)
		{
			return null;
		}

		// Get minimap bounds
		int minimapX = minimap.getCanvasLocation().getX();
		int minimapY = minimap.getCanvasLocation().getY();
		int minimapWidth = minimap.getWidth();
		int minimapHeight = minimap.getHeight();

		// Draw a semi-transparent mask over the minimap
		// Using a dark gray with 80% opacity for visibility
		graphics.setColor(new Color(50, 50, 50, 204));
		graphics.fillRect(minimapX, minimapY, minimapWidth, minimapHeight);

		return new Dimension(minimapWidth, minimapHeight);
	}
}
