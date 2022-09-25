package top.e404.itemrepo.hook

import top.e404.eplugin.hook.EHookManager
import top.e404.itemrepo.PL

object HookManager : EHookManager(
    PL,
    SfHook,
    MiHook,
)