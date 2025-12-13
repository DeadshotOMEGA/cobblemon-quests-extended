package cobblemonquestsextended.cobblemon_quests_extended.client.gui.selectors;

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

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Abstract base class for selector screens with categorized, searchable items.
 * Provides common functionality for displaying grouped items with collapsible sections,
 * search filtering, and multi-select support.
 *
 * @param <T> the item type being selected (e.g., String, ResourceLocation)
 * @param <C> the category enum type for grouping items
 */
@Environment(EnvType.CLIENT)
public abstract class AbstractSelectorScreen<T, C extends Enum<C>> extends AbstractButtonListScreen {

    protected final Map<C, Boolean> expandedCategories;
    protected final List<T> selectedItems;
    protected final Consumer<List<T>> onClose;
    protected final boolean multiSelect;
    protected final Class<C> categoryClass;

    /**
     * Creates a new selector screen.
     *
     * @param categoryClass the class of the category enum
     * @param title the screen title component
     * @param multiSelect whether multiple items can be selected
     * @param initialSelection the initially selected items (may be empty)
     * @param onClose callback when screen closes with selected items
     */
    protected AbstractSelectorScreen(
            Class<C> categoryClass,
            Component title,
            boolean multiSelect,
            List<T> initialSelection,
            Consumer<List<T>> onClose
    ) {
        this.categoryClass = categoryClass;
        this.multiSelect = multiSelect;
        this.selectedItems = new ArrayList<>(initialSelection);
        this.onClose = onClose;
        this.expandedCategories = new EnumMap<>(categoryClass);

        // Start with all categories expanded
        for (C category : categoryClass.getEnumConstants()) {
            expandedCategories.put(category, true);
        }

        setTitle(title);
        setHasSearchBox(true);
        showBottomPanel(multiSelect); // Show accept/cancel buttons for multi-select
        showCloseButton(true);
        setBorder(1, 1, 1);
    }

    // ==================== Abstract Methods (subclasses must implement) ====================

    /**
     * Gets all available items grouped by their category.
     * The map should contain all non-empty categories.
     *
     * @return a map of category to list of items in that category
     */
    protected abstract Map<C, List<T>> getGroupedItems();

    /**
     * Gets the display name component for an item.
     *
     * @param item the item to get the display name for
     * @return the display name component
     */
    protected abstract Component getItemDisplayName(T item);

    /**
     * Gets the color associated with a category.
     *
     * @param category the category
     * @return the category color
     */
    protected abstract Color4I getCategoryColor(C category);

    /**
     * Gets the translation key for a category's display name.
     *
     * @param category the category
     * @return the translation key
     */
    protected abstract String getCategoryTranslationKey(C category);

    /**
     * Called when an item is selected (clicked).
     * For single-select mode, this typically closes the screen.
     * For multi-select mode, this toggles the item's selection state.
     *
     * @param item the item that was clicked
     */
    protected abstract void onItemSelected(T item);

    /**
     * Checks if an item is currently selected.
     *
     * @param item the item to check
     * @return true if the item is selected
     */
    protected abstract boolean isItemSelected(T item);

    // ==================== Optional Override Methods ====================

    /**
     * Gets the number of items in a category. Override if you need custom counting.
     *
     * @param category the category
     * @return the item count
     */
    protected int getCategoryItemCount(C category) {
        Map<C, List<T>> grouped = getGroupedItems();
        List<T> items = grouped.get(category);
        return items != null ? items.size() : 0;
    }

    /**
     * Builds the tooltip for a category header. Override to customize.
     *
     * @param list the tooltip list to add components to
     * @param category the category
     */
    protected void addCategoryTooltip(TooltipList list, C category) {
        list.add(Component.translatable(getCategoryTranslationKey(category))
                .withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD));
        int itemCount = getCategoryItemCount(category);
        list.add(Component.translatable("cobblemon_quests_extended.gui.item_count", itemCount)
                .withStyle(ChatFormatting.GRAY));
        list.add(Component.translatable("cobblemon_quests_extended.gui.click_to_toggle")
                .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
    }

    /**
     * Builds the tooltip for an item button. Override to customize.
     *
     * @param list the tooltip list to add components to
     * @param item the item
     * @param category the item's category
     */
    protected void addItemTooltip(TooltipList list, T item, C category) {
        list.add(getItemDisplayName(item).copy().withStyle(ChatFormatting.WHITE));
        list.add(Component.translatable("cobblemon_quests_extended.config.category")
                .withStyle(ChatFormatting.GRAY)
                .append(": ")
                .append(Component.translatable(getCategoryTranslationKey(category))
                        .withStyle(getCategoryFormatting(category))));
        if (isItemSelected(item)) {
            list.add(Component.translatable("cobblemon_quests_extended.gui.selected")
                    .withStyle(ChatFormatting.GREEN));
        }
    }

    /**
     * Gets the ChatFormatting style for a category based on its color.
     * Override if you need custom formatting beyond color-based.
     *
     * @param category the category
     * @return the formatting style
     */
    protected ChatFormatting getCategoryFormatting(C category) {
        // Default to WHITE, subclasses can override for specific formatting
        return ChatFormatting.WHITE;
    }

