package it.hurts.sskirillss.relics.items.relics.feet;

import it.hurts.sskirillss.relics.client.renderer.items.models.OldBootModel;
import it.hurts.sskirillss.relics.client.tooltip.base.AbilityTooltip;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicTooltip;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicAttribute;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicLoot;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.loot.LootTables;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class OldBootItem extends RelicItem<OldBootItem.Stats> {
    public OldBootItem() {
        super(RelicData.builder()
                .rarity(Rarity.COMMON)
                .config(Stats.class)
                .loot(RelicLoot.builder()
                        .table(LootTables.FISHING.toString())
                        .chance(0.1F)
                        .build())
                .build());
    }

    @Override
    public RelicTooltip getTooltip(ItemStack stack) {
        return RelicTooltip.builder()
                .ability(AbilityTooltip.builder()
                        .arg("+" + (int) (config.speedModifier * 100) + "%")
                        .build())
                .build();
    }

    @Override
    public RelicAttribute getAttributes(ItemStack stack) {
        return RelicAttribute.builder()
                .attribute(new RelicAttribute.Modifier(Attributes.MOVEMENT_SPEED, config.speedModifier))
                .build();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public BipedModel<LivingEntity> getModel() {
        return new OldBootModel();
    }

    public static class Stats extends RelicStats {
        public float speedModifier = 0.05F;
    }
}