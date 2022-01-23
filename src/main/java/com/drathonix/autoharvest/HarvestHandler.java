package com.drathonix.autoharvest;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.CaveVinesPlant;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class HarvestHandler implements Listener{
    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEvent ev){
        //Don't interfere with other plugins
        if(ev.isCancelled()) return;
        if(ev.getAction() == Action.RIGHT_CLICK_BLOCK){
            handleCropHarvest(ev.getClickedBlock(), ev.getItem(),ev.getPlayer());
        }
    }
    @EventHandler(priority = EventPriority.LOWEST)
    public void onDispenserActivation(BlockDispenseEvent ev){
        //Don't interfere with other plugins
        if(ev.isCancelled()) return;
        Block b = ev.getBlock();
        if(b.getState() instanceof Dispenser){
            //Do not interfere with vanilla automealing.
            Vector facing = ((Directional)b.getBlockData()).getFacing().getDirection();
            Block target = b.getWorld().getBlockAt(b.getLocation().add(facing));
            BlockData bd = target.getBlockData();
            //Cancel only if a harvest activated.
            handleCropHarvest(target,ev.getItem(),ev.getBlock());
            if(ev.getItem().getType() != Material.BONE_MEAL) ev.setCancelled(MaterialValidator.isCropBlock(target));
            else ev.setCancelled(MaterialValidator.isFullyGrown(bd));
        }
    }

    public boolean handleCropHarvest(Block target, ItemStack harvestItem, Object harvester) {
        if (harvester instanceof Player && MaterialValidator.isPlayerHarvestable(target)) {
            Collection<ItemStack> drops = removeOneSeed(target.getDrops(harvestItem));
            Player plr = (Player) harvester;
            Map<Integer, ItemStack> remainder = plr.getInventory().addItem(drops.toArray(new ItemStack[0]));
            dropAt(target.getLocation(), target.getWorld(), remainder.values());
        }
        else if(MaterialValidator.isDispenserHarvestable(target)){
            Collection<ItemStack> drops = removeOneSeed(target.getDrops(harvestItem));
            dropAt(target.getLocation(), target.getWorld(),drops);
        }
        else return false;
        resetAge(target);
        return true;
    }

    private void resetAge(Block target) {
        if(target.getBlockData() instanceof CaveVinesPlant){
            CaveVinesPlant dat = (CaveVinesPlant) target.getBlockData();
            dat.setBerries(false);
            target.setBlockData(dat);
        }
        else if(target.getBlockData() instanceof Ageable) {
            Ageable dat = (Ageable) target.getBlockData();
            if(target.getType() == Material.SWEET_BERRY_BUSH) dat.setAge(dat.getMaximumAge()-2);
            else dat.setAge(0);
            target.setBlockData(dat);
        }
    }

    private Collection<ItemStack> removeOneSeed(Collection<ItemStack> drops) {
        ArrayList<ItemStack> dropsOut = new ArrayList<>();
        for (ItemStack drop : drops) {
            if(!MaterialValidator.isSeededCropResult(drop)){
                drop.setAmount(drop.getAmount()-1);
                if(drop.getAmount() > 0){
                    dropsOut.add(drop);
                }
            }
            else dropsOut.add(drop);
        }
        return dropsOut;
    }

    public void dropAt(Location location, World world, Collection<ItemStack> items) {
        for (ItemStack item : items) {
            world.dropItem(location,item);
        }
    }
}
