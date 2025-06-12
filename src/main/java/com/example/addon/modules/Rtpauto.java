package dev.yourname.stashfinder;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class StashFinderModule extends Module {

    private enum State {
        RTPING, MINING, CHECKING
    }

    private State state = State.RTPING;
    private BlockPos startMinePos;

    public StashFinderModule() {
        super("StashFinder", "Automatically RTPs, mines, and searches for stashes.", Category.Misc);
    }

    @Override
    public void onActivate() {
        state = State.RTPING;
        ChatUtils.info("StashFinder activated.");
    }

    @Override
    public void onDeactivate() {
        state = State.RTPING;
        ChatUtils.info("StashFinder deactivated.");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        ClientPlayerEntity player = mc.player;
        if (player == null || mc.world == null) return;

        switch (state) {
            case RTPING -> {
                ChatUtils.sendPlayerMsg("rtp");
                state = State.MINING;
                startMinePos = player.getBlockPos();
            }

            case MINING -> {
                BlockPos current = player.getBlockPos();
                BlockPos below = current.down();

                if (below.getY() <= -60 || mc.world.getBlockState(below).getBlock() == Blocks.BEDROCK) {
                    state = State.CHECKING;
                    break;
                }

                mc.interactionManager.updateBlockBreakingProgress(below, Direction.DOWN);
            }

            case CHECKING -> {
                if (foundStashNearby(player.getBlockPos())) {
                    ChatUtils.info("STASH FOUND!");
                    mc.player.networkHandler.disconnect(mc.player.getName(), "STASH FOUND!");
                    this.toggle(); // disables module after disconnect
                } else {
                    ChatUtils.info("No stash found. RTPing again...");
                    state = State.RTPING;
                }
            }
        }
    }

    private boolean foundStashNearby(BlockPos pos) {
        int radius = 8;

        int chestCount = 0;
        int shulkerCount = 0;
        boolean foundSpawner = false;

        for (BlockPos checkPos : BlockPos.iterate(
                pos.add(-radius, -radius, -radius),
                pos.add(radius, radius, radius))) {

            Block block = mc.world.getBlockState(checkPos).getBlock();

            if (block == Blocks.CHEST) chestCount++;
            else if (block == Blocks.SHULKER_BOX || isAnyShulkerVariant(block)) shulkerCount++;
            else if (block == Blocks.SPAWNER) foundSpawner = true;

            if (chestCount >= 3 || shulkerCount >= 2 || foundSpawner) return true;
        }

        return false;
    }

    private boolean isAnyShulkerVariant(Block block) {
        return block.getTranslationKey().toLowerCase().contains("shulker_box");
    }
}
