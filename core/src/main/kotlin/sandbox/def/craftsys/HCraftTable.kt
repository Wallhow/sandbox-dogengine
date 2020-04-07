package sandbox.def.craftsys

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.Viewport
import com.kotcrab.vis.ui.widget.VisImage
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import dogengine.Kernel
import dogengine.ecs.systems.controllers.EventInputListener
import dogengine.utils.log
import ktx.actors.onClick
import ktx.actors.setPosition
import ktx.vis.table
import sandbox.go.environment.ObjectList
import sandbox.sandbox.go.assetAtlas
import sandbox.sandbox.go.player.Player

/**
 * Класс в котором отрисовывается меню крафта
 */
class HCraftTable(private val player: Player, sb: SpriteBatch) : EventInputListener() {
    private var isShowNow: Boolean = false
    private var isInvisibleNow: Boolean = false
    private val view = Kernel.getInjector().getInstance(Viewport::class.java)
    private val stage = Stage(view, sb)
    private lateinit var root: VisTable
    private lateinit var leftCol: VerticalGroup
    private lateinit var rightCol: VisTable
    private lateinit var moreInfoCraft: VisLabel
    private lateinit var nameCraft: VisTextButton
    private lateinit var iconItemCraft: VisImage
    private lateinit var buttonCraftRecipe: VisTextButton
    private val craftList: CraftRecipes = CraftRecipes(player.getInventory())
    private val dotTexture = Kernel.getInjector().getProvider(Kernel.DotTexture::class.java).get().get()
    var isVisible: Boolean = false

    fun show() {
        isVisible = true
        isShowNow = true

    }
    fun hide() {
        isInvisibleNow = true
    }

    init {
        stage.setDebugInvisible(true)
        stage.addActor(getRootTable())
        Kernel.getInjector().getInstance(InputMultiplexer::class.java).addProcessor(stage)
    }

    override fun keyPressed(key: Int): Boolean {
        if(key == Input.Keys.I) {
            if(!isShowNow || !isInvisibleNow) {
                if(!isVisible) show()
                else hide()
            }
            log(isVisible)
        }
        return false
    }

    private fun getRootTable(): VisTable {
        nameCraft = VisTextButton("").apply {
            isVisible = false
        }
        leftCol = VerticalGroup().apply {
            sizeBy(50f, 400f)
            addActor(nameCraft)
        }

        moreInfoCraft = VisLabel("Список рецептов пуст :)").apply {
            setAlignment(Align.center)
            setWrap(true)
        }
        buttonCraftRecipe = VisTextButton("craft").apply {
            isVisible = false
            color = Color.CORAL
            onClick {
                currentRecipe?.let {
                    it.needItems.forEach { needs ->
                        val index = player.getInventory().contain(needs.first)
                        player.getInventory().pop(index, needs.second)
                    }
                    player.getInventory().push(it.itemCraft)
                    if (!it.isAvailable()) {
                        invisibleCraftRecipe()
                    }
                }
            }
        }
        iconItemCraft = VisImage()

        rightCol = VisTable().apply {
            add(iconItemCraft).expandX().center().row()
            add(moreInfoCraft).expand().fill().align(Align.center)
            row()
            add(buttonCraftRecipe).bottom().center()
        }

        root = table {
            color = Color.BROWN
            background = TextureRegionDrawable(dotTexture)
            setBounds(-300f, view.screenHeight / 2 - 200f, 300f, 400f)
            scrollPane(leftCol) { }.cell(align = Align.left, width = 50f)
            add(rightCol).width(250f).height(400f).expand()
            debug = true
        }

        return root
    }

    private var currentRecipe: CraftRecipe? = null

    var timeAcc = 0f
    val moveDuration = 1f
    fun update() {
        if (isVisible && isShowNow) {
            timeAcc += Gdx.graphics.deltaTime
            val posX = Interpolation.bounceOut.apply(-300f, 10f, timeAcc / moveDuration)
            root.setPosition(posX, root.y)
            if (moveDuration <= timeAcc) {
                isShowNow = false
                timeAcc = 0f
            }
        } else if (isVisible && isInvisibleNow) {
            timeAcc += Gdx.graphics.deltaTime
            val posX = Interpolation.exp5In.apply(10f, -300f, timeAcc / moveDuration)
            root.setPosition(posX, root.y)
            if (moveDuration <= timeAcc) {
                isInvisibleNow = false
                isVisible = false
                timeAcc = 0f
            }
        }

        if (isVisible) {
            craftList.getAvailableRecipes() {
                showCraftRecipe(it)
            }
            stage.act()
            stage.draw()
        }

    }

    private fun showCraftRecipe(recipe: CraftRecipe) {
        nameCraft.setText(recipe.name)
        nameCraft.setScale(0.8f)
        nameCraft.isVisible = true
        moreInfoCraft.isVisible = true
        nameCraft.onClick {
            if (recipe.itemCraft != ObjectList.ZERO) {
                iconItemCraft.isVisible = true
                iconItemCraft.drawable = TextureRegionDrawable(assetAtlas().findRegion(recipe.itemCraft.name_res))
            }
            moreInfoCraft.setText(recipe.moreInfo)
            buttonCraftRecipe.isVisible = true
            currentRecipe = recipe
        }
    }

    private fun invisibleCraftRecipe() {
        buttonCraftRecipe.isVisible = false
        nameCraft.isVisible = false
        nameCraft.setText("")
        moreInfoCraft.isVisible = false
        moreInfoCraft.setText("")
        iconItemCraft.isVisible = false
    }

}