/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package kokabiel.samurai.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.mojang.logging.LogUtils;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;

import kokabiel.samurai.Samurai;
import kokabiel.samurai.event.events.KeyDownEvent;
import kokabiel.samurai.event.events.Render2DEvent;
import kokabiel.samurai.event.events.TickEvent;
import kokabiel.samurai.event.listeners.KeyDownListener;
import kokabiel.samurai.event.listeners.Render2DListener;
import kokabiel.samurai.event.listeners.TickListener;
import kokabiel.samurai.gui.GridDefinition.RelativeUnit;
import kokabiel.samurai.gui.colors.Color;
import kokabiel.samurai.gui.colors.RainbowColor;
import kokabiel.samurai.gui.colors.RandomColor;
import kokabiel.samurai.gui.components.GridComponent;
import kokabiel.samurai.gui.components.ImageComponent;
import kokabiel.samurai.gui.components.ModuleComponent;
import kokabiel.samurai.gui.components.SeparatorComponent;
import kokabiel.samurai.gui.components.StackPanelComponent;
import kokabiel.samurai.gui.components.StringComponent;
import kokabiel.samurai.gui.navigation.HudWindow;
import kokabiel.samurai.gui.navigation.NavigationBar;
import kokabiel.samurai.gui.navigation.Page;
import kokabiel.samurai.gui.navigation.Window;
import kokabiel.samurai.gui.navigation.huds.ArmorHud;
import kokabiel.samurai.gui.navigation.huds.CoordsHud;
import kokabiel.samurai.gui.navigation.huds.DayHud;
import kokabiel.samurai.gui.navigation.huds.FPSHud;
import kokabiel.samurai.gui.navigation.huds.ModuleArrayListHud;
import kokabiel.samurai.gui.navigation.huds.ModuleSelectorHud;
import kokabiel.samurai.gui.navigation.huds.NetherCoordsHud;
import kokabiel.samurai.gui.navigation.huds.PingHud;
import kokabiel.samurai.gui.navigation.huds.RadarHud;
import kokabiel.samurai.gui.navigation.huds.SpeedHud;
import kokabiel.samurai.gui.navigation.huds.TimeHud;
import kokabiel.samurai.gui.navigation.huds.WatermarkHud;
import kokabiel.samurai.gui.navigation.windows.AntiCheatWindow;
import kokabiel.samurai.gui.navigation.windows.AuthCrackerWindow;
import kokabiel.samurai.gui.navigation.windows.GoToWindow;
import kokabiel.samurai.gui.navigation.windows.HudOptionsWindow;
import kokabiel.samurai.gui.navigation.windows.MacroWindow;
import kokabiel.samurai.gui.navigation.windows.SettingsWindow;
import kokabiel.samurai.gui.navigation.windows.ToggleHudsTab;
import kokabiel.samurai.managers.SettingManager;
import kokabiel.samurai.module.Category;
import kokabiel.samurai.module.Module;
import kokabiel.samurai.settings.types.BooleanSetting;
import kokabiel.samurai.settings.types.ColorSetting;
import kokabiel.samurai.settings.types.FloatSetting;
import kokabiel.samurai.settings.types.KeybindSetting;
import kokabiel.samurai.utils.input.CursorStyle;
import kokabiel.samurai.utils.input.Input;
import kokabiel.samurai.utils.render.Render2D;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;

public class GuiManager implements KeyDownListener, TickListener, Render2DListener {
	private static final MinecraftClient MC = MinecraftClient.getInstance();
	private static CursorStyle currentCursor = CursorStyle.Default;
	private static String tooltip = null;

	public KeybindSetting clickGuiButton = KeybindSetting.builder().id("key.clickgui").displayName("ClickGUI Key")
			.defaultValue(InputUtil.fromKeyCode(GLFW.GLFW_KEY_GRAVE_ACCENT, 0)).build();

	private final KeyBinding esc = new KeyBinding("key.esc", GLFW.GLFW_KEY_ESCAPE, "key.categories.samurai");

	private boolean clickGuiOpen = false;
	private final HashMap<Object, Window> pinnedHuds = new HashMap<Object, Window>();

	// Navigation Bar and Pages
	public NavigationBar clickGuiNavBar;
	public Page modulesPane = new Page("Modules");
	public Page toolsPane = new Page("Tools");
	public Page hudPane = new Page("Hud");

	// Global HUD Settings
	public static BooleanSetting enableCustomTitle = BooleanSetting.builder().id("enable_custom_title")
			.displayName("Enable Custom Title Screen").defaultValue(true).build();

	public static BooleanSetting enableTooltips = BooleanSetting.builder().id("enable_tooltips")
			.displayName("Enable Tooltips").defaultValue(true).build();

	public static ColorSetting foregroundColor = ColorSetting.builder().id("hud_foreground_color")
			.displayName("GUI Foreground Color").description("Color of the foreground.")
			.defaultValue(new Color(238, 21, 247)).build();

	public static ColorSetting borderColor = ColorSetting.builder().id("hud_border_color")
			.displayName("GUI Border Color").description("Color of the borders.").defaultValue(new Color(0, 0, 0))
			.build();

	public static ColorSetting backgroundColor = ColorSetting.builder().id("hud_background_color")
			.displayName("GUI Background Color").description("Color of the background.")
			.defaultValue(new Color(0, 0, 0, 50)).build();

	public static FloatSetting roundingRadius = FloatSetting.builder().id("hud_rounding_radius")
			.displayName("Corner Rounding").description("The radius of the rounding on hud.").defaultValue(6f)
			.minValue(0f).maxValue(10f).step(1f).build();

	public static FloatSetting dragSmoothening = FloatSetting.builder().id("gui_drag_smoothening")
			.displayName("Drag Smooth Speed").description("The value for the dragging smoothening").defaultValue(1.0f)
			.minValue(0.1f).maxValue(2.0f).step(0.1f).build();

	public static RainbowColor rainbowColor = new RainbowColor();
	public static RandomColor randomColor = new RandomColor();

	private Framebuffer guiFrameBuffer;

	public ModuleSelectorHud moduleSelector;
	public ArmorHud armorHud;
	public RadarHud radarHud;
	public TimeHud timeHud;
	public DayHud dayHud;
	public ModuleArrayListHud moduleArrayListHud;
	public WatermarkHud watermarkHud;
	public CoordsHud coordsHud;
	public NetherCoordsHud netherCoordsHud;
	public FPSHud fpsHud;
	public PingHud pingHud;
	public SpeedHud speedHud;

	public GuiManager() {
		clickGuiNavBar = new NavigationBar();

		net.minecraft.client.util.Window window = MC.getWindow();

		guiFrameBuffer = new SimpleFramebuffer(window.getWidth(), window.getHeight(), false);

		SettingManager.registerGlobalSetting(borderColor);
		SettingManager.registerGlobalSetting(backgroundColor);
		SettingManager.registerGlobalSetting(foregroundColor);
		SettingManager.registerGlobalSetting(roundingRadius);

		SettingManager.registerSetting(clickGuiButton);
		Samurai.getInstance().eventManager.AddListener(KeyDownListener.class, this);
		Samurai.getInstance().eventManager.AddListener(TickListener.class, this);
		Samurai.getInstance().eventManager.AddListener(Render2DListener.class, this);
	}

	public Framebuffer getFrameBuffer() {
		return guiFrameBuffer;
	}

	public void Initialize() {
		System.out.println("Initializing");
		toolsPane.addWindow(new AuthCrackerWindow());
		toolsPane.addWindow(new GoToWindow());
		toolsPane.addWindow(new MacroWindow());

		moduleSelector = new ModuleSelectorHud();
		armorHud = new ArmorHud(0, 0);
		radarHud = new RadarHud(0, 0);
		timeHud = new TimeHud(0, 0);
		dayHud = new DayHud(0, 0);
		moduleArrayListHud = new ModuleArrayListHud(0, 0);
		watermarkHud = new WatermarkHud(0, 0);
		coordsHud = new CoordsHud(0, 0);
		netherCoordsHud = new NetherCoordsHud(0, 0);
		fpsHud = new FPSHud(0, 0);
		pingHud = new PingHud(0, 0);
		speedHud = new SpeedHud(0, 0);

		ArrayList<HudWindow> huds = Lists.newArrayList(moduleSelector, armorHud, radarHud, timeHud, dayHud,
				moduleArrayListHud, watermarkHud, coordsHud, netherCoordsHud, fpsHud, pingHud, speedHud);
		hudPane.addWindow(new HudOptionsWindow());
		hudPane.addWindow(new ToggleHudsTab(huds));
		Map<String, Category> categories = Category.getAllCategories();
		float xOffset = 50;

		for (Category category : categories.values()) {
			Window tab = new Window(category.getName(), xOffset, 75.0f);
			StackPanelComponent stackPanel = new StackPanelComponent();
			stackPanel.setMargin(new Margin(null, 30f, null, null));

			GridComponent gridComponent = new GridComponent();
			gridComponent.addColumnDefinition(new GridDefinition(30, RelativeUnit.Absolute)); // Fill 30px
			gridComponent.addColumnDefinition(new GridDefinition(1, RelativeUnit.Relative)); // Fill all remaining space

			ImageComponent img = new ImageComponent(category.getIcon());
			img.setMargin(new Margin(4f, 0f, 4f, 0f));
			gridComponent.addChild(img);

			StringComponent title = new StringComponent(category.getName());
			title.setIsHitTestVisible(false);
			gridComponent.addChild(title);

			stackPanel.addChild(gridComponent);

			SeparatorComponent separator = new SeparatorComponent();
			separator.setIsHitTestVisible(false);
			stackPanel.addChild(separator);

			// Loop through modules and add them to the correct category
			for (Module module : Samurai.getInstance().moduleManager.modules) {
				if (module.getCategory().equals(category)) {
					ModuleComponent button = new ModuleComponent(module);
					stackPanel.addChild(button);
				}
			}

			tab.addChild(stackPanel);
			tab.setMaxWidth(600f);
			modulesPane.addWindow(tab);

			xOffset += tab.getMinWidth() + 10;
		}

		modulesPane.addWindow(new SettingsWindow());
		modulesPane.addWindow(new AntiCheatWindow());

		clickGuiNavBar.addPane(modulesPane);
		clickGuiNavBar.addPane(toolsPane);
		clickGuiNavBar.addPane(hudPane);

		modulesPane.initialize();
		toolsPane.initialize();
		hudPane.initialize();

		clickGuiNavBar.setSelectedIndex(0);
	}

	public static CursorStyle getCursor() {
		return currentCursor;
	}

	public static void setCursor(CursorStyle cursor) {
		currentCursor = cursor;
		Input.setCursorStyle(currentCursor);
	}

	public static String getTooltip() {
		return tooltip;
	}

	public static void setTooltip(String tt) {
		if (tooltip != tt)
			tooltip = tt;
	}

	public void addWindow(Window hud, String pageName) {
		for (Page page : clickGuiNavBar.getPanes()) {
			if (page.getTitle().equals(pageName)) {
				page.addWindow(hud);
				page.moveToFront(hud);
				hud.initialize();
				break;
			}
		}
	}

	public void removeWindow(Window hud, String pageName) {
		for (Page page : clickGuiNavBar.getPanes()) {
			if (page.getTitle().equals(pageName)) {
				page.removeWindow(hud);
				break;
			}
		}
	}

