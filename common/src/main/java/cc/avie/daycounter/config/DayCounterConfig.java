package cc.avie.daycounter.config;

import com.google.gson.JsonObject;
import cc.avie.daycounter.hud.HUD;
import cc.avie.daycounter.hud.KeyBindings;
import cc.avie.tabbylib.api.HudPosition;
import cc.avie.tabbylib.api.TabbyConfig;
import cc.avie.tabbylib.api.option.BooleanOption;
import cc.avie.tabbylib.api.option.ColorOption;
import cc.avie.tabbylib.api.option.DoubleOption;
import cc.avie.tabbylib.api.option.EnumOption;
import cc.avie.tabbylib.api.option.HudPositionOption;
import cc.avie.tabbylib.api.option.KeyBindOption;
import cc.avie.tabbylib.api.option.Options;
import cc.avie.tabbylib.api.option.StringOption;
import net.minecraft.network.chat.Component;

import java.util.Locale;

public final class DayCounterConfig {
	public enum TextFormat {
		DAY,
		NUMBER,
		DAY_AND_TIME,
		CUSTOM
	}

	public enum BedIcon {
		OFF,
		HOTBAR,
		COUNTER
	}

	public static final BooleanOption HUD_VISIBLE = Options.toggle("hudVisible", true);
	public static final BooleanOption DEBUG = Options.toggle("debugEnabled", false);
	public static KeyBindOption TOGGLE_KEY;

	public static final EnumOption<TextFormat> TEXT_FORMAT = Options.choice("textFormat", TextFormat.DAY);
	public static final StringOption CUSTOM_TEXT = Options.text("customText", "Day {day}")
		.maxLength(64)
		.hint(Component.literal("Day {day} - {time}"))
		.visibleWhen(() -> TEXT_FORMAT.getPending() == TextFormat.CUSTOM);
	public static final BooleanOption TWELVE_HOUR = Options.toggle("twelveHourClock", false)
		.visibleWhen(() -> TEXT_FORMAT.getPending() == TextFormat.DAY_AND_TIME || TEXT_FORMAT.getPending() == TextFormat.CUSTOM);

	public static final EnumOption<BedIcon> BED_ICON = Options.choice("bedIcon", BedIcon.OFF);

	public static final ColorOption TEXT_COLOR = Options.color("textColor", 0xFFFFFFFF);
	public static final BooleanOption TEXT_SHADOW = Options.toggle("textShadow", true);
	public static final DoubleOption SCALE = Options.decimal("scale", 1.0)
		.slider(0.5, 3.0, 0.25)
		.format(value -> Component.literal(String.format(Locale.ROOT, "%.2fx", value)));
	public static final BooleanOption BACKGROUND_VISIBLE = Options.toggle("backgroundVisible", true);
	public static final ColorOption BACKGROUND_COLOR = Options.colorWithAlpha("backgroundColor", 0x90000000)
		.dependsOn(BACKGROUND_VISIBLE);
	public static final BooleanOption BORDER_VISIBLE = Options.toggle("borderVisible", false);
	public static final ColorOption BORDER_COLOR = Options.colorWithAlpha("borderColor", 0xFFFFFF00)
		.dependsOn(BORDER_VISIBLE);

	public static final HudPositionOption POSITION = Options.hudPosition("hudPosition",
		HudPosition.of(HudPosition.CENTER, HudPosition.END, 0, -50), HUD::preview);

	private static TabbyConfig config;

	private DayCounterConfig() {
	}

	public static void init(String modId) {
		TOGGLE_KEY = Options.keyBind("toggleKey", KeyBindings.toggleHudKey);

		config = TabbyConfig.builder(modId)
			.translationId("day-counter")
			.fileName("daycounter")
			.migration(DayCounterConfig::migrateOldPosition)
			.category("general", category -> category
				.add(HUD_VISIBLE, TOGGLE_KEY, POSITION)
				.section("text", section -> section.add(TEXT_FORMAT, CUSTOM_TEXT, TWELVE_HOUR))
				.section("appearance", section -> section.add(BED_ICON, TEXT_COLOR, TEXT_SHADOW, SCALE,
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
