package com.drathonix.autoharvest;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.CaveVinesPlant;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class MaterialValidator {
    public static Map<Material,Material> doNotReduce = new HashMap<>();
    public static Map<Material,Material> dispenserOnly = new HashMap<>();
    public static Map<Material,Material> ignored = new HashMap<>();
    static{
        dispenserOnly.put(Material.SWEET_BERRY_BUSH,Material.SWEET_BERRY_BUSH);
        //Put both of these to be safe
        dispenserOnly.put(Material.CAVE_VINES,Material.CAVE_VINES);
        dispenserOnly.put(Material.CAVE_VINES_PLANT,Material.CAVE_VINES_PLANT);
        ignored.put(Material.MELON_STEM,Material.MELON_STEM);
        ignored.put(Material.PUMPKIN_STEM,Material.PUMPKIN_STEM);
        doNotReduce.put(Material.WHEAT,Material.WHEAT_SEEDS);
        doNotReduce.put(Material.BEETROOT,Material.BEETROOT_SEEDS);
        doNotReduce.put(Material.SWEET_BERRIES,Material.SWEET_BERRIES);
        doNotReduce.put(Material.GLOW_BERRIES,Material.GLOW_BERRIES);
    }
    public static boolean isCropBlock(Block b) {
        return !isIgnored(b.getType()) && (b.getBlockData() instanceof Ageable || b.getBlockData() instanceof CaveVinesPlant);
    }
    public static boolean isPlayerHarvestable(Block b){
        if(isIgnored(b.getType())) return false;
        else return isFullyGrown(b.getBlockData());
    }
    public static boolean isDispenserHarvestable(Block b){
        if(isIgnored(b.getType())) return false;
        return isFullyGrown(b.getBlockData());
    }

    private static boolean isIgnored(Material m) {
        return ignored.containsKey(m);
    }

    public static boolean isFullyGrown(BlockData in){
        if(in instanceof CaveVinesPlant) {
            return ((CaveVinesPlant) in).isBerries();
        }
        else if(in instanceof Ageable){
            Ageable ageable = (Ageable) in;
            return ageable.getAge() == ageable.getMaximumAge();
        }
        else return false;
    }
    public static boolean isSeededCropResult(ItemStack stack) {
        return doNotReduce.containsKey(stack.getType());
    }
}
