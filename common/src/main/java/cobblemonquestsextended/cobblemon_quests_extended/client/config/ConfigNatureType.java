package cobblemonquestsextended.cobblemon_quests_extended.client.config;

import cobblemonquestsextended.cobblemon_quests_extended.client.gui.selectors.SelectNatureScreen;
import cobblemonquestsextended.cobblemon_quests_extended.registry.NatureCategory;
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
 * Custom ConfigValue for selecting Pokemon natures.
 * Opens a SelectNatureScreen when clicked, showing natures grouped by stat modifiers.
 * Uses NatureCategory for grouping and coloring based on boosted stats.
 */
@Environment(EnvType.CLIENT)
public class ConfigNatureType extends ConfigValue<String> {

    /**
     * Default color used when no nature is selected.
     */
    public static final Color4I DEFAULT_COLOR = Color4I.rgb(0x808080);

    public ConfigNatureType() {
        // Default constructor
    }

    @Override
    public Component getStringForGUI(@Nullable String value) {
        if (value == null || value.isEmpty()) {
            return NULL_TEXT;
        }
        // Use Cobblemon's translation for natures
        return Component.translatable("cobblemon.nature." + value.toLowerCase());
    }

    @Override
    public Color4I getColor(@Nullable String value) {
        if (value == null || value.isEmpty()) {
            return DEFAULT_COLOR;
        }
        NatureCategory category = NatureCategory.fromNature(value);
        return category.getColor();
    }

    @Override
    public void onClicked(Widget clickedWidget, MouseButton button, ConfigCallback callback) {
        if (getCanEdit()) {
            new SelectNatureScreen(this, callback).openGui();
        }
    }

    @Override
    public void addInfo(TooltipList list) {
        if (value != null && !value.isEmpty()) {
            NatureCategory category = NatureCategory.fromNature(value);

            // Show stat changes
            String boost = category.getStatBoost();
            String penalty = category.getStatPenalty();

            if (boost != null && penalty != null) {
                list.add(Component.literal("+")
                        .withStyle(ChatFormatting.GREEN)
                        .append(Component.translatable("cobblemon.stat." + boost)));
                list.add(Component.literal("-")
                        .withStyle(ChatFormatting.RED)
                        .append(Component.translatable("cobblemon.stat." + penalty)));
            } else {
                list.add(Component.translatable("cobblemon_quests_extended.nature.neutral")
                        .withStyle(ChatFormatting.GRAY));
            }
        }
    }
}
