package cobblemonquestsextended.cobblemon_quests_extended.client.config;

import cobblemonquestsextended.cobblemon_quests_extended.client.gui.selectors.SelectRegionScreen;
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
 * Custom ConfigValue for selecting Pokemon regions.
 * Displays 9 regions with region-specific colors and generation information.
 */
@Environment(EnvType.CLIENT)
public class ConfigRegionType extends ConfigValue<String> {

    /**
     * Region color map for the 9 Pokemon regions.
     */
    private static final Map<String, Integer> REGION_COLORS = Map.ofEntries(
        Map.entry("kanto", 0xE74C3C),       // Red
        Map.entry("johto", 0xF1C40F),       // Gold
        Map.entry("hoenn", 0x27AE60),       // Emerald
        Map.entry("sinnoh", 0x3498DB),      // Diamond blue
        Map.entry("unova", 0x2C3E50),       // Dark gray
        Map.entry("kalos", 0x9B59B6),       // Purple
        Map.entry("alola", 0xF39C12),       // Orange
        Map.entry("galar", 0xE91E63),       // Magenta
        Map.entry("paldea", 0x1ABC9C)       // Teal
    );

    /**
     * Region to generation mapping.
     */
    private static final Map<String, Integer> REGION_GENERATIONS = Map.ofEntries(
        Map.entry("kanto", 1),
        Map.entry("johto", 2),
        Map.entry("hoenn", 3),
        Map.entry("sinnoh", 4),
        Map.entry("unova", 5),
        Map.entry("kalos", 6),
        Map.entry("alola", 7),
        Map.entry("galar", 8),
        Map.entry("paldea", 9)
    );

    @Override
    public Component getStringForGUI(@Nullable String value) {
        if (value == null || value.isEmpty()) {
            return NULL_TEXT;
        }
        return Component.translatable("cobblemon_quests_extended.region." + value);
    }

    @Override
    public Color4I getColor(@Nullable String value) {
        if (value == null) {
            return Color4I.rgb(0x808080);
        }
        return Color4I.rgb(REGION_COLORS.getOrDefault(value.toLowerCase(), 0x808080));
    }

    @Override
    public void onClicked(Widget clickedWidget, MouseButton button, ConfigCallback callback) {
        if (getCanEdit()) {
            new SelectRegionScreen(this, callback).openGui();
        }
    }

    @Override
    public void addInfo(TooltipList list) {
        if (value != null && !value.isEmpty()) {
            Integer gen = REGION_GENERATIONS.get(value.toLowerCase());
            if (gen != null) {
                list.add(Component.translatable("cobblemon_quests_extended.config.generation")
                    .withStyle(ChatFormatting.GRAY)
                    .append(": ")
                    .append(Component.literal("Gen " + gen).withStyle(ChatFormatting.WHITE)));
            }
        }
    }
}