    /**
     * Gets the string identifier for an item (used for debugging/display).
     * Override if items have specific IDs.
     *
     * @param item the item
     * @return the item identifier string
     */
    protected String getItemId(T item) {
        return item.toString();
    }

    // ==================== Screen Lifecycle ====================

    @Override
    public boolean onClosedByKey(Key key) {
        if (super.onClosedByKey(key)) {
            doCancel();
            return true;
        }
        return false;
    }

    @Override
    public boolean onInit() {
        int calculatedWidth = mainPanel.getWidgets().isEmpty() ? 200 : mainPanel.getWidgets().getFirst().width + 20;
        setWidth(Math.max(calculatedWidth, 280));
        setHeight(getScreen().getGuiScaledHeight() * 4 / 5);
        return true;
    }

    @Override
    protected void doCancel() {
        onClose.accept(selectedItems);
    }

    @Override
    protected void doAccept() {
        onClose.accept(selectedItems);
    }

    // ==================== Button List Building ====================

    @Override
    public void addButtons(Panel panel) {
        Map<C, List<T>> groupedItems = getGroupedItems();

        // Add category headers and item buttons
        for (C category : categoryClass.getEnumConstants()) {
            List<T> items = groupedItems.get(category);
            if (items == null || items.isEmpty()) {
                continue;
            }

            // Add category header
            panel.add(new CategoryHeaderButton(panel, category, items.size()));

            // Add item buttons if category is expanded
            if (Boolean.TRUE.equals(expandedCategories.get(category))) {
                for (T item : items) {
                    panel.add(new ItemButton(panel, item, category));
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

    /**
     * Toggles the expanded state of a category and refreshes the button list.
     *
     * @param category the category to toggle
     */
    protected void toggleCategory(C category) {
        expandedCategories.put(category, !Boolean.TRUE.equals(expandedCategories.get(category)));
        refreshWidgets();
    }

    /**
     * Toggles the selection state of an item (for multi-select mode).
     *
     * @param item the item to toggle
     */
    protected void toggleItemSelection(T item) {
        if (selectedItems.contains(item)) {
            selectedItems.remove(item);
        } else {
            selectedItems.add(item);
        }
        refreshWidgets();
    }

    // ==================== Inner Classes ====================

    /**
     * Header button for a category that can be clicked to expand/collapse.
     */
    protected class CategoryHeaderButton extends SimpleTextButton {
        protected final C category;
        protected final int itemCount;

        public CategoryHeaderButton(Panel panel, C category, int itemCount) {
            super(panel, Component.empty(), Color4I.empty());
            this.category = category;
            this.itemCount = itemCount;
            setTitle(createCategoryTitle());
            setHeight(14);
        }

        private Component createCategoryTitle() {
            Boolean expanded = expandedCategories.get(category);
            String arrow = Boolean.TRUE.equals(expanded) ? "[-] " : "[+] ";
            return Component.literal(arrow)
                    .withStyle(ChatFormatting.GRAY)
                    .append(Component.translatable(getCategoryTranslationKey(category))
                            .withStyle(getCategoryFormatting(category)))
                    .append(Component.literal(" (" + itemCount + ")")
                            .withStyle(ChatFormatting.DARK_GRAY));
        }

        @Override
        public void onClicked(MouseButton button) {
            playClickSound();
            toggleCategory(category);
        }

        @Override
        public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
            // Category header background with color indicator
            getCategoryColor(category).withAlpha(40).draw(graphics, x, y, w, h);
            if (isMouseOver) {
                Color4I.WHITE.withAlpha(20).draw(graphics, x, y, w, h);
            }
            Color4I.GRAY.withAlpha(60).draw(graphics, x, y + h, w, 1);
        }

        @Override
        public void addMouseOverText(TooltipList list) {
            addCategoryTooltip(list, category);
        }
    }

    /**
     * Button for an individual item in the list.
     */
    protected class ItemButton extends SimpleTextButton {
        protected final T item;
        protected final C category;

        public ItemButton(Panel panel, T item, C category) {
            super(panel, Component.empty(), Color4I.empty());
            this.item = item;
            this.category = category;
            setTitle(createItemDisplayComponent());
            setHeight(12);
        }

        private Component createItemDisplayComponent() {
            Component base = Component.literal("  ")
                    .append(getItemDisplayName(item).copy().withStyle(ChatFormatting.WHITE));

            // Add selection indicator for multi-select mode
            if (multiSelect && isItemSelected(item)) {
                return Component.literal("[*] ")
                        .withStyle(ChatFormatting.GREEN)
                        .append(base);
            }
            return base;
        }

        @Override
        public void onClicked(MouseButton button) {
            playClickSound();
            onItemSelected(item);
        }

        @Override
        public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
            // Draw category color indicator on left edge
            getCategoryColor(category).withAlpha(100).draw(graphics, x, y, 3, h);

            // Selection highlight for multi-select mode
            if (multiSelect && isItemSelected(item)) {
                Color4I.GREEN.withAlpha(30).draw(graphics, x, y, w, h);
            }

            if (isMouseOver) {
                Color4I.WHITE.withAlpha(30).draw(graphics, x, y, w, h);
            }
            Color4I.GRAY.withAlpha(40).draw(graphics, x, y + h, w, 1);
        }

        @Override
        public void addMouseOverText(TooltipList list) {
            addItemTooltip(list, item, category);
        }
    }
}
