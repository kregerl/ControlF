package com.loucaskreger.hwylf.compat.jei;

import com.loucaskreger.hwylf.Hwylf;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.util.ResourceLocation;

@JeiPlugin
public class Plugin implements IModPlugin {

	public static IJeiRuntime runtime;

	@Override
	public ResourceLocation getPluginUid() {
		return new ResourceLocation(Hwylf.MOD_ID, "jei");
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
		runtime = jeiRuntime;
	}

}
