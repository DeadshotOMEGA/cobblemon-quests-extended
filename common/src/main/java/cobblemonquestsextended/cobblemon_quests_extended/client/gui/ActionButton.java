package cobblemonquestsextended.cobblemon_quests_extended.client.gui;

import cobblemonquestsextended.cobblemon_quests_extended.client.config.ConfigActionType;
import cobblemonquestsextended.cobblemon_quests_extended.registry.ActionDefinition;
import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.SimpleTextButton;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

/**
 * Reusable widget for displaying an action in a list.
 * Shows action name with a category-colored icon/badge and provides rich tooltips on hover.
 */
@Environment(EnvType.CLIENT)
public class ActionButton extends SimpleTextButton {

    private final ActionDefinition action;
    private final Consumer<ActionDefinition> onSelect;

    /**
     * Creates an ActionButton for the given action definition.
     *
     * @param panel    the parent panel
     * @param action   the action definition to display
     * @param onSelect callback when the action is selected
     */
    public ActionButton(Panel panel, ActionDefinition action, Consumer<ActionDefinition> onSelect) {
        super(panel, createDisplayName(action), Color4I.empty());
        this.action = action;
        this.onSelect = onSelect;
        setHeight(16);
    }

    /**
     * Creates the display name component for the action, including category badge.
     *
     * @param action the action definition
     * @return the formatted display component
     */
    private static Component createDisplayName(ActionDefinition action) {
        ChatFormatting categoryColor = ConfigActionType.getCategoryFormatting(action.category());

        return Component.literal("")
                .append(Component.literal("[")
                        .withStyle(categoryColor))
                .append(Component.translatable(action.category().getTranslationKey())
                        .withStyle(categoryColor))
                .append(Component.literal("] ")
                        .withStyle(categoryColor))
                .append(Component.translatable(action.translationKey())
                        .withStyle(ChatFormatting.WHITE));
    }

    /**
     * Gets the action definition associated with this button.
     *
     * @return the action definition
     */
    public ActionDefinition getAction() {
        return action;
    }

    @Override
    public void onClicked(MouseButton button) {
        playClickSound();
        if (onSelect != null) {
            onSelect.accept(action);
        }
    }

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        // Draw category color indicator on the left edge
        Color4I categoryColor = ConfigActionType.getCategoryColor(action.category());
        categoryColor.withAlpha(120).draw(graphics, x, y, 3, h);

        // Highlight on hover
        if (isMouseOver) {
            Color4I.WHITE.withAlpha(30).draw(graphics, x, y, w, h);
        }

        // Bottom separator line
        Color4I.GRAY.withAlpha(40).draw(graphics, x, y + h, w, 1);
    }

    @Override
    public void addMouseOverText(TooltipList list) {
        // Action name (bold)
        list.add(Component.translatable(action.translationKey())
                .withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD));

        // Category with color
        list.add(Component.translatable("cobblemon_quests_extended.config.category")
                .withStyle(ChatFormatting.GRAY)
                .append(": ")
                .append(Component.translatable(action.category().getTranslationKey())
                        .withStyle(ConfigActionType.getCategoryFormatting(action.category()))));

        // Description (if translation key exists and is translated)
        String descriptionKey = action.translationKey() + ".description";
        Component description = Component.translatable(descriptionKey);
        if (!description.getString().equals(descriptionKey)) {
            list.add(Component.empty());
            list.add(description.copy().withStyle(ChatFormatting.GRAY));
        }

        // Example usage (if translation key exists and is translated)
        String exampleKey = action.translationKey() + ".example";
        Component example = Component.translatable(exampleKey);
        if (!example.getString().equals(exampleKey)) {
            list.add(Component.translatable("cobblemon_quests_extended.gui.example")
                    .withStyle(ChatFormatting.DARK_GRAY)
                    .append(": ")
                    .append(example.copy().withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY)));
        }

        // Requires Pokemon indicator
        list.add(Component.empty());
        list.add(Component.translatable("cobblemon_quests_extended.config.requires_pokemon")
                .withStyle(ChatFormatting.GRAY)
                .append(": ")
                .append(action.requiresPokemon()
                        ? Component.translatable("gui.yes").withStyle(ChatFormatting.GREEN)
                        : Component.translatable("gui.no").withStyle(ChatFormatting.RED)));

        // Action ID for reference
        list.add(Component.literal("ID: ").withStyle(ChatFormatting.DARK_GRAY)
                .append(Component.literal(action.id()).withStyle(ChatFormatting.DARK_GRAY)));
    }
}
