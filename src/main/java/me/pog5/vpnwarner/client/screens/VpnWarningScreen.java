package me.pog5.vpnwarner.client.screens;

import me.pog5.vpnwarner.client.VpnwarnerClient;
import me.pog5.vpnwarner.client.utils.VpnDetection;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.WarningScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Date;

public class VpnWarningScreen extends WarningScreen {
    private static final Text HEADER = Text.literal("CAUTION: You have a VPN enabled!").formatted(Formatting.BOLD, Formatting.RED);
    private static final Text MESSAGE = Text.literal("").append("Some servers (such as Loka) will ").append(Text.literal("ban your __account__").formatted(Formatting.RED)).append(" for using a VPN, and subsequently your main IP address as well.").append("\n").append(Text.literal("Detected VPN: ").formatted(Formatting.BOLD)).append(Text.literal(VpnwarnerClient.DETECTED_VPN).formatted(Formatting.RED, Formatting.BOLD));
    private static final Text NARRATED_TEXT = HEADER.copy().append("\n").append(MESSAGE);
    private final Screen parent;

    public VpnWarningScreen(Screen parent) {
        super(HEADER, MESSAGE, null, NARRATED_TEXT);
        this.parent = parent;
        if (!VpnDetection.isVpnEnabled()) this.client.setScreen(this.parent);
    }

    @Override
    protected void initButtons(int yOffset) {

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Wait... "), button -> {
            VpnwarnerClient.DISMISSED_WARNING = true;
            this.client.setScreen(this.parent);
        }).dimensions(this.width / 2 - 155, 100 + yOffset, 150, 20).build());
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.BACK, button -> this.client.setScreen(this.parent)).dimensions(this.width / 2 - 155 + 160, 100 + yOffset, 150, 20).build());
        this.children().forEach(child -> {
            if (child instanceof ButtonWidget button && button.getMessage().getString().contains("Wait")) {
                button.active = false;
                var thread = new Thread(() -> {
                    Date start = new Date();
                    while (new Date().getTime() - start.getTime() < 5000) {
                        button.setMessage(Text.literal("Wait... (" + (5 - (new Date().getTime() - start.getTime()) / 1000) + ")"));
                    }
                    if (this.client.currentScreen == this) {
                        button.active = true;
                        button.setMessage(Text.literal("Continue"));
                    }
                });
                thread.start();
            }
        });
    }
}
