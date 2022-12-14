package top.e404.eplugin.hook.itemrepo

import top.e404.eplugin.EPlugin
import top.e404.eplugin.hook.EHook
import top.e404.itemrepo.ItemRepo
import top.e404.itemrepo.PL
import top.e404.itemrepo.config.ItemManager

@Suppress("UNUSED")
open class ItemRepoHook(
    override val plugin: EPlugin,
) : EHook(plugin, "ItemRepo") {
    val ir: ItemRepo
        get() = PL

    fun getIrItem(id: String) = ItemManager.getItem(id)
}