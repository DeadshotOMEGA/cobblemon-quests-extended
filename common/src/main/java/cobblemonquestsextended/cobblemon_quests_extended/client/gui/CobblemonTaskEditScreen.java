package cobblemonquestsextended.cobblemon_quests_extended.client.gui;

import cobblemonquestsextended.cobblemon_quests_extended.CobblemonQuests;
import cobblemonquestsextended.cobblemon_quests_extended.domain.CobblemonTaskModel;
import cobblemonquestsextended.cobblemon_quests_extended.domain.validation.TaskValidator;
import cobblemonquestsextended.cobblemon_quests_extended.domain.validation.ValidationIssue;
import cobblemonquestsextended.cobblemon_quests_extended.domain.validation.ValidationResult;
import cobblemonquestsextended.cobblemon_quests_extended.preview.LivePreviewPanel;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.ConfigValue;
import dev.ftb.mods.ftblibrary.config.ui.EditConfigScreen;
import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.ui.Button;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.SimpleTextButton;
import dev.ftb.mods.ftblibrary.ui.Theme;
import cobblemonquestsextended.cobblemon_quests_extended.tasks.CobblemonTask;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

import static cobblemonquestsextended.cobblemon_quests_extended.CobblemonQuests.MOD_ID;
import static dev.ftb.mods.ftblibrary.util.TextComponentUtils.hotkeyTooltip;

/**
 * Custom EditConfigScreen for CobblemonTask that provides dynamic field visibility UX.
 *
 * When the user changes the "actions" field, the Accept button changes to "Update Fields"
 * to indicate that the configuration needs to be refreshed to show/hide conditional fields.
 *
 * Also provides real-time validation feedback with errors and warnings.
 */
@Environment(EnvType.CLIENT)
public class CobblemonTaskEditScreen extends EditConfigScreen {
    private final CobblemonTask task;
    private final List<String> initialActions;
    private final Runnable onUpdateFields;
    private boolean actionsChanged = false;
    private Button acceptButton;
    private ConfigValue<List<String>> actionsConfig;

    // Validation
    private final TaskValidator validator = new TaskValidator();
    private ValidationResult lastValidationResult = ValidationResult.valid();

    // Live preview panel
    private LivePreviewPanel previewPanel;
    private static final int PREVIEW_PANEL_WIDTH = 220;
    private static final int PREVIEW_PANEL_MARGIN = 10;

    // Button text components
    private static final Component ACCEPT_TEXT = Component.translatable("gui.accept");
    private static final Component UPDATE_FIELDS_TEXT = Component.translatable(MOD_ID + ".task.update_fields");
    private static final Component VALIDATION_ERROR_TEXT = Component.translatable(MOD_ID + ".task.fix_errors");

    public CobblemonTaskEditScreen(ConfigGroup group, CobblemonTask task, Runnable onUpdateFields) {
        super(group);
        this.task = task;
        this.initialActions = new ArrayList<>(task.actions);
        this.onUpdateFields = onUpdateFields;
        this.actionsConfig = findActionsConfig(group);
        CobblemonQuests.LOGGER.info("[EditScreen] Opened edit screen for task id={}, actions={}", task.id, task.actions);
    }

