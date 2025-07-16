package net.daimon.moreQuestOptions.mixin;

import dev.ftb.mods.ftbquests.quest.task.Task;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Task.class)
public interface TaskAccessor {


    ///  OptionalTask
    @Accessor
    boolean getOptionalTask();

    @Accessor("optionalTask")
    void setOptionalTask(boolean optionalTask);


}
