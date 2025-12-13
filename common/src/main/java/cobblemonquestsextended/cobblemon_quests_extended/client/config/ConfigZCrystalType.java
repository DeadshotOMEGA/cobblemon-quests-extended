package cobblemonquestsextended.cobblemon_quests_extended.client.config;

import cobblemonquestsextended.cobblemon_quests_extended.client.gui.selectors.SelectZCrystalScreen;
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
 * Custom ConfigValue for selecting Z-Crystals.
 * Opens a SelectZCrystalScreen when clicked, showing all available Z-Crystals
 * with colors matching their associated Pokemon type.
 */
@Environment(EnvType.CLIENT)
public class ConfigZCrystalType extends ConfigValue<String> {

    /**
     * Default color used when no Z-Crystal is selected.
     */
    public static final Color4I DEFAULT_COLOR = Color4I.rgb(0x808080);

    /**
     * Z-Crystal colors mapped from their associated Pokemon type.
     * Type-specific Z-Crystals use the type color.
     * Pokemon-specific Z-Crystals use the signature Pokemon's primary type color.
     */
    private static final Map<String, Integer> ZCRYSTAL_COLORS = Map.ofEntries(
            // Type-specific Z-Crystals (use type colors)
            Map.entry("normalium-z", 0xA8A878),
            Map.entry("firium-z", 0xF08030),
            Map.entry("waterium-z", 0x6890F0),
            Map.entry("grassium-z", 0x78C850),
            Map.entry("electrium-z", 0xF8D030),
            Map.entry("icium-z", 0x98D8D8),
            Map.entry("fightinium-z", 0xC03028),
            Map.entry("poisonium-z", 0xA040A0),
            Map.entry("groundium-z", 0xE0C068),
            Map.entry("flyinium-z", 0xA890F0),
            Map.entry("psychium-z", 0xF85888),
            Map.entry("buginium-z", 0xA8B820),
            Map.entry("rockium-z", 0xB8A038),
            Map.entry("ghostium-z", 0x705898),
            Map.entry("dragonium-z", 0x7038F8),
            Map.entry("darkinium-z", 0x705848),
            Map.entry("steelium-z", 0xB8B8D0),
            Map.entry("fairium-z", 0xEE99AC),
            // Pokemon-specific Z-Crystals (use signature Pokemon's type color)
            Map.entry("aloraichium-z", 0xF85888),   // Alolan Raichu - Psychic
            Map.entry("decidium-z", 0x78C850),      // Decidueye - Grass
            Map.entry("eevium-z", 0xA8A878),        // Eevee - Normal
            Map.entry("incinium-z", 0xF08030),     // Incineroar - Fire
            Map.entry("kommonium-z", 0x7038F8),    // Kommo-o - Dragon
            Map.entry("lunalium-z", 0xF85888),     // Lunala - Psychic
            Map.entry("lycanium-z", 0xB8A038),     // Lycanroc - Rock
            Map.entry("marshadium-z", 0xC03028),   // Marshadow - Fighting
            Map.entry("mewnium-z", 0xF85888),      // Mew - Psychic
            Map.entry("mimikium-z", 0x705898),     // Mimikyu - Ghost
            Map.entry("pikanium-z", 0xF8D030),     // Pikachu - Electric
            Map.entry("pikashunium-z", 0xF8D030),  // Ash-Pikachu - Electric
            Map.entry("primarium-z", 0x6890F0),    // Primarina - Water
            Map.entry("snorlium-z", 0xA8A878),     // Snorlax - Normal
            Map.entry("solganium-z", 0xF85888),    // Solgaleo - Psychic
            Map.entry("tapunium-z", 0xEE99AC),     // Tapu - Fairy
            Map.entry("ultranecrozium-z", 0xF85888) // Necrozma - Psychic
    );

    public ConfigZCrystalType() {
        // Default constructor
    }

    @Override
    public Component getStringForGUI(@Nullable String value) {
        if (value == null || value.isEmpty()) {
            return NULL_TEXT;
        }
        return Component.translatable("cobblemon_quests_extended.z_crystal." + value);
    }

    @Override
    public Color4I getColor(@Nullable String value) {
        if (value == null || value.isEmpty()) {
            return DEFAULT_COLOR;
        }
        return Color4I.rgb(ZCRYSTAL_COLORS.getOrDefault(value.toLowerCase(), 0x808080));
    }

    @Override
    public void onClicked(Widget clickedWidget, MouseButton button, ConfigCallback callback) {
        if (getCanEdit()) {
            new SelectZCrystalScreen(this, callback).openGui();
        }
    }

    @Override
    public void addInfo(TooltipList list) {
        if (value != null && !value.isEmpty()) {
            list.add(Component.translatable("cobblemon_quests_extended.z_crystal.desc")
                    .withStyle(ChatFormatting.GRAY));

            // Check if Pokemon-specific Z-Crystal
            if (isPokemonSpecific(value)) {
                list.add(Component.translatable("cobblemon_quests_extended.z_crystal.pokemon_specific")
                        .withStyle(ChatFormatting.YELLOW));
            }
        }
    }

    /**
     * Gets the color for a specific Z-Crystal.
     *
     * @param zCrystal the Z-Crystal name
     * @return the RGB color value, or gray if not found
     */
    public static int getZCrystalColorValue(String zCrystal) {
        return ZCRYSTAL_COLORS.getOrDefault(zCrystal.toLowerCase(), 0x808080);
    }

    /**
     * Checks if a Z-Crystal is Pokemon-specific.
     *
     * @param zCrystal the Z-Crystal name
     * @return true if Pokemon-specific
     */
    public static boolean isPokemonSpecific(String zCrystal) {
        if (zCrystal == null) return false;
        String lower = zCrystal.toLowerCase();
        return lower.contains("aloraichium") || lower.contains("decidium") ||
               lower.contains("eevium") || lower.contains("incinium") ||
               lower.contains("kommonium") || lower.contains("lunalium") ||
               lower.contains("lycanium") || lower.contains("marshadium") ||
               lower.contains("mewnium") || lower.contains("mimikium") ||
               lower.contains("pikanium") || lower.contains("pikashunium") ||
               lower.contains("primarium") || lower.contains("snorlium") ||
               lower.contains("solganium") || lower.contains("tapunium") ||
               lower.contains("ultranecrozium");
    }
}
