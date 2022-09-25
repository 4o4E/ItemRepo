package top.e404.itemrepo.config

import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import top.e404.eplugin.config.EConfig
import top.e404.eplugin.config.JarConfig
import top.e404.itemrepo.PL

object ItemManager : EConfig(
    PL,
    "items.yml",
    JarConfig(PL, "items.yml"),
    true
) {
    var items = mutableMapOf<String, ItemStack>()
        private set

    override fun YamlConfiguration.onLoad() {
        val map = mutableMapOf<String, ItemStack>()
        for (id in getKeys(false)) try {
            map[id] = getItemStack(id) ?: throw Exception("$id is null")
        } catch (e: Exception) {
            plugin.warn("加载物品${id}时出现异常, 跳过", e)
        }
        items = map
        plugin.info("&a加载物品完成, 共${items.size}个")
    }

    override fun YamlConfiguration.onSave() {
        items.forEach { (id, item) -> set(id, item) }
    }

    fun getItem(id: String) = items[id]
}