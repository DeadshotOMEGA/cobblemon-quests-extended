package cobblemonquestsextended.cobblemon_quests_extended.client.gui.selectors;

import cobblemonquestsextended.cobblemon_quests_extended.client.config.ConfigBiomeType;
import cobblemonquestsextended.cobblemon_quests_extended.registry.BiomeCategory;
import dev.ftb.mods.ftblibrary.config.ConfigCallback;
import dev.ftb.mods.ftblibrary.icon.Color4I;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Selector screen for biomes.
 * Groups biomes by category (Forest, Plains, Desert, etc.).
 */
@Environment(EnvType.CLIENT)
public class SelectBiomeScreen extends AbstractSelectorScreen<String, BiomeCategory> {

    private final ConfigBiomeType config;
    private final ConfigCallback callback;
    private Map<BiomeCategory, List<String>> cachedGroupedItems;

    public SelectBiomeScreen(ConfigBiomeType config, ConfigCallback callback) {
        super(
                BiomeCategory.class,
                Component.translatable("cobblemon_quests_extended.gui.select_biome"),
                false,
                config.getValue() != null ? List.of(config.getValue()) : List.of(),
                items -> {
                    if (!items.isEmpty()) {
                        config.setCurrentValue(items.get(0));
                        callback.save(true);
                    } else {
                        callback.save(false);
                    }
                }
        );
        this.config = config;
        this.callback = callback;
    }

    @Override
    protected Map<BiomeCategory, List<String>> getGroupedItems() {
        if (cachedGroupedItems == null) {
            cachedGroupedItems = buildGroupedItems();
        }
        return cachedGroupedItems;
    }

    private Map<BiomeCategory, List<String>> buildGroupedItems() {
        Map<BiomeCategory, List<String>> result = new EnumMap<>(BiomeCategory.class);
        for (BiomeCategory category : BiomeCategory.values()) {
            result.put(category, new ArrayList<>());
        }

        // Try to get biomes from the client's registry
        try {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level != null) {
                mc.level.registryAccess().registryOrThrow(Registries.BIOME).keySet().forEach(loc -> {
                    String biomeId = loc.toString();
                    BiomeCategory category = BiomeCategory.categorize(loc);
                    result.get(category).add(biomeId);
                });
            }
        } catch (Exception e) {
            // Fallback: add some common biomes
            addFallbackBiomes(result);
        }

        // Remove empty categories
        result.entrySet().removeIf(entry -> entry.getValue().isEmpty());

        return result;
    }

    private void addFallbackBiomes(Map<BiomeCategory, List<String>> result) {
        // Common vanilla biomes as fallback
        result.get(BiomeCategory.PLAINS).addAll(List.of("minecraft:plains", "minecraft:sunflower_plains"));
        result.get(BiomeCategory.FOREST).addAll(List.of("minecraft:forest", "minecraft:birch_forest", "minecraft:dark_forest"));
        result.get(BiomeCategory.DESERT).addAll(List.of("minecraft:desert", "minecraft:badlands"));
        result.get(BiomeCategory.OCEAN).addAll(List.of("minecraft:ocean", "minecraft:deep_ocean", "minecraft:warm_ocean"));
        result.get(BiomeCategory.MOUNTAIN).addAll(List.of("minecraft:mountains", "minecraft:snowy_mountains"));
        result.get(BiomeCategory.JUNGLE).addAll(List.of("minecraft:jungle", "minecraft:bamboo_jungle"));
        result.get(BiomeCategory.SWAMP).addAll(List.of("minecraft:swamp", "minecraft:mangrove_swamp"));
        result.get(BiomeCategory.TUNDRA).addAll(List.of("minecraft:snowy_plains", "minecraft:ice_spikes"));
        result.get(BiomeCategory.CAVE).addAll(List.of("minecraft:dripstone_caves", "minecraft:lush_caves"));
        result.get(BiomeCategory.NETHER).addAll(List.of("minecraft:nether_wastes", "minecraft:crimson_forest", "minecraft:warped_forest"));
        result.get(BiomeCategory.END).addAll(List.of("minecraft:the_end", "minecraft:end_highlands"));
    }

    @Override
    protected Component getItemDisplayName(String item) {
        ResourceLocation loc = ResourceLocation.tryParse(item);
        if (loc == null) {
            return Component.literal(item);
        }
        String path = loc.getPath();
        String formatted = formatBiomeName(path);
        if (loc.getNamespace().equals("minecraft")) {
            return Component.literal(formatted);
        }
        return Component.literal(formatted)
                .append(Component.literal(" (" + loc.getNamespace() + ")").withStyle(ChatFormatting.GRAY));
    }

    private String formatBiomeName(String path) {
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

    @Override
    protected Color4I getCategoryColor(BiomeCategory category) {
        return category.getColor();
    }

    @Override
    protected String getCategoryTranslationKey(BiomeCategory category) {
        return category.getTranslationKey();
    }

    @Override
    protected void onItemSelected(String item) {
        config.setCurrentValue(item);
        callback.save(true);
    }

    @Override
    protected boolean isItemSelected(String item) {
        return item.equals(config.getValue());
    }

    @Override
    protected ChatFormatting getCategoryFormatting(BiomeCategory category) {
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
