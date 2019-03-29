package xyz.jvmejiro.fishing_game201903_core.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.ashley.mapperFor
import xyz.jvmejiro.fishing_game201903_core.IllegalEntityException
import xyz.jvmejiro.fishing_game201903_core.components.*

class RenderSystem(private val batch: SpriteBatch) : IteratingSystem(
    allOf(
        TextureComponent::class,
        Position::class,
        Size::class,
        Rotation::class,
        Direction::class
    ).get()
) {

    private val renderEntities = mutableListOf<Entity>()

    companion object {
        val TEXTURE_COMPONENT_MAPPER: ComponentMapper<TextureComponent> = mapperFor()
        val POSITION_MAPPER: ComponentMapper<Position> = mapperFor()
        val SIZE_MAPPER: ComponentMapper<Size> = mapperFor()
        val ROTATION_MAPPER: ComponentMapper<Rotation> = mapperFor()
        val DIRECTION_MAPPER: ComponentMapper<Direction> = mapperFor()
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        renderEntities.sortWith(compareBy({ it[TEXTURE_COMPONENT_MAPPER]?.zLevel }, { it[POSITION_MAPPER]?.value?.y }))
        draw()
        renderEntities.clear()
    }

    private fun draw() {
        batch.begin()
        renderEntities.forEach { entity ->
            val textureComponent = entity[TEXTURE_COMPONENT_MAPPER] ?: throw IllegalEntityException()
            val position = entity[POSITION_MAPPER] ?: throw IllegalEntityException()
            val size = entity[SIZE_MAPPER] ?: throw IllegalEntityException()
            val rotation = entity[ROTATION_MAPPER] ?: throw IllegalEntityException()
            val direction = entity[DIRECTION_MAPPER] ?: throw IllegalEntityException()

            val isXFlip = Math.signum(direction.value.x) < 0

            val textureRegion = textureComponent.texture ?: return@forEach
            batch.draw(
                textureRegion.texture,
                position.value.x, position.value.y,
                rotation.axis.x, rotation.axis.y,
                size.value.x, size.value.y,
                1f, 1f,
                rotation.degree,
                textureRegion.regionX, textureRegion.regionY, textureRegion.regionWidth, textureRegion.regionHeight,
                isXFlip, false
            )
        }
        batch.end()
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val texture = entity[TEXTURE_COMPONENT_MAPPER] ?: throw IllegalEntityException()
        if (texture.texture != null && texture.isVisible) {
            renderEntities += entity
        }
    }
}