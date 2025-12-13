package cobblemonquestsextended.cobblemon_quests_extended.client.config;

import cobblemonquestsextended.cobblemon_quests_extended.client.gui.selectors.SelectPokeBallScreen;
import dev.ftb.mods.ftblibrary.config.ConfigCallback;
import dev.ftb.mods.ftblibrary.config.ConfigValue;
import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.ui.Widget;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Custom ConfigValue for selecting Poke Balls.
 * Opens a SelectPokeBallScreen when clicked, showing all Cobblemon Poke Balls
 * organized by category (standard, apricorn, special, other).
 */
@Environment(EnvType.CLIENT)
public class ConfigPokeBallType extends ConfigValue<String> {

    /**
     * Default color for Poke Balls (classic red).
     */
    public static final Color4I DEFAULT_COLOR = Color4I.rgb(0xE74C3C);

    /**
     * Colors for common ball types, keyed by ball ID.
     */
    private static final Map<String, Integer> BALL_COLORS = Map.ofEntries(
            Map.entry("poke_ball", 0xE74C3C),         // Red
            Map.entry("great_ball", 0x3498DB),        // Blue
            Map.entry("ultra_ball", 0xF1C40F),        // Yellow
            Map.entry("master_ball", 0x9B59B6),       // Purple
            Map.entry("safari_ball", 0x27AE60),       // Green
            Map.entry("fast_ball", 0xF39C12),         // Orange
            Map.entry("level_ball", 0xF1C40F),        // Gold
            Map.entry("lure_ball", 0x00BCD4),         // Cyan
            Map.entry("heavy_ball", 0x607D8B),        // Gray
            Map.entry("love_ball", 0xE91E63),         // Pink
            Map.entry("friend_ball", 0x8BC34A),       // Light green
            Map.entry("moon_ball", 0x1A237E),         // Dark blue
            Map.entry("sport_ball", 0xFF5722),        // Deep orange
            Map.entry("net_ball", 0x009688),          // Teal
            Map.entry("nest_ball", 0x4CAF50),         // Green
            Map.entry("repeat_ball", 0x795548),       // Brown
            Map.entry("timer_ball", 0x424242),        // Dark gray
            Map.entry("luxury_ball", 0x212121),       // Black
            Map.entry("premier_ball", 0xFFFFFF),      // White
            Map.entry("dusk_ball", 0x4E342E),         // Dark brown
            Map.entry("heal_ball", 0xF48FB1),         // Light pink
            Map.entry("quick_ball", 0x2196F3),        // Light blue
            Map.entry("cherish_ball", 0xB71C1C),      // Dark red
            Map.entry("dream_ball", 0xE1BEE7),        // Light purple
            Map.entry("beast_ball", 0x304FFE),        // Bright blue
            Map.entry("dive_ball", 0x0277BD),         // Deep blue
            Map.entry("park_ball", 0xFFEB3B)          // Bright yellow
    );

    public ConfigPokeBallType() {
        // Default constructor
    }

    @Override
    public Component getStringForGUI(@Nullable String value) {
        if (value == null || value.isEmpty()) {
            return NULL_TEXT;
        }
        // Use Cobblemon's translation for pokeballs
        return Component.translatable("item.cobblemon." + value.toLowerCase());
    }

    @Override
    public Color4I getColor(@Nullable String value) {
        if (value == null || value.isEmpty()) {
            return DEFAULT_COLOR;
        }
        return Color4I.rgb(BALL_COLORS.getOrDefault(value.toLowerCase(), 0xE74C3C));
    }

    @Override
    public void onClicked(Widget clickedWidget, MouseButton button, ConfigCallback callback) {
        if (getCanEdit()) {
            new SelectPokeBallScreen(this, callback).openGui();
        }
    }

    @Override
    public void addInfo(TooltipList list) {
        if (value != null && !value.isEmpty()) {
            // Show ball category
            String category = getBallCategory(value);
            list.add(Component.translatable("cobblemon_quests_extended.config.ball_category")
                    .withStyle(ChatFormatting.GRAY)
                    .append(": ")
                    .append(Component.translatable("cobblemon_quests_extended.ball_category." + category)
                            .withStyle(ChatFormatting.WHITE)));
        }
    }

    /**
     * Gets the color for a specific Poke Ball by ID.
     *
     * @param ballId the ball ID
     * @return the RGB color value, or default red if not found
     */
    public static int getBallColorValue(String ballId) {
        return BALL_COLORS.getOrDefault(ballId.toLowerCase(), 0xE74C3C);
    }

    /**
     * Categorizes a ball into a broad category for grouping.
     *
     * @param ball the ball ID
     * @return the category name: "standard", "apricorn", "special", or "other"
     */
    public static String getBallCategory(String ball) {
        String lower = ball.toLowerCase();
        if (lower.equals("poke_ball") || lower.equals("great_ball") ||
                lower.equals("ultra_ball") || lower.equals("master_ball")) {
            return "standard";
        }
        if (lower.contains("apricorn") || lower.equals("fast_ball") || lower.equals("level_ball") ||
                lower.equals("lure_ball") || lower.equals("heavy_ball") || lower.equals("love_ball") ||
                lower.equals("friend_ball") || lower.equals("moon_ball")) {
            return "apricorn";
        }
        if (lower.equals("beast_ball") || lower.equals("dream_ball") || lower.equals("cherish_ball")) {
            return "special";
        }
        return "other";
    }
}
