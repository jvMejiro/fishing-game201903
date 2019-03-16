package xyz.jvmejiro.fishing_game201903_core.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.ashley.mapperFor
import ktx.math.vec2
import xyz.jvmejiro.fishing_game201903_core.*

class ShapeRenderSystem(private val batch: ShapeRenderer) : IteratingSystem(
    allOf(
        TextureComponent::class,
        Size::class,
        Position::class
    ).get(),
    10
) {
    private lateinit var renderTargets: MutableList<Entity>

    companion object {
        private val TEXTURE_COMPONENT_MAPPER: ComponentMapper<TextureComponent> = mapperFor()
        private val SIZE_MAPPER: ComponentMapper<Size> = mapperFor()
        private val POSITION_MAPPER: ComponentMapper<Position> = mapperFor()
        private val HITBOX_MAPPER: ComponentMapper<Hitbox> = mapperFor()
        private val ROTATION_MAPPER: ComponentMapper<Rotation> = mapperFor()
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        if (entity[TEXTURE_COMPONENT_MAPPER]?.isVisible == true) {
            renderTargets.add(entity)
        }
    }

    override fun update(deltaTime: Float) {
        renderTargets = mutableListOf()
        super.update(deltaTime)
        renderTargets.sortedWith(
            compareBy(
                { it[TEXTURE_COMPONENT_MAPPER]?.zLevel },
                { -(it[POSITION_MAPPER]?.value?.y ?: 0.0f) })
        )
        renderTargets.forEach {
            val size = it[SIZE_MAPPER] ?: return@forEach
            val position = it[POSITION_MAPPER] ?: return@forEach
            val hitbox = it[HITBOX_MAPPER]
            val rotation = it[ROTATION_MAPPER]

            batch.begin(ShapeRenderer.ShapeType.Line)

            draw(position, rotation, size)
            hitbox?.let { drawHitbox(it, position, rotation) }
            batch.end()
        }
    }

    private fun draw(
        position: Position,
        rotation: Rotation?,
        size: Size
    ) {
        val axis = rotation?.axis ?: vec2()
        val degree = rotation?.degree ?: 0.0f
        batch.color = Color.RED
        batch.rect(
            position.value.x, position.value.y,
            axis.x, axis.y,
            size.value.x, size.value.y,
            1.0f, 1.0f,
            degree
        )
    }

    private fun drawHitbox(
        hitbox: Hitbox,
        position: Position,
        rotation: Rotation?
    ) {
        batch.color = Color.BLUE.apply { a = 0.5f }
        when (hitbox.type) {
            ShapeType.Rectangle ->
                batch.rect(
                    position.value.x + hitbox.offset.x, position.value.y + hitbox.offset.y,
                    hitbox.size.x, hitbox.size.y
                )
            ShapeType.Circle -> batch.ellipse(
                position.value.x + hitbox.offset.x,
                position.value.y + hitbox.offset.y,
                hitbox.size.x,
                hitbox.size.y
            )
        }
    }
}