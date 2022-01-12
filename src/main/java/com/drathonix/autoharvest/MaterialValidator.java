package com.drathonix.autoharvest;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.type.CaveVinesPlant;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class MaterialValidator {
    private final static int NORMALGROWTHMAX = 7;
    private final static int FASTERGROWTHMAX = 3;
    public static Map<Material,Integer> cropMap = new HashMap<>();
    public static Map<Material,Material> doNotReduce = new HashMap<>();
    public static Map<Material,Material> dispenserOnly = new HashMap<>();
    static{
        cropMap.put(Material.WHEAT,NORMALGROWTHMAX);
        cropMap.put(Material.POTATOES,NORMALGROWTHMAX);
        cropMap.put(Material.CARROTS,NORMALGROWTHMAX);
        cropMap.put(Material.BEETROOT,FASTERGROWTHMAX);
        cropMap.put(Material.NETHER_WART,FASTERGROWTHMAX);
        cropMap.put(Material.COCOA,FASTERGROWTHMAX);
        dispenserOnly.put(Material.SWEET_BERRY_BUSH,Material.SWEET_BERRY_BUSH);
        //Put both of these to be safe
        dispenserOnly.put(Material.CAVE_VINES,Material.CAVE_VINES);
        dispenserOnly.put(Material.CAVE_VINES_PLANT,Material.CAVE_VINES_PLANT);
        doNotReduce.put(Material.WHEAT,Material.WHEAT_SEEDS);
        doNotReduce.put(Material.BEETROOT,Material.WHEAT_SEEDS);
        doNotReduce.put(Material.SWEET_BERRIES,Material.SWEET_BERRIES);
        doNotReduce.put(Material.GLOW_BERRIES,Material.GLOW_BERRIES);
    }
    public static boolean isCropBlock(Block in){
        return cropMap.containsKey(in.getType()) || dispenserOnly.containsKey(in.getType());
    }
    public static boolean isCropAndFullyGrown(Block in){
        if(!isCropBlock(in)) return false;
        if(in.getBlockData() instanceof Ageable){
            return isFullyGrown(in);
        }
        return false;
    }
    public static boolean shouldDispenserHarvest(Block in){
        if(dispenserOnly.containsKey(in.getType())){
            if(in.getBlockData() instanceof CaveVinesPlant){
                return ((CaveVinesPlant)in.getBlockData()).isBerries();
            }
            if(in.getBlockData() instanceof Ageable){
                return isFullyGrown(in);
            }
        }
        return isCropAndFullyGrown(in);
    }
    public static boolean isFullyGrown(Block in){
        Ageable dat = (Ageable) in.getBlockData();
        return dat.getMaximumAge() == dat.getAge();
    }

    public static boolean isSeededCropResult(ItemStack drop) {
        return doNotReduce.containsKey(drop.getType());
    }
}
