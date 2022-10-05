package top.e404.itemrepo.command

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import top.e404.eplugin.EPlugin.Companion.color
import top.e404.eplugin.command.ECommand
import top.e404.itemrepo.PL
import top.e404.itemrepo.config.ItemManager
import top.e404.itemrepo.config.Lang

object Add : ECommand(
    PL,
    "add",
    "(?i)add",
    false,
    "itemrepo.admin"
) {
    override val usage: String
        get() = Lang["command.usage.add"].color()

    override fun onCommand(sender: CommandSender, args: Array<out String>) {
        if (args.size != 2) {
            plugin.sendMsgWithPrefix(sender, usage)
            return
        }
        val id = args[1]
        if (id in ItemManager.items) {
            plugin.sendMsgWithPrefix(sender, Lang["command.id_already_exists", "id" to id])
            return
        }
        sender as Player
        val item = sender.inventory.itemInMainHand
        if (item.type.isAir) {
            plugin.sendMsgWithPrefix(sender, Lang["command.empty_hand"])
            return
        }
        ItemManager.items[id] = item.clone().apply { amount = 1 }
        ItemManager.scheduleSave()
        plugin.sendMsgWithPrefix(sender, Lang["command.add_done", "id" to id])
    }
}