package cobblemonquestsextended.cobblemon_quests_extended.client.gui.selectors;

import cobblemonquestsextended.cobblemon_quests_extended.client.config.ConfigDynamaxType;
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
 * Simple selector screen for Dynamax types (2 options: Dynamax, Gigantamax).
 */
@Environment(EnvType.CLIENT)
public class SelectDynamaxScreen extends AbstractButtonListScreen {

    private final ConfigDynamaxType config;
    private final ConfigCallback callback;

    private static final List<String> ALL_DYNAMAX_TYPES = List.of("dynamax", "gigantamax");

    public SelectDynamaxScreen(ConfigDynamaxType config, ConfigCallback callback) {
        this.config = config;
        this.callback = callback;
        setTitle(Component.translatable("cobblemon_quests_extended.gui.select_dynamax_type"));
        setHasSearchBox(false);
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
        for (String dynamaxType : ALL_DYNAMAX_TYPES) {
            panel.add(new DynamaxTypeButton(panel, dynamaxType));
        }
        int width = panel.getWidgets().stream()
                .map(Widget::getWidth)
                .max(Integer::compare)
                .orElse(160);
        panel.getWidgets().forEach(w -> w.setWidth(width));
    }

    @Override
    public boolean onInit() {
        setWidth(200);
        setHeight(100);
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

    private int getTypeColor(String dynamaxType) {
        return ConfigDynamaxType.getDynamaxColorValue(dynamaxType);
    }

    private class DynamaxTypeButton extends SimpleTextButton {
        private final String dynamaxType;

        public DynamaxTypeButton(Panel panel, String dynamaxType) {
            super(panel, Component.translatable("cobblemon_quests_extended.dynamax_type." + dynamaxType),
                    Color4I.empty());
            this.dynamaxType = dynamaxType;
            setHeight(18);
        }

        @Override
        public void onClicked(MouseButton button) {
            playClickSound();
            config.setCurrentValue(dynamaxType);
            callback.save(true);
        }

        @Override
        public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
            int color = getTypeColor(dynamaxType);

            // Left color bar (thicker for visual impact)
            Color4I.rgb(color).withAlpha(100).draw(graphics, x, y, 5, h);

            // Gradient-like effect for dynamax energy feel
            Color4I.rgb(color).withAlpha(30).draw(graphics, x + 5, y, w - 5, h);

            // Selected state
            if (dynamaxType.equals(config.getValue())) {
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
            list.add(Component.translatable("cobblemon_quests_extended.dynamax_type." + dynamaxType)
                    .withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD));
            list.add(Component.translatable("cobblemon_quests_extended.dynamax_type." + dynamaxType + ".desc")
                    .withStyle(ChatFormatting.GRAY));
            if (dynamaxType.equals("gigantamax")) {
                list.add(Component.translatable("cobblemon_quests_extended.dynamax_type.gigantamax.special")
                        .withStyle(ChatFormatting.LIGHT_PURPLE));
            }
        }
    }
}
