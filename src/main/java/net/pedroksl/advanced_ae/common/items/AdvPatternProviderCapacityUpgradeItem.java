package net.pedroksl.advanced_ae.common.items;

import appeng.blockentity.networking.CableBusBlockEntity;
import appeng.parts.AEBasePart;
import com.glodblock.github.extendedae.util.FCUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import net.pedroksl.advanced_ae.common.AAESingletons;
import net.pedroksl.advanced_ae.common.entities.AdvPatternProviderEntity;
import net.pedroksl.advanced_ae.common.entities.SmallAdvPatternProviderEntity;
import net.pedroksl.advanced_ae.common.parts.SmallAdvPatternProviderPart;

import javax.annotation.Nonnull;
import java.util.List;

public class AdvPatternProviderCapacityUpgradeItem extends Item {
	public AdvPatternProviderCapacityUpgradeItem() {
		super(new Item.Properties());
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Nonnull
	@Override
	public InteractionResult useOn(@Nonnull UseOnContext context) {
		var pos = context.getClickedPos();
		var world = context.getLevel();
		var entity = world.getBlockEntity(pos);
		if (entity != null) {
			var ctx = new BlockPlaceContext(context);
			var tClazz = entity.getClass();
			if (tClazz == SmallAdvPatternProviderEntity.class) {
				var originState = world.getBlockState(pos);
				var state = AAESingletons.ADV_PATTERN_PROVIDER.getStateForPlacement(ctx);
				if (state == null) {
					return InteractionResult.PASS;
				}
				for (var sp : originState.getValues().entrySet()) {
					var pt = sp.getKey();
					var va = sp.getValue();
					try {
						if (state.hasProperty(pt)) {
							state = state.<Comparable, Comparable>setValue((Property) pt, va);
						}
					} catch (Exception ignore) {
						// NO-OP
					}
				}
				BlockEntity te = new AdvPatternProviderEntity(pos, state);
				FCUtil.replaceTile(world, pos, entity, te, state);
				context.getItemInHand().shrink(1);
				return InteractionResult.CONSUME;

			} else if (entity instanceof CableBusBlockEntity cable) {
				Vec3 hitVec = context.getClickLocation();
				Vec3 hitInBlock = new Vec3(hitVec.x - pos.getX(), hitVec.y - pos.getY(), hitVec.z - pos.getZ());
				var part = cable.getCableBus().selectPartLocal(hitInBlock).part;
				if (part instanceof AEBasePart basePart && (part.getClass() == SmallAdvPatternProviderPart.class)) {
					var side = basePart.getSide();
					var contents = new CompoundTag();

					var partItem = AAESingletons.ADV_PATTERN_PROVIDER_PART;

					part.writeToNBT(contents, world.registryAccess());
					var p = cable.replacePart(partItem, side, context.getPlayer(), null);
					if (p != null) {
						p.readFromNBT(contents, world.registryAccess());
					}
				} else {
					return InteractionResult.PASS;
				}
				context.getItemInHand().shrink(1);
				return InteractionResult.sidedSuccess(world.isClientSide);
			}
		}
		return InteractionResult.PASS;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
		super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

		tooltipComponents.add(Component.empty().append("§7Upgrades an Advanced Pattern Provider to the maximum amount of pattern slots"));
	}
}
