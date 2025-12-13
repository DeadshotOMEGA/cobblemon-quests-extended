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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private final ConfigGroup configGroup;
    private final List<String> initialActions;
    private final Runnable onUpdateFields;
    private boolean actionsChanged = false;
    private Button acceptButton;

    // Cached ConfigValue references for all editable fields
    private final Map<String, ConfigValue<?>> configValueCache = new HashMap<>();

    // Validation
    private final TaskValidator validator = new TaskValidator();
    private ValidationResult lastValidationResult = ValidationResult.valid();

    // Live preview panel
    private LivePreviewPanel previewPanel;
    private static final int PREVIEW_PANEL_WIDTH = 190;
    private static final int PREVIEW_PANEL_GAP = 12;

    // Button text components
    private static final Component ACCEPT_TEXT = Component.translatable("gui.accept");
    private static final Component UPDATE_FIELDS_TEXT = Component.translatable(MOD_ID + ".task.update_fields");
    private static final Component VALIDATION_ERROR_TEXT = Component.translatable(MOD_ID + ".task.fix_errors");

    public CobblemonTaskEditScreen(ConfigGroup group, CobblemonTask task, Runnable onUpdateFields) {
        super(group);
        this.task = task;
        this.configGroup = group;
        this.initialActions = new ArrayList<>(task.actions);
        this.onUpdateFields = onUpdateFields;
        // Cache all ConfigValues for real-time preview updates
        cacheAllConfigValues(group);
        CobblemonQuests.LOGGER.info("[EditScreen] Opened edit screen for task id={}, actions={}", task.id, task.actions);
    }

    /**
     * Recursively caches all ConfigValues from the group and its subgroups.
     * This allows reading current edited values for the preview panel.
     */
    private void cacheAllConfigValues(ConfigGroup group) {
        for (ConfigValue<?> value : group.getValues()) {
            configValueCache.put(value.id, value);
        }
        for (ConfigGroup subgroup : group.getSubgroups()) {
            cacheAllConfigValues(subgroup);
        }
    }

    /**
     * Gets a ConfigValue by id from the cache.
     */
    @SuppressWarnings("unchecked")
    private <T> ConfigValue<T> getConfigValue(String id) {
        return (ConfigValue<T>) configValueCache.get(id);
    }

    /**
     * Gets the current value from a ConfigValue, or returns the default if not found.
     */
    @SuppressWarnings("unchecked")
    private <T> T getCurrentValue(String id, T defaultValue) {
        ConfigValue<T> config = getConfigValue(id);
        if (config != null) {
            return config.getValue();
        }
        return defaultValue;
    }

    @Override
    public Component getTitle() {
        return Component.translatable(MOD_ID + ".task.title");
    }

    @Override
    public boolean onInit() {
        // Call parent to calculate base dimensions
        boolean result = super.onInit();

        // Cap the base content width to a reasonable size
        int maxContentWidth = 280;
        int contentWidth = Math.min(width, maxContentWidth);

        // Set total width: content area + gap + preview panel (preview on RIGHT)
        setWidth(contentWidth + PREVIEW_PANEL_GAP + PREVIEW_PANEL_WIDTH);

        return result;
    }

    @Override
    public void alignWidgets() {
        // Calculate dimensions
        int contentWidth = width - PREVIEW_PANEL_WIDTH - PREVIEW_PANEL_GAP;
        int topPanelHeight = getTopPanelHeight();
        int bottomPanelHeight = 25; // BOTTOM_PANEL_H

        // Position top panel (spans full width for title)
        topPanel.setPosAndSize(0, 0, width, topPanelHeight);
        topPanel.alignWidgets();

        // Position main panel on the LEFT (config area only)
        mainPanel.setPosAndSize(0, topPanelHeight, contentWidth, height - topPanelHeight - bottomPanelHeight);
        mainPanel.alignWidgets();

        // Force button widths to fit within content area
        int buttonWidth = contentWidth - 14; // Account for scrollbar
        mainPanel.getWidgets().forEach(w -> w.setWidth(buttonWidth));

        // Position scrollBar at right edge of main panel
        if (scrollBar != null) {
            scrollBar.setPosAndSize(contentWidth - 12, topPanelHeight, 12, height - topPanelHeight - bottomPanelHeight);
        }

        // Position bottom panel (spans full width for buttons)
        if (bottomPanel != null) {
            bottomPanel.setPosAndSize(0, height - bottomPanelHeight, width, bottomPanelHeight);
            bottomPanel.alignWidgets();
        }

        // Position preview panel on the RIGHT side (completely separate from mainPanel)
        if (previewPanel != null) {
            previewPanel.setPosAndSize(
                contentWidth + PREVIEW_PANEL_GAP,
                topPanelHeight,
                PREVIEW_PANEL_WIDTH,
                height - topPanelHeight - bottomPanelHeight
            );
        }
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
        ConfigValue<List<String>> actionsConfig = getConfigValue("actions");
        if (actionsConfig != null) {
            List<String> currentActions = actionsConfig.getValue();
            currentlyChanged = !initialActions.equals(currentActions);
        }

        // Only update button if state changed
        if (currentlyChanged != actionsChanged) {
            actionsChanged = currentlyChanged;
            updateAcceptButton();
        }

        // Build model from current ConfigValue state and run validation
        CobblemonTaskModel currentModel = buildModelFromConfigValues();
        runValidation(currentModel);

        // Refresh preview panel with latest model
        refreshPreviewPanel(currentModel);
    }

    /**
     * Builds a CobblemonTaskModel from current ConfigValue state.
     * This reads the edited values directly from the config UI, not the stale task object.
     */
    private CobblemonTaskModel buildModelFromConfigValues() {
        return CobblemonTaskModel.builder()
            .id(task.id)
            .amount(getCurrentValue("amount", task.amount))
            .shiny(getCurrentValue("shiny", task.shiny))
            .timeMin(getCurrentValue("time_min", task.timeMin))
            .timeMax(getCurrentValue("time_max", task.timeMax))
            .minLevel(getCurrentValue("min_level", task.minLevel))
            .maxLevel(getCurrentValue("max_level", task.maxLevel))
            .dexProgress(getCurrentValue("dex_progress", task.dexProgress))
            .actions(getCurrentValue("actions", task.actions))
            .biomes(getCurrentValue("biomes", task.biomes))
            .dimensions(getCurrentValue("dimensions", task.dimensions))
            .forms(getCurrentValue("forms", task.forms))
            .genders(getCurrentValue("genders", task.genders))
            .pokeBallsUsed(getCurrentValue("pokeballs", task.pokeBallsUsed))
            .pokemons(getCurrentValue("pokemons", task.pokemons))
            .pokemonTypes(getCurrentValue("pokemon_types", task.pokemonTypes))
            .regions(getCurrentValue("regions", task.regions))
            .natures(getCurrentValue("natures", task.natures))
            .teraTypes(getCurrentValue("tera_types", task.teraTypes))
            .megaForms(getCurrentValue("mega_forms", task.megaForms))
            .zCrystals(getCurrentValue("z_crystals", task.zCrystals))
            .dynamaxTypes(getCurrentValue("dynamax_types", task.dynamaxTypes))
            .build();
    }

    /**
     * Validates the current task configuration and updates the button state.
     * Uses the pre-built model from ConfigValue state.
     */
    private void runValidation(CobblemonTaskModel model) {
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

        // Build initial model and run validation
        CobblemonTaskModel initialModel = buildModelFromConfigValues();
        runValidation(initialModel);

        // Add live preview panel (positioned in alignWidgets)
        previewPanel = new LivePreviewPanel(this, task);
        previewPanel.refresh(initialModel);
        add(previewPanel);
    }

    /**
     * Refreshes the preview panel with a model built from current ConfigValue state.
     */
    private void refreshPreviewPanel(CobblemonTaskModel model) {
        if (previewPanel != null) {
            previewPanel.refresh(model);
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

        ConfigValue<List<String>> actionsConfig = getConfigValue("actions");
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
