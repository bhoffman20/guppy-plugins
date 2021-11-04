/*
 * Copyright (c) 2019, Ron Young <https://github.com/raiyni>
 * Copyright (c) 2021, Truth Forger <https://github.com/Blackberry0Pie>
 * Copyright (c) 2018, Adam <Adam@sigterm.info>
 * Copyright (c) 2018, Kamiel
 * Copyright (c) 2019, Rami <https://github.com/Rami-J>
 * All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *     list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.guppy.guppyswap;

import com.google.common.annotations.VisibleForTesting;
import static com.google.common.base.Predicates.alwaysTrue;
import static com.google.common.base.Predicates.equalTo;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Provides;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.KeyCode;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.NPC;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.menuentryswapper.ShiftDepositMode;
import net.runelite.client.plugins.menuentryswapper.ShiftWithdrawMode;
import net.runelite.client.util.Text;

@Slf4j
@PluginDescriptor(
	name = "Guppy Swap"
)
public class GuppySwapPlugin extends Plugin
{
	private static final Set<MenuAction> NPC_MENU_TYPES = ImmutableSet.of(
		MenuAction.NPC_FIRST_OPTION,
		MenuAction.NPC_SECOND_OPTION,
		MenuAction.NPC_THIRD_OPTION,
		MenuAction.NPC_FOURTH_OPTION,
		MenuAction.NPC_FIFTH_OPTION,
		MenuAction.EXAMINE_NPC);

	@Inject
	private Client client;

	@Inject
	private GuppySwapConfig config;

	private final Multimap<String, Swap> swaps = LinkedHashMultimap.create();
	private final ArrayListMultimap<String, Integer> optionIndexes = ArrayListMultimap.create();

	@Provides
	GuppySwapConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(GuppySwapConfig.class);
	}

	@Override
	public void startUp()
	{
		setupSwaps();
	}

	@Override
	public void shutDown()
	{
		swaps.clear();
	}

	private void dirtySwapMenuEntryAdded(MenuEntryAdded menuEntryAdded) {
		final int widgetGroupId = WidgetInfo.TO_GROUP(menuEntryAdded.getActionParam1());
		final boolean isDepositBoxPlayerInventory = widgetGroupId == WidgetID.DEPOSIT_BOX_GROUP_ID;
		final boolean isChambersOfXericStorageUnitPlayerInventory = widgetGroupId == WidgetID.CHAMBERS_OF_XERIC_STORAGE_UNIT_INVENTORY_GROUP_ID;

		// Swap default option to "Fill" for essence pouches
		if(menuEntryAdded.getTarget().contains("pouch")) {
			if (menuEntryAdded.getType() == MenuAction.CC_OP.getId()
					&& menuEntryAdded.getIdentifier() == (isDepositBoxPlayerInventory || isChambersOfXericStorageUnitPlayerInventory ? 1 : 2)
					&& (menuEntryAdded.getOption().startsWith("Deposit-")))
			{
				ShiftDepositMode shiftDepositMode = ShiftDepositMode.EXTRA_OP;
				final int opId = shiftDepositMode.getIdentifier();
				final int actionId = opId >= 6 ? MenuAction.CC_OP_LOW_PRIORITY.getId() : MenuAction.CC_OP.getId();
				bankModeSwap(actionId, opId);
			}
		}

		// Wear binding necklace and ring of dueling by default
		if(menuEntryAdded.getTarget().contains("binding") || menuEntryAdded.getTarget().contains("dueling")) {
			if (menuEntryAdded.getType() == MenuAction.CC_OP.getId()
					&& menuEntryAdded.getIdentifier() == (isDepositBoxPlayerInventory || isChambersOfXericStorageUnitPlayerInventory ? 1 : 2)
					&& (menuEntryAdded.getOption().startsWith("Deposit-")))
			{
				ShiftDepositMode shiftDepositMode = ShiftDepositMode.EXTRA_OP;
				final int opId = shiftDepositMode.getIdentifier();
				final int actionId = opId >= 6 ? MenuAction.CC_OP_LOW_PRIORITY.getId() : MenuAction.CC_OP.getId();
				bankModeSwap(actionId, opId);
			}
		}

		if(menuEntryAdded.getTarget().contains("essence")) {
			if (menuEntryAdded.getType() == MenuAction.CC_OP.getId()
					&& menuEntryAdded.getIdentifier() == 1
					&& menuEntryAdded.getOption().startsWith("Withdraw")) {

				ShiftWithdrawMode shiftWithdrawMode = ShiftWithdrawMode.WITHDRAW_ALL;

				final int actionId, opId;
				actionId = shiftWithdrawMode.getMenuAction().getId();
				opId = shiftWithdrawMode.getIdentifier();
				bankModeSwap(actionId, opId);
			}
		}

//		if(menuEntryAdded.getIdentifier() == 557) {
//
//
//			WidgetMenuOption op = new WidgetMenuOption("cast","magic imbue",9764864);
//			menuManager.addManagedCustomMenu(op);
//
//			// WidgetInfo.FIXED_VIEWPORT_MAGIC_TAB
//			//WidgetMenuOption op = new WidgetMenuOption("cast","magic imbue",9764864);
//			//menuManager.addManagedCustomMenu(op);
//		}

	}

	private void dirtySwapMenuClicked(MenuOptionClicked event) {
		//System.out.println("Clicked ["+ event.getMenuOption() + "] on widget [" + event.getWidgetId() + "] at index [" + event.getSelectedItemIndex() + "] action [" + event.getMenuAction().name() + "] item id [" + event.getId() + "] param0 [" + event.getParam0() + "] param1 [" +event.getParam1() + "]");

		//final Spellbook pook = Spellbook.getByID(client.getVar(Varbits.SPELLBOOK));
		//System.out.println(pook.getConfigKey());

//		Widget magic = client.getWidget(WidgetInfo.RESIZABLE_VIEWPORT_MAGIC_TAB);
//		System.out.println(Arrays.asList(magic.getChildren()));
//
//		if(event.getId() == 557) {
//			System.out.println("Earth Rune Clicked");
//			// WidgetInfo.FIXED_VIEWPORT_MAGIC_TAB
//
//
//		}
	}

	@VisibleForTesting
	void setupSwaps()
	{
		swap("fill", "giant pouch","empty", config::swapEmptyEssencePouch);
		swap("fill", "large pouch","empty", config::swapEmptyEssencePouch);
		swap("fill", "medium pouch","empty", config::swapEmptyEssencePouch);
		swap("fill", "small pouch","empty", config::swapEmptyEssencePouch);

		swap("remove", "crafting cape(t)","teleport", config::swapEmptyEssencePouch);
		swapContains("remove", alwaysTrue(),"tele to poh", config::swapEmptyEssencePouch);
		swapContains("remove", alwaysTrue(), "duel arena", config::swapEmptyEssencePouch);

		swap("venerate", "lunar", config::swapEmptyEssencePouch);
		swap("venerate", "arceuus", config::swapEmptyEssencePouch);

	}

	private void bankModeSwap(int entryTypeId, int entryIdentifier)
	{
		MenuEntry[] menuEntries = client.getMenuEntries();

		for (int i = menuEntries.length - 1; i >= 0; --i)
		{
			MenuEntry entry = menuEntries[i];

			if (entry.getType() == entryTypeId && entry.getIdentifier() == entryIdentifier)
			{
				// Raise the priority of the op so it doesn't get sorted later
				entry.setType(MenuAction.CC_OP.getId());

				menuEntries[i] = menuEntries[menuEntries.length - 1];
				menuEntries[menuEntries.length - 1] = entry;

				client.setMenuEntries(menuEntries);
				break;
			}
		}
	}

	private <T extends Enum<?> & SwapMode> void swapMode(String option, Class<T> mode, Supplier<T> enumGet)
	{
		for (T e : mode.getEnumConstants())
		{
			swaps.put(option, new Swap(
				alwaysTrue(),
				e.checkTarget(),
				e.getOption().toLowerCase(),
				() -> (!e.checkShift() || (e.checkShift() && !shiftModifier())) & e == enumGet.get(),
				e.strict()
			));
		}
	}

	private void swap(String option, String swappedOption, Supplier<Boolean> enabled)
	{
		swap(option, alwaysTrue(), swappedOption, enabled);
	}

	private void swap(String option, String target, String swappedOption, Supplier<Boolean> enabled)
	{
		swap(option, equalTo(target), swappedOption, enabled);
	}

	private void swap(String option, Predicate<String> targetPredicate, String swappedOption, Supplier<Boolean> enabled)
	{
		swaps.put(option, new Swap(alwaysTrue(), targetPredicate, swappedOption, enabled, true));
	}

	private void swapContains(String option, Predicate<String> targetPredicate, String swappedOption, Supplier<Boolean> enabled)
	{
		swaps.put(option, new Swap(alwaysTrue(), targetPredicate, swappedOption, enabled, false));
	}

	private void swapMenuEntry(int index, MenuEntry menuEntry)
	{
		final int eventId = menuEntry.getIdentifier();
		final MenuAction menuAction = MenuAction.of(menuEntry.getType());
		final String option = Text.removeTags(menuEntry.getOption()).toLowerCase();
		final String target = Text.removeTags(menuEntry.getTarget()).toLowerCase();
		final NPC hintArrowNpc = client.getHintArrowNpc();

		if (hintArrowNpc != null
			&& hintArrowNpc.getIndex() == eventId
			&& NPC_MENU_TYPES.contains(menuAction))
		{
			return;
		}

		if (shiftModifier() && (menuAction == MenuAction.ITEM_FIRST_OPTION
			|| menuAction == MenuAction.ITEM_SECOND_OPTION
			|| menuAction == MenuAction.ITEM_THIRD_OPTION
			|| menuAction == MenuAction.ITEM_FOURTH_OPTION
			|| menuAction == MenuAction.ITEM_FIFTH_OPTION
			|| menuAction == MenuAction.ITEM_USE))
		{
			// don't perform swaps on items when shift is held; instead prefer the client menu swap, which
			// we may have overwrote
			return;
		}

		Collection<Swap> swaps = this.swaps.get(option);
		for (Swap swap : swaps)
		{
			if (swap.getTargetPredicate().test(target) && swap.getEnabled().get())
			{
				if (swap(swap.getSwappedOption(), target, index, swap.isStrict()))
				{
					break;
				}
			}
		}
	}

	@Subscribe
	public void onClientTick(ClientTick clientTick)
	{
		// The menu is not rebuilt when it is open, so don't swap or else it will
		// repeatedly swap entries
		if (client.getGameState() != GameState.LOGGED_IN || client.isMenuOpen())
		{
			return;
		}

		MenuEntry[] menuEntries = client.getMenuEntries();

		// Build option map for quick lookup in findIndex
		int idx = 0;
		optionIndexes.clear();
		for (MenuEntry entry : menuEntries)
		{
			String option = Text.removeTags(entry.getOption()).toLowerCase();
			optionIndexes.put(option, idx++);
		}

		// Perform swaps
		idx = 0;
		for (MenuEntry entry : menuEntries)
		{
			swapMenuEntry(idx++, entry);
		}
	}

	private boolean swap(String option, String target, int index, boolean strict)
	{
		MenuEntry[] menuEntries = client.getMenuEntries();

		// find option to swap with
		int optionIdx = findIndex(menuEntries, index, option, target, strict);

		if (optionIdx >= 0)
		{
			swap(optionIndexes, menuEntries, optionIdx, index);
			return true;
		}

		return false;
	}

	private int findIndex(MenuEntry[] entries, int limit, String option, String target, boolean strict)
	{
		if (strict)
		{
			List<Integer> indexes = optionIndexes.get(option);

			// We want the last index which matches the target, as that is what is top-most
			// on the menu
			for (int i = indexes.size() - 1; i >= 0; --i)
			{
				int idx = indexes.get(i);
				MenuEntry entry = entries[idx];
				String entryTarget = Text.removeTags(entry.getTarget()).toLowerCase();

				// Limit to the last index which is prior to the current entry
				if (idx < limit && entryTarget.equals(target))
				{
					return idx;
				}
			}
		}
		else
		{
			// Without strict matching we have to iterate all entries up to the current limit...
			for (int i = limit - 1; i >= 0; i--)
			{
				MenuEntry entry = entries[i];
				String entryOption = Text.removeTags(entry.getOption()).toLowerCase();
				String entryTarget = Text.removeTags(entry.getTarget()).toLowerCase();

				if (entryOption.contains(option.toLowerCase()) && entryTarget.equals(target))
				{
					return i;
				}
			}

		}

		return -1;
	}

	private void swap(ArrayListMultimap<String, Integer> optionIndexes, MenuEntry[] entries, int index1, int index2)
	{
		MenuEntry entry1 = entries[index1],
			entry2 = entries[index2];

		entries[index1] = entry2;
		entries[index2] = entry1;

		client.setMenuEntries(entries);

		// Update optionIndexes
		String option1 = Text.removeTags(entry1.getOption()).toLowerCase(),
			option2 = Text.removeTags(entry2.getOption()).toLowerCase();

		List<Integer> list1 = optionIndexes.get(option1),
			list2 = optionIndexes.get(option2);

		// call remove(Object) instead of remove(int)
		list1.remove((Integer) index1);
		list2.remove((Integer) index2);

		sortedInsert(list1, index2);
		sortedInsert(list2, index1);
	}

	private static <T extends Comparable<? super T>> void sortedInsert(List<T> list, T value) // NOPMD: UnusedPrivateMethod: false positive
	{
		int idx = Collections.binarySearch(list, value);
		list.add(idx < 0 ? -idx - 1 : idx, value);
	}

	private boolean shiftModifier()
	{
		return client.isKeyPressed(KeyCode.KC_SHIFT);
	}
}
