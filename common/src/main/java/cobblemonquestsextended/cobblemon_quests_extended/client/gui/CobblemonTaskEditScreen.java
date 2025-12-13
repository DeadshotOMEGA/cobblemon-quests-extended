package cobblemonquestsextended.cobblemon_quests_extended.client.gui;

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
 */
@Environment(EnvType.CLIENT)
public class CobblemonTaskEditScreen extends EditConfigScreen {
    private final CobblemonTask task;
    private final List<String> initialActions;
    private final Runnable onUpdateFields;
    private boolean actionsChanged = false;
    private Button acceptButton;
    private ConfigValue<List<String>> actionsConfig;

    // Button text components
    private static final Component ACCEPT_TEXT = Component.translatable("gui.accept");
    private static final Component UPDATE_FIELDS_TEXT = Component.translatable(MOD_ID + ".task.update_fields");

    public CobblemonTaskEditScreen(ConfigGroup group, CobblemonTask task, Runnable onUpdateFields) {
        super(group);
        this.task = task;
        this.initialActions = new ArrayList<>(task.actions);
        this.onUpdateFields = onUpdateFields;
        this.actionsConfig = findActionsConfig(group);
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
    }

    /**
     * Updates the Accept button text based on whether actions have changed.
     */
    private void updateAcceptButton() {
        if (acceptButton != null && bottomPanel != null) {
            if (actionsChanged) {
                ((SimpleTextButton) acceptButton).setTitle(UPDATE_FIELDS_TEXT);
            } else {
                ((SimpleTextButton) acceptButton).setTitle(ACCEPT_TEXT);
            }
            // Recalculate button positions after text change affects button width
            bottomPanel.alignWidgets();
        }
    }

    @Override
    protected void doAccept() {
        if (actionsChanged && actionsConfig != null) {
            // Actions changed - manually apply actions to task, then reopen
            // We don't use group.save(true) because it triggers the savedCallback
            // which calls gui.run() and returns to parent screen before we can reopen
            List<String> newActions = actionsConfig.getValue();
            task.actions.clear();
            task.actions.addAll(newActions);
            closeGui(false);
            // Schedule reopen for next tick to let GUI system properly reset
            Minecraft.getInstance().tell(onUpdateFields);
        } else {
            // No changes - normal accept behavior
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
