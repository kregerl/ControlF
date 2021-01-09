package com.loucaskreger.controlf.util;

import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public class ChestUtil {

	public static BlockPos getAttachedChest(BlockState state, BlockPos pos) {

		Direction dir = ChestBlock.getDirectionToAttached(state);
		BlockPos secondPos = pos;
		switch (dir) {
		case EAST:
			secondPos = pos.add(1, 0, 0);
			break;
		case WEST:
			secondPos = pos.add(-1, 0, 0);
			break;
		case NORTH:
			secondPos = pos.add(0, 0, -1);
			break;
		case SOUTH:
			secondPos = pos.add(0, 0, 1);
			break;
		case DOWN:
			// Cant connect vertically.
			break;
		case UP:
			// Cant connect vertically.
			break;
		default:
			// Empty
			break;
		}
		return secondPos;
	}

}
