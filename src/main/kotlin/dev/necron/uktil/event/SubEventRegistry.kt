/*
 * Copyright (C) 2025 Yqloss
 *
 * This file is part of Yqloss Client (Mixin).
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 (GPLv2)
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Yqloss Client (Mixin). If not, see <https://www.gnu.org/licenses/old-licenses/gpl-2.0.html>.
 */

package dev.necron.uktil.event

import kotlin.reflect.KClass

class SubEventRegistry(
    private val parent: ManagerEventRegistry<Any?>,
    private val key: Any?,
) : EventRegistry {
    override fun <T : Event> register(
        type: KClass<T>,
        priority: Int,
        handler: EventHandler<T>,
    ) = parent.register(key, type, priority, handler)

    override fun <T : Event> registerOnly(
        type: KClass<T>,
        priority: Int,
        handler: EventHandler<T>,
    ) = parent.registerOnly(key, type, priority, handler)

    override fun unregister(handler: EventHandler<*>) = parent.unregister(handler)

    override fun unregisterOnly(handler: EventHandler<*>) = parent.unregisterOnly(handler)

    override fun unregisterAll(handler: EventHandler<*>) = parent.unregisterAll(handler)

    override fun clear() = parent.unregisterKey(key)
}
