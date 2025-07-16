package net.daimon.moreQuestOptions.mixin;

import dev.ftb.mods.ftbquests.quest.QuestObjectBase;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(QuestObjectBase.class)
public interface QuestObjectBaseAccessor {

    @Accessor
    List<String> getTags();


    @Accessor
    ItemStack getRawIcon();

    @Accessor("rawIcon")
    void setRawIcon(ItemStack rawIcon);
}
