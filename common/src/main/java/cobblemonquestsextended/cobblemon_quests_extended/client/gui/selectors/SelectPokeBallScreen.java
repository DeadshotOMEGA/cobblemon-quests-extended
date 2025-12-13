package cobblemonquestsextended.cobblemon_quests_extended.client.gui.selectors;

import cobblemonquestsextended.cobblemon_quests_extended.client.config.ConfigPokeBallType;
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
 * Selector screen for Poke Balls.
 */
@Environment(EnvType.CLIENT)
public class SelectPokeBallScreen extends AbstractButtonListScreen {

    private final ConfigPokeBallType config;
    private final ConfigCallback callback;

    private static final List<String> ALL_BALLS = List.of(
            // Standard
            "poke_ball", "great_ball", "ultra_ball", "master_ball",
            // Apricorn
            "fast_ball", "level_ball", "lure_ball", "heavy_ball",
            "love_ball", "friend_ball", "moon_ball",
            // Special
            "safari_ball", "sport_ball", "dream_ball", "beast_ball", "cherish_ball",
            // Other
            "net_ball", "nest_ball", "repeat_ball", "timer_ball",
            "luxury_ball", "premier_ball", "dusk_ball", "heal_ball",
            "quick_ball", "dive_ball", "park_ball"
    );

    private static final Map<String, Integer> BALL_COLORS = Map.ofEntries(
            Map.entry("poke_ball", 0xE74C3C),
            Map.entry("great_ball", 0x3498DB),
            Map.entry("ultra_ball", 0xF1C40F),
            Map.entry("master_ball", 0x9B59B6),
            Map.entry("safari_ball", 0x27AE60),
            Map.entry("fast_ball", 0xF39C12),
            Map.entry("level_ball", 0xF1C40F),
            Map.entry("lure_ball", 0x00BCD4),
            Map.entry("heavy_ball", 0x607D8B),
            Map.entry("love_ball", 0xE91E63),
            Map.entry("friend_ball", 0x8BC34A),
            Map.entry("moon_ball", 0x1A237E),
            Map.entry("sport_ball", 0xFF5722),
            Map.entry("net_ball", 0x009688),
            Map.entry("nest_ball", 0x4CAF50),
            Map.entry("repeat_ball", 0x795548),
            Map.entry("timer_ball", 0x424242),
            Map.entry("luxury_ball", 0x212121),
            Map.entry("premier_ball", 0xEEEEEE),
            Map.entry("dusk_ball", 0x4E342E),
            Map.entry("heal_ball", 0xF48FB1),
            Map.entry("quick_ball", 0x2196F3),
            Map.entry("cherish_ball", 0xB71C1C),
            Map.entry("dream_ball", 0xE1BEE7),
            Map.entry("beast_ball", 0x304FFE),
            Map.entry("dive_ball", 0x0277BD),
            Map.entry("park_ball", 0xFFEB3B)
    );

    public SelectPokeBallScreen(ConfigPokeBallType config, ConfigCallback callback) {
        this.config = config;
        this.callback = callback;
        setTitle(Component.translatable("cobblemon_quests_extended.gui.select_pokeball"));
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
        for (String ball : ALL_BALLS) {
            panel.add(new PokeBallButton(panel, ball));
        }
        int width = panel.getWidgets().stream()
                .map(Widget::getWidth)
                .max(Integer::compare)
                .orElse(180);
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

    private class PokeBallButton extends SimpleTextButton {
        private final String ball;

        public PokeBallButton(Panel panel, String ball) {
            super(panel, Component.translatable("item.cobblemon." + ball), Color4I.empty());
            this.ball = ball;
            setHeight(14);
        }

        @Override
        public void onClicked(MouseButton button) {
            playClickSound();
            config.setCurrentValue(ball);
            callback.save(true);
        }

        @Override
        public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
            int color = BALL_COLORS.getOrDefault(ball, 0xE74C3C);
            Color4I.rgb(color).withAlpha(80).draw(graphics, x, y, 4, h);

            if (ball.equals(config.getValue())) {
                Color4I.GREEN.withAlpha(30).draw(graphics, x, y, w, h);
            }
            if (isMouseOver) {
                Color4I.WHITE.withAlpha(30).draw(graphics, x, y, w, h);
            }
            Color4I.GRAY.withAlpha(40).draw(graphics, x, y + h, w, 1);
        }

        @Override
        public void addMouseOverText(TooltipList list) {
            list.add(Component.translatable("item.cobblemon." + ball)
                    .withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD));
            String category = ConfigPokeBallType.getBallCategory(ball);
            list.add(Component.translatable("cobblemon_quests_extended.config.ball_category")
                    .withStyle(ChatFormatting.GRAY)
                    .append(": ")
                    .append(Component.translatable("cobblemon_quests_extended.ball_category." + category)
                            .withStyle(ChatFormatting.WHITE)));
        }
    }
}
