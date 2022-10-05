package top.e404.itemrepo.command

import org.bukkit.command.CommandSender
import top.e404.eplugin.EPlugin.Companion.color
import top.e404.eplugin.command.ECommand
import top.e404.itemrepo.PL
import top.e404.itemrepo.config.Config
import top.e404.itemrepo.config.ItemManager
import top.e404.itemrepo.config.Lang
import top.e404.itemrepo.hook.HookManager

object Reload : ECommand(
    PL,
    "reload",
    "(?i)reload|r",
    false,
    "itemrepo.admin"
) {
    override val usage: String
        get() = Lang["command.usage.reload"].color()

    override fun onCommand(sender: CommandSender, args: Array<out String>) {
        plugin.runTaskAsync {
            Lang.load(sender)
            Config.load(sender)
            ItemManager.load(sender)
            HookManager.checkHooks()
            plugin.sendMsgWithPrefix(sender, Lang["command.reload_done"])
        }
    }
}