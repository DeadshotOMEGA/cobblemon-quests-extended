package cobblemonquestsextended.cobblemon_quests_extended.client.gui.selectors;

import cobblemonquestsextended.cobblemon_quests_extended.client.config.ConfigNatureType;
import cobblemonquestsextended.cobblemon_quests_extended.registry.NatureCategory;
import dev.ftb.mods.ftblibrary.config.ConfigCallback;
import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Selector screen for Pokemon natures (25 natures).
 * Groups natures by stat boost category.
 */
@Environment(EnvType.CLIENT)
public class SelectNatureScreen extends AbstractSelectorScreen<String, NatureCategory> {

    private final ConfigNatureType config;
    private final ConfigCallback callback;

    private static final List<String> ALL_NATURES = List.of(
            "hardy", "lonely", "brave", "adamant", "naughty",
            "bold", "docile", "relaxed", "impish", "lax",
            "timid", "hasty", "serious", "jolly", "naive",
            "modest", "mild", "quiet", "bashful", "rash",
            "calm", "gentle", "sassy", "careful", "quirky"
    );

    public SelectNatureScreen(ConfigNatureType config, ConfigCallback callback) {
        super(
                NatureCategory.class,
                Component.translatable("cobblemon_quests_extended.gui.select_nature"),
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
    protected Map<NatureCategory, List<String>> getGroupedItems() {
        Map<NatureCategory, List<String>> result = new EnumMap<>(NatureCategory.class);
        Map<NatureCategory, List<String>> grouped = ALL_NATURES.stream()
                .collect(Collectors.groupingBy(NatureCategory::fromNature));
        result.putAll(grouped);
        return result;
    }

    @Override
    protected Component getItemDisplayName(String item) {
        return Component.translatable("cobblemon.nature." + item);
    }

    @Override
    protected Color4I getCategoryColor(NatureCategory category) {
        return category.getColor();
    }

    @Override
    protected String getCategoryTranslationKey(NatureCategory category) {
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
    protected void addItemTooltip(TooltipList list, String item, NatureCategory category) {
        super.addItemTooltip(list, item, category);
        String boost = category.getStatBoost();
        String penalty = category.getStatPenalty();
        if (boost != null && penalty != null) {
            list.add(Component.literal("+10% ")
                    .withStyle(ChatFormatting.GREEN)
                    .append(Component.translatable("cobblemon.stat." + boost)));
            list.add(Component.literal("-10% ")
                    .withStyle(ChatFormatting.RED)
                    .append(Component.translatable("cobblemon.stat." + penalty)));
        }
    }

    @Override
    protected ChatFormatting getCategoryFormatting(NatureCategory category) {
        return switch (category) {
            case NEUTRAL -> ChatFormatting.GRAY;
            case ATTACK_UP -> ChatFormatting.RED;
            case DEFENSE_UP -> ChatFormatting.BLUE;
            case SP_ATK_UP -> ChatFormatting.LIGHT_PURPLE;
            case SP_DEF_UP -> ChatFormatting.GREEN;
            case SPEED_UP -> ChatFormatting.YELLOW;
        };
    }
}
