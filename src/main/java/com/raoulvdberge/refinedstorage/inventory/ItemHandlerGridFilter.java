package com.raoulvdberge.refinedstorage.inventory;

import com.raoulvdberge.refinedstorage.RSUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemStackHandler;

public class ItemHandlerGridFilter extends ItemStackHandler {
    private ItemStack stack;

    public ItemHandlerGridFilter(ItemStack stack) {
        super(27);

        this.stack = stack;

        if (stack.hasTagCompound()) {
            RSUtils.readItems(this, 0, stack.getTagCompound());
        }
    }

    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);

        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }

        RSUtils.writeItems(this, 0, stack.getTagCompound());
    }

    public NonNullList<ItemStack> getFilteredItems() {
        return stacks;
    }
}
