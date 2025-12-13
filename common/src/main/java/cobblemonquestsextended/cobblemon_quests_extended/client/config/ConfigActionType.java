package cobblemonquestsextended.cobblemon_quests_extended.client.config;

import cobblemonquestsextended.cobblemon_quests_extended.client.gui.SelectActionScreen;
import cobblemonquestsextended.cobblemon_quests_extended.registry.ActionDefinition;
import cobblemonquestsextended.cobblemon_quests_extended.registry.ActionRegistry;
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
 * Custom ConfigValue for selecting action types from the ActionRegistry.
 * Opens a SelectActionScreen when clicked, showing categorized actions with rich tooltips.
 */
@Environment(EnvType.CLIENT)
public class ConfigActionType extends ConfigValue<String> {

    /**
     * Color used for displaying action type values in the config UI.
     */
    public static final Color4I COLOR = Color4I.rgb(0x5DADE2);

    public ConfigActionType() {
        // Default constructor - actions are pulled from ActionRegistry
    }

    @Override
    public Component getStringForGUI(@Nullable String value) {
        if (value == null || value.isEmpty()) {
            return NULL_TEXT;
        }

        // Get the action definition to show translated name
        return ActionRegistry.getAction(value)
                .map(def -> Component.translatable(def.translationKey()))
                .orElse(Component.literal(value));
    }

    @Override
    public Color4I getColor(@Nullable String value) {
        if (value == null || value.isEmpty()) {
            return COLOR;
        }

        // Use category-specific color if available
        return ActionRegistry.getAction(value)
                .map(def -> getCategoryColor(def.category()))
                .orElse(COLOR);
    }

    @Override
    public void onClicked(Widget clickedWidget, MouseButton button, ConfigCallback callback) {
        if (getCanEdit()) {
            new SelectActionScreen(this, callback).openGui();
        }
    }

    @Override
    public void addInfo(TooltipList list) {
        if (value != null && !value.isEmpty()) {
            ActionRegistry.getAction(value).ifPresent(def -> {
                // Show category
                list.add(Component.translatable("cobblemon_quests_extended.config.category")
                        .withStyle(ChatFormatting.GRAY)
                        .append(": ")
                        .append(Component.translatable(def.category().getTranslationKey())
                                .withStyle(getCategoryFormatting(def.category()))));

                // Show requires Pokemon
                list.add(Component.translatable("cobblemon_quests_extended.config.requires_pokemon")
                        .withStyle(ChatFormatting.GRAY)
                        .append(": ")
                        .append(def.requiresPokemon()
                                ? Component.translatable("gui.yes").withStyle(ChatFormatting.GREEN)
                                : Component.translatable("gui.no").withStyle(ChatFormatting.RED)));
            });
        }
    }

    /**
     * Gets the Color4I for a given action category.
     *
     * @param category the action category
     * @return the corresponding color
     */
    public static Color4I getCategoryColor(cobblemonquestsextended.cobblemon_quests_extended.registry.ActionCategory category) {
        return switch (category) {
            case CATCH -> Color4I.rgb(0x2ECC71);      // Green - obtaining Pokemon
            case BATTLE -> Color4I.rgb(0xE74C3C);     // Red - combat
            case EVOLUTION -> Color4I.rgb(0x9B59B6); // Purple - evolution
            case TRADE -> Color4I.rgb(0xF39C12);      // Orange - trading
            case POKEDEX -> Color4I.rgb(0x3498DB);    // Blue - pokedex
            case GIMMICK -> Color4I.rgb(0xE91E63);    // Pink - mega/tera/z-moves
            case OTHER -> Color4I.rgb(0x95A5A6);      // Gray - miscellaneous
        };
    }

    /**
     * Gets the ChatFormatting for a given action category.
     *
     * @param category the action category
     * @return the corresponding chat formatting
     */
    public static ChatFormatting getCategoryFormatting(cobblemonquestsextended.cobblemon_quests_extended.registry.ActionCategory category) {
        return switch (category) {
            case CATCH -> ChatFormatting.GREEN;
            case BATTLE -> ChatFormatting.RED;
            case EVOLUTION -> ChatFormatting.LIGHT_PURPLE;
            case TRADE -> ChatFormatting.GOLD;
            case POKEDEX -> ChatFormatting.AQUA;
            case GIMMICK -> ChatFormatting.LIGHT_PURPLE;
            case OTHER -> ChatFormatting.GRAY;
        };
    }
}
