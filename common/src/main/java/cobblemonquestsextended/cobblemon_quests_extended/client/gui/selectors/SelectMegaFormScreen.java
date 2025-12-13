package cobblemonquestsextended.cobblemon_quests_extended.client.gui.selectors;

import cobblemonquestsextended.cobblemon_quests_extended.client.config.ConfigMegaFormType;
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
 * Simple selector screen for Mega forms (4 options).
 */
@Environment(EnvType.CLIENT)
public class SelectMegaFormScreen extends AbstractButtonListScreen {

    private final ConfigMegaFormType config;
    private final ConfigCallback callback;

    private static final List<String> ALL_MEGA_FORMS = List.of("mega", "mega_x", "mega_y", "primal");
    private static final int MEGA_COLOR = 0xE91E63;
    private static final int MEGA_X_COLOR = 0x3498DB;
    private static final int MEGA_Y_COLOR = 0xE74C3C;
    private static final int PRIMAL_COLOR = 0xF39C12;

    public SelectMegaFormScreen(ConfigMegaFormType config, ConfigCallback callback) {
        this.config = config;
        this.callback = callback;
        setTitle(Component.translatable("cobblemon_quests_extended.gui.select_mega_form"));
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
        for (String form : ALL_MEGA_FORMS) {
            panel.add(new MegaFormButton(panel, form));
        }
        int width = panel.getWidgets().stream()
                .map(Widget::getWidth)
                .max(Integer::compare)
                .orElse(150);
        panel.getWidgets().forEach(w -> w.setWidth(width));
    }

    @Override
    public boolean onInit() {
        setWidth(200);
        setHeight(140);
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

    private int getFormColor(String form) {
        return switch (form.toLowerCase()) {
            case "mega" -> MEGA_COLOR;
            case "mega_x" -> MEGA_X_COLOR;
            case "mega_y" -> MEGA_Y_COLOR;
            case "primal" -> PRIMAL_COLOR;
            default -> MEGA_COLOR;
        };
    }

    private class MegaFormButton extends SimpleTextButton {
        private final String form;

        public MegaFormButton(Panel panel, String form) {
            super(panel, Component.translatable("cobblemon_quests_extended.mega_form." + form), Color4I.empty());
            this.form = form;
            setHeight(16);
        }

        @Override
        public void onClicked(MouseButton button) {
            playClickSound();
            config.setCurrentValue(form);
            callback.save(true);
        }

        @Override
        public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
            Color4I.rgb(getFormColor(form)).withAlpha(60).draw(graphics, x, y, 4, h);
            if (form.equals(config.getValue())) {
                Color4I.GREEN.withAlpha(30).draw(graphics, x, y, w, h);
            }
            if (isMouseOver) {
                Color4I.WHITE.withAlpha(30).draw(graphics, x, y, w, h);
            }
            Color4I.GRAY.withAlpha(40).draw(graphics, x, y + h, w, 1);
        }

        @Override
        public void addMouseOverText(TooltipList list) {
            list.add(Component.translatable("cobblemon_quests_extended.mega_form." + form)
                    .withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD));
            list.add(Component.translatable("cobblemon_quests_extended.mega_form." + form + ".desc")
                    .withStyle(ChatFormatting.GRAY));
        }
    }
}
