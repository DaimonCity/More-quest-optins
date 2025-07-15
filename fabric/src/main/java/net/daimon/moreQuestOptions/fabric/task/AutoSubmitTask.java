package net.daimon.moreQuestOptions.fabric.task;

import dev.ftb.mods.ftblibrary.config.Tristate;
import dev.ftb.mods.ftbquests.integration.item_filtering.ItemMatchingSystem;
import dev.ftb.mods.ftbquests.item.MissingItem;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.task.ItemTask;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import static net.daimon.moreQuestOptions.fabric.MoreQuestOptionsFabric.AUTO_SUBMIT_TASK;
import static net.daimon.moreQuestOptions.fabric.MoreQuestOptionsFabric.MOD_LOGGER;

public class AutoSubmitTask extends ItemTask {

    public AutoSubmitTask(long id, Quest quest) {
        super(id, quest);
    }

    @Override
    public TaskType getType() {
        return AUTO_SUBMIT_TASK;
    }

    @Override
    public boolean consumesResources() {
        return true;
    }

    @Override
    public boolean submitItemsOnInventoryChange() {
        return true;
    }

    @Override
    public void submitTask(TeamData teamData, ServerPlayer player, ItemStack craftedItem) {
        MOD_LOGGER.info("Submit Task");
        MOD_LOGGER.info("Is task screen only: " + isTaskScreenOnly());
        MOD_LOGGER.info("Is checkTaskSequence: " + checkTaskSequence(teamData));
        MOD_LOGGER.info("Is teamData.isCompleted: " + teamData.isCompleted(this));
        MOD_LOGGER.info("Is craftedItem.isEmpty: " + craftedItem.isEmpty());
        if (!this.isTaskScreenOnly() && this.checkTaskSequence(teamData) && !teamData.isCompleted(this) && !(this.getItemStack().getItem() instanceof MissingItem) && !(craftedItem.getItem() instanceof MissingItem)) {
            if (craftedItem.isEmpty()) {
                boolean changed = false;

                for(int i = 0; i < player.getInventory().items.size(); ++i) {
                    ItemStack stack = (ItemStack)player.getInventory().items.get(i);
                    ItemStack stack1 = this.insert(teamData, stack, false);
                    if (stack != stack1) {
                        changed = true;
                        player.getInventory().items.set(i, stack1.isEmpty() ? ItemStack.EMPTY : stack1);
                    }
                }

                MOD_LOGGER.info("Is changed " + changed);
                if (changed) {
                    player.getInventory().setChanged();
                    player.containerMenu.broadcastChanges();
                }
            }
        }

    }

}
