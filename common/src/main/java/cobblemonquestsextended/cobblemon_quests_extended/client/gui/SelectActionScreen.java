package cobblemonquestsextended.cobblemon_quests_extended.client.gui;

import cobblemonquestsextended.cobblemon_quests_extended.client.config.ConfigActionType;
import cobblemonquestsextended.cobblemon_quests_extended.registry.ActionCategory;
import cobblemonquestsextended.cobblemon_quests_extended.registry.ActionDefinition;
import cobblemonquestsextended.cobblemon_quests_extended.registry.ActionRegistry;
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

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Selection screen for choosing action types from the ActionRegistry.
 * Groups actions by category with collapsible sections and rich tooltips.
 */
@Environment(EnvType.CLIENT)
public class SelectActionScreen extends AbstractButtonListScreen {

    private final ConfigActionType config;
    private final ConfigCallback callback;
    private final Map<ActionCategory, Boolean> expandedCategories;

    public SelectActionScreen(ConfigActionType config, ConfigCallback callback) {
        this.config = config;
        this.callback = callback;
        this.expandedCategories = new EnumMap<>(ActionCategory.class);

        // Start with all categories expanded
        for (ActionCategory category : ActionCategory.values()) {
            expandedCategories.put(category, true);
        }

        setTitle(Component.translatable("cobblemon_quests_extended.gui.select_action"));
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
        // Group actions by category
        Map<ActionCategory, List<ActionDefinition>> actionsByCategory = new EnumMap<>(ActionCategory.class);
        for (ActionCategory category : ActionCategory.values()) {
            List<ActionDefinition> actions = ActionRegistry.getActionsByCategory(category);
            if (!actions.isEmpty()) {
                actionsByCategory.put(category, actions);
            }
        }

        // Add category headers and action buttons
        for (ActionCategory category : ActionCategory.values()) {
            List<ActionDefinition> actions = actionsByCategory.get(category);
            if (actions == null || actions.isEmpty()) {
                continue;
            }

            // Add category header
            panel.add(new CategoryHeaderButton(panel, category));

            // Add action buttons if category is expanded
            if (Boolean.TRUE.equals(expandedCategories.get(category))) {
                for (ActionDefinition action : actions) {
                    panel.add(new ActionButton(panel, action));
                }
            }
        }

        // Set consistent width for all buttons
        int width = panel.getWidgets().stream()
                .map(Widget::getWidth)
                .max(Integer::compare)
                .orElse(200);
        panel.getWidgets().forEach(w -> w.setWidth(width));
    }

