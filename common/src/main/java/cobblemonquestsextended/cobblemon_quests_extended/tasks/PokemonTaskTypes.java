package cobblemonquestsextended.cobblemon_quests_extended.tasks;

import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import dev.ftb.mods.ftbquests.quest.task.TaskTypes;
import net.minecraft.resources.ResourceLocation;

import static cobblemonquestsextended.cobblemon_quests_extended.CobblemonQuests.MOD_ID;

public interface PokemonTaskTypes {

    ResourceLocation icon = ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/item/poke_ball_icon.png");
    TaskType COBBLEMON = TaskTypes.register(ResourceLocation.fromNamespaceAndPath("cobblemon_tasks", "cobblemon_task"), CobblemonTask::new, () -> Icon.getIcon(icon));

    static void init() {
    }
}
