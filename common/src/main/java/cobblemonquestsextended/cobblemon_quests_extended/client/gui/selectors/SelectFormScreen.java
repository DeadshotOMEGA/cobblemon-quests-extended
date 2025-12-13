package cobblemonquestsextended.cobblemon_quests_extended.client.gui.selectors;

import cobblemonquestsextended.cobblemon_quests_extended.client.config.ConfigFormType;
import cobblemonquestsextended.cobblemon_quests_extended.registry.FormCategory;
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
 * Selector screen for Pokemon forms/aspects.
 * Groups forms by category (regional, mega, dynamax, legendary, other).
 */
@Environment(EnvType.CLIENT)
public class SelectFormScreen extends AbstractSelectorScreen<String, FormCategory> {

    private final ConfigFormType config;
    private final ConfigCallback callback;

    private static final List<String> ALL_FORMS = List.of(
            // Regional forms
            "alolan", "galarian", "hisuian", "paldean",
            // Mega forms
            "mega", "mega_x", "mega_y", "primal",
            // Dynamax forms
            "gmax", "gigantamax",
            // Legendary forms
            "origin", "altered", "therian", "incarnate", "crowned", "unbound",
            // Other forms
            "zen", "pirouette", "resolute", "white", "black", "dusk", "midnight", "dawn"
    );

    public SelectFormScreen(ConfigFormType config, ConfigCallback callback) {
        super(
                FormCategory.class,
                Component.translatable("cobblemon_quests_extended.gui.select_form"),
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
    protected Map<FormCategory, List<String>> getGroupedItems() {
        Map<FormCategory, List<String>> result = new EnumMap<>(FormCategory.class);
        Map<FormCategory, List<String>> grouped = ALL_FORMS.stream()
                .collect(Collectors.groupingBy(FormCategory::fromForm));
        result.putAll(grouped);
        return result;
    }

    @Override
    protected Component getItemDisplayName(String item) {
        return Component.translatable("cobblemon_quests_extended.form." + item.toLowerCase());
    }

    @Override
    protected Color4I getCategoryColor(FormCategory category) {
        return category.getColor4I();
    }

    @Override
    protected String getCategoryTranslationKey(FormCategory category) {
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
    protected ChatFormatting getCategoryFormatting(FormCategory category) {
        return switch (category) {
            case REGIONAL -> ChatFormatting.GOLD;
            case MEGA -> ChatFormatting.LIGHT_PURPLE;
            case DYNAMAX -> ChatFormatting.RED;
            case LEGENDARY -> ChatFormatting.DARK_PURPLE;
            case OTHER -> ChatFormatting.GRAY;
        };
    }
}
