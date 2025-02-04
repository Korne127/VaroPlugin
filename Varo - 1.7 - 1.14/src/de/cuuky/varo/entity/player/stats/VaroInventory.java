package de.cuuky.varo.entity.player.stats;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import de.cuuky.varo.serialize.identifier.VaroSerializeField;
import de.cuuky.varo.serialize.identifier.VaroSerializeable;
import de.cuuky.varo.utils.Utils;

public class VaroInventory implements VaroSerializeable {

	@VaroSerializeField(path = "inventory")
	private HashMap<String, ItemStack> inventoryList;

	@VaroSerializeField(path = "size")
	private int size;

	private Inventory inventory;

	public VaroInventory() {}

	public VaroInventory(int size) {
		inventoryList = new HashMap<>();
		this.size = 54 < size ? 54 : (size < 9 ? 9 : Utils.getNextToNine(size));

		createInventory();
	}

	private void createInventory() {
		inventory = Bukkit.createInventory(null, size, "§aBackpack");
	}

	public Inventory getInventory() {
		return inventory;
	}

	public void clear() {
		inventoryList.clear();
		inventory.clear();
	}

	@Override
	public void onDeserializeEnd() {
		createInventory();

		for(String i : inventoryList.keySet())
			inventory.setItem(Integer.valueOf(i), inventoryList.get(i));
	}

	@Override
	public void onSerializeStart() {
		for(int i = 0; i < inventory.getSize(); i++) {
			ItemStack stack = inventory.getItem(i);
			if(stack != null && stack.getType() != Material.AIR)
				inventoryList.put(String.valueOf(i), stack);
		}
	}
}
