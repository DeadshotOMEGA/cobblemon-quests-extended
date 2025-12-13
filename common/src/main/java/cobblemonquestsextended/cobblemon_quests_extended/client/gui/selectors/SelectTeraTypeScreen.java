package cobblemonquestsextended.cobblemon_quests_extended.client.gui.selectors;

import cobblemonquestsextended.cobblemon_quests_extended.client.config.ConfigTeraType;
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
 * Selector screen for Tera types (18 Pokemon types + stellar).
 */
@Environment(EnvType.CLIENT)
public class SelectTeraTypeScreen extends AbstractButtonListScreen {

    private final ConfigTeraType config;
    private final ConfigCallback callback;

    private static final List<String> ALL_TERA_TYPES = List.of(
            "normal", "fire", "water", "grass", "electric", "ice",
            "fighting", "poison", "ground", "flying", "psychic", "bug",
            "rock", "ghost", "dragon", "dark", "steel", "fairy", "stellar"
    );

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
            Map.entry("stellar", 0xFFFFFF)
    );

    public SelectTeraTypeScreen(ConfigTeraType config, ConfigCallback callback) {
        this.config = config;
        this.callback = callback;
        setTitle(Component.translatable("cobblemon_quests_extended.gui.select_tera_type"));
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
        for (String teraType : ALL_TERA_TYPES) {
            panel.add(new TeraTypeButton(panel, teraType));
        }
        int width = panel.getWidgets().stream()
                .map(Widget::getWidth)
                .max(Integer::compare)
                .orElse(180);
        panel.getWidgets().forEach(w -> w.setWidth(width));
    }

    @Override
    public boolean onInit() {
        setWidth(200);
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

    private class TeraTypeButton extends SimpleTextButton {
        private final String teraType;

        public TeraTypeButton(Panel panel, String teraType) {
            super(panel, teraType.equals("stellar")
                    ? Component.translatable("cobblemon_quests_extended.tera_type.stellar")
                    : Component.translatable("cobblemon.type." + teraType), Color4I.empty());
            this.teraType = teraType;
            setHeight(14);
        }

        private Component getDisplayName(String type) {
            if (type.equals("stellar")) {
                return Component.translatable("cobblemon_quests_extended.tera_type.stellar");
            }
            return Component.translatable("cobblemon.type." + type);
        }

        @Override
        public void onClicked(MouseButton button) {
            playClickSound();
            config.setCurrentValue(teraType);
            callback.save(true);
        }

        @Override
        public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
            int color = TYPE_COLORS.getOrDefault(teraType, 0x808080);
            Color4I.rgb(color).withAlpha(80).draw(graphics, x, y, 4, h);

            // Crystal-like shimmer effect for tera types
            Color4I.rgb(color).withAlpha(20).draw(graphics, x + 4, y, w - 4, h);

            if (teraType.equals(config.getValue())) {
                Color4I.GREEN.withAlpha(30).draw(graphics, x, y, w, h);
            }
            if (isMouseOver) {
                Color4I.WHITE.withAlpha(40).draw(graphics, x, y, w, h);
            }
            Color4I.GRAY.withAlpha(40).draw(graphics, x, y + h, w, 1);
        }

        @Override
        public void addMouseOverText(TooltipList list) {
            list.add(getDisplayName(teraType).copy().withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD));
            list.add(Component.translatable("cobblemon_quests_extended.tera_type.desc")
                    .withStyle(ChatFormatting.GRAY));
            if (teraType.equals("stellar")) {
                list.add(Component.translatable("cobblemon_quests_extended.tera_type.stellar.desc")
                        .withStyle(ChatFormatting.LIGHT_PURPLE));
            }
        }
    }
}
