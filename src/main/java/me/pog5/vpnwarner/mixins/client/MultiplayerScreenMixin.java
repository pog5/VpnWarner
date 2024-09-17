package me.pog5.vpnwarner.mixins.client;

import me.pog5.vpnwarner.client.VpnwarnerClient;
import me.pog5.vpnwarner.client.screens.VpnWarningScreen;
import me.pog5.vpnwarner.client.utils.VpnDetection;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerScreen.class)
public class MultiplayerScreenMixin {

    @Final
    @Shadow
    private Screen parent;

    @Inject(method = "connect()V", at = @At("HEAD"), cancellable = true)
    private void showWarningScreen(CallbackInfo ci) {
        if (VpnDetection.isVpnEnabled() && !VpnwarnerClient.DISMISSED_WARNING) {
            MinecraftClient.getInstance().setScreen(new VpnWarningScreen(((MultiplayerScreen) (Object) this)));
            ci.cancel();
        } else {
            VpnwarnerClient.DISMISSED_WARNING = false;
        }
    }

    @Inject(method = "connect(Lnet/minecraft/client/network/ServerInfo;)V", at = @At("HEAD"), cancellable = true)
    private void showWarningScreenArged(CallbackInfo ci) {
        if (VpnDetection.isVpnEnabled() && !VpnwarnerClient.DISMISSED_WARNING) {
            MinecraftClient.getInstance().setScreen(new VpnWarningScreen(((MultiplayerScreen) (Object) this)));
            ci.cancel();
        } else {
            VpnwarnerClient.DISMISSED_WARNING = false;
        }
    }
}
