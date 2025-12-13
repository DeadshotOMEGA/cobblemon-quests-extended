package cobblemonquestsextended.cobblemon_quests_extended.client.config;

import cobblemonquestsextended.cobblemon_quests_extended.client.gui.selectors.SelectDimensionScreen;
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
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/**
 * Custom ConfigValue for selecting dimensions (overworld, nether, end, etc.).
 * Opens a SelectDimensionScreen when clicked, showing available dimensions with appropriate colors.
 */
@Environment(EnvType.CLIENT)
public class ConfigDimensionType extends ConfigValue<String> {

    /**
     * Standard dimension colors
     */
    private static final int OVERWORLD_COLOR = 0x7CB342;  // Green
    private static final int NETHER_COLOR = 0xE74C3C;     // Red
    private static final int END_COLOR = 0x9B59B6;        // Purple
    private static final int MODDED_COLOR = 0x3498DB;     // Blue (for unknown/modded)

    public ConfigDimensionType() {
        // Default constructor
    }

    @Override
    public Component getStringForGUI(@Nullable String value) {
        if (value == null || value.isEmpty()) {
            return NULL_TEXT;
        }

        // Format: "minecraft:overworld" -> "Overworld"
        // Format: "aether:the_aether" -> "The Aether (aether)"
        ResourceLocation loc = ResourceLocation.tryParse(value);
        if (loc == null) {
            return Component.literal(value);
        }

        String path = loc.getPath();
        String formatted = formatDimensionName(path);

        if (loc.getNamespace().equals("minecraft")) {
            return Component.literal(formatted);
        } else {
            return Component.literal(formatted)
                    .append(Component.literal(" (" + loc.getNamespace() + ")")
                            .withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public Color4I getColor(@Nullable String value) {
        if (value == null || value.isEmpty()) {
            return Color4I.rgb(OVERWORLD_COLOR);
        }
        ResourceLocation loc = ResourceLocation.tryParse(value);
        if (loc == null) {
            return Color4I.rgb(MODDED_COLOR);
        }

        String path = loc.getPath();
        if (path.contains("nether")) {
            return Color4I.rgb(NETHER_COLOR);
        } else if (path.contains("end") && !path.contains("legend")) {
            return Color4I.rgb(END_COLOR);
        } else if (path.equals("overworld")) {
            return Color4I.rgb(OVERWORLD_COLOR);
        }
        return Color4I.rgb(MODDED_COLOR);
    }

    @Override
    public void onClicked(Widget clickedWidget, MouseButton button, ConfigCallback callback) {
        if (getCanEdit()) {
            new SelectDimensionScreen(this, callback).openGui();
        }
    }

    @Override
    public void addInfo(TooltipList list) {
        if (value != null && !value.isEmpty()) {
            ResourceLocation loc = ResourceLocation.tryParse(value);
            if (loc != null) {
                list.add(Component.translatable("cobblemon_quests_extended.config.dimension_id")
                        .withStyle(ChatFormatting.GRAY)
                        .append(": ")
                        .append(Component.literal(loc.toString()).withStyle(ChatFormatting.WHITE)));
            }
        }
    }

    /**
     * Formats dimension path to display name.
     *
     * @param path the dimension path
     * @return formatted display name
     */
    private static String formatDimensionName(String path) {
        // Handle common cases
        if (path.equals("overworld")) return "Overworld";
        if (path.equals("the_nether")) return "The Nether";
        if (path.equals("the_end")) return "The End";

        // Convert snake_case to Title Case
        StringBuilder result = new StringBuilder();
        String[] parts = path.split("_");
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) result.append(" ");
            String part = parts[i];
            if (!part.isEmpty()) {
                result.append(Character.toUpperCase(part.charAt(0)));
                if (part.length() > 1) {
                    result.append(part.substring(1).toLowerCase());
                }
            }
        }
        return result.toString();
    }
}
