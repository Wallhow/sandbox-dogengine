package sandbox.sandbox.def.gui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.ArrayMap
import com.kotcrab.vis.ui.widget.*
import dogengine.Kernel
import dogengine.ecs.systems.controllers.EventInputListener
import dogengine.utils.GameCamera
import dogengine.utils.log
import ktx.actors.onClick
import ktx.vis.table
import sandbox.dev.craftsys.CraftRecipe
import sandbox.dev.craftsys.CraftRecipes
import sandbox.sandbox.go.objects.ItemList
import sandbox.sandbox.getTextureDot
import sandbox.sandbox.go.assetAtlas
import sandbox.sandbox.go.player.Player

/**
 * Класс в котором отрисовывается меню крафта
 */
class HCraftTable(private val player: Player) : EventInputListener() {
    private val backgroundColor: Color = Color.DARK_GRAY
    private val gameCamera = Kernel.getInjector().getInstance(GameCamera::class.java)

    private lateinit var root: VisTable
    private lateinit var recipeLine: VerticalGroup
    private lateinit var submenu: VisTable
    private lateinit var moreInfoRecipe: VisLabel
    private lateinit var iconItemCraft: VisImage
    private lateinit var buttonRecipeCraft: VisTextButton
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
    private val recipeManager: CraftRecipesManager = CraftRecipesManager(recipes)
    private val drawable = TextureRegionDrawable(dotTexture)

    init {
        createTable()

    }

    private fun createTable(): VisTable {
        recipeLine = VerticalGroup().apply {
            setSize(50f, 400f)
            space(5f)
            color = backgroundColor
        }

        moreInfoRecipe = VisLabel("Список рецептов пуст :)").apply {
            setAlignment(Align.center)
            setWrap(true)
        }
        buttonRecipeCraft = VisTextButton("craft").apply {
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
        val hideRightCol = VisTextButton("x").apply {
            setSize(16f,16f)
            onClick {
                hideSubmenu()
            }
        }
        submenu = VisTable().apply {
            isVisible = false
            background = drawable
            color = backgroundColor.cpy().apply { a = 0.75f }
            add(iconItemCraft).expandX().center().padLeft(20f)
            add(hideRightCol).right()
            row()
            add(moreInfoRecipe).expand().colspan(2).fill().align(Align.center)
            row()
            add(buttonRecipeCraft).bottom().colspan(2).expandX().fillX().center()
        }

        root = table {
            setBounds(-300f, gameCamera.getViewport().worldHeight/2f - 200f, 300f, 400f)
            scrollPane(recipeLine) { }.cell(align = Align.left, width = 50f)
            add(submenu).width(250f).height(400f).expand()
        }

        return root
    }

    private fun show() {
        isVisible = true
        isShowNow = true

    }
    private fun hide() {
        isInvisibleNow = true
    }
    private fun hideSubmenu() {
        submenu.isVisible = false
    }
    private fun showSubmenu() {
        submenu.isVisible = true
    }
    private fun clearSubmenu() {
        buttonRecipeCraft.isVisible = false
        moreInfoRecipe.isVisible = false
        moreInfoRecipe.setText("")
        iconItemCraft.isVisible = false
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
            hideSubmenu()
            timeAcc = 0f
        }
    }

    //Тут  создаются доступные крафты
    private fun showCraftRecipe(recipe: CraftRecipe) {
        if (isRecipeOpened(recipe)) return

        val iconDrawable = TextureRegionDrawable(assetAtlas().findRegion(recipe.itemCraft.resourcesName))
        iconDrawable.setMinSize(24f,24f)
        val iconRecipe = VisImageButton(iconDrawable)
        recipeManager.openRecipe(recipe)
        iconRecipe.name = recipe.name
        iconRecipe.background = drawable
        iconRecipe.color = Color.LIGHT_GRAY

        iconRecipe.onClick {
            moreInfoRecipe.isVisible = true
            if (recipe.itemCraft != ItemList.ZERO) {
                iconItemCraft.isVisible = true
                iconItemCraft.drawable = TextureRegionDrawable(assetAtlas().findRegion(recipe.itemCraft.resourcesName))
            }
            moreInfoRecipe.setText(recipe.moreInfo)
            buttonRecipeCraft.isVisible = true
            currentRecipe = recipe
            showSubmenu()
        }
        recipeLine.addActor(iconRecipe)

    }
    private fun invisibleCraftRecipe(recipe: CraftRecipe) {
        recipeLine.findActor<VisImageButton>(recipe.name).remove()
        if(currentRecipe == recipe) {
            clearSubmenu()
            hideSubmenu()
        }
    }

    fun update() {
        val delta = Gdx.graphics.deltaTime

        if (isVisible && isShowNow) {
            showProcess(delta)
        }
        else if (isVisible && isInvisibleNow) {
            hideProcess(delta)
        }
        if (isVisible) {
            recipes.getAvailableRecipes {
                showCraftRecipe(it)
            }
            //Если какой-то из ранее открытых рецептов закрылся вызываем метод
            //в который передаем закрывшийся рецепт
            recipeManager.closingRecipes {
                invisibleCraftRecipe(it)
            }
        }
    }

    override fun keyPressed(key: Int): Boolean {
        if (key == Input.Keys.I) {
            if (!isShowNow || !isInvisibleNow) {
                if (!isVisible) show()
                else hide()
            }
            log(isVisible)
        }
        return false
    }

    //Вспомагательные методы
    private fun isRecipeOpened(recipe: CraftRecipe): Boolean {
        return recipeLine.findActor<VisImageButton>(recipe.name) != null
    }

    fun getRoot(): VisTable {
        return root
    }


    private class CraftRecipesManager(private val craftRecipes: CraftRecipes) {
        private val availableRecipes = ArrayMap<Int, CraftRecipe>()

        fun openRecipe(recipe: CraftRecipe) {
            availableRecipes.put(recipe.name.hashCode(), recipe)
        }

        fun closingRecipes(function: (recipe: CraftRecipe) -> Unit) {
            availableRecipes.forEach {
                if (!craftRecipes.isAvailableRecipe(it.value)) {
                    function.invoke(it.value)
                    availableRecipes.removeValue(it.value, true)
                }
            }
        }


    }
}