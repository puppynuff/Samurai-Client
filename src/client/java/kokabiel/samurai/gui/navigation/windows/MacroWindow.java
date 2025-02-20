/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package kokabiel.samurai.gui.navigation.windows;

import java.util.function.Function;

import kokabiel.samurai.Samurai;
import kokabiel.samurai.SamuraiClient;
import kokabiel.samurai.gui.GridDefinition;
import kokabiel.samurai.gui.GridDefinition.RelativeUnit;
import kokabiel.samurai.gui.Margin;
import kokabiel.samurai.gui.UIElement;
import kokabiel.samurai.gui.components.ButtonComponent;
import kokabiel.samurai.gui.components.GridComponent;
import kokabiel.samurai.gui.components.ItemsComponent;
import kokabiel.samurai.gui.components.SeparatorComponent;
import kokabiel.samurai.gui.components.StackPanelComponent;
import kokabiel.samurai.gui.components.StringComponent;
import kokabiel.samurai.gui.components.TextBoxComponent;
import kokabiel.samurai.gui.navigation.Window;
import kokabiel.samurai.managers.macros.Macro;

public class MacroWindow extends Window {
	private ButtonComponent startButton;
	private StringComponent startButtonText;
	private ButtonComponent replayButton;
	private StringComponent replayButtonText;

	private TextBoxComponent filenameText;
	private ItemsComponent<Macro> macrosList;
	private ButtonComponent saveButton;

	private Runnable startRunnable;
	private Runnable endRunnable;
	private Runnable replayRunnable;

	public MacroWindow() {
		super("Macro", 895, 150);

		this.minWidth = 350f;

		StackPanelComponent stackPanel = new StackPanelComponent();

		stackPanel.addChild(new StringComponent("Macros"));
		stackPanel.addChild(new SeparatorComponent());

		StringComponent label = new StringComponent("Records your inputs and plays them back.");
		stackPanel.addChild(label);

		startRunnable = new Runnable() {
			@Override
			public void run() {
				Samurai.getInstance().guiManager.setClickGuiOpen(false);
				Samurai.getInstance().macroManager.getRecorder().startRecording();
				startButtonText.setText("Stop Recording");
				startButton.setOnClick(endRunnable);
			}
		};

		endRunnable = new Runnable() {
			@Override
			public void run() {
				Samurai.getInstance().macroManager.getRecorder().stopRecording();
				startButtonText.setText("Record");
				startButton.setOnClick(startRunnable);
			}
		};
		startButton = new ButtonComponent(startRunnable);
		startButtonText = new StringComponent("Record");
		startButton.addChild(startButtonText);
		stackPanel.addChild(startButton);

		stackPanel.addChild(new StringComponent("Filename:"));

		GridComponent fileNameGrid = new GridComponent();
		fileNameGrid.addColumnDefinition(new GridDefinition(1, RelativeUnit.Relative));
		fileNameGrid.addColumnDefinition(new GridDefinition(75, RelativeUnit.Absolute));

		filenameText = new TextBoxComponent();
		fileNameGrid.addChild(filenameText);

		// Save Button
		saveButton = new ButtonComponent(new Runnable() {
			@Override
			public void run() {
				SamuraiClient samurai = Samurai.getInstance();
				Macro currentMacro = samurai.macroManager.getCurrentlySelected();
				currentMacro.setName(filenameText.getText());
				samurai.macroManager.addMacro(currentMacro);

				// Reload the items control.
				macrosList.setItemsSource(samurai.macroManager.getMacros());
			}
		});
		saveButton.setMargin(new Margin(0f, 2f, 8f, 2f));
		saveButton.addChild(new StringComponent("Save"));
		fileNameGrid.addChild(saveButton);
		stackPanel.addChild(fileNameGrid);

		// Macros List Label
		StringComponent macroListLabel = new StringComponent("Macros");
		stackPanel.addChild(macroListLabel);
		stackPanel.addChild(new SeparatorComponent());

		// Add Macros ItemComponents
		Function<Macro, UIElement> test = (s -> {
			GridComponent macroItemGrid = new GridComponent();
			macroItemGrid.addColumnDefinition(new GridDefinition(1.0f, RelativeUnit.Relative));
			macroItemGrid.addColumnDefinition(new GridDefinition(35f, RelativeUnit.Absolute));
			macroItemGrid.addColumnDefinition(new GridDefinition(35f, RelativeUnit.Absolute));
			macroItemGrid.setMargin(new Margin(4f, 0f, 4f, 0f));

			macroItemGrid.addChild(new StringComponent(s.getName()));

			ButtonComponent playMacroButton = new ButtonComponent(new Runnable() {
				@Override
				public void run() {
					Samurai.getInstance().macroManager.getPlayer().play(s);
				}
			});

			playMacroButton.addChild(new StringComponent("▶"));
			playMacroButton.setMargin(new Margin(2f));
			macroItemGrid.addChild(playMacroButton);

			ButtonComponent deleteMacroButton = new ButtonComponent(new Runnable() {
				@Override
				public void run() {
					Samurai.getInstance().macroManager.removeMacro(s);
					// Reload the items control.
					macrosList.setItemsSource(Samurai.getInstance().macroManager.getMacros());
				}
			});

			deleteMacroButton.addChild(new StringComponent("🗑"));
			deleteMacroButton.setMargin(new Margin(2f));
			macroItemGrid.addChild(deleteMacroButton);

			return macroItemGrid;
		});

		macrosList = new ItemsComponent<Macro>(Samurai.getInstance().macroManager.getMacros(), test);
		stackPanel.addChild(macrosList);

		// Add stackpanel to child.
		addChild(stackPanel);
	}
}