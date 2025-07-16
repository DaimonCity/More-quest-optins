package net.daimon.moreQuestOptions.fabric.task;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.StringConfig;
import dev.ftb.mods.ftblibrary.config.Tristate;
import dev.ftb.mods.ftbquests.client.ConfigIconItemStack;
import dev.ftb.mods.ftbquests.integration.item_filtering.ItemMatchingSystem;
import dev.ftb.mods.ftbquests.item.MissingItem;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.task.ItemTask;
import dev.ftb.mods.ftbquests.quest.task.Task;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import net.daimon.moreQuestOptions.mixin.ItemTaskAccessor;
import net.daimon.moreQuestOptions.mixin.QuestObjectBaseAccessor;
import net.daimon.moreQuestOptions.mixin.TaskAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.regex.Pattern;

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
    public boolean submitItemsOnInventoryChange() {
        return true;
    }

    @Override
    public ItemStack insert(TeamData teamData, ItemStack stack, boolean simulate) {
        if (!teamData.isCompleted(this) && this.test(stack)) {
            long add = Math.min(stack.getCount(), this.getMaxProgress() - teamData.getProgress(this));
            if (add > 0L) {
                if (!simulate && teamData.getFile().isServerSide()) {
                    teamData.addProgress(this, add);
                }

                ItemStack copy = stack.copy();
                copy.setCount((int) ((long) stack.getCount() - add));
                return copy;
            }
        }

        return stack;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void fillConfigGroup(ConfigGroup config) {
        if (this.hasTitleConfig()) {
            config.addString("title", this.getRawTitle(), this::setRawTitle, "").setNameKey("ftbquests.title").setOrder(-127);
        }

        if (this.hasIconConfig()) {
            (config.add("icon", new ConfigIconItemStack(), ((QuestObjectBaseAccessor)this).getRawIcon(), (v) -> ((QuestObjectBaseAccessor)this).setRawIcon(v), ItemStack.EMPTY)).setNameKey("ftbquests.icon").setOrder(-126);
        }

        config.addList("tags", ((QuestObjectBaseAccessor)this).getTags(), new StringConfig(Pattern.compile("^[a-z0-9_]*$")), "").setNameKey("ftbquests.tags").setOrder(-125);

        config.addBool("disable_toast", this.disableToast, (v) -> this.disableToast = v, false).setNameKey("ftbquests.disable_completion_toast").setCanEdit(this.getQuestChapter() == null || !this.getQuestChapter().isAlwaysInvisible()).setOrder(127); // QObject
        config.addBool("optional_task", ((TaskAccessor) this).getOptionalTask(), (v) -> ((TaskAccessor) this).setOptionalTask(v), false).setNameKey("ftbquests.quest.misc.optional_task"); // Task
        config.addItemStack("item", this.getItemStack(), (v) -> this.setStackAndCount(v, (int) this.getMaxProgress()), ItemStack.EMPTY, true, false).setNameKey("ftbquests.task.ftbquests.item");
        config.addLong("count", this.getMaxProgress(), (v) -> this.setStackAndCount(this.getItemStack(), v.intValue()), 1L, 1L, Long.MAX_VALUE);
        config.addEnum("only_from_crafting", ((ItemTaskAccessor) this).getOnlyFromCrafting(), (v) -> ((ItemTaskAccessor) this).setOnlyFromCrafting(v), Tristate.NAME_MAP);
        config.addEnum("match_components", ((ItemTaskAccessor) this).getMatchComponents(), (v) -> ((ItemTaskAccessor) this).setMatchComponents(v), ItemMatchingSystem.ComponentMatchType.NAME_MAP);
        config.addBool("task_screen_only", this.isTaskScreenOnly(), (v) -> ((ItemTaskAccessor) this).setTaskScreenOnly(v), false);
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

                for (int i = 0; i < player.getInventory().items.size(); ++i) {
                    ItemStack stack = (ItemStack) player.getInventory().items.get(i);
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
