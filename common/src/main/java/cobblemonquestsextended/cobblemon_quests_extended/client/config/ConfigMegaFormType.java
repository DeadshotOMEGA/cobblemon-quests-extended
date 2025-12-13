package cobblemonquestsextended.cobblemon_quests_extended.client.config;

import cobblemonquestsextended.cobblemon_quests_extended.client.gui.selectors.SelectMegaFormScreen;
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
 * Custom ConfigValue for selecting Mega forms.
 * Displays 4 mega form options (mega, mega_x, mega_y, primal) with form-specific colors.
 */
@Environment(EnvType.CLIENT)
public class ConfigMegaFormType extends ConfigValue<String> {

    /**
     * Color for standard Mega form (pink).
     */
    private static final int MEGA_COLOR = 0xE91E63;

    /**
     * Color for Mega X form (blue).
     */
    private static final int MEGA_X_COLOR = 0x3498DB;

    /**
     * Color for Mega Y form (red).
     */
    private static final int MEGA_Y_COLOR = 0xE74C3C;

    /**
     * Color for Primal form (orange).
     */
    private static final int PRIMAL_COLOR = 0xF39C12;

    @Override
    public Component getStringForGUI(@Nullable String value) {
        if (value == null || value.isEmpty()) {
            return NULL_TEXT;
        }
        return Component.translatable("cobblemon_quests_extended.mega_form." + value);
    }

    @Override
    public Color4I getColor(@Nullable String value) {
        if (value == null) {
            return Color4I.rgb(MEGA_COLOR);
        }
        return switch (value.toLowerCase()) {
            case "mega" -> Color4I.rgb(MEGA_COLOR);
            case "mega_x" -> Color4I.rgb(MEGA_X_COLOR);
            case "mega_y" -> Color4I.rgb(MEGA_Y_COLOR);
            case "primal" -> Color4I.rgb(PRIMAL_COLOR);
            default -> Color4I.rgb(MEGA_COLOR);
        };
    }

    @Override
    public void onClicked(Widget clickedWidget, MouseButton button, ConfigCallback callback) {
        if (getCanEdit()) {
            new SelectMegaFormScreen(this, callback).openGui();
        }
    }

    @Override
    public void addInfo(TooltipList list) {
        if (value != null && !value.isEmpty()) {
            String descKey = "cobblemon_quests_extended.mega_form." + value + ".desc";
            list.add(Component.translatable(descKey).withStyle(ChatFormatting.GRAY));
        }
    }
}
