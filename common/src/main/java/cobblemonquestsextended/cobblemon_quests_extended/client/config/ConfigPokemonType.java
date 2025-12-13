package cobblemonquestsextended.cobblemon_quests_extended.client.config;

import cobblemonquestsextended.cobblemon_quests_extended.client.gui.selectors.SelectPokemonScreen;
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
 * Custom ConfigValue for selecting Pokemon species.
 * Opens a SelectPokemonScreen when clicked, showing Pokemon with types and translated names.
 */
@Environment(EnvType.CLIENT)
public class ConfigPokemonType extends ConfigValue<String> {

    /**
     * Default color used when no Pokemon type is available.
     */
    public static final Color4I DEFAULT_COLOR = Color4I.rgb(0x5DADE2);

    /**
     * Type colors for displaying Pokemon by their primary type.
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

    public ConfigPokemonType() {
        // Default constructor
    }

    @Override
    public Component getStringForGUI(@Nullable String value) {
        if (value == null || value.isEmpty()) {
            return NULL_TEXT;
        }

        // Try to get translated species name from Cobblemon
        // Use ResourceLocation format: cobblemon:pikachu -> pikachu
        String speciesId = value.contains(":") ? value.split(":")[1] : value;
        return Component.translatable("cobblemon.species." + speciesId + ".name");
    }

    @Override
    public Color4I getColor(@Nullable String value) {
        if (value == null || value.isEmpty()) {
            return Color4I.rgb(0x808080);
        }

        // Default color - actual Cobblemon integration would look up species primary type
        // This will be enhanced when we integrate with Cobblemon's species data
        return DEFAULT_COLOR;
    }

    @Override
    public void onClicked(Widget clickedWidget, MouseButton button, ConfigCallback callback) {
        if (getCanEdit()) {
            new SelectPokemonScreen(this, callback).openGui();
        }
    }

    @Override
    public void addInfo(TooltipList list) {
        if (value != null && !value.isEmpty()) {
            // Show Pokemon ID
            list.add(Component.translatable("cobblemon_quests_extended.config.pokemon_id")
                    .withStyle(ChatFormatting.GRAY)
                    .append(": ")
                    .append(Component.literal(value).withStyle(ChatFormatting.WHITE)));
        }
    }

    /**
     * Gets the color for a Pokemon type.
     *
     * @param type the type name (lowercase)
     * @return the color as an integer
     */
    public static int getTypeColor(String type) {
        return TYPE_COLORS.getOrDefault(type.toLowerCase(), 0x808080);
    }

    /**
     * Gets the Color4I for a Pokemon type.
     *
     * @param type the type name (lowercase)
     * @return the Color4I for the type
     */
    public static Color4I getTypeColor4I(String type) {
        return Color4I.rgb(getTypeColor(type));
    }
}
