package top.e404.itemrepo.command

import io.github.thebusybiscuit.slimefun4.implementation.Slimefun
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import top.e404.eplugin.EPlugin.Companion.color
import top.e404.eplugin.command.ECommand
import top.e404.itemrepo.PL
import top.e404.itemrepo.config.ItemManager
import top.e404.itemrepo.config.Lang
import top.e404.itemrepo.hook.IaHook
import top.e404.itemrepo.hook.MiHook
import top.e404.itemrepo.hook.SfHook

object Take : ECommand(
    PL,
    "take",
    "(?i)take",
    false,
    "itemrepo.admin"
) {
    override val usage: String
        get() = Lang["command.usage.take"].color()

    private val allow = listOf("mc", "mi", "sf", "ia")

    override fun onTabComplete(
        sender: CommandSender,
        args: Array<out String>,
        complete: MutableList<String>
    ) {
        when (args.size) {
            2 -> complete.addAll(allow)
            3 -> complete.addOnlinePlayers()
            4 -> {
                val last = args.last()
                when (args[1].lowercase()) {
                    "mc" -> ItemManager.items.keys
                        .let { names ->
                            if (last.isBlank()) complete.addAll(names)
                            else names.forEach { if (it.contains(last, true)) complete.add(it) }
                        }
                    "mi" -> if (MiHook.enable) {
                        MiHook.mi
                            .types
                            .allTypeNames
                            .let { names ->
                                if (last.isBlank()) complete.addAll(names)
                                else names.forEach { if (it.contains(last, true)) complete.add(it) }
                            }
                    }
                    "sf" -> if (SfHook.enable) {
                        Slimefun.getRegistry()
                            .slimefunItemIds
                            .keys
                            .let { names ->
                                if (last.isBlank()) complete.addAll(names)
                                else names.forEach { if (it.contains(last, true)) complete.add(it) }
                            }
                    }
                    "ia" -> if (IaHook.enable) {
                        IaHook.getAllItem()
                            .map { it.namespacedID }
                            .let { names ->
                                if (last.isBlank()) complete.addAll(names)
                                else names.forEach { if (it.contains(last, true)) complete.add(it) }
                            }
                    }
                }
            }
            5 -> {
                if (!args[1].equals("mi", true)) return
                if (!MiHook.enable) return
                val type = MiHook.mi
                    .types
                    .get(args[3].uppercase())
                    ?: return
                val last = args.last()
                MiHook.mi
                    .templates
                    .getTemplates(type)
                    .map { it.id }
                    .let { names ->
                        if (last.isBlank()) complete.addAll(names)
                        else names.forEach { if (it.contains(last, true)) complete.add(it) }
                    }
            }
        }
    }

    override fun onCommand(sender: CommandSender, args: Array<out String>) {
        if (args.size < 4) {
            sender.sendMessage(usage)
            return
        }
        val (_, type, playerId) = args
        val p = Bukkit.getPlayer(playerId)
        // 无效玩家
        if (p == null) {
            plugin.sendMsgWithPrefix(sender, Lang["command.invalid_player", "player" to playerId])
            return
        }
        when (type.lowercase()) {
            "mc" -> {
                val itemId = args[3]
                val count = if (args.size == 4) 1 else args[4].toIntOrNull()
                if (count == null) {
                    plugin.sendMsgWithPrefix(sender, Lang["command.invalid_number", "number" to args[4]])
                    return
                }
                val item = ItemManager.getItem(itemId)
                // 无效id
                if (item == null) {
                    plugin.sendMsgWithPrefix(sender, Lang["command.invalid_item", "item" to itemId])
                    return
                }
                val success = p.takeItem(count) { item.isSimilar(it) }
                plugin.sendMsgWithPrefix(
                    sender,
                    Lang[
                            if (success) "command.take" else "command.take_not_enough",
                            "item" to itemId,
                            "player" to playerId,
                            "count" to count,
                    ]
                )
            }
            "mi" -> {
                if (!MiHook.enable) {
                    plugin.sendMsgWithPrefix(sender, Lang["hook.non_enable", "hook" to MiHook.name])
                    return
                }
                val itemType = args[3]
                val itemId = args[4]
                val count = if (args.size == 5) 1 else args[5].toIntOrNull()
                if (count == null) {
                    plugin.sendMsgWithPrefix(sender, Lang["command.invalid_number", "number" to args[4]])
                    return
                }
                val success = p.takeItem(count) {
                    val nbtItem = MiHook.getNbtItem(it)
                    if (!nbtItem.hasType()) return@takeItem false
                    val t = nbtItem.type
                    val id = nbtItem.getString("MMOITEMS_ITEM_ID")
                    t.equals(itemType, true) && id == itemId
                }
                plugin.sendMsgWithPrefix(
                    sender,
                    Lang[
                            if (success) "command.take" else "command.take_not_enough",
                            "item" to "$itemType > $itemId",
                            "player" to playerId,
                            "count" to count,
                    ]
                )
            }
            "sf" -> {
                if (!SfHook.enable) {
                    plugin.sendMsgWithPrefix(sender, Lang["hook.non_enable", "hook" to SfHook.name])
                    return
                }
                val itemId = args[3]
                val count = if (args.size == 4) 1 else args[4].toIntOrNull()
                if (count == null) {
                    plugin.sendMsgWithPrefix(sender, Lang["command.invalid_number", "number" to args[4]])
                    return
                }
                val success = p.takeItem(count) {
                    SfHook.getId(it)?.equals(itemId, true) ?: false
                }
                plugin.sendMsgWithPrefix(
                    sender,
                    Lang[
                            if (success) "command.take" else "command.take_not_enough",
                            "item" to itemId,
                            "player" to playerId,
                            "count" to count,
                    ]
                )
            }
            "ia" -> {
                if (!IaHook.enable) {
                    plugin.sendMsgWithPrefix(sender, Lang["hook.non_enable", "hook" to IaHook.name])
                    return
                }
                val namespacedID = args[3]
                val count = if (args.size == 4) 1 else args[4].toIntOrNull()
                if (count == null) {
                    plugin.sendMsgWithPrefix(sender, Lang["command.invalid_number", "number" to args[4]])
                    return
                }
                val success = p.takeItem(count) {
                    IaHook.getIaItemInfo(it)?.namespacedID?.equals(namespacedID, true) ?: false
                }
                plugin.sendMsgWithPrefix(
                    sender,
                    Lang[
                            if (success) "command.take" else "command.take_not_enough",
                            "item" to namespacedID,
                            "player" to playerId,
                            "count" to count,
                    ]
                )
            }
            else -> sender.sendMessage(usage)
        }
    }

    /**
     * 从玩家背包拿取指定物品
     *
     * @param condition 判断物品是否是需要拿取的数量
     * @return 若没有完成拿取则返回false
     */
    private fun Player.takeItem(count: Int, condition: (ItemStack) -> Boolean): Boolean {
        var i = count // 剩余待拿取
        for ((index, itemStack) in inventory.withIndex()) {
            if (itemStack == null
                || itemStack.type.isAir
                || !condition(itemStack)
            ) continue
            var amount = itemStack.amount
            // 物品数量小于要扣的数量
            if (amount <= i) {
                i -= amount
                itemStack.type = Material.AIR
                inventory.setItem(index, itemStack)
                continue
            }
            // 物品数量大于要扣的数量
            amount -= i
            itemStack.amount = amount
            inventory.setItem(index, itemStack)
            i = 0
            break
        }
        return i == 0
    }
}