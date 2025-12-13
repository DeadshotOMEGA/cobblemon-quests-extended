package cobblemonquestsextended.cobblemon_quests_extended.client.gui.selectors;

import cobblemonquestsextended.cobblemon_quests_extended.client.config.ConfigTypeSelector;
import cobblemonquestsextended.cobblemon_quests_extended.registry.PokemonTypeCategory;
import dev.ftb.mods.ftblibrary.config.ConfigCallback;
import dev.ftb.mods.ftblibrary.icon.Color4I;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Selector screen for Pokemon types (18 types).
 * Groups types by physical/special category.
 */
@Environment(EnvType.CLIENT)
public class SelectTypeScreen extends AbstractSelectorScreen<String, PokemonTypeCategory> {

    private final ConfigTypeSelector config;
    private final ConfigCallback callback;

    private static final List<String> ALL_TYPES = List.of(
            "normal", "fire", "water", "grass", "electric", "ice",
            "fighting", "poison", "ground", "flying", "psychic", "bug",
            "rock", "ghost", "dragon", "dark", "steel", "fairy"
    );

    public SelectTypeScreen(ConfigTypeSelector config, ConfigCallback callback) {
        super(
                PokemonTypeCategory.class,
                Component.translatable("cobblemon_quests_extended.gui.select_type"),
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
    protected Map<PokemonTypeCategory, List<String>> getGroupedItems() {
        Map<PokemonTypeCategory, List<String>> result = new EnumMap<>(PokemonTypeCategory.class);
        Map<PokemonTypeCategory, List<String>> grouped = ALL_TYPES.stream()
                .collect(Collectors.groupingBy(type -> {
                    PokemonTypeCategory cat = PokemonTypeCategory.fromType(type);
                    return cat != null ? cat : PokemonTypeCategory.PHYSICAL;
                }));
        result.putAll(grouped);
        return result;
    }

    @Override
    protected Component getItemDisplayName(String item) {
        return Component.translatable("cobblemon.type." + item);
    }

    @Override
    protected Color4I getCategoryColor(PokemonTypeCategory category) {
        return category.getColor();
    }

    @Override
    protected String getCategoryTranslationKey(PokemonTypeCategory category) {
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
    protected ChatFormatting getCategoryFormatting(PokemonTypeCategory category) {
        return category == PokemonTypeCategory.PHYSICAL ? ChatFormatting.RED : ChatFormatting.BLUE;
    }
}
