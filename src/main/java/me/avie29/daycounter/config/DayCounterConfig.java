package me.avie29.daycounter.config;

import com.google.gson.JsonObject;
import me.avie29.daycounter.hud.HUD;
import me.avie29.daycounter.hud.KeyBindings;
import me.avie29.tabbylib.api.HudPosition;
import me.avie29.tabbylib.api.TabbyConfig;
import me.avie29.tabbylib.api.option.BooleanOption;
import me.avie29.tabbylib.api.option.ColorOption;
import me.avie29.tabbylib.api.option.DoubleOption;
import me.avie29.tabbylib.api.option.EnumOption;
import me.avie29.tabbylib.api.option.HudPositionOption;
import me.avie29.tabbylib.api.option.KeyBindOption;
import me.avie29.tabbylib.api.option.StringOption;
import net.minecraft.network.chat.Component;

import java.util.Locale;

public final class DayCounterConfig {
	public enum TextFormat {
		DAY,
		NUMBER,
		DAY_AND_TIME,
		CUSTOM
	}

	public static final BooleanOption HUD_VISIBLE = BooleanOption.builder("hudVisible", true).build();
	public static final BooleanOption DEBUG = BooleanOption.builder("debugEnabled", false).build();
	public static KeyBindOption TOGGLE_KEY;

	public static final EnumOption<TextFormat> TEXT_FORMAT = EnumOption.builder("textFormat", TextFormat.DAY).build();
	public static final StringOption CUSTOM_TEXT = StringOption.builder("customText", "Day {day}")
		.maxLength(64)
		.hint(Component.literal("Day {day} - {time}"))
		.visibleWhen(() -> TEXT_FORMAT.getPending() == TextFormat.CUSTOM)
		.build();
	public static final BooleanOption TWELVE_HOUR = BooleanOption.builder("twelveHourClock", false)
		.visibleWhen(() -> TEXT_FORMAT.getPending() == TextFormat.DAY_AND_TIME || TEXT_FORMAT.getPending() == TextFormat.CUSTOM)
		.build();

	public static final ColorOption TEXT_COLOR = ColorOption.builder("textColor", 0xFFFFFFFF).build();
	public static final BooleanOption TEXT_SHADOW = BooleanOption.builder("textShadow", true).build();
	public static final DoubleOption SCALE = DoubleOption.builder("scale", 1.0)
		.slider(0.5, 3.0, 0.25)
		.formatter(value -> Component.literal(String.format(Locale.ROOT, "%.2fx", value)))
		.build();
	public static final BooleanOption BACKGROUND_VISIBLE = BooleanOption.builder("backgroundVisible", true).build();
	public static final ColorOption BACKGROUND_COLOR = ColorOption.builder("backgroundColor", 0x90000000)
		.alpha()
		.dependsOn(BACKGROUND_VISIBLE)
		.build();
	public static final BooleanOption BORDER_VISIBLE = BooleanOption.builder("borderVisible", false).build();
	public static final ColorOption BORDER_COLOR = ColorOption.builder("borderColor", 0xFFFFFF00)
		.alpha()
		.dependsOn(BORDER_VISIBLE)
		.build();

	public static final HudPositionOption POSITION = HudPositionOption.builder("hudPosition",
		HudPosition.of(HudPosition.CENTER, HudPosition.END, 0, -50), HUD::preview).build();

	private static TabbyConfig config;

	private DayCounterConfig() {
	}

	public static void init(String modId) {
		TOGGLE_KEY = KeyBindOption.builder("toggleKey", KeyBindings.toggleHudKey).build();

		config = TabbyConfig.builder(modId)
			.translationId("day-counter")
			.fileName("daycounter")
			.migration(DayCounterConfig::migrateOldPosition)
			.category("general", category -> category
				.add(HUD_VISIBLE, TOGGLE_KEY, POSITION)
				.group("text", group -> group.add(TEXT_FORMAT, CUSTOM_TEXT, TWELVE_HOUR))
				.group("appearance", group -> group.add(TEXT_COLOR, TEXT_SHADOW, SCALE,
					BACKGROUND_VISIBLE, BACKGROUND_COLOR, BORDER_VISIBLE, BORDER_COLOR)))
			.category("advanced", category -> category
				.label(Component.translatable("config.day-counter.advanced.description"))
				.add(DEBUG))
			.build();
	}

	public static TabbyConfig get() {
		return config;
	}

	private static boolean migrateOldPosition(JsonObject json) {
		if (!json.has("hudAnchorX") || json.has("hudPosition")) {
			return false;
		}
		boolean custom = json.has("useCustomPosition") && json.get("useCustomPosition").getAsBoolean();
		boolean anchored = json.has("hudAnchorInitialized") && json.get("hudAnchorInitialized").getAsBoolean();
		if (custom && anchored) {
			JsonObject position = new JsonObject();
			position.addProperty("anchorX", json.get("hudAnchorX").getAsInt());
			position.addProperty("anchorY", json.get("hudAnchorY").getAsInt());
			position.addProperty("offsetX", json.get("hudOffsetX").getAsInt());
			position.addProperty("offsetY", json.get("hudOffsetY").getAsInt());
			json.add("hudPosition", position);
		}
		for (String key : new String[]{"useCustomPosition", "hudX", "hudY", "hudScreenWidth", "hudScreenHeight",
			"hudAnchorX", "hudAnchorY", "hudOffsetX", "hudOffsetY", "hudAnchorInitialized"}) {
			json.remove(key);
		}
		return true;
	}
}
