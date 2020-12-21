package com.loucaskreger.controlf.config;

import org.apache.commons.lang3.tuple.Pair;
import net.minecraftforge.common.ForgeConfigSpec;

public class Config {

	public static final ClientConfig CLIENT;
	public static final ForgeConfigSpec CLIENT_SPEC;

	public static int red;
	public static int green;
	public static int blue;

	static {
		final Pair<ClientConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
		CLIENT_SPEC = specPair.getRight();
		CLIENT = specPair.getLeft();
	}

	public static void bakeConfig() {
		red = ClientConfig.red.get();
		green = ClientConfig.green.get();
		blue = ClientConfig.blue.get();

	}
}
