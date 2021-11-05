/*
 * Copyright (c) 2018, Adam <Adam@sigterm.info>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
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
package net.runelite.client.plugins.guppyswap;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup(GuppySwapConfig.GROUP)
public interface GuppySwapConfig extends Config
{
	String GROUP = "guppy-swap";

	@ConfigSection(
		name = "Item Swaps",
		description = "All options that swap item menu entries",
		position = 0,
		closedByDefault = true
	)
	String itemSection = "items";
//
//	@ConfigSection(
//		name = "NPC Swaps",
//		description = "All options that swap NPC menu entries",
//		position = 1,
//		closedByDefault = true
//	)
//	String npcSection = "npcs";
//
//	@ConfigSection(
//		name = "Object Swaps",
//		description = "All options that swap object menu entries",
//		position = 2,
//		closedByDefault = true
//	)
//	String objectSection = "objects";
//
//	@ConfigSection(
//		name = "UI Swaps",
//		description = "All options that swap entries in the UI (except Items)",
//		position = 3,
//		closedByDefault = true
//	)
//	String uiSection = "ui";

	@ConfigSection(
			name = "Shady Swaps",
			description = "Shady Shit",
			position = 0,
			closedByDefault = false
	)
	String shadySection = "shady";

//	enum KaramjaGlovesMode
//	{
//		WEAR,
//		GEM_MINE,
//		DURADEL,
//	}

	@ConfigItem(
		position = -2,
		keyName = "shiftClickCustomization",
		name = "Customizable shift-click",
		description = "Allows customization of shift-clicks on items",
		section = itemSection
	)
	default boolean shiftClickCustomization()
	{
		return true;
	}

//	@ConfigItem(
//		keyName = "swapFairyRing",
//		name = "Fairy ring",
//		description = "Swap Zanaris with Last-destination or Configure on Fairy rings",
//		section = objectSection
//	)
//	default FairyRingMode swapFairyRing()
//	{
//		return FairyRingMode.LAST_DESTINATION;
//	}

//	@ConfigItem(
//		keyName = "swapKaramjaGloves",
//		name = "Karamja Gloves",
//		description = "Swap Wear with the Gem Mine or Duradel teleport on the Karamja Gloves 3 and 4",
//		section = itemSection
//	)
//	default KaramjaGlovesMode swapKaramjaGlovesMode()
//	{
//		return KaramjaGlovesMode.WEAR;
//	}

	@ConfigItem(
			keyName = "swapEmptyEssencePouch",
			name = "Empty Essence Pouch",
			description = "Empty Essence Pouch",
			section = shadySection
	)
	default boolean swapEmptyEssencePouch()
	{
		return true;
	}

	@ConfigItem(
			keyName = "swapBankFillEssencePouch",
			name = "Bank Fill Essence Pouch",
			description = "Bank Fill Essence Pouch",
			section = shadySection
	)
	default boolean swapBankFillEssencePouch() { return true; }

	@ConfigItem(
			keyName = "swapBankWithdrawAllEssence",
			name = "Withdraw-All Essence",
			description = "Withdraw-All Pure Essence from bank",
			section = shadySection
	)
	default boolean swapBankWithdrawAllEssence() { return true; }

	@ConfigItem(
			keyName = "swapWornTeleportItems",
			name = "Worn Teleports",
			description = "Swaps Remove with Teleport for worn items",
			section = shadySection
	)
	default boolean swapWornTeleportItems() { return true; }

	@ConfigItem(
			keyName = "swapBankWearJewelry",
			name = "Wear Jewelry in bank",
			description = "Swaps Deposit with Wear for jewelry in bank",
			section = shadySection
	)
	default boolean swapBankWearJewelry() { return true; }

	@ConfigItem(
			keyName = "swapQuickAltarSpellbooks",
			name = "Quick swap spellbooks",
			description = "Switches between Arceuus and Lunar spellbooks",
			section = shadySection
	)
	default boolean swapQuickAltarSpellbooks() { return true; }
}
