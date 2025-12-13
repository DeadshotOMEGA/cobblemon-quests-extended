package cobblemonquestsextended.cobblemon_quests_extended.client.gui.selectors;

import cobblemonquestsextended.cobblemon_quests_extended.client.config.ConfigRegionType;
import dev.ftb.mods.ftblibrary.config.ConfigCallback;
import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.SimpleTextButton;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.Widget;
import dev.ftb.mods.ftblibrary.ui.input.Key;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftblibrary.ui.misc.AbstractButtonListScreen;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Map;

/**
 * Simple selector screen for Pokemon regions (9 regions).
 * Displays as a flat list without categorization.
 */
@Environment(EnvType.CLIENT)
public class SelectRegionScreen extends AbstractButtonListScreen {

    private final ConfigRegionType config;
    private final ConfigCallback callback;

    private static final List<String> ALL_REGIONS = List.of(
            "kanto", "johto", "hoenn", "sinnoh", "unova",
            "kalos", "alola", "galar", "paldea"
    );

    private static final Map<String, Integer> REGION_COLORS = Map.of(
            "kanto", 0xE74C3C, "johto", 0xF1C40F, "hoenn", 0x27AE60,
            "sinnoh", 0x3498DB, "unova", 0x2C3E50, "kalos", 0x9B59B6,
            "alola", 0xF39C12, "galar", 0xE91E63, "paldea", 0x1ABC9C
    );

    private static final Map<String, Integer> REGION_GENERATIONS = Map.of(
            "kanto", 1, "johto", 2, "hoenn", 3, "sinnoh", 4,
            "unova", 5, "kalos", 6, "alola", 7, "galar", 8, "paldea", 9
    );

    public SelectRegionScreen(ConfigRegionType config, ConfigCallback callback) {
        this.config = config;
        this.callback = callback;
        setTitle(Component.translatable("cobblemon_quests_extended.gui.select_region"));
        setHasSearchBox(true);
        showBottomPanel(false);
        showCloseButton(true);
        setBorder(1, 1, 1);
    }

    @Override
    public boolean onClosedByKey(Key key) {
        if (super.onClosedByKey(key)) {
            callback.save(false);
            return true;
        }
        return false;
    }

    @Override
    public void addButtons(Panel panel) {
        for (String region : ALL_REGIONS) {
            panel.add(new RegionButton(panel, region));
        }
        int width = panel.getWidgets().stream()
                .map(Widget::getWidth)
                .max(Integer::compare)
                .orElse(200);
        panel.getWidgets().forEach(w -> w.setWidth(width));
    }

    @Override
    public boolean onInit() {
        setWidth(220);
        setHeight(getScreen().getGuiScaledHeight() * 3 / 5);
        return true;
    }

    @Override
    protected void doCancel() {
        callback.save(false);
    }

    @Override
    protected void doAccept() {
        callback.save(true);
    }

    private class RegionButton extends SimpleTextButton {
        private final String region;

        public RegionButton(Panel panel, String region) {
            super(panel, Component.translatable("cobblemon_quests_extended.region." + region), Color4I.empty());
            this.region = region;
            setHeight(14);
        }

        @Override
        public void onClicked(MouseButton button) {
            playClickSound();
            config.setCurrentValue(region);
            callback.save(true);
        }

        @Override
        public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
            Color4I.rgb(REGION_COLORS.getOrDefault(region, 0x808080)).withAlpha(60).draw(graphics, x, y, 3, h);
            if (region.equals(config.getValue())) {
                Color4I.GREEN.withAlpha(30).draw(graphics, x, y, w, h);
            }
            if (isMouseOver) {
                Color4I.WHITE.withAlpha(30).draw(graphics, x, y, w, h);
            }
            Color4I.GRAY.withAlpha(40).draw(graphics, x, y + h, w, 1);
        }

        @Override
        public void addMouseOverText(TooltipList list) {
            list.add(Component.translatable("cobblemon_quests_extended.region." + region)
                    .withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD));
            Integer gen = REGION_GENERATIONS.get(region);
            if (gen != null) {
                list.add(Component.translatable("cobblemon_quests_extended.config.generation")
                        .withStyle(ChatFormatting.GRAY)
                        .append(": ")
                        .append(Component.literal("Gen " + gen).withStyle(ChatFormatting.WHITE)));
            }
        }
    }
}
