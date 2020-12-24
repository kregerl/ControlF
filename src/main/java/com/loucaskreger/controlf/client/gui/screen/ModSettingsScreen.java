package com.loucaskreger.controlf.client.gui.screen;

import java.util.function.Predicate;

import com.loucaskreger.controlf.config.ClientConfig;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class ModSettingsScreen extends Screen {

	private TextFieldWidget redField;
	private TextFieldWidget greenField;
	private TextFieldWidget blueField;
	private TextFieldWidget alphaField;

	private String redText;
	private String greenText;
	private String blueText;
	private String alphaText;

	private static final String[] COLORS = { "Red:", "Green:", "Blue:", "Alpha:" };

	private static final Predicate<String> VALIDATOR = s -> {
		if (s.isEmpty())
			return true;
		boolean flag = s.matches("^[0-9]+");
		int value;
		if (flag) {
			value = Integer.parseInt(s);
			if (value > 0 && value < 256) {
				return true;
			}
		}
		return false;
	};

	public ModSettingsScreen(ITextComponent titleIn) {
		super(titleIn);
	}

	public ModSettingsScreen() {
		this(new StringTextComponent("Testing"));
	}

	@Override
	public void tick() {
		this.redField.tick();
		this.greenField.tick();
		this.blueField.tick();
		this.alphaField.tick();

	}

	@Override
	protected void init() {
//		super.init();

		this.redField = new TextFieldWidget(this.font, this.width / 2 - 160, 30, 50, 10, null);
		this.greenField = new TextFieldWidget(this.font, this.width / 2 - 160, 50, 50, 10, null);
		this.blueField = new TextFieldWidget(this.font, this.width / 2 - 160, 70, 50, 10, null);
		this.alphaField = new TextFieldWidget(this.font, this.width / 2 - 160, 90, 50, 10, null);

		this.redField.setText(ClientConfig.red.get().toString());
		this.redField.setValidator(VALIDATOR);
		this.redField.setResponder(text -> this.redText = text);

		this.greenField.setText(ClientConfig.green.get().toString());
		this.greenField.setValidator(VALIDATOR);
		this.greenField.setResponder(text -> this.greenText = text);

		this.blueField.setText(ClientConfig.blue.get().toString());
		this.blueField.setValidator(VALIDATOR);
		this.blueField.setResponder(text -> this.blueText = text);
		
		this.alphaField.setText(ClientConfig.alpha.get().toString());
		this.alphaField.setValidator(VALIDATOR);
		this.alphaField.setResponder(text -> this.alphaText = text);
		
		
		
		this.children.add(redField);
		this.children.add(greenField);
		this.children.add(blueField);
		this.children.add(alphaField);
//		this.addButton(new Button(32, 32, 25, 100, "Test", null));
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		super.render(mouseX, mouseY, partialTicks);
		this.renderBackground();
		this.drawCenteredString(font, "Color", this.width / 2 - 135, 20, 0xFFFFFF);

		for (int i = 0; i < COLORS.length; i++) {
			this.drawString(this.font, COLORS[i], this.width / 2 - 194, 32 + (i * 20), 0xFFFFFF);
		}
		this.redField.render(mouseX, mouseY, partialTicks);
		this.greenField.render(mouseX, mouseY, partialTicks);
		this.blueField.render(mouseX, mouseY, partialTicks);
		this.alphaField.render(mouseX, mouseY, partialTicks);
//		super.renderBackground();

	}
}
