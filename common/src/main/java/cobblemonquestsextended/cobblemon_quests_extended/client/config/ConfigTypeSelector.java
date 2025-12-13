package cobblemonquestsextended.cobblemon_quests_extended.client.config;

import cobblemonquestsextended.cobblemon_quests_extended.client.gui.selectors.SelectTypeScreen;
import cobblemonquestsextended.cobblemon_quests_extended.registry.PokemonTypeCategory;
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
 * Custom ConfigValue for selecting Pokemon types.
 * Displays 18 Pokemon types with type-specific colors and categorization.
 */
@Environment(EnvType.CLIENT)
public class ConfigTypeSelector extends ConfigValue<String> {

    /**
     * Type color map for the 18 Pokemon types.
     */
    private static final Map<String, Integer> TYPE_COLORS = Map.ofEntries(
        Map.entry("normal", 0xA8A878),
        Map.entry("fire", 0xF08030),
        Map.entry("water", 0x6890F0),
        Map.entry("grass", 0x78C850),
        Map.entry("electric", 0xF8D030),
        Map.entry("ice", 0x98D8D8),
        Map.entry("fighting", 0xC03028),
        Map.entry("poison", 0xA040A0),
        Map.entry("ground", 0xE0C068),
        Map.entry("flying", 0xA890F0),
        Map.entry("psychic", 0xF85888),
        Map.entry("bug", 0xA8B820),
        Map.entry("rock", 0xB8A038),
        Map.entry("ghost", 0x705898),
        Map.entry("dragon", 0x7038F8),
        Map.entry("dark", 0x705848),
        Map.entry("steel", 0xB8B8D0),
        Map.entry("fairy", 0xEE99AC)
    );

    @Override
    public Component getStringForGUI(@Nullable String value) {
        if (value == null || value.isEmpty()) {
            return NULL_TEXT;
        }
        return Component.translatable("cobblemon.type." + value);
    }

    @Override
    public Color4I getColor(@Nullable String value) {
        if (value == null) {
            return Color4I.rgb(0x808080);
        }
        return Color4I.rgb(TYPE_COLORS.getOrDefault(value.toLowerCase(), 0x808080));
    }

    @Override
    public void onClicked(Widget clickedWidget, MouseButton button, ConfigCallback callback) {
        if (getCanEdit()) {
            new SelectTypeScreen(this, callback).openGui();
        }
    }

    @Override
    public void addInfo(TooltipList list) {
        if (value != null && !value.isEmpty()) {
            PokemonTypeCategory category = PokemonTypeCategory.fromType(value);
            if (category != null) {
                list.add(Component.translatable("cobblemon_quests_extended.category.type_category")
                    .withStyle(ChatFormatting.GRAY)
                    .append(": ")
                    .append(Component.translatable(category.getTranslationKey())));
            }
        }
    }
}
