package top.e404.itemrepo.command

import org.bukkit.command.CommandSender
import top.e404.eplugin.EPlugin.Companion.color
import top.e404.eplugin.command.ECommand
import top.e404.itemrepo.PL
import top.e404.itemrepo.config.ItemManager
import top.e404.itemrepo.config.Lang

object Del : ECommand(
    PL,
    "del",
    "(?i)del",
    false,
    "itemrepo.admin"
) {
    override val usage: String
        get() = Lang["command.usage.del"].color()

    override fun onCommand(sender: CommandSender, args: Array<out String>) {
        if (args.size != 2) {
            plugin.sendMsgWithPrefix(sender, usage)
            return
        }
        val id = args[1]
        if (id !in ItemManager.items) {
            plugin.sendMsgWithPrefix(
                sender,
                Lang[
                        "command.id_non_exists",
                        "id" to id,
                        "ids" to ItemManager.items.keys.joinToString(", ")
                ]
            )
            return
        }
        ItemManager.items.remove(id)
        ItemManager.scheduleSave()
        plugin.sendMsgWithPrefix(sender, Lang["command.del_done", "id" to id])
    }
}