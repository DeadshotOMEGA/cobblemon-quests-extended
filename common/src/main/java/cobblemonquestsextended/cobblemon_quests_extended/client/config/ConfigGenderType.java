package cobblemonquestsextended.cobblemon_quests_extended.client.config;

import cobblemonquestsextended.cobblemon_quests_extended.client.gui.selectors.SelectGenderScreen;
import dev.ftb.mods.ftblibrary.config.ConfigCallback;
import dev.ftb.mods.ftblibrary.config.ConfigValue;
import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.ui.Widget;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

/**
 * Custom ConfigValue for selecting Pokemon genders.
 * Displays 3 gender options (male, female, genderless) with gender-specific colors.
 */
@Environment(EnvType.CLIENT)
public class ConfigGenderType extends ConfigValue<String> {

    /**
     * Color for male gender (blue).
     */
    private static final int MALE_COLOR = 0x3498DB;

    /**
     * Color for female gender (pink).
     */
    private static final int FEMALE_COLOR = 0xE91E63;

    /**
     * Color for genderless gender (gray).
     */
    private static final int GENDERLESS_COLOR = 0x95A5A6;

    @Override
    public Component getStringForGUI(@Nullable String value) {
        if (value == null || value.isEmpty()) {
            return NULL_TEXT;
        }
        return Component.translatable("cobblemon_quests_extended.gender." + value);
    }

    @Override
    public Color4I getColor(@Nullable String value) {
        if (value == null) {
            return Color4I.rgb(GENDERLESS_COLOR);
        }
        return switch (value.toLowerCase()) {
            case "male" -> Color4I.rgb(MALE_COLOR);
            case "female" -> Color4I.rgb(FEMALE_COLOR);
            default -> Color4I.rgb(GENDERLESS_COLOR);
        };
    }

    @Override
    public void onClicked(Widget clickedWidget, MouseButton button, ConfigCallback callback) {
        if (getCanEdit()) {
            new SelectGenderScreen(this, callback).openGui();
        }
    }

    @Override
    public void addInfo(TooltipList list) {
        // Simple type, no additional info needed
    }
}
