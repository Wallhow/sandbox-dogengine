package sandbox.def.craftsys

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.Viewport
import com.kotcrab.vis.ui.layout.VerticalFlowGroup
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import dogengine.Kernel
import dogengine.utils.log
import ktx.actors.onClick
import ktx.vis.table
import sandbox.go.environment.ObjectList
import sandbox.sandbox.go.player.Player

/**
 * Класс в котором отрисовывается меню крафта
 */
class HCraftTable(private val player: Player, sb: SpriteBatch) {
    private val view = Kernel.getInjector().getInstance(Viewport::class.java)
    private val stage = Stage(view, sb)
    private lateinit var root: VisTable
    private lateinit var leftCol: VerticalGroup
    private lateinit var rightCol: VisTable
    private lateinit var moreInfoCraft: VisLabel
    private lateinit var nameCraft: VisTextButton
    private lateinit var buttonCraftRecipe: VisTextButton
    private val craftList: CraftRecipes = CraftRecipes(player.getInventory())
    init {
        stage.setDebugInvisible(true)
        stage.addActor(getRootTable())
        Kernel.getInjector().getInstance(InputMultiplexer::class.java).addProcessor(stage)
    }

    private fun getRootTable(): VisTable {
        nameCraft = VisTextButton("").apply {
            isVisible = false
        }
        leftCol = VerticalGroup().apply {
            sizeBy(100f, 400f)
            addActor(nameCraft)
        }

        moreInfoCraft = VisLabel("Выберите рецепт").apply {
            setAlignment(Align.left)
            setWrap(true)
        }
        buttonCraftRecipe = VisTextButton("craft").apply {
            isVisible = true
            color = Color.CORAL
            onClick {
                currentRecipe?.let {
                    val ind1 = player.getInventory().contain(ObjectList.ROCK)
                    val ind2 = player.getInventory().contain(ObjectList.WOOD)
                    player.getInventory().pop(ind1, 3)
                    player.getInventory().pop(ind2, 1)
                    player.getInventory().push(ObjectList.CANDY)
                    if (!it.isAvailable()) {
                        buttonCraftRecipe.isVisible = false
                        nameCraft.isVisible = false
                        moreInfoCraft.isVisible = false
                    }
                }
            }
        }

        rightCol = VisTable().apply {
            add(moreInfoCraft).expand().fill()
            row()
            add(buttonCraftRecipe).bottom().center()
        }

        root = table {
            setBounds(10f, view.screenHeight/2-200f, 300f, 400f)
            scrollPane(leftCol) { }.cell(align = Align.left, width = 100f)
            add(rightCol).width(200f).height(400f).expand()
            debug = true
        }

        return root
    }

    private var currentRecipe: CraftRecipe? = null
    fun update() {
        craftList.getRecipes().forEach {
            val iti = it
            if (iti.isAvailable()) {
                nameCraft.setText(iti.name)
                nameCraft.isVisible = true
                moreInfoCraft.isVisible = true
                nameCraft.onClick {
                    moreInfoCraft.setText(iti.moreInfo)
                    buttonCraftRecipe.isVisible = true
                    currentRecipe = it
                }
                //leftCol.addActor(nameCraft)
            }
        }

        stage.act()
        stage.draw()
    }


}