/*
 * Copyright (c) 2019, Ron Young <https://github.com/raiyni>
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

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("guppyswap")
public interface GuppySwapConfig extends Config
{
	@ConfigSection(
		name = "Item Swaps",
		description = "All options that swap item menu entries",
		position = 0
	)
	String itemSection = "items";

	@ConfigSection(
		name = "NPC Swaps",
		description = "All options that swap NPC menu entries",
		position = 1
	)
	String npcSection = "npcs";

	@ConfigSection(
		name = "Object Swaps",
		description = "All options that swap object menu entries",
		position = 2
	)
	String objectSection = "objects";

	@ConfigSection(
		name = "UI Swaps",
		description = "All options that swap entries in the UI (except Items)",
		position = 3
	)
	String uiSection = "ui";

	@ConfigSection(
			name = "Shady Swaps",
			description = "Options that maybe you shouldn't use",
			position = 4
	)
	String shadySection = "shady";


	// ============================ CONFIG ITEMS ============================
	@ConfigItem(
		keyName = "emptyEssencePouch",
		name = "Empty Essence Pouch",
		description = "Change the left click option to Empty for Essence Pouches.",
		section = shadySection
	)
	default boolean emptyEssencePouch()
	{
		return true;
	}

}