    @Override
    public boolean onInit() {
        int calculatedWidth = mainPanel.getWidgets().isEmpty() ? 200 : mainPanel.getWidgets().getFirst().width + 20;
        setWidth(Math.max(calculatedWidth, 250));
        setHeight(getScreen().getGuiScaledHeight() * 4 / 5);
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

    /**
     * Toggles the expanded state of a category and refreshes the button list.
     */
    private void toggleCategory(ActionCategory category) {
        expandedCategories.put(category, !Boolean.TRUE.equals(expandedCategories.get(category)));
        refreshWidgets();
    }

    /**
     * Header button for a category that can be clicked to expand/collapse.
     */
    private class CategoryHeaderButton extends SimpleTextButton {
        private final ActionCategory category;

        public CategoryHeaderButton(Panel panel, ActionCategory category) {
            super(panel, createCategoryTitle(category, expandedCategories.get(category)), Color4I.empty());
            this.category = category;
            setHeight(14);
        }

        private static Component createCategoryTitle(ActionCategory category, Boolean expanded) {
            String arrow = Boolean.TRUE.equals(expanded) ? "[-] " : "[+] ";
            return Component.literal(arrow)
                    .withStyle(ChatFormatting.GRAY)
                    .append(Component.translatable(category.getTranslationKey())
                            .withStyle(ConfigActionType.getCategoryFormatting(category)));
        }

        @Override
        public void onClicked(MouseButton button) {
            playClickSound();
            toggleCategory(category);
        }

        @Override
        public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
            // Category header background
            ConfigActionType.getCategoryColor(category).withAlpha(40).draw(graphics, x, y, w, h);
            if (isMouseOver) {
                Color4I.WHITE.withAlpha(20).draw(graphics, x, y, w, h);
            }
            Color4I.GRAY.withAlpha(60).draw(graphics, x, y + h, w, 1);
        }

        @Override
        public void addMouseOverText(TooltipList list) {
            int actionCount = ActionRegistry.getActionsByCategory(category).size();
            list.add(Component.translatable(category.getTranslationKey())
                    .withStyle(ConfigActionType.getCategoryFormatting(category)));
            list.add(Component.translatable("cobblemon_quests_extended.gui.action_count", actionCount)
                    .withStyle(ChatFormatting.GRAY));
            list.add(Component.translatable("cobblemon_quests_extended.gui.click_to_toggle")
                    .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
        }
    }

    /**
     * Button for an individual action in the list.
     */
    private class ActionButton extends SimpleTextButton {
        private final ActionDefinition action;

        public ActionButton(Panel panel, ActionDefinition action) {
            super(panel, createActionDisplayName(action), Color4I.empty());
            this.action = action;
            setHeight(12);
        }

        private static Component createActionDisplayName(ActionDefinition action) {
            // Add category-colored badge/marker before action name
            return Component.literal("  ")
                    .append(Component.literal("[")
                            .withStyle(ConfigActionType.getCategoryFormatting(action.category())))
                    .append(Component.translatable(action.category().getTranslationKey())
                            .withStyle(ConfigActionType.getCategoryFormatting(action.category())))
                    .append(Component.literal("] ")
                            .withStyle(ConfigActionType.getCategoryFormatting(action.category())))
                    .append(Component.translatable(action.translationKey())
                            .withStyle(ChatFormatting.WHITE));
        }

        @Override
        public void onClicked(MouseButton button) {
            playClickSound();
            config.setCurrentValue(action.id());
            callback.save(true);
        }

        @Override
        public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
            // Draw subtle category color indicator on the left edge
            ConfigActionType.getCategoryColor(action.category()).withAlpha(100).draw(graphics, x, y, 3, h);

            if (isMouseOver) {
                Color4I.WHITE.withAlpha(30).draw(graphics, x, y, w, h);
            }
            Color4I.GRAY.withAlpha(40).draw(graphics, x, y + h, w, 1);
        }

        @Override
        public void addMouseOverText(TooltipList list) {
            // Action name
            list.add(Component.translatable(action.translationKey())
                    .withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD));

            // Category
            list.add(Component.translatable("cobblemon_quests_extended.config.category")
                    .withStyle(ChatFormatting.GRAY)
                    .append(": ")
                    .append(Component.translatable(action.category().getTranslationKey())
                            .withStyle(ConfigActionType.getCategoryFormatting(action.category()))));

            // Description (if translation exists)
            String descriptionKey = action.translationKey() + ".description";
            Component description = Component.translatable(descriptionKey);
            // Only add description if it's actually translated (not just the key itself)
            if (!description.getString().equals(descriptionKey)) {
                list.add(Component.empty());
                list.add(description.copy().withStyle(ChatFormatting.GRAY));
            }

            // Example (if translation exists)
            String exampleKey = action.translationKey() + ".example";
            Component example = Component.translatable(exampleKey);
            if (!example.getString().equals(exampleKey)) {
                list.add(Component.translatable("cobblemon_quests_extended.gui.example")
                        .withStyle(ChatFormatting.DARK_GRAY)
                        .append(": ")
                        .append(example.copy().withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY)));
            }

            // Requires Pokemon
            list.add(Component.empty());
            list.add(Component.translatable("cobblemon_quests_extended.config.requires_pokemon")
                    .withStyle(ChatFormatting.GRAY)
                    .append(": ")
                    .append(action.requiresPokemon()
                            ? Component.translatable("gui.yes").withStyle(ChatFormatting.GREEN)
                            : Component.translatable("gui.no").withStyle(ChatFormatting.RED)));

            // Action ID for debugging/reference
            list.add(Component.literal("ID: ").withStyle(ChatFormatting.DARK_GRAY)
                    .append(Component.literal(action.id()).withStyle(ChatFormatting.DARK_GRAY)));
        }
    }
}
