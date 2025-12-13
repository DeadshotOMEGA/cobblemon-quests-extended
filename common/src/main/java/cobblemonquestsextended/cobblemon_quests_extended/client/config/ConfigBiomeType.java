package cobblemonquestsextended.cobblemon_quests_extended.client.config;

import cobblemonquestsextended.cobblemon_quests_extended.client.gui.selectors.SelectBiomeScreen;
import cobblemonquestsextended.cobblemon_quests_extended.registry.BiomeCategory;
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
 * Custom ConfigValue for selecting biomes.
 * Opens a SelectBiomeScreen when clicked, showing biomes grouped by category with appropriate colors.
 */
@Environment(EnvType.CLIENT)
public class ConfigBiomeType extends ConfigValue<String> {

    public ConfigBiomeType() {
        // Default constructor
    }

    @Override
    public Component getStringForGUI(@Nullable String value) {
        if (value == null || value.isEmpty()) {
            return NULL_TEXT;
        }

        // Format: "minecraft:plains" -> "Plains"
        // Format: "terralith:volcanic_peaks" -> "Volcanic Peaks (terralith)"
        ResourceLocation loc = ResourceLocation.tryParse(value);
        if (loc == null) {
            return Component.literal(value);
        }

        String path = loc.getPath();
        // Convert snake_case to Title Case
        String formatted = formatBiomeName(path);

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
            return Color4I.rgb(0x808080);
        }
        ResourceLocation loc = ResourceLocation.tryParse(value);
        if (loc == null) {
            return Color4I.rgb(0x808080);
        }
        BiomeCategory category = BiomeCategory.categorize(loc);
        return category.getColor();
    }

    @Override
    public void onClicked(Widget clickedWidget, MouseButton button, ConfigCallback callback) {
        if (getCanEdit()) {
            new SelectBiomeScreen(this, callback).openGui();
        }
    }

    @Override
    public void addInfo(TooltipList list) {
        if (value != null && !value.isEmpty()) {
            ResourceLocation loc = ResourceLocation.tryParse(value);
            if (loc != null) {
                BiomeCategory category = BiomeCategory.categorize(loc);
                list.add(Component.translatable("cobblemon_quests_extended.config.biome_category")
                        .withStyle(ChatFormatting.GRAY)
                        .append(": ")
                        .append(Component.translatable(category.getTranslationKey())
                                .withStyle(getCategoryFormatting(category))));
            }
        }
    }

    /**
     * Converts snake_case to Title Case.
     *
     * @param path the biome path
     * @return formatted name
     */
    private static String formatBiomeName(String path) {
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

    /**
     * Gets the ChatFormatting for a biome category.
     *
     * @param category the biome category
     * @return the corresponding chat formatting
     */
    public static ChatFormatting getCategoryFormatting(BiomeCategory category) {
        return switch (category) {
            case FOREST -> ChatFormatting.DARK_GREEN;
            case PLAINS -> ChatFormatting.GREEN;
            case DESERT -> ChatFormatting.YELLOW;
            case OCEAN -> ChatFormatting.BLUE;
            case MOUNTAIN -> ChatFormatting.WHITE;
            case CAVE -> ChatFormatting.DARK_GRAY;
            case JUNGLE -> ChatFormatting.GREEN;
            case SWAMP -> ChatFormatting.DARK_GREEN;
            case TUNDRA -> ChatFormatting.AQUA;
            case NETHER -> ChatFormatting.RED;
            case END -> ChatFormatting.DARK_PURPLE;
            case OTHER -> ChatFormatting.GRAY;
        };
    }
}