	@Override
	public void onKeyDown(KeyDownEvent event) {
		if (clickGuiButton.getValue().getCode() == event.GetKey() && MC.currentScreen == null) {
			setClickGuiOpen(!this.clickGuiOpen);
			this.toggleMouse();
		}
	}

	public void setHudActive(HudWindow hud, boolean state) {
		if (state) {
			pinnedHuds.put(hud.getClass(), hud);
			hud.activated.silentSetValue(true);
			hudPane.addWindow(hud);
		} else {
			this.pinnedHuds.remove(hud.getClass());
			hud.activated.silentSetValue(false);
			hudPane.removeWindow(hud);
		}
	}

	@Override
	public void onTick(TickEvent.Pre event) {

	}

	@Override
	public void onTick(TickEvent.Post event) {
		/**
		 * Moves the selected Tab to where the user moves their mouse.
		 */
		if (this.clickGuiOpen) {
			clickGuiNavBar.update();
		}

		/**
		 * Updates each of the Tab GUIs that are currently on the screen.
		 */
		for (Window hud : pinnedHuds.values()) {
			hud.update();
		}

		if (this.esc.isPressed() && this.clickGuiOpen) {
			this.clickGuiOpen = false;
			this.toggleMouse();
		}
	}

	@Override
	public void onRender(Render2DEvent event) {

		RenderSystem.disableCull();
		RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		DrawContext drawContext = event.getDrawContext();
		float tickDelta = event.getRenderTickCounter().getTickDelta(false);

		MatrixStack matrixStack = drawContext.getMatrices();
		matrixStack.push();

		int guiScale = MC.getWindow().calculateScaleFactor(MC.options.getGuiScale().getValue(), MC.forcesUnicodeFont());
		matrixStack.scale(1.0f / guiScale, 1.0f / guiScale, 1.0f);

		net.minecraft.client.util.Window window = (net.minecraft.client.util.Window) MC.getWindow();
		Matrix4f matrix = matrixStack.peek().getPositionMatrix();

		/**
		 * Render ClickGUI and Sidebar
		 */
		if (this.clickGuiOpen) {
			Render2D.drawBox(matrix, 0, 0, window.getWidth(), window.getHeight(), new Color(26, 26, 26, 100));
			clickGuiNavBar.draw(drawContext, tickDelta);
		}

		// Render HUDS
		if (!this.clickGuiOpen) {
			for (Window hud : pinnedHuds.values()) {
				hud.draw(drawContext, tickDelta);
			}
		}

		// Draw Tooltip on top of all UI elements
		if (tooltip != null && GuiManager.enableTooltips.getValue()) {
			int mouseX = (int) MC.mouse.getX();
			int mouseY = (int) MC.mouse.getY();
			int tooltipWidth = Render2D.getStringWidth(tooltip) + 2;
			int tooltipHeight = 10;

			Render2D.drawRoundedBox(matrixStack.peek().getPositionMatrix(), mouseX + 12, mouseY + 12,
					(tooltipWidth + 4) * 2, (tooltipHeight + 4) * 2, GuiManager.roundingRadius.getValue(),
					GuiManager.backgroundColor.getValue().getAsSolid());
			Render2D.drawString(drawContext, tooltip, mouseX + 18, mouseY + 18, GuiManager.foregroundColor.getValue());
		}

		matrixStack.pop();
		RenderSystem.enableCull();
	}

	/**
	 * Gets whether or not the Click GUI is currently open.
	 *
	 * @return State of the Click GUI.
	 */
	public boolean isClickGuiOpen() {
		return this.clickGuiOpen;
	}

	public void setClickGuiOpen(boolean state) {
		this.clickGuiOpen = state;
		setTooltip(null);
	}

	/**
	 * Locks and unlocks the Mouse.
	 */
	public void toggleMouse() {
		if (MC.mouse.isCursorLocked()) {
			MC.mouse.unlockCursor();
		} else {
			MC.mouse.lockCursor();
		}
	}
}
