package top.e404.itemrepo.config

import top.e404.eplugin.config.EConfig
import top.e404.eplugin.config.JarConfig
import top.e404.itemrepo.PL

object Config : EConfig(
    plugin = PL,
    path = "config.yml",
    default = JarConfig(PL, "config.yml")
) {
    var debug: Boolean
        get() = config.getBoolean("debug")
        set(value) {
            config.set("debug", value)
        }
}