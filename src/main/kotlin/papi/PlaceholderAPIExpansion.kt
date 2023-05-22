package top.e404.itemrepo.papi

import org.bukkit.entity.Player
import top.e404.eplugin.hook.placeholderapi.PapiExpansion
import top.e404.itemrepo.PL
import top.e404.itemrepo.config.ItemManager
import top.e404.itemrepo.config.Lang
import top.e404.itemrepo.hook.IaHook
import top.e404.itemrepo.hook.MiHook
import top.e404.itemrepo.hook.SfHook

/**
 * # 占位符
 *
 * - `%itemcount_empty%` 计算玩家背包中空格子的数量
 * - `%itemcount_sf_<id>%` 计算玩家背包中指定粘液科技物品的数量
 * - `%itemcount_mi_<type>_<id>%` 计算玩家背包中指定mi物品的数量
 * - `%itemcount_ia_<namespaceID>%` 计算玩家背包中指定ia物品的数量
 * - `%itemcount_mc_<id>%` 计算玩家背包中指定物品的数量
 */
object PlaceholderAPIExpansion : PapiExpansion(PL, "itemcount") {
    private val regex = Regex("(?<itemType>[^_]+)_<(?<p1>[^>]+)>(_<(?<p2>[^>]+)>)?")

    override fun onPlaceholderRequest(player: Player?, params: String): String? {
        if (player == null) return "player is null"
        val result = regex.find(params) ?: return null
        val itemType = result.groups["itemType"]!!.value
        val p1 = result.groups["p1"]!!.value
        val p2 = result.groups["p2"]?.value
        return when (itemType.lowercase()) {
            "empty" -> player.inventory.count { it == null || it.type.isAir }.toString()
            "mc" -> {
                var c = 0
                val item = ItemManager.getItem(p1) ?: return "不存在的物品: $p1"
                for (i in player.inventory) {
                    if (i == null || i.type.isAir) continue
                    if (!i.isSimilar(item)) continue
                    c += i.amount
                }
                c.toString()
            }

            "sf" -> {
                var c = 0
                if (!SfHook.enable) {
                    PL.warn(Lang["hook.non_enable", "hook" to SfHook.name])
                    return "0"
                }
                for (i in player.inventory) {
                    if (i == null || i.type.isAir) continue
                    val sfItem = SfHook.getId(i) ?: continue
                    if (sfItem == p1) c += i.amount
                }
                c.toString()
            }

            "mi" -> {
                if (!MiHook.enable) {
                    PL.warn(Lang["hook.non_enable", "hook" to MiHook.name])
                    return "0"
                }
                if (p2 == null) return null
                var c = 0
                for (i in player.inventory) {
                    if (i == null || i.type.isAir) continue
                    val miItem = MiHook.getNbtItem(i)
                    if (!miItem.hasType()) continue
                    val type = miItem.type
                    val id = miItem.getString("MMOITEMS_ITEM_ID")
                    if (type.equals(p1, true) && id == p2) c += i.amount
                }
                c.toString()
            }

            "ia" -> {
                if (!IaHook.enable) {
                    PL.warn(Lang["hook.non_enable", "hook" to IaHook.name])
                    return "0"
                }
                var c = 0
                for (i in player.inventory) {
                    if (i == null || i.type.isAir) continue
                    val stack = IaHook.getIaItemInfo(i) ?: continue
                    if (stack.namespacedID == p1) c += i.amount
                }
                c.toString()
            }

            else -> null
        }
    }
}
