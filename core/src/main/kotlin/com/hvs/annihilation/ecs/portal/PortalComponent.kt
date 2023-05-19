package com.hvs.annihilation.ecs.portal

import com.github.quillraven.fleks.Entity

data class PortalComponent(
    var id: Int = -1,
    var toMap: String = "",
    var toPortal: Int = -1,
    var entitiesToMove: MutableSet<Entity> = mutableSetOf()
)
