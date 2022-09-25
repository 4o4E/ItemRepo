package top.e404.itemrepo.command

import org.bukkit.command.CommandSender
import top.e404.eplugin.EPlugin.Companion.color
import top.e404.eplugin.command.ECommand
import top.e404.itemrepo.PL
import top.e404.itemrepo.config.ItemManager
import top.e404.itemrepo.config.Lang

object Save : ECommand(
    PL,
    "save",
    Regex("(?i)save|s"),
    false,
    "itemrepo.admin"
) {
    override val usage: String
        get() = Lang["command.usage.save"].color()

    override fun onCommand(sender: CommandSender, args: Array<out String>) {
        plugin.runTaskAsync {
            ItemManager.save(sender)
            plugin.sendMsgWithPrefix(sender, Lang["command.save_done"])
        }
    }
}