package com.example.addon.modules;

import com.example.addon.AddonTemplate;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.Vec3d;

public class HealthDisplay extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgRender = settings.createGroup("Render");

    private final Setting<Color> highHealthColor = sgRender.add(new ColorSetting.Builder()
        .name("high-health-color")
        .description("Color for high health (above 70%).")
        .defaultValue(Color.GREEN)
        .build()
    );

    private final Setting<Color> midHealthColor = sgRender.add(new ColorSetting.Builder()
        .name("mid-health-color")
        .description("Color for medium health (30% - 70%).")
        .defaultValue(Color.YELLOW)
        .build()
    );

    private final Setting<Color> lowHealthColor = sgRender.add(new ColorSetting.Builder()
        .name("low-health-color")
        .description("Color for low health (below 30%).")
        .defaultValue(Color.RED)
        .build()
    );

    public HealthDisplay() {
        super(AddonTemplate.CATEGORY, "health-display", "Shows opponents' health above their heads.");
    }

    @EventHandler
    private void onRender3D(Render3DEvent event) {
        if (mc.world == null || mc.player == null) return;

        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player) continue; // Skip yourself
            if (player.isDead()) continue;

            // Optional: skip friends or teammates (if you implement friend system)
            // if (isFriend(player)) continue;

            float health = player.getHealth();
            float maxHealth = player.getMaxHealth();
            float absorption = player.getAbsorptionAmount();
            float totalHealth = health + absorption;

            float healthPercent = totalHealth / maxHealth;

            Color healthColor;
            if (healthPercent > 0.7f) healthColor = highHealthColor.get();
            else if (healthPercent > 0.3f) healthColor = midHealthColor.get();
            else healthColor = lowHealthColor.get();

            // Position above the player's head
            Vec3d pos = player.getPos().add(0, player.getHeight() + 0.5, 0);

            // Prepare health text, show absorption as +X if any
            String healthText = String.format("%.1f", health / 2.0); // Convert health to hearts (half hearts)
            if (absorption > 0) healthText += String.format(" +%.1f", absorption / 2.0);

            // Render the text at the 3D position
            drawText(event, healthText, pos, healthColor);
        }
    }

    // Helper method to draw floating text in the world (uses Minecraft's TextRenderer)
    private void drawText(Render3DEvent event, String text, Vec3d pos, Color color) {
        MatrixStack matrices = event.matrixStack;

        double x = pos.x - mc.getEntityRenderDispatcher().camera.getPos().x;
        double y = pos.y - mc.getEntityRenderDispatcher().camera.getPos().y;
        double z = pos.z - mc.getEntityRenderDispatcher().camera.getPos().z;

        matrices.push();
        matrices.translate(x, y, z);

        // Face the camera
        matrices.multiply(mc.getEntityRenderDispatcher().camera.getRotation());

        // Scale down the text
        float scale = 0.025f;
        matrices.scale(-scale, -scale, scale);

        // Draw the text centered horizontally
        int width = mc.textRenderer.getWidth(text);
        mc.textRenderer.draw(matrices, new LiteralText(text), -width / 2f, 0, color.getPacked());

        matrices.pop();
    }

    // Optional: friend check method stub
    /*
    private boolean isFriend(PlayerEntity player) {
        // Implement your friend system or team check here
        return false;
    }
    */
}
