package com.chartableminimap;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ImageUtil;

import java.awt.image.BufferedImage;


@PluginDescriptor(
	name = "Chartable Minimap",
	description = "A plugin that blocks your minimap so you can fill in your own map as you explore Gielinor.",
		tags = {"chart", "charting", "chartable", "minimap", "FoW", "Fog of War", "Explore", "sextant", "spyglass"},
		enabledByDefault = true
)
@Slf4j

public class ChartableMinimapPlugin extends Plugin {

	// Initialize Image buffers and config keys

	static final String CONFIG_KEY_WALKING_RADIUS = "walkingRadius";

	protected ChartableMinimapPanel panel;

	@Inject
	private Client client;

	@Inject
	private ChartableMinimapConfig config;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private MinimapMaskOverlay minimapMaskOverlay;

	private NavigationButton navigationButton;

	@Override
	protected void startUp() throws Exception
	{
		log.debug("CM is in Startup");

		log.debug("CM is about to instantiate a new panel and rebuild");

		panel = new ChartableMinimapPanel(this);
		panel.rebuild();

		log.debug("CM is about to buffer the icon image");

		final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "/chartable_minimap_icon.png");

		log.debug("CM is about to build the Navigation button");

		navigationButton = NavigationButton.builder()
				.tooltip("Chartable Minimap")
				.icon(icon)
				.priority(2)
				.panel(panel)
				.build();

		log.debug("CM Navigation Button Built");

		clientToolbar.addNavigation(navigationButton);

		log.debug("CM Navigation Button added to toolbar");

		// Register the minimap mask overlay
		overlayManager.add(minimapMaskOverlay);

		log.debug("CM Minimap Mask Overlay added");

		System.out.println("Chartable Minimap has started!");
		log.debug("Chartable Minimap started!");
	}

	@Override
	protected void shutDown() throws Exception
	{
		// Unregister the minimap mask overlay
		overlayManager.remove(minimapMaskOverlay);
		
		clientToolbar.removeNavigation(navigationButton);

		System.out.println("Chartable Minimap has stopped :/");
		log.debug("Chartable Minimap stopped :/");
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "ChartableMinimap says " + config.walkingradius(), null);
		}
	}

	@Provides
    ChartableMinimapConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ChartableMinimapConfig.class);
	}
}
