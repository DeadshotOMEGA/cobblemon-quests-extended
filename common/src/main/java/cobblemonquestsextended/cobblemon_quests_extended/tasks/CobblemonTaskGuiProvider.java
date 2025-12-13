package cobblemonquestsextended.cobblemon_quests_extended.tasks;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.task.Task;
import cobblemonquestsextended.cobblemon_quests_extended.client.gui.CobblemonTaskEditScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

import java.util.function.BiConsumer;

import static cobblemonquestsextended.cobblemon_quests_extended.CobblemonQuests.MOD_ID;

/**
 * Custom GuiProvider for CobblemonTask that supports dynamic field visibility.
 *
 * When the user changes the "actions" field, the Accept button changes to "Update Fields".
 * Clicking it reopens the screen with rebuilt fields based on the new actions.
 * This provides clear UX for the conditional field visibility feature.
 */
@Environment(EnvType.CLIENT)
public class CobblemonTaskGuiProvider {

    /**
     * Opens the task configuration GUI with dynamic rebuild support.
     * Called by FTB Quests when creating a new CobblemonTask.
     *
     * @param gui The parent GUI runnable
     * @param quest The quest this task belongs to
     * @param callback Called when configuration is accepted with the task and extra NBT
     */
    public static void openTaskGui(Runnable gui, Quest quest, BiConsumer<Task, CompoundTag> callback) {
        CobblemonTask task = new CobblemonTask(0L, quest);
        openConfigScreen(gui, task, callback);
    }

    /**
     * Opens the configuration screen with dynamic field visibility support.
     * Uses custom CobblemonTaskEditScreen that changes Accept button to "Update Fields"
     * when actions are modified.
     *
     * @param gui The parent GUI runnable
     * @param task The task being configured
     * @param callback Called when configuration is complete (null for edit mode)
     */
    public static void openConfigScreen(Runnable gui, CobblemonTask task, BiConsumer<Task, CompoundTag> callback) {
        // Create config group with short id to keep breadcrumbs short
        ConfigGroup group = new ConfigGroup("task", accepted -> {
            if (!accepted) {
                gui.run();
                return;
            }

            // Normal accept - complete the configuration
            gui.run();
            if (callback != null) {
                callback.accept(task, task.getType().makeExtraNBT());
            }
        }) {
            @Override
            public Component getName() {
                return Component.translatable(MOD_ID + ".task.title");
            }

            @Override
            public String getNameKey() {
                return MOD_ID + ".config.root";
            }
        };

        // Populate config fields directly (skip createSubGroup to avoid deep breadcrumb nesting)
        task.fillConfigGroup(group);

        // Open custom edit screen with dynamic button behavior
        // When actions change, "Accept" becomes "Update Fields" and reopens the screen
        new CobblemonTaskEditScreen(group, task, () -> {
            openConfigScreen(gui, task, callback);
        }).openGui();
    }
}
