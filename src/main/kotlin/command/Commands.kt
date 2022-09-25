package top.e404.itemrepo.command

import top.e404.eplugin.command.ECommandManager
import top.e404.itemrepo.PL

object Commands : ECommandManager(
    PL,
    "itemrepo",
    Debug,
    Reload, Save,
    Take,
    Get, Give,
    Add, Del,
)