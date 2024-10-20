package com.gregtechceu.gtceu.core.mixins;

import com.gregtechceu.gtceu.client.model.GTMetadataSection;
import com.gregtechceu.gtceu.client.shader.GTShaders;
import com.gregtechceu.gtceu.client.util.BloomEffectUtil;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraftforge.client.model.lighting.QuadLighter;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = QuadLighter.class, remap = false)
public class QuadLighterMixin {

    @WrapOperation(method = "process",
                   at = @At(value = "INVOKE",
                            target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;putBulkData(Lcom/mojang/blaze3d/vertex/PoseStack$Pose;Lnet/minecraft/client/renderer/block/model/BakedQuad;[FFFF[IIZ)V",
                            remap = true))
    private void gtceu$renderToEmissiveBuffer(VertexConsumer instance, PoseStack.Pose poseEntry, BakedQuad quad,
                                              float[] colorMuls, float red, float green, float blue,
                                              int[] combinedLights, int combinedOverlay, boolean mulColor,
                                              Operation<Void> original) {
        BlockPos chunkOrigin = BloomEffectUtil.CURRENT_RENDERING_CHUNK_POS.get();
        // Check if quad is full brightness OR we have bloom enabled for the quad
        if (GTShaders.allowedShader() && chunkOrigin != null &&
                (!quad.isShade() || GTMetadataSection.hasBloom(quad.getSprite()))) {
            original.call(BloomEffectUtil.getOrStartBloomBuffer(chunkOrigin), poseEntry, quad,
                    colorMuls, red, green, blue,
                    combinedLights, combinedOverlay, mulColor);
        }
        original.call(instance, poseEntry, quad,
                colorMuls, red, green, blue,
                combinedLights, combinedOverlay, mulColor);
    }
}
