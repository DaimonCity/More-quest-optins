package net.daimon.moreQuestOptions.fabric;

import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftbquests.api.FTBQuestsAPI;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import dev.ftb.mods.ftbquests.quest.task.TaskTypes;
import net.daimon.moreQuestOptions.MoreQuestOptions;
import net.daimon.moreQuestOptions.fabric.task.AutoSubmitTask;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MoreQuestOptionsFabric implements ModInitializer {
    public static final Logger MOD_LOGGER = LoggerFactory.getLogger("MoreQuestOptions");
    public static TaskType AUTO_SUBMIT_TASK;



    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        MoreQuestOptions.init();
        AUTO_SUBMIT_TASK = TaskTypes.register(FTBQuestsAPI.rl("autosubmit"), AutoSubmitTask::new, () -> Icon.getIcon("minecraft:item/ender_pearl"));
    }
}
