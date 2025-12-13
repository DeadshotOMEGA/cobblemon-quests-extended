package cobblemonquestsextended.cobblemon_quests_extended.client.config;

import cobblemonquestsextended.cobblemon_quests_extended.client.gui.selectors.SelectTeraTypeScreen;
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
 * Custom ConfigValue for selecting Tera types.
 * Opens a SelectTeraTypeScreen when clicked, showing all available Tera types
 * with colors matching their Pokemon type.
 */
@Environment(EnvType.CLIENT)
public class ConfigTeraType extends ConfigValue<String> {

    /**
     * Default color used when no Tera type is selected.
     */
    public static final Color4I DEFAULT_COLOR = Color4I.rgb(0x808080);

    /**
     * Type colors matching Pokemon type aesthetics.
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
            Map.entry("fairy", 0xEE99AC),
            Map.entry("stellar", 0xFFFFFF)  // White for stellar type
    );

    public ConfigTeraType() {
        // Default constructor
    }

    @Override
    public Component getStringForGUI(@Nullable String value) {
        if (value == null || value.isEmpty()) {
            return NULL_TEXT;
        }
        // Use Cobblemon's type translation, or custom for stellar
        if (value.equalsIgnoreCase("stellar")) {
            return Component.translatable("cobblemon_quests_extended.tera_type.stellar");
        }
        return Component.translatable("cobblemon.type." + value.toLowerCase());
    }

    @Override
    public Color4I getColor(@Nullable String value) {
        if (value == null || value.isEmpty()) {
            return DEFAULT_COLOR;
        }
        return Color4I.rgb(TYPE_COLORS.getOrDefault(value.toLowerCase(), 0x808080));
    }

    @Override
    public void onClicked(Widget clickedWidget, MouseButton button, ConfigCallback callback) {
        if (getCanEdit()) {
            new SelectTeraTypeScreen(this, callback).openGui();
        }
    }

    @Override
    public void addInfo(TooltipList list) {
        if (value != null && !value.isEmpty()) {
            // Show tera type description
            list.add(Component.translatable("cobblemon_quests_extended.tera_type.desc")
                    .withStyle(ChatFormatting.GRAY));

            // Show if stellar (special type)
            if (value.equalsIgnoreCase("stellar")) {
                list.add(Component.translatable("cobblemon_quests_extended.tera_type.stellar.desc")
                        .withStyle(ChatFormatting.LIGHT_PURPLE));
            }
        }
    }

    /**
     * Gets the color for a specific Tera type.
     *
     * @param teraType the Tera type name
     * @return the RGB color value, or gray if not found
     */
    public static int getTypeColorValue(String teraType) {
        return TYPE_COLORS.getOrDefault(teraType.toLowerCase(), 0x808080);
    }

    /**
     * Checks if a Tera type is the special stellar type.
     *
     * @param teraType the Tera type name
     * @return true if stellar
     */
    public static boolean isStellar(String teraType) {
        return teraType != null && teraType.equalsIgnoreCase("stellar");
    }
}
