package com.nisska.gptclient;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudElementRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayDeque;
import java.util.Deque;

public final class GptClient implements ClientModInitializer {
    public static boolean hudEnabled = true;
    public static boolean fullbrightEnabled = false;
    public static boolean performanceMode = false;
    public static final Deque<Long> clicks = new ArrayDeque<>();
    private static KeyMapping fullbrightKey;
    private static KeyMapping hudKey;
    private static KeyMapping performanceKey;
    private static KeyMapping zoomKey;
    private static double savedGamma = 1.0;
    private static int savedFov = 70;
    private static boolean zooming = false;
    private static boolean savedShadowSetting = true;

    @Override
    public void onInitializeClient() {
        fullbrightKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.gptclient.fullbright", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_G, "category.gptclient"));
        hudKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.gptclient.hud", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_O, "category.gptclient"));
        performanceKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.gptclient.performance", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_P, "category.gptclient"));
        zoomKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.gptclient.zoom", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_C, "category.gptclient"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (fullbrightKey.consumeClick()) toggleFullbright(client);
            while (hudKey.consumeClick()) hudEnabled = !hudEnabled;
            while (performanceKey.consumeClick()) togglePerformance(client);

            if (client.player != null) {
                boolean wantsZoom = zoomKey.isDown();
                if (wantsZoom && !zooming) {
                    savedFov = client.options.fov().get();
                    client.options.fov().set(Math.max(30, savedFov / 4));
                    zooming = true;
                } else if (!wantsZoom && zooming) {
                    client.options.fov().set(savedFov);
                    zooming = false;
                }
            } else if (zooming) {
                client.options.fov().set(savedFov);
                zooming = false;
            }

            long now = System.currentTimeMillis();
            while (!clicks.isEmpty() && now - clicks.peekFirst() > 1000) clicks.removeFirst();
        });

        HudElementRegistry.addLast(Identifier.fromNamespaceAndPath("gptclient", "utility_hud"),
                (graphics, tickCounter) -> renderHud(Minecraft.getInstance(), graphics));
    }

    private static void toggleFullbright(Minecraft client) {
        if (client.options == null) return;
        if (!fullbrightEnabled) {
            savedGamma = client.options.gamma().get();
            client.options.gamma().set(16.0);
            fullbrightEnabled = true;
        } else {
            client.options.gamma().set(savedGamma);
            fullbrightEnabled = false;
        }
    }

    private static void togglePerformance(Minecraft client) {
        if (client.options == null) return;
        if (!performanceMode) {
            savedShadowSetting = client.options.entityShadows().get();
            client.options.entityShadows().set(false);
            performanceMode = true;
        } else {
            client.options.entityShadows().set(savedShadowSetting);
            performanceMode = false;
        }
        if (client.player != null) {
            client.player.displayClientMessage(Component.literal("GPT Client performance mode: " + (performanceMode ? "ON" : "OFF")), true);
        }
    }

    private static void renderHud(Minecraft client, GuiGraphics g) {
        if (!hudEnabled || client.options.hideGui || client.player == null) return;
        int x = 8;
        int y = 8;
        g.drawString(client.font, "GPT Client", x, y, 0xFFFFFF, true);
        y += 12;
        g.drawString(client.font, "FPS: " + client.getFps(), x, y, 0x55FF55, true);
        y += 12;
        g.drawString(client.font, "CPS: " + clicks.size(), x, y, 0x55FFFF, true);
        y += 14;
        g.drawString(client.font, "[W]", x + 23, y, isDown(client, GLFW.GLFW_KEY_W) ? 0xFFFF55 : 0xFFFFFF, true);
        y += 11;
        g.drawString(client.font, "[A] [S] [D]", x, y,
                (isDown(client, GLFW.GLFW_KEY_A) || isDown(client, GLFW.GLFW_KEY_S) || isDown(client, GLFW.GLFW_KEY_D)) ? 0xFFFF55 : 0xFFFFFF, true);
        y += 12;
        g.drawString(client.font, "G: Light  C: Zoom  O: HUD  P: Perf", x, y, 0xAAAAAA, true);
    }

    private static boolean isDown(Minecraft client, int key) {
        return InputConstants.isKeyDown(client.getWindow(), key);
    }

    public static void recordClick(int button, int action) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT && action == GLFW.GLFW_PRESS) {
            clicks.addLast(System.currentTimeMillis());
        }
    }
}
