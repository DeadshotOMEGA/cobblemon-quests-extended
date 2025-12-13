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

        try {
            // Try Cobblemon's translation key first
            String translationKey = "cobblemon.species." + speciesId + ".name";
            if (net.minecraft.client.resources.language.I18n.exists(translationKey)) {
                return Component.translatable(translationKey);
            }
        } catch (Exception e) {
            // Translation lookup failed, use fallback
        }

        // Fall back to nicely formatted name
        return Component.literal(formatPokemonName(speciesId));
    }

    /**
     * Formats a Pokemon ID into a readable display name.
     * Converts underscores/dashes to spaces and capitalizes words.
     */
    private String formatPokemonName(String name) {
        if (name == null || name.isEmpty()) {
            return "Unknown";
        }

        try {
            // Replace underscores and dashes with spaces, then capitalize each word
            String[] words = name.replace("_", " ").replace("-", " ").split(" ");
            StringBuilder result = new StringBuilder();
            for (String word : words) {
                if (word != null && !word.isEmpty()) {
                    if (result.length() > 0) {
                        result.append(" ");
                    }
                    result.append(Character.toUpperCase(word.charAt(0)));
                    if (word.length() > 1) {
                        result.append(word.substring(1).toLowerCase());
                    }
                }
            }
            return result.length() > 0 ? result.toString() : name;
        } catch (Exception e) {
            // Fallback to original name if formatting fails
            return name;
        }
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
