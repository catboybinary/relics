package it.hurts.sskirillss.relics.client.screen.description.widgets.ability;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.sskirillss.relics.client.screen.base.IHoverableWidget;
import it.hurts.sskirillss.relics.client.screen.description.AbilityDescriptionScreen;
import it.hurts.sskirillss.relics.client.screen.description.widgets.base.AbstractDescriptionWidget;
import it.hurts.sskirillss.relics.client.screen.utils.ScreenUtils;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityEntry;
import it.hurts.sskirillss.relics.items.relics.base.utils.AbilityUtils;
import it.hurts.sskirillss.relics.items.relics.base.utils.LevelingUtils;
import it.hurts.sskirillss.relics.network.NetworkHandler;
import it.hurts.sskirillss.relics.network.packets.leveling.PacketRelicTweak;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class AbilityUpgradeButtonWidget extends AbstractDescriptionWidget implements IHoverableWidget {
    private final AbilityDescriptionScreen screen;
    private final String ability;

    public AbilityUpgradeButtonWidget(int x, int y, AbilityDescriptionScreen screen, String ability) {
        super(x, y, 18, 18);

        this.screen = screen;
        this.ability = ability;
    }

    @Override
    public boolean isLocked() {
        return !AbilityUtils.mayPlayerUpgrade(MC.player, screen.stack, ability);
    }

    @Override
    public void onPress() {
        if (!isLocked())
            NetworkHandler.sendToServer(new PacketRelicTweak(screen.pos, ability, PacketRelicTweak.Operation.INCREASE));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(guiGraphics, pMouseX, pMouseY, pPartialTick);

        TextureManager manager = MC.getTextureManager();

        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, AbilityDescriptionScreen.TEXTURE);

        manager.bindForSetup(AbilityDescriptionScreen.TEXTURE);

        if (AbilityUtils.mayPlayerUpgrade(MC.player, screen.stack, ability)) {
            guiGraphics.blit(AbilityDescriptionScreen.TEXTURE, getX(), getY(), 258, 0, 18, 18, 512, 512);

            if (isHovered)
                guiGraphics.blit(AbilityDescriptionScreen.TEXTURE, getX() - 1, getY() - 1, 318, 0, 20, 20, 512, 512);
        } else {
            guiGraphics.blit(AbilityDescriptionScreen.TEXTURE, getX(), getY(), 258, 20, 18, 18, 512, 512);

            RelicAbilityEntry abilityData = AbilityUtils.getRelicAbilityEntry(screen.stack.getItem(), ability);

            if (abilityData == null)
                return;

            if (AbilityUtils.canUseAbility(screen.stack, ability) && !abilityData.getStats().isEmpty() && isHovered)
                guiGraphics.blit(AbilityDescriptionScreen.TEXTURE, getX() - 1, getY() - 1, 318, 22, 20, 20, 512, 512);
        }
    }

    @Override
    public void onHovered(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (!AbilityUtils.canUseAbility(screen.stack, ability))
            return;

        RelicAbilityEntry data = AbilityUtils.getRelicAbilityEntry(screen.stack.getItem(), ability);

        if (data.getStats().isEmpty())
            return;

        List<FormattedCharSequence> tooltip = Lists.newArrayList();

        PoseStack poseStack = guiGraphics.pose();

        int maxWidth = 100;
        int renderWidth = 0;

        int requiredPoints = data.getRequiredPoints();
        int requiredExperience = AbilityUtils.getUpgradeRequiredExperience(screen.stack, ability);

        int points = LevelingUtils.getPoints(screen.stack);
        int experience = MC.player.totalExperience;

        MutableComponent negativeStatus = Component.translatable("tooltip.relics.relic.status.negative");
        MutableComponent positiveStatus = Component.translatable("tooltip.relics.relic.status.positive");

        List<MutableComponent> entries = Lists.newArrayList(
                Component.translatable("tooltip.relics.relic.upgrade.description"),
                Component.literal(" "));

        if (!AbilityUtils.isAbilityMaxLevel(screen.stack, ability))
            entries.add(Component.translatable("tooltip.relics.relic.upgrade.cost", requiredPoints,
                    (requiredPoints > points ? negativeStatus : positiveStatus), requiredExperience,
                    (requiredExperience > experience ? negativeStatus : positiveStatus)));
        else
            entries.add(Component.literal("▶ ").append(Component.translatable("tooltip.relics.relic.upgrade.locked")));

        for (MutableComponent entry : entries) {
            int entryWidth = (MC.font.width(entry) + 4) / 2;

            if (entryWidth > renderWidth)
                renderWidth = Math.min(entryWidth, maxWidth);

            tooltip.addAll(MC.font.split(entry, maxWidth * 2));
        }

        int height = tooltip.size() * 4;

        int renderX = getX() + width + 3;
        int renderY = getY() - height / 2;

        ScreenUtils.drawTexturedTooltipBorder(guiGraphics, new ResourceLocation(Reference.MODID, "textures/gui/tooltip/border/paper.png"),
                renderWidth, height, renderX, renderY);

        int yOff = 0;

        poseStack.scale(0.5F, 0.5F, 0.5F);

        for (FormattedCharSequence entry : tooltip) {
            guiGraphics.drawString(MC.font, entry, (renderX + 9) * 2, (renderY + 9 + yOff) * 2, 0x412708, false);

            yOff += 4;
        }

        poseStack.scale(1F, 1F, 1F);
    }
}