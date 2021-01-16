package com.loucaskreger.hwylf.networking;

import com.loucaskreger.hwylf.Hwylf;
import com.loucaskreger.hwylf.networking.packet.CheckInventoryRequestPacket;
import com.loucaskreger.hwylf.networking.packet.CheckInventoryResponsePacket;
import com.loucaskreger.hwylf.networking.packet.CheckSearchInventoryRequestPacket;
import com.loucaskreger.hwylf.networking.packet.CheckSearchInventoryResponsePacket;
import com.loucaskreger.hwylf.networking.packet.FindItemsContainingStringRequest;
import com.loucaskreger.hwylf.networking.packet.ItemContainingStringResponse;
import com.loucaskreger.hwylf.networking.packet.ItemLocationRequestPacket;
import com.loucaskreger.hwylf.networking.packet.ItemLocationResponsePacket;
import com.loucaskreger.hwylf.networking.packet.RemoveItemsNotContainingStringResponse;
import com.loucaskreger.hwylf.networking.packet.ResetRendersPacket;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class Networking {

	public static SimpleChannel INSTANCE;
	private static int id = 0;

	public static int nextId() {
		return id++;
	}

	public static void registerMessages() {
		INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(Hwylf.MOD_ID, "controlf"), () -> "1.0",
				s -> true, s -> true);
		INSTANCE.registerMessage(nextId(), ItemLocationRequestPacket.class, ItemLocationRequestPacket::toBytes,
				ItemLocationRequestPacket::new, ItemLocationRequestPacket::handle);

		INSTANCE.registerMessage(nextId(), ItemLocationResponsePacket.class, ItemLocationResponsePacket::toBytes,
				ItemLocationResponsePacket::new, ItemLocationResponsePacket::handle);

		INSTANCE.registerMessage(nextId(), CheckInventoryRequestPacket.class, CheckInventoryRequestPacket::toBytes,
				CheckInventoryRequestPacket::new, CheckInventoryRequestPacket::handle);

		INSTANCE.registerMessage(nextId(), CheckInventoryResponsePacket.class, CheckInventoryResponsePacket::toBytes,
				CheckInventoryResponsePacket::new, CheckInventoryResponsePacket::handle);

		INSTANCE.registerMessage(nextId(), FindItemsContainingStringRequest.class,
				FindItemsContainingStringRequest::toBytes, FindItemsContainingStringRequest::new,
				FindItemsContainingStringRequest::handle);

		INSTANCE.registerMessage(nextId(), ItemContainingStringResponse.class, ItemContainingStringResponse::toBytes,
				ItemContainingStringResponse::new, ItemContainingStringResponse::handle);

		INSTANCE.registerMessage(nextId(), RemoveItemsNotContainingStringResponse.class,
				RemoveItemsNotContainingStringResponse::toBytes, RemoveItemsNotContainingStringResponse::new,
				RemoveItemsNotContainingStringResponse::handle);

		INSTANCE.registerMessage(nextId(), CheckSearchInventoryRequestPacket.class,
				CheckSearchInventoryRequestPacket::toBytes, CheckSearchInventoryRequestPacket::new,
				CheckSearchInventoryRequestPacket::handle);

		INSTANCE.registerMessage(nextId(), CheckSearchInventoryResponsePacket.class,
				CheckSearchInventoryResponsePacket::toBytes, CheckSearchInventoryResponsePacket::new,
				CheckSearchInventoryResponsePacket::handle);

		INSTANCE.registerMessage(nextId(), ResetRendersPacket.class, ResetRendersPacket::toBytes,
				ResetRendersPacket::new, ResetRendersPacket::handle);

	}

}
