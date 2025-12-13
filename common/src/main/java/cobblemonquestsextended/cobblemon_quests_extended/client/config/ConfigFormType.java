package cobblemonquestsextended.cobblemon_quests_extended.client.config;

import cobblemonquestsextended.cobblemon_quests_extended.client.gui.selectors.SelectFormScreen;
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
 * Custom ConfigValue for selecting Pokemon forms/aspects.
 * Opens a SelectFormScreen when clicked, showing forms like alolan, galarian, hisuian, etc.
 */
@Environment(EnvType.CLIENT)
public class ConfigFormType extends ConfigValue<String> {

    /**
     * Default color used when no form category matches.
     */
    public static final Color4I DEFAULT_COLOR = Color4I.rgb(0x808080);

    /**
     * Form category colors for visual distinction.
     */
    private static final Map<String, Integer> FORM_COLORS = Map.ofEntries(
            // Regional forms
            Map.entry("alolan", 0xF8D030),    // Yellow/gold for tropical
            Map.entry("galarian", 0xE91E63),  // Magenta for UK
            Map.entry("hisuian", 0x6D4C41),   // Brown for ancient
            Map.entry("paldean", 0x1ABC9C),   // Teal for Spain
            // Special forms
            Map.entry("mega", 0xE91E63),      // Pink for mega
            Map.entry("gmax", 0xE74C3C),      // Red for gigantamax
            Map.entry("origin", 0x9B59B6),    // Purple for origin
            Map.entry("altered", 0x3498DB),   // Blue for altered
            Map.entry("therian", 0xF39C12),   // Orange for therian
            Map.entry("incarnate", 0x27AE60), // Green for incarnate
            Map.entry("zen", 0x00BCD4),       // Cyan for zen mode
            Map.entry("standard", 0x95A5A6),  // Gray for standard
            Map.entry("primal", 0xE91E63)     // Pink for primal
    );

    public ConfigFormType() {
        // Default constructor
    }

    @Override
    public Component getStringForGUI(@Nullable String value) {
        if (value == null || value.isEmpty()) {
            return NULL_TEXT;
        }

        return Component.translatable("cobblemon_quests_extended.form." + value.toLowerCase());
    }

    @Override
    public Color4I getColor(@Nullable String value) {
        if (value == null || value.isEmpty()) {
            return DEFAULT_COLOR;
        }

        // Check if the form matches any known pattern
        String lower = value.toLowerCase();
        for (Map.Entry<String, Integer> entry : FORM_COLORS.entrySet()) {
            if (lower.contains(entry.getKey())) {
                return Color4I.rgb(entry.getValue());
            }
        }

        return DEFAULT_COLOR;
    }

    @Override
    public void onClicked(Widget clickedWidget, MouseButton button, ConfigCallback callback) {
        if (getCanEdit()) {
            new SelectFormScreen(this, callback).openGui();
        }
    }

    @Override
    public void addInfo(TooltipList list) {
        if (value != null && !value.isEmpty()) {
            // Determine form category
            String category = getFormCategory(value);
            list.add(Component.translatable("cobblemon_quests_extended.config.form_category")
                    .withStyle(ChatFormatting.GRAY)
                    .append(": ")
                    .append(Component.translatable("cobblemon_quests_extended.form_category." + category)
                            .withStyle(getCategoryFormatting(category))));
        }
    }

    /**
     * Categorizes a form into a broad category.
     *
     * @param form the form name
     * @return the category name
     */
    public static String getFormCategory(String form) {
        String lower = form.toLowerCase();

        if (lower.contains("alolan") || lower.contains("galarian") ||
                lower.contains("hisuian") || lower.contains("paldean")) {
            return "regional";
        }

        if (lower.contains("mega") || lower.contains("primal")) {
            return "mega";
        }

        if (lower.contains("gmax") || lower.contains("gigantamax")) {
            return "dynamax";
        }

        if (lower.contains("origin") || lower.contains("altered") ||
                lower.contains("therian") || lower.contains("incarnate")) {
            return "legendary";
        }

        return "other";
    }

    /**
     * Gets the ChatFormatting for a form category.
     *
     * @param category the form category
     * @return the corresponding chat formatting
     */
    public static ChatFormatting getCategoryFormatting(String category) {
        return switch (category) {
            case "regional" -> ChatFormatting.GOLD;
            case "mega" -> ChatFormatting.LIGHT_PURPLE;
            case "dynamax" -> ChatFormatting.RED;
            case "legendary" -> ChatFormatting.DARK_PURPLE;
            default -> ChatFormatting.GRAY;
        };
    }

    /**
     * Gets the color for a specific form.
     *
     * @param form the form name
     * @return the color as an integer
     */
    public static int getFormColor(String form) {
        String lower = form.toLowerCase();
        for (Map.Entry<String, Integer> entry : FORM_COLORS.entrySet()) {
            if (lower.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return 0x808080;
    }
}
