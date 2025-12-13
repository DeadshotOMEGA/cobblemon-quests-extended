package cobblemonquestsextended.cobblemon_quests_extended.client.gui.selectors;

import cobblemonquestsextended.cobblemon_quests_extended.client.config.ConfigGenderType;
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
 * Simple selector screen for Pokemon genders (3 options).
 */
@Environment(EnvType.CLIENT)
public class SelectGenderScreen extends AbstractButtonListScreen {

    private final ConfigGenderType config;
    private final ConfigCallback callback;

    private static final List<String> ALL_GENDERS = List.of("male", "female", "genderless");
    private static final int MALE_COLOR = 0x3498DB;
    private static final int FEMALE_COLOR = 0xE91E63;
    private static final int GENDERLESS_COLOR = 0x95A5A6;

    public SelectGenderScreen(ConfigGenderType config, ConfigCallback callback) {
        this.config = config;
        this.callback = callback;
        setTitle(Component.translatable("cobblemon_quests_extended.gui.select_gender"));
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
        for (String gender : ALL_GENDERS) {
            panel.add(new GenderButton(panel, gender));
        }
        int width = panel.getWidgets().stream()
                .map(Widget::getWidth)
                .max(Integer::compare)
                .orElse(150);
        panel.getWidgets().forEach(w -> w.setWidth(width));
    }

    @Override
    public boolean onInit() {
        setWidth(180);
        setHeight(120);
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

    private int getGenderColor(String gender) {
        return switch (gender.toLowerCase()) {
            case "male" -> MALE_COLOR;
            case "female" -> FEMALE_COLOR;
            default -> GENDERLESS_COLOR;
        };
    }

    private class GenderButton extends SimpleTextButton {
        private final String gender;

        public GenderButton(Panel panel, String gender) {
            super(panel, Component.translatable("cobblemon_quests_extended.genders." + gender), Color4I.empty());
            this.gender = gender;
            setHeight(16);
        }

        @Override
        public void onClicked(MouseButton button) {
            playClickSound();
            config.setCurrentValue(gender);
            callback.save(true);
        }

        @Override
        public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
            Color4I.rgb(getGenderColor(gender)).withAlpha(60).draw(graphics, x, y, 4, h);
            if (gender.equals(config.getValue())) {
                Color4I.GREEN.withAlpha(30).draw(graphics, x, y, w, h);
            }
            if (isMouseOver) {
                Color4I.WHITE.withAlpha(30).draw(graphics, x, y, w, h);
            }
            Color4I.GRAY.withAlpha(40).draw(graphics, x, y + h, w, 1);
        }

        @Override
        public void addMouseOverText(TooltipList list) {
            list.add(Component.translatable("cobblemon_quests_extended.genders." + gender)
                    .withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD));
        }
    }
}
