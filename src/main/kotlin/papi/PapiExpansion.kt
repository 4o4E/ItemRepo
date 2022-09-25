package top.e404.itemrepo.papi

import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.entity.Player
import top.e404.itemrepo.PL
import top.e404.itemrepo.config.ItemManager
import top.e404.itemrepo.config.Lang
import top.e404.itemrepo.hook.MiHook
import top.e404.itemrepo.hook.SfHook
import java.util.regex.Pattern

/**
 * # 占位符
 *
 * - `%itemcount_empty%` 计算玩家背包中空格子的数量
 * - `%itemcount_sf_<id>%` 计算玩家背包中指定粘液科技物品的数量
 * - `%itemcount_mi_<type>_<id>%` 计算玩家背包中指定mi物品的数量
 * - `%itemcount_mc_<id>%` 计算玩家背包中指定物品的数量
 */
object PapiExpansion : PlaceholderExpansion() {
    override fun getIdentifier() = "itemcount"
    override fun getAuthor() = "404E"
    override fun getVersion() = PL.description.version

    private val pattern = Pattern.compile("(?<itemType>[^_]+)(_<(?<p1>[^>]+)>)?(_<(?<p2>[^>]+)>)?")!!

    override fun onPlaceholderRequest(player: Player?, params: String): String? {
        if (player == null) return "player is null"
        val matcher = pattern.matcher(params)
        if (!matcher.find()) return null
        val itemType = matcher.group("itemType")!!
        val p1 = matcher.group("p1")
        val p2 = matcher.group("p2")
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
            else -> null
        }
    }
}