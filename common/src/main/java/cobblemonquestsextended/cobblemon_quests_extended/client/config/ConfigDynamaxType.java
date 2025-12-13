package cobblemonquestsextended.cobblemon_quests_extended.client.config;

import cobblemonquestsextended.cobblemon_quests_extended.client.gui.selectors.SelectDynamaxScreen;
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

/**
 * Custom ConfigValue for selecting Dynamax types.
 * Displays 2 options: Dynamax (red) and Gigantamax (pink/magenta).
 */
@Environment(EnvType.CLIENT)
public class ConfigDynamaxType extends ConfigValue<String> {

    /**
     * Color for standard Dynamax (red).
     */
    private static final int DYNAMAX_COLOR = 0xE53935;

    /**
     * Color for Gigantamax (pink/magenta).
     */
    private static final int GIGANTAMAX_COLOR = 0xE91E63;

    /**
     * Default color when nothing selected.
     */
    private static final int DEFAULT_COLOR = 0x808080;

    public ConfigDynamaxType() {
        // Default constructor
    }

    @Override
    public Component getStringForGUI(@Nullable String value) {
        if (value == null || value.isEmpty()) {
            return NULL_TEXT;
        }
        return Component.translatable("cobblemon_quests_extended.dynamax_type." + value);
    }

    @Override
    public Color4I getColor(@Nullable String value) {
        if (value == null || value.isEmpty()) {
            return Color4I.rgb(DEFAULT_COLOR);
        }
        return switch (value.toLowerCase()) {
            case "dynamax" -> Color4I.rgb(DYNAMAX_COLOR);
            case "gigantamax" -> Color4I.rgb(GIGANTAMAX_COLOR);
            default -> Color4I.rgb(DEFAULT_COLOR);
        };
    }

    @Override
    public void onClicked(Widget clickedWidget, MouseButton button, ConfigCallback callback) {
        if (getCanEdit()) {
            new SelectDynamaxScreen(this, callback).openGui();
        }
    }

    @Override
    public void addInfo(TooltipList list) {
        if (value != null && !value.isEmpty()) {
            String descKey = "cobblemon_quests_extended.dynamax_type." + value + ".desc";
            list.add(Component.translatable(descKey).withStyle(ChatFormatting.GRAY));

            if (value.equalsIgnoreCase("gigantamax")) {
                list.add(Component.translatable("cobblemon_quests_extended.dynamax_type.gigantamax.special")
                        .withStyle(ChatFormatting.LIGHT_PURPLE));
            }
        }
    }

    /**
     * Gets the color for a Dynamax type.
     *
     * @param dynamaxType the Dynamax type name
     * @return the RGB color value
     */
    public static int getDynamaxColorValue(String dynamaxType) {
        if (dynamaxType == null) return DEFAULT_COLOR;
        return switch (dynamaxType.toLowerCase()) {
            case "dynamax" -> DYNAMAX_COLOR;
            case "gigantamax" -> GIGANTAMAX_COLOR;
            default -> DEFAULT_COLOR;
        };
    }

    /**
     * Checks if a type is Gigantamax.
     *
     * @param dynamaxType the Dynamax type name
     * @return true if Gigantamax
     */
    public static boolean isGigantamax(String dynamaxType) {
        return dynamaxType != null && dynamaxType.equalsIgnoreCase("gigantamax");
    }
}