    /**
     * Finds the "actions" ConfigValue by traversing the group and its subgroups.
     */
    @SuppressWarnings("unchecked")
    private ConfigValue<List<String>> findActionsConfig(ConfigGroup group) {
        // Check values in this group
        for (ConfigValue<?> value : group.getValues()) {
            if ("actions".equals(value.id)) {
                return (ConfigValue<List<String>>) value;
            }
        }
        // Recursively check subgroups
        for (ConfigGroup subgroup : group.getSubgroups()) {
            ConfigValue<List<String>> found = findActionsConfig(subgroup);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    @Override
    public Component getTitle() {
        return Component.translatable(MOD_ID + ".task.title");
    }

    @Override
    protected Panel createBottomPanel() {
        return new DynamicBottomPanel();
    }

    @Override
    public void tick() {
        super.tick();

        // Check if actions have changed since screen opened
        // Compare with the ConfigValue's current edited value, not task.actions
        // (FTB Library stores an internal copy during editing)
        boolean currentlyChanged = false;
        if (actionsConfig != null) {
            List<String> currentActions = actionsConfig.getValue();
            currentlyChanged = !initialActions.equals(currentActions);
        }

        // Only update button if state changed
        if (currentlyChanged != actionsChanged) {
            actionsChanged = currentlyChanged;
            updateAcceptButton();
        }

        // Run validation on current task state
        runValidation();

        // Refresh preview panel with latest task state
        refreshPreviewPanel();
    }

    /**
     * Validates the current task configuration and updates the button state.
     */
    private void runValidation() {
        CobblemonTaskModel model = CobblemonTaskModel.fromTask(task);
        ValidationResult result = validator.validate(model);

        // Only update if result changed (avoid unnecessary redraws)
        if (!isSameValidation(result, lastValidationResult)) {
            CobblemonQuests.LOGGER.info("[EditScreen] Validation changed: {} errors, {} warnings",
                result.getErrorCount(), result.getWarningCount());
            for (ValidationIssue issue : result.getAll()) {
                CobblemonQuests.LOGGER.info("[EditScreen]   - [{}] {}: {}",
                    issue.severity(), issue.field(), issue.messageKey());
            }
            lastValidationResult = result;
            updateAcceptButton();
        }
    }

    /**
     * Compares two validation results for equality.
     */
    private boolean isSameValidation(ValidationResult a, ValidationResult b) {
        if (a.getErrorCount() != b.getErrorCount()) return false;
        if (a.getWarningCount() != b.getWarningCount()) return false;
        return a.getAll().equals(b.getAll());
    }

    @Override
    public void addWidgets() {
        super.addWidgets();
        // Run initial validation
        runValidation();

        // Add live preview panel on the right side
        addPreviewPanel();
    }

    /**
     * Adds the live preview panel to the right side of the screen.
     */
    private void addPreviewPanel() {
        // Calculate position: right side of screen with margin
        int panelX = width - PREVIEW_PANEL_WIDTH - PREVIEW_PANEL_MARGIN;
        int panelY = 30; // Below title bar
        int panelHeight = height - 80; // Leave room for bottom panel

        previewPanel = new LivePreviewPanel(this, task);
        previewPanel.setPosAndSize(panelX, panelY, PREVIEW_PANEL_WIDTH, panelHeight);
        add(previewPanel);

        CobblemonQuests.LOGGER.info("[EditScreen] Added preview panel at x={}, y={}, w={}, h={}",
            panelX, panelY, PREVIEW_PANEL_WIDTH, panelHeight);
    }

    /**
     * Refreshes the preview panel with current task state.
     */
    private void refreshPreviewPanel() {
        if (previewPanel != null) {
            previewPanel.refresh();
        }
    }

    /**
     * Updates the Accept button text based on state:
     * - "Fix Errors" if validation errors exist
     * - "Update Fields" if actions have changed
     * - "Accept" otherwise
     */
    private void updateAcceptButton() {
        if (acceptButton != null && bottomPanel != null) {
            String newState;
            if (lastValidationResult.getErrorCount() > 0) {
                ((SimpleTextButton) acceptButton).setTitle(VALIDATION_ERROR_TEXT);
                newState = "FIX_ERRORS";
            } else if (actionsChanged) {
                ((SimpleTextButton) acceptButton).setTitle(UPDATE_FIELDS_TEXT);
                newState = "UPDATE_FIELDS";
            } else {
                ((SimpleTextButton) acceptButton).setTitle(ACCEPT_TEXT);
                newState = "ACCEPT";
            }
            CobblemonQuests.LOGGER.info("[EditScreen] Button state: {} (errors={}, actionsChanged={})",
                newState, lastValidationResult.getErrorCount(), actionsChanged);
            // Recalculate button positions after text change affects button width
            bottomPanel.alignWidgets();
        }
    }

    @Override
    protected void doAccept() {
        CobblemonQuests.LOGGER.info("[EditScreen] doAccept called - errors={}, actionsChanged={}",
            lastValidationResult.getErrorCount(), actionsChanged);

        // Block save if there are validation errors
        if (lastValidationResult.getErrorCount() > 0) {
            CobblemonQuests.LOGGER.info("[EditScreen] BLOCKED: Cannot save - {} validation errors exist",
                lastValidationResult.getErrorCount());
            for (ValidationIssue issue : lastValidationResult.getErrors()) {
                CobblemonQuests.LOGGER.info("[EditScreen]   Error: [{}] {}", issue.field(), issue.messageKey());
            }
            // Play error sound to indicate the user must fix the issue
            playClickSound();
            // The button text "Fix Errors" already indicates the problem
            // User must add at least one action before saving
            return;
        }

        if (actionsChanged && actionsConfig != null) {
            // Actions changed - manually apply actions to task, then reopen
            CobblemonQuests.LOGGER.info("[EditScreen] Actions changed - updating and reopening screen");
            List<String> newActions = actionsConfig.getValue();
            CobblemonQuests.LOGGER.info("[EditScreen] New actions: {}", newActions);
            task.actions.clear();
            task.actions.addAll(newActions);
            closeGui(false);
            // Schedule reopen for next tick to let GUI system properly reset
            Minecraft.getInstance().tell(onUpdateFields);
        } else {
            // No changes - normal accept behavior
            CobblemonQuests.LOGGER.info("[EditScreen] Normal accept - saving task");
            super.doAccept();
        }
    }

    /**
     * Custom bottom panel that exposes the accept button for text updates.
     */
    private class DynamicBottomPanel extends Panel {
        private final Button buttonCancel;

        public DynamicBottomPanel() {
            super(CobblemonTaskEditScreen.this);

            acceptButton = SimpleTextButton.accept(this, mb -> doAccept(), hotkeyTooltip("⇧ + Enter"));
            buttonCancel = SimpleTextButton.cancel(this, mb -> doCancel(), hotkeyTooltip("ESC"));
        }

        @Override
        public void addWidgets() {
            add(acceptButton);
            add(buttonCancel);
        }

        @Override
        public void alignWidgets() {
            buttonCancel.setPos(width - buttonCancel.width - 5, 2);
            acceptButton.setPos(buttonCancel.posX - acceptButton.width - 5, 2);
        }

        @Override
        public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
            theme.drawPanelBackground(graphics, x, y, w, h);
            Color4I.GRAY.withAlpha(64).draw(graphics, x, y, w, 1);
        }
    }
}
