package com.example.addon.modules;

import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.renderer.ShapeMode;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;

public class SpawnerESP extends Module {
    public SpawnerESP() {
        super(Categories.Render, "Spawner-ESP", "Highlights mob spawners.");
    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        if (mc.world == null || mc.player == null) return;

        for (BlockPos pos : BlockPos.iterateOutwards(mc.player.getBlockPos(), 64, 64, 64)) {
            if (mc.world.getBlockState(pos).getBlock() == Blocks.SPAWNER) {
                event.renderer.box(
                    pos,
                    new Color(255, 0, 0, 50),  // fill
                    new Color(255, 0, 0),      // outline
                    ShapeMode.Both,
                    0
                );
            }
        }
    }
}
