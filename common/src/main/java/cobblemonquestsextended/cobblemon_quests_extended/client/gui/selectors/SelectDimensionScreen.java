package cobblemonquestsextended.cobblemon_quests_extended.client.gui.selectors;

import cobblemonquestsextended.cobblemon_quests_extended.client.config.ConfigDimensionType;
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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple selector screen for dimensions.
 */
@Environment(EnvType.CLIENT)
public class SelectDimensionScreen extends AbstractButtonListScreen {

    private final ConfigDimensionType config;
    private final ConfigCallback callback;

    private static final int OVERWORLD_COLOR = 0x7CB342;
    private static final int NETHER_COLOR = 0xE74C3C;
    private static final int END_COLOR = 0x9B59B6;
    private static final int MODDED_COLOR = 0x3498DB;

    public SelectDimensionScreen(ConfigDimensionType config, ConfigCallback callback) {
        this.config = config;
        this.callback = callback;
        setTitle(Component.translatable("cobblemon_quests_extended.gui.select_dimension"));
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
        for (String dimension : getDimensions()) {
            panel.add(new DimensionButton(panel, dimension));
        }
        int width = panel.getWidgets().stream()
                .map(Widget::getWidth)
                .max(Integer::compare)
                .orElse(200);
        panel.getWidgets().forEach(w -> w.setWidth(width));
    }

    private List<String> getDimensions() {
        List<String> dimensions = new ArrayList<>();
        dimensions.add("minecraft:overworld");
        dimensions.add("minecraft:the_nether");
        dimensions.add("minecraft:the_end");

        // Try to get modded dimensions
        try {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level != null) {
                mc.level.registryAccess().registryOrThrow(Registries.DIMENSION_TYPE).keySet().forEach(loc -> {
                    String dimId = loc.toString();
                    if (!dimensions.contains(dimId)) {
                        dimensions.add(dimId);
                    }
                });
            }
        } catch (Exception ignored) {
            // Use default dimensions
        }

        return dimensions;
    }

    @Override
    public boolean onInit() {
        setWidth(250);
        setHeight(getScreen().getGuiScaledHeight() / 2);
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

    private int getDimensionColor(String dimension) {
        if (dimension.contains("nether")) return NETHER_COLOR;
        if (dimension.contains("end") && !dimension.contains("legend")) return END_COLOR;
        if (dimension.equals("minecraft:overworld")) return OVERWORLD_COLOR;
        return MODDED_COLOR;
    }

    private String formatDimensionName(String dimension) {
        ResourceLocation loc = ResourceLocation.tryParse(dimension);
        if (loc == null) return dimension;

        String path = loc.getPath();
        if (path.equals("overworld")) return "Overworld";
        if (path.equals("the_nether")) return "The Nether";
        if (path.equals("the_end")) return "The End";

        StringBuilder result = new StringBuilder();
        String[] parts = path.split("_");
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) result.append(" ");
            String part = parts[i];
            if (!part.isEmpty()) {
                result.append(Character.toUpperCase(part.charAt(0)));
                if (part.length() > 1) {
                    result.append(part.substring(1).toLowerCase());
                }
            }
        }
        return result.toString();
    }

    private class DimensionButton extends SimpleTextButton {
        private final String dimension;

        public DimensionButton(Panel panel, String dimension) {
            super(panel, Component.literal(formatDimensionName(dimension)), Color4I.empty());
            this.dimension = dimension;
            setHeight(14);
        }

        @Override
        public void onClicked(MouseButton button) {
            playClickSound();
            config.setCurrentValue(dimension);
            callback.save(true);
        }

        @Override
        public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
            Color4I.rgb(getDimensionColor(dimension)).withAlpha(60).draw(graphics, x, y, 4, h);
            if (dimension.equals(config.getValue())) {
                Color4I.GREEN.withAlpha(30).draw(graphics, x, y, w, h);
            }
            if (isMouseOver) {
                Color4I.WHITE.withAlpha(30).draw(graphics, x, y, w, h);
            }
            Color4I.GRAY.withAlpha(40).draw(graphics, x, y + h, w, 1);
        }

        @Override
        public void addMouseOverText(TooltipList list) {
            list.add(Component.literal(formatDimensionName(dimension))
                    .withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD));
            list.add(Component.translatable("cobblemon_quests_extended.config.dimension_id")
                    .withStyle(ChatFormatting.GRAY)
                    .append(": ")
                    .append(Component.literal(dimension).withStyle(ChatFormatting.DARK_GRAY)));
        }
    }
}
