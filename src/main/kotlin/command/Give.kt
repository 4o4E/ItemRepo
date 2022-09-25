package top.e404.itemrepo.command

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.inventory.ItemStack
import top.e404.eplugin.EPlugin.Companion.color
import top.e404.eplugin.command.ECommand
import top.e404.itemrepo.PL
import top.e404.itemrepo.config.ItemManager
import top.e404.itemrepo.config.Lang

object Give : ECommand(
    PL,
    "give",
    Regex("(?i)give"),
    false,
    "itemrepo.admin"
) {
    override val usage: String
        get() = Lang["command.usage.give"].color()

    override fun onTabComplete(
        sender: CommandSender,
        args: Array<out String>,
        complete: MutableList<String>
    ) {
        when (args.size) {
            2 -> complete.addOnlinePlayers()
            3 -> {
                val last = args.last()
                ItemManager.items.keys.forEach { id ->
                    if (id in last) complete.add(id)
                }
            }
        }
    }

    override fun onCommand(sender: CommandSender, args: Array<out String>) {
        if (args.size < 3) {
            sender.sendMessage(usage)
            return
        }
        val playerId = args[1]
        val itemId = args[2]
        val count = if (args.size == 3) 1 else args[3].toIntOrNull()
        if (count == null) {
            plugin.sendMsgWithPrefix(sender, Lang["command.invalid_number", "number" to args[3]])
            return
        }
        val item = ItemManager.getItem(itemId)
        // 无效id
        if (item == null) {
            plugin.sendMsgWithPrefix(
                sender,
                Lang[
                        "command.id_non_exists",
                        "id" to itemId,
                        "ids" to ItemManager.items.keys.joinToString(", ")
                ]
            )
            return
        }
        val p = Bukkit.getPlayer(playerId)
        // 无效玩家
        if (p == null) {
            plugin.sendMsgWithPrefix(sender, Lang["command.invalid_player", "player" to playerId])
            return
        }
        val inv = p.inventory
        val maxStackSize = item.type.maxStackSize
        val list = ArrayList<ItemStack>(count / maxStackSize + 1)
        var i = count
        while (true) {
            if (i < maxStackSize) {
                list.add(item.clone().apply { amount = i })
                break
            }
            i -= maxStackSize
            list.add(item.clone().apply { amount = maxStackSize })
        }
        var drop = false
        for (itemStack in list) {
            val firstEmpty = inv.firstEmpty()
            if (firstEmpty == -1) {
                p.world.dropItem(p.location, itemStack)
                drop = true
                continue
            }
            inv.setItem(firstEmpty, itemStack)
        }
        if (drop) {
            plugin.sendMsgWithPrefix(p, Lang["command.give_with_drop.target"])
            plugin.sendMsgWithPrefix(sender, Lang["command.give_with_drop.trigger", "player" to p.name])
        }
        plugin.sendMsgWithPrefix(
            sender,
            Lang[
                    "command.give",
                    "item" to itemId,
                    "player" to playerId,
                    "count" to count,
            ]
        )
    }
}