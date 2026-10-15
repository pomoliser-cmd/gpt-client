
package com.nisska.gptclient.mixin;

import com.nisska.gptclient.GptClient;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin {
    @Inject(method = "onPress", at = @At("HEAD"))
    private void gptclient$trackClicks(long window, int button, int action, int mods, CallbackInfo ci) {
        GptClient.recordClick(button, action);
    }
}
