package top.e404.itemrepo

import top.e404.eplugin.EPlugin
import top.e404.itemrepo.command.Commands
import top.e404.itemrepo.config.Config
import top.e404.itemrepo.config.ItemManager
import top.e404.itemrepo.config.Lang
import top.e404.itemrepo.hook.HookManager
import top.e404.itemrepo.papi.PapiExpansion

class ItemRepo : EPlugin() {
    override val debugPrefix: String
        get() = langManager.getOrElse("debug_prefix") { "&7[&b物品仓库DEBUG&7]" }
    override val prefix: String
        get() = langManager.getOrElse("prefix") { "&7[&6物品仓库&7]" }

    override fun enableDebug() = Config.debug
    override val langManager by lazy { Lang }

    override fun onEnable() {
        PL = this
        Config.load(null)
        Lang.load(null)
        ItemManager.load(null)
        Commands.register()
        PapiExpansion.register()
        HookManager.register()
        info("&a加载完成")
    }

    override fun onDisable() {
        cancelAllTask()
        ItemManager.shutdown()
        info("&a卸载完成")
    }
}

lateinit var PL: ItemRepo
    private set