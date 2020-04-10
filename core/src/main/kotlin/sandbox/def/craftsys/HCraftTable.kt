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
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ArrayMap
import com.badlogic.gdx.utils.viewport.Viewport
import com.kotcrab.vis.ui.widget.*
import dogengine.Kernel
import dogengine.ecs.systems.controllers.EventInputListener
import dogengine.utils.log
import ktx.actors.onClick
import ktx.vis.table
import sandbox.go.environment.ItemList
import sandbox.sandbox.getTextureDot
import sandbox.sandbox.go.assetAtlas
import sandbox.sandbox.go.player.Player

/**
 * Класс в котором отрисовывается меню крафта
 */
class HCraftTable(private val player: Player, sb: SpriteBatch) : EventInputListener() {
    private val backgroundColor: Color = Color.DARK_GRAY
    private val view = Kernel.getInjector().getInstance(Viewport::class.java)
    private val stage = Stage(view, sb)
    private lateinit var root: VisTable
    private lateinit var leftCol: VerticalGroup
    private lateinit var rightCol: VisTable
    private lateinit var moreInfoRecipe: VisLabel
    private lateinit var nameRecipe: VisTextButton
    private lateinit var iconItemCraft: VisImage
    private lateinit var buttonCraftRecipe: VisTextButton
    private var currentRecipe: CraftRecipe? = null
    private val recipes: CraftRecipes = CraftRecipes(player.getInventory())
    private val dotTexture = getTextureDot()
    private var isVisible: Boolean = false
    private var isShowNow: Boolean = false
    private var isInvisibleNow: Boolean = false
    private val fromPositionTableX = -300f
    private val toPositionTableX = 10f
    private var timeAcc = 0f
    private val moveTableDuration = 1f
    private val availableRecipesButton = Array<VisImageButton>()
    private val availableRecipes = ArrayMap<VisImageButton,CraftRecipe>()

    private fun show() {
        isVisible = true
        isShowNow = true

    }
    private fun hide() {
        isInvisibleNow = true
    }

    init {
        stage.setDebugInvisible(true)
        stage.addActor(getRootTable())
        Kernel.getInjector().getInstance(InputMultiplexer::class.java).addProcessor(stage)
    }
    private fun getRootTable(): VisTable {
        nameRecipe = VisTextButton("").apply {
            isVisible = false
        }
        leftCol = VerticalGroup().apply {
            sizeBy(50f, 400f)
            addActor(nameRecipe)
        }

        moreInfoRecipe = VisLabel("Список рецептов пуст :)").apply {
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
                }
            }
        }
        iconItemCraft = VisImage()

        rightCol = VisTable().apply {
            add(iconItemCraft).expandX().center().row()
            add(moreInfoRecipe).expand().fill().align(Align.center)
            row()
            add(buttonCraftRecipe).bottom().center()
        }

        root = table {
            color = backgroundColor
            background = TextureRegionDrawable(dotTexture)
            setBounds(-300f, view.screenHeight / 2 - 200f, 300f, 400f)
            scrollPane(leftCol) { }.cell(align = Align.left, width = 50f)
            add(rightCol).width(250f).height(400f).expand()
            debug = true
        }

        return root
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
    fun update() {
        val delta = Gdx.graphics.deltaTime
        if (isVisible && isShowNow) {
            showProcess(delta)
        } else if (isVisible && isInvisibleNow) {
            hideProcess(delta)
        }
        if (isVisible) {
            recipes.getAvailableRecipes() {
                showCraftRecipe(it)
            }
            stage.act()
            availableRecipesButton.forEach {
                val rec = availableRecipes[it]
                if(!recipes.isAvailableRecipe(rec)) {
                    invisibleCraftRecipe(rec)
                    availableRecipes.removeValue(rec,true)
                    availableRecipesButton.removeValue(it,true)
                }
            }
            stage.draw()
        }

    }

    private fun showProcess(delta: Float) {
        timeAcc += delta
        val posX = Interpolation.bounceOut.apply(fromPositionTableX, toPositionTableX, timeAcc / moveTableDuration)
        root.setPosition(posX, root.y)
        if (moveTableDuration <= timeAcc) {
            isShowNow = false
            timeAcc = 0f
        }
    }
    private fun hideProcess(delta: Float) {
        timeAcc += delta
        val posX = Interpolation.exp5In.apply(toPositionTableX, fromPositionTableX, timeAcc / moveTableDuration)
        root.setPosition(posX, root.y)
        if (moveTableDuration <= timeAcc) {
            isInvisibleNow = false
            isVisible = false
            timeAcc = 0f
        }
    }

    private fun showCraftRecipe(recipe: CraftRecipe) {
        if(leftCol.findActor<VisImageButton>(recipe.name) == null) {
            val nameRec = VisImageButton(TextureRegionDrawable(assetAtlas().findRegion(recipe.itemCraft.resourcesName)))
            availableRecipesButton.add(nameRec)
            availableRecipes.put(nameRec,recipe)


            nameRec.name = recipe.name
            nameRec.setScale(0.7f)
            nameRec.onClick {
                moreInfoRecipe.isVisible = true
                if (recipe.itemCraft != ItemList.ZERO) {
                    iconItemCraft.isVisible = true
                    iconItemCraft.drawable = TextureRegionDrawable(assetAtlas().findRegion(recipe.itemCraft.resourcesName))
                }
                moreInfoRecipe.setText(recipe.moreInfo)
                buttonCraftRecipe.isVisible = true
                currentRecipe = recipe
            }
            leftCol.addActor(nameRec)
        }
        /*nameRecipe.setText(recipe.name)
        nameRecipe.setScale(0.8f)
        nameRecipe.isVisible = true
        moreInfoRecipe.isVisible = true
        nameRecipe.onClick {
            if (recipe.itemCraft != ItemList.ZERO) {
                iconItemCraft.isVisible = true
                iconItemCraft.drawable = TextureRegionDrawable(assetAtlas().findRegion(recipe.itemCraft.resourcesName))
            }
            moreInfoRecipe.setText(recipe.moreInfo)
            buttonCraftRecipe.isVisible = true
            currentRecipe = recipe
        }*/
    }
    private fun invisibleCraftRecipe(recipe: CraftRecipe) {
        leftCol.findActor<VisImageButton>(recipe.name).remove()
        buttonCraftRecipe.isVisible = false
        nameRecipe.isVisible = false
        nameRecipe.setText("")
        moreInfoRecipe.isVisible = false
        moreInfoRecipe.setText("")
        iconItemCraft.isVisible = false
    }

}