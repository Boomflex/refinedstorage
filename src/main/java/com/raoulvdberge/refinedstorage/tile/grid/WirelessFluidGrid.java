
package com.raoulvdberge.refinedstorage.tile.grid;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.network.grid.IFluidGridHandler;
import com.raoulvdberge.refinedstorage.api.network.grid.IItemGridHandler;
import com.raoulvdberge.refinedstorage.block.EnumGridType;
import com.raoulvdberge.refinedstorage.gui.grid.GridFilter;
import com.raoulvdberge.refinedstorage.gui.grid.GridTab;
import com.raoulvdberge.refinedstorage.gui.grid.GuiGrid;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerBasic;
import com.raoulvdberge.refinedstorage.item.ItemWirelessFluidGrid;
import com.raoulvdberge.refinedstorage.network.MessageWirelessFluidGridSettingsUpdate;
import com.raoulvdberge.refinedstorage.tile.TileController;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import java.util.Collections;
import java.util.List;

public class WirelessFluidGrid implements IGrid {
    private ItemStack stack;

    private int controllerDimension;
    private BlockPos controller;

    private int sortingType;
    private int sortingDirection;
    private int searchBoxMode;

    public WirelessFluidGrid(int controllerDimension, ItemStack stack) {
        this.controllerDimension = controllerDimension;
        this.controller = new BlockPos(ItemWirelessFluidGrid.getX(stack), ItemWirelessFluidGrid.getY(stack), ItemWirelessFluidGrid.getZ(stack));

        this.stack = stack;

        this.sortingType = ItemWirelessFluidGrid.getSortingType(stack);
        this.sortingDirection = ItemWirelessFluidGrid.getSortingDirection(stack);
        this.searchBoxMode = ItemWirelessFluidGrid.getSearchBoxMode(stack);
    }

    public ItemStack getStack() {
        return stack;
    }

    @Override
    public EnumGridType getType() {
        return EnumGridType.FLUID;
    }

    @Override
    public BlockPos getNetworkPosition() {
        return controller;
    }

    @Override
    public IItemGridHandler getItemHandler() {
        return null;
    }

    @Override
    public IFluidGridHandler getFluidHandler() {
        TileController controller = getController();

        return controller != null ? controller.getFluidGridHandler() : null;
    }

    @Override
    public String getGuiTitle() {
        return "gui.refinedstorage:fluid_grid";
    }

    @Override
    public int getViewType() {
        return 0;
    }

    @Override
    public int getSortingType() {
        return sortingType;
    }

    @Override
    public int getSortingDirection() {
        return sortingDirection;
    }

    @Override
    public int getSearchBoxMode() {
        return searchBoxMode;
    }

    @Override
    public int getTabSelected() {
        return 0;
    }

    @Override
    public void onViewTypeChanged(int type) {
        // NO OP
    }

    @Override
    public void onSortingTypeChanged(int type) {
        RS.INSTANCE.network.sendToServer(new MessageWirelessFluidGridSettingsUpdate(getSortingDirection(), type, getSearchBoxMode()));

        this.sortingType = type;

        GuiGrid.markForSorting();
    }

    @Override
    public void onSortingDirectionChanged(int direction) {
        RS.INSTANCE.network.sendToServer(new MessageWirelessFluidGridSettingsUpdate(direction, getSortingType(), getSearchBoxMode()));

        this.sortingDirection = direction;

        GuiGrid.markForSorting();
    }

    @Override
    public void onSearchBoxModeChanged(int searchBoxMode) {
        RS.INSTANCE.network.sendToServer(new MessageWirelessFluidGridSettingsUpdate(getSortingDirection(), getSortingType(), searchBoxMode));

        this.searchBoxMode = searchBoxMode;
    }

    @Override
    public void onTabSelectionChanged(int tab) {
        // NO OP
    }

    @Override
    public List<GridFilter> getFilteredItems() {
        return Collections.emptyList();
    }

    @Override
    public List<GridTab> getTabs() {
        return Collections.emptyList();
    }

    @Override
    public ItemHandlerBasic getFilter() {
        return null;
    }

    @Override
    public TileDataParameter<Integer> getRedstoneModeConfig() {
        return null;
    }

    @Override
    public boolean isActive() {
        return true;
    }

    public void onClose(EntityPlayer player) {
        TileController controller = getController();

        if (controller != null) {
            controller.getNetworkItemHandler().onClose(player);
        }
    }

    private TileController getController() {
        World world = DimensionManager.getWorld(controllerDimension);

        if (world != null) {
            TileEntity tile = world.getTileEntity(controller);

            return tile instanceof TileController ? (TileController) tile : null;
        }

        return null;
    }
}
