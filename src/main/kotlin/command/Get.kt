package top.e404.itemrepo.command

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import top.e404.eplugin.EPlugin.Companion.color
import top.e404.eplugin.command.ECommand
import top.e404.itemrepo.PL
import top.e404.itemrepo.config.ItemManager
import top.e404.itemrepo.config.Lang

object Get : ECommand(
    PL,
    "get",
    Regex("(?i)get"),
    true,
    "itemrepo.admin"
) {
    override val usage: String
        get() = Lang["command.usage.get"].color()

    override fun onTabComplete(
        sender: CommandSender,
        args: Array<out String>,
        complete: MutableList<String>
    ) {
        when (args.size) {
            2 -> {
                val last = args.last()
                ItemManager.items.keys.forEach { id ->
                    if (id in last) complete.add(id)
                }
            }
        }
    }

    override fun onCommand(sender: CommandSender, args: Array<out String>) {
        if (args.size < 2) {
            sender.sendMessage(usage)
            return
        }
        val itemId = args[1]
        val count = if (args.size == 2) 1 else args[2].toIntOrNull()
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
        sender as Player
        val inv = sender.inventory
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
                sender.world.dropItem(sender.location, itemStack)
                drop = true
                continue
            }
            inv.setItem(firstEmpty, itemStack)
        }
        if (drop) plugin.sendMsgWithPrefix(sender, Lang["command.get_with_drop"])
        plugin.sendMsgWithPrefix(
            sender,
            Lang[
                    "command.give",
                    "item" to itemId,
                    "player" to sender.name,
                    "count" to count,
            ]
        )
    }
}