package xyz.jvmejiro.fishing_game201903_core.systems

import com.badlogic.ashley.core.Family.all
import com.badlogic.ashley.systems.IteratingSystem
import xyz.jvmejiro.fishing_game201903_core.Hitbox
import xyz.jvmejiro.fishing_game201903_core.Position
import xyz.jvmejiro.fishing_game201903_core.Size
import xyz.jvmejiro.fishing_game201903_core.TextureComponent

class ShapeRenderSystem : IteratingSystem(
    all(
        TextureComponent::class.java,
        Size::class.java,
        Position::class.java,
        Hitbox::class.java
    ).get()
) {

}