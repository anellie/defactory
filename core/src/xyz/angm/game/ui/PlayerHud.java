package xyz.angm.game.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import com.kotcrab.vis.ui.layout.GridGroup;
import com.kotcrab.vis.ui.widget.*;
import xyz.angm.game.Game;
import xyz.angm.game.ui.screens.GameScreen;
import xyz.angm.game.world.blocks.BlockProperties;
import xyz.angm.game.world.blocks.Material;

import static xyz.angm.game.ui.screens.Screen.VIEWPORT_HEIGHT;
import static xyz.angm.game.ui.screens.Screen.VIEWPORT_WIDTH;
import static xyz.angm.game.world.entities.Player.PLAYER_HEALTH;
import static xyz.angm.game.world.entities.Player.PLAYER_STAMINA;

/** The player HUD containing all GUI elements of the player, like health bar or inventory. */
public class PlayerHud extends Group {

    /** Width of status bars. */
    static final int BAR_WIDTH = 400;
    /** Height of status bars. */
    static final int BAR_HEIGHT = 15;

    private final GameScreen screen;
    private final VisProgressBar healthBar = new VisProgressBar(0, PLAYER_HEALTH, 1, false, "health-bar");
    private final VisProgressBar staminaBar =
            new VisProgressBar(0, PLAYER_STAMINA, PLAYER_STAMINA / BAR_WIDTH, false, "stamina-bar");
    private final ObjectMap<Material, Label> materialLabels = new OrderedMap<>();
    private final VisWindow waveWindow = new VisWindow(Localization.get("hudWave", 0));
    private final BuildTooltip buildTooltip = new BuildTooltip();

    /** Construct a new HUD.
     * @param screen The screen the HUD will be a part of */
    public PlayerHud(GameScreen screen) {
        super();
        this.screen = screen;
        reload();
    }

    /** Rebuilds the player HUD. Call after a locale change. */
    public void reload() {
        clear();

        // Window containing health + stamina bar
        VisWindow barWindow = new VisWindow(Localization.get("hudStatus"));
        barWindow.add(healthBar).size(BAR_WIDTH, BAR_HEIGHT).row();
        barWindow.add(staminaBar).size(BAR_WIDTH, BAR_HEIGHT);
        barWindow.pack();
        addActor(barWindow);

        // Window containing a selection of blocks the player can build
        VisWindow buildWindow = new VisWindow(Localization.get("hudBuild"));
        ObjectMap<String, GridGroup> buildSelectionCategories = new ObjectMap<>();
        ButtonGroup<VisImageButton> buildSelectionButtons = new ButtonGroup<>();
        buildSelectionButtons.setMaxCheckCount(1);

        BlockProperties.getAllBlocks().forEach(properties -> {
            if (!properties.displayedInBuildMenu) return;

            VisImageButton button = new VisImageButton(new TextureRegionDrawable(Game.assets.get(properties.getFullTexturePath(), Texture.class)));
            buildSelectionButtons.add(button);
            button.addListener(buildTooltip.getClickListener(properties));
            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    screen.getWorld().getPlayer().setBlockSelected(properties.id);
                }
            });

            GridGroup category = buildSelectionCategories.get(properties.category);
            if (category != null) category.addActor(button);
            else {
                GridGroup newCategory = new GridGroup(32, 4);
                newCategory.addActor(button);
                buildWindow.add(new VisLabel(Localization.get("category" + properties.category))).width(300).row();
                buildWindow.add(newCategory).width(300).row();
                buildSelectionCategories.put(properties.category, newCategory);
            }
        });

        buildWindow.pack();
        buildWindow.setPosition(VIEWPORT_WIDTH, 0, Align.bottomRight);
        addActor(buildWindow);
        buildTooltip.pack();
        buildTooltip.setPosition(VIEWPORT_WIDTH, buildWindow.getHeight(), Align.bottomRight);
        addActor(buildTooltip);

        // Window containing the players inventory
        VisWindow inventoryWindow = new VisWindow(Localization.get("hudInventory"));
        for (Material material : Material.values()) {
            inventoryWindow.add(new VisImage(material.getTexture())).size(32).align(Align.left).padRight(20);
            Label label = new VisLabel();
            materialLabels.put(material, label);
            inventoryWindow.add(label).size(32).align(Align.right).padBottom(5).row();
        }
        inventoryWindow.pack();
        inventoryWindow.setPosition(0, VIEWPORT_HEIGHT, Align.topLeft);
        addActor(inventoryWindow);

        // Window containing the current wave of monsters
        waveWindow.pack();
        waveWindow.setPosition(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, Align.topRight);
        addActor(waveWindow);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        healthBar.setValue(screen.getWorld().getPlayer().getHealth());
        staminaBar.setValue(screen.getWorld().getPlayer().getStamina());
        waveWindow.getTitleLabel().setText(Localization.get("hudWave", screen.getWorld().getPlayer().getBeastWave()));
        materialLabels.entries().forEach(material -> material.value.setText(screen.getWorld().getPlayer().inventory.get(material.key)));
    }

    /** A window displaying various info about a block. */
    private class BuildTooltip extends VisWindow {

        private BuildTooltip() {
            super(Localization.get("hudTooltipTitle"));
            setVisible(false);
        }

        // Returns a ClickListener to be used with a button for the passed in block type
        private ClickListener getClickListener(BlockProperties properties) {
            return new ClickListener() {
                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    display(properties);
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    setVisible(false);
                }
            };
        }

        // Update and display this tooltip
        private void display(BlockProperties props) {
            clear();

            getTitleLabel().setText(Localization.get("block" + props.name));
            add(new VisLabel(Localization.get("desc" + props.name))).colspan(2).width(300).padBottom(20).padTop(5).row();

            add(new VisLabel(Localization.get("hudTooltipBuildCost"))).colspan(2).width(300).row();
            props.buildMaterials.forEach(((material, amount) -> {
                add(new VisImage(material.getTexture())).size(32).align(Align.right);
                add(new VisLabel("" + amount)).align(Align.left).row();
            }));

            if (props.materialRequired != null) add(new VisLabel(Localization.get("hudTooltipMaterialRequired")));
            if (props.materialProduced != null) add(new VisLabel(Localization.get("hudTooltipMaterialProduced"))).row();
            if (props.materialRequired != null) add(new VisImage(props.materialRequired.getTexture())).size(32);
            if (props.materialProduced != null) add(new VisImage(props.materialProduced.getTexture())).size(32).row();
            if (props.range > -1) add(new VisLabel(Localization.get("hudTooltipRange", props.range))).colspan(2).width(300).padTop(20).row();

            padBottom(5f);
            pack();
            setPosition(VIEWPORT_WIDTH, getY(), Align.bottomRight);
            setVisible(true);
        }
    }
}
