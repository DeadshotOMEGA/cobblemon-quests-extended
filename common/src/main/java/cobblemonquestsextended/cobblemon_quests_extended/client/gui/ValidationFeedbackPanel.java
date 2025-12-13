package cobblemonquestsextended.cobblemon_quests_extended.client.gui;

import cobblemonquestsextended.cobblemon_quests_extended.domain.validation.ValidationIssue;
import cobblemonquestsextended.cobblemon_quests_extended.domain.validation.ValidationResult;
import cobblemonquestsextended.cobblemon_quests_extended.domain.validation.ValidationSeverity;
import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.Widget;
import dev.ftb.mods.ftblibrary.ui.WidgetType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

import static cobblemonquestsextended.cobblemon_quests_extended.CobblemonQuests.MOD_ID;

/**
 * Panel that displays validation feedback (errors and warnings).
 * Shows at the top of the config screen when there are validation issues.
 */
@Environment(EnvType.CLIENT)
public class ValidationFeedbackPanel extends Panel {

    private static final int ERROR_COLOR = 0xFFE74C3C;    // Red
    private static final int WARNING_COLOR = 0xFFF39C12;  // Orange
    private static final int INFO_COLOR = 0xFF3498DB;    // Blue
    private static final int BG_COLOR = 0x80000000;       // Semi-transparent black

    private static final int ITEM_HEIGHT = 12;
    private static final int MAX_VISIBLE_ISSUES = 4;

    private ValidationResult result;
    private final List<IssueEntry> entries = new ArrayList<>();

    public ValidationFeedbackPanel(Panel parent) {
        super(parent);
        this.result = ValidationResult.valid();
        setHeight(0);
    }

    /**
     * Updates the validation result to display.
     */
    public void setValidationResult(ValidationResult result) {
        this.result = result;
        rebuildEntries();
    }

    private void rebuildEntries() {
        entries.clear();

        if (!result.hasIssues()) {
            setHeight(0);
            return;
        }

        // Add errors first, then warnings, then infos
        for (ValidationIssue issue : result.getErrors()) {
            entries.add(new IssueEntry(issue));
        }
        for (ValidationIssue issue : result.getWarnings()) {
            entries.add(new IssueEntry(issue));
        }
        for (ValidationIssue issue : result.getInfos()) {
            entries.add(new IssueEntry(issue));
        }

        // Calculate height
        int visibleCount = Math.min(entries.size(), MAX_VISIBLE_ISSUES);
        int headerHeight = result.getErrorCount() > 0 ? 14 : 0;
        setHeight(headerHeight + visibleCount * ITEM_HEIGHT + 4);
    }

    @Override
    public void addWidgets() {
        // Clear existing widgets
        widgets.clear();

        int y = result.getErrorCount() > 0 ? 14 : 0;
        int count = 0;

        for (IssueEntry entry : entries) {
            if (count >= MAX_VISIBLE_ISSUES) break;

            IssueWidget widget = new IssueWidget(this, entry.issue);
            widget.setPos(4, y);
            widget.setWidth(width - 8);
            add(widget);

            y += ITEM_HEIGHT;
            count++;
        }

        // Show "and X more..." if there are more issues
        if (entries.size() > MAX_VISIBLE_ISSUES) {
            int remaining = entries.size() - MAX_VISIBLE_ISSUES;
            // This will be drawn in drawBackground
        }
    }

    @Override
    public void alignWidgets() {
        for (Widget w : widgets) {
            w.setWidth(width - 8);
        }
    }

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        if (!result.hasIssues()) return;

        // Background
        Color4I.rgba(BG_COLOR).draw(graphics, x, y, w, h);

        // Header line for errors
        if (result.getErrorCount() > 0) {
            String errorText = Component.translatable(MOD_ID + ".validation.errors", result.getErrorCount()).getString();
            Color4I.rgb(ERROR_COLOR).draw(graphics, x, y, w, 1);
            theme.drawString(graphics, Component.literal(errorText).withStyle(s -> s.withColor(ERROR_COLOR)), x + 4, y + 3);
        }

        // "And X more..." text
        if (entries.size() > MAX_VISIBLE_ISSUES) {
            int remaining = entries.size() - MAX_VISIBLE_ISSUES;
            int textY = y + h - ITEM_HEIGHT;
            String moreText = Component.translatable(MOD_ID + ".validation.more", remaining).getString();
            theme.drawString(graphics, Component.literal(moreText).withStyle(s -> s.withColor(0xAAAAAA)), x + 4, textY);
        }

        // Bottom border
        Color4I.GRAY.withAlpha(128).draw(graphics, x, y + h - 1, w, 1);
    }

    /**
     * Returns true if there are any validation errors (not just warnings).
     */
    public boolean hasErrors() {
        return result.getErrorCount() > 0;
    }

    /**
     * Simple entry holder.
     */
    private record IssueEntry(ValidationIssue issue) {}

    /**
     * Widget for displaying a single validation issue.
     */
    private static class IssueWidget extends Widget {
        private final ValidationIssue issue;

        public IssueWidget(Panel parent, ValidationIssue issue) {
            super(parent);
            this.issue = issue;
            setHeight(ITEM_HEIGHT);
        }

        @Override
        public void draw(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
            int color = switch (issue.severity()) {
                case ERROR -> ERROR_COLOR;
                case WARNING -> WARNING_COLOR;
                case INFO -> INFO_COLOR;
            };

            // Severity indicator bar
            Color4I.rgb(color).draw(graphics, x, y + 1, 2, h - 2);

            // Field name in brackets
            String fieldName = "[" + issue.field() + "] ";
            theme.drawString(graphics, Component.literal(fieldName).withStyle(s -> s.withColor(0x888888)), x + 5, y + 2);

            // Message
            int fieldWidth = theme.getStringWidth(Component.literal(fieldName));
            Component message = Component.translatable(issue.messageKey(), issue.messageArgs());
            theme.drawString(graphics, message.copy().withStyle(s -> s.withColor(color)), x + 5 + fieldWidth, y + 2);
        }

        @Override
        public WidgetType getWidgetType() {
            return WidgetType.DISABLED;
        }
    }
}
