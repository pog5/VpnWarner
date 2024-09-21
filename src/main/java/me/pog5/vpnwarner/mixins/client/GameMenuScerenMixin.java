package me.pog5.vpnwarner.mixins.client;

import me.pog5.vpnwarner.client.VpnwarnerClient;
import net.minecraft.client.gui.screen.GameMenuScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameMenuScreen.class)
public class GameMenuScerenMixin {
    @Inject(method = "disconnect", at = @At("HEAD"))
    private void Spoofer$handleDisconnect(CallbackInfo ci) {
        VpnwarnerClient.DISMISSED_WARNING = false;
    }
}
