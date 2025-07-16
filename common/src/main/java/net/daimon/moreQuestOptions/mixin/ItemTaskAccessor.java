package net.daimon.moreQuestOptions.mixin;


import dev.ftb.mods.ftblibrary.config.Tristate;
import dev.ftb.mods.ftbquests.integration.item_filtering.ItemMatchingSystem;
import dev.ftb.mods.ftbquests.quest.task.ItemTask;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemTask.class)
public interface ItemTaskAccessor {

    /// TaskScreenOnly - only set method (get is existed in FTB Quests)
    @Accessor("taskScreenOnly")
    void setTaskScreenOnly(boolean taskScreenOnly);

    /// MatchComponents Field - set and get methods
    @Accessor
    ItemMatchingSystem.ComponentMatchType getMatchComponents();

    @Accessor("matchComponents")
    void setMatchComponents(ItemMatchingSystem.ComponentMatchType matchComponents);

    /// OnlyFromCrafting Field - set and get methods
    @Accessor
    Tristate getOnlyFromCrafting();

    @Accessor("onlyFromCrafting")
    void setOnlyFromCrafting(Tristate onlyFromCrafting);



}
