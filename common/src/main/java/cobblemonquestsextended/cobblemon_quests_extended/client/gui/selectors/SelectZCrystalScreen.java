package cobblemonquestsextended.cobblemon_quests_extended.client.gui.selectors;

import cobblemonquestsextended.cobblemon_quests_extended.client.config.ConfigZCrystalType;
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

/**
 * Selector screen for Z-Crystals (18 type-specific + 17 Pokemon-specific).
 */
@Environment(EnvType.CLIENT)
public class SelectZCrystalScreen extends AbstractButtonListScreen {

    private final ConfigZCrystalType config;
    private final ConfigCallback callback;

    private static final List<String> TYPE_ZCRYSTALS = List.of(
            "normalium-z", "firium-z", "waterium-z", "grassium-z", "electrium-z",
            "icium-z", "fightinium-z", "poisonium-z", "groundium-z", "flyinium-z",
            "psychium-z", "buginium-z", "rockium-z", "ghostium-z", "dragonium-z",
            "darkinium-z", "steelium-z", "fairium-z"
    );

    private static final List<String> POKEMON_ZCRYSTALS = List.of(
            "aloraichium-z", "decidium-z", "eevium-z", "incinium-z", "kommonium-z",
            "lunalium-z", "lycanium-z", "marshadium-z", "mewnium-z", "mimikium-z",
            "pikanium-z", "pikashunium-z", "primarium-z", "snorlium-z", "solganium-z",
            "tapunium-z", "ultranecrozium-z"
    );

    public SelectZCrystalScreen(ConfigZCrystalType config, ConfigCallback callback) {
        this.config = config;
        this.callback = callback;
        setTitle(Component.translatable("cobblemon_quests_extended.gui.select_z_crystal"));
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
        // Add section header for type-specific Z-Crystals
        panel.add(new SectionHeader(panel, "cobblemon_quests_extended.z_crystal.section.type"));
        for (String zCrystal : TYPE_ZCRYSTALS) {
            panel.add(new ZCrystalButton(panel, zCrystal, false));
        }

        // Add section header for Pokemon-specific Z-Crystals
        panel.add(new SectionHeader(panel, "cobblemon_quests_extended.z_crystal.section.pokemon"));
        for (String zCrystal : POKEMON_ZCRYSTALS) {
            panel.add(new ZCrystalButton(panel, zCrystal, true));
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
        setHeight(getScreen().getGuiScaledHeight() * 3 / 4);
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

    private class SectionHeader extends SimpleTextButton {
        public SectionHeader(Panel panel, String translationKey) {
            super(panel, Component.translatable(translationKey).withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD),
                    Color4I.empty());
            setHeight(16);
        }

        @Override
        public void onClicked(MouseButton button) {
            // Headers are not clickable
        }

        @Override
        public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
            Color4I.rgb(0x444444).withAlpha(80).draw(graphics, x, y, w, h);
            Color4I.GRAY.withAlpha(60).draw(graphics, x, y + h, w, 1);
        }
    }

    private class ZCrystalButton extends SimpleTextButton {
        private final String zCrystal;
        private final boolean isPokemonSpecific;

        public ZCrystalButton(Panel panel, String zCrystal, boolean isPokemonSpecific) {
            super(panel, Component.translatable("cobblemon_quests_extended.z_crystal." + zCrystal), Color4I.empty());
            this.zCrystal = zCrystal;
            this.isPokemonSpecific = isPokemonSpecific;
            setHeight(14);
        }

        @Override
        public void onClicked(MouseButton button) {
            playClickSound();
            config.setCurrentValue(zCrystal);
            callback.save(true);
        }

        @Override
        public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
            int color = ConfigZCrystalType.getZCrystalColorValue(zCrystal);

            // Left color bar
            Color4I.rgb(color).withAlpha(80).draw(graphics, x, y, 4, h);

            // Crystal shimmer effect
            Color4I.rgb(color).withAlpha(20).draw(graphics, x + 4, y, w - 4, h);

            // Selected state
            if (zCrystal.equals(config.getValue())) {
                Color4I.GREEN.withAlpha(30).draw(graphics, x, y, w, h);
            }

            // Hover state
            if (isMouseOver) {
                Color4I.WHITE.withAlpha(40).draw(graphics, x, y, w, h);
            }

            // Bottom separator
            Color4I.GRAY.withAlpha(40).draw(graphics, x, y + h, w, 1);
        }

        @Override
        public void addMouseOverText(TooltipList list) {
            list.add(Component.translatable("cobblemon_quests_extended.z_crystal." + zCrystal)
                    .withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD));
            list.add(Component.translatable("cobblemon_quests_extended.z_crystal.desc")
                    .withStyle(ChatFormatting.GRAY));
            if (isPokemonSpecific) {
                list.add(Component.translatable("cobblemon_quests_extended.z_crystal.pokemon_specific")
                        .withStyle(ChatFormatting.YELLOW));
            }
        }
    }
}
