package org.egordorichev.lasttry.entity.entities.item.tile;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import org.egordorichev.lasttry.entity.Entity;
import org.egordorichev.lasttry.entity.asset.Assets;
import org.egordorichev.lasttry.entity.component.IdComponent;
import org.egordorichev.lasttry.entity.engine.system.systems.CameraSystem;
import org.egordorichev.lasttry.entity.entities.camera.CameraComponent;
import org.egordorichev.lasttry.entity.entities.item.Item;
import org.egordorichev.lasttry.entity.entities.item.ItemUseComponent;
import org.egordorichev.lasttry.entity.entities.item.StackComponent;
import org.egordorichev.lasttry.entity.entities.item.tile.helper.TileHelper;
import org.egordorichev.lasttry.graphics.Graphics;
import org.egordorichev.lasttry.util.binary.BinaryPacker;

import java.util.Objects;

/**
 * Represents a block/wall in the world
 */
public class Tile extends Item {
	/**
	 * Tile size
	 */
	public static short SIZE = 8;
	/**
	 * The cracks
	 */
	public static TextureRegion[][] cracks;

	public Tile(String id) {
		super(id);

		if (cracks == null) {
			// TODO: optimize?
			cracks = Assets.getTexture("util/tile_cracks").split(SIZE, SIZE);
		}

		this.addComponent(TileComponent.class);
		this.getComponent(ItemUseComponent.class).autoUse = true;
		this.getComponent(StackComponent.class).max = 999;
	}

	/**
	 * @return Helper, used for this block
	 */
	public TileHelper getHelper() {
		return TileHelper.main;
	}

	/**
	 * Renders the tile
	 *
	 * @param x     Tile X
	 * @param y     Tile Y
	 * @param data  Tile data
	 * @param light How light the wall is
	 */
	public void render(short x, short y, int data, float light) {
		this.render(x, y, data, light, this.getNeighbors(x, y));
	}

	/**
	 * Renders the tile
	 *
	 * @param x     Tile X
	 * @param y     Tile Y
	 * @param data  Tile data
	 * @param light How light the tile is
	 * @param neighbors Packed binary representing neighbors
	 */
	public void render(short x, short y, int data, float light, int neighbors) {
		Graphics.batch.setColor(light, light, light, 1.0f);

		int variant = this.getVariant(data);
		int health = this.getHealth(data);

		Graphics.batch.draw(this.getComponent(TileComponent.class).tiles[variant][neighbors], x * SIZE, y * SIZE);

		if (health < 3 && health > 0) {
			// Render the cracks
			Graphics.batch.draw(cracks[0][health - 1], x * SIZE, y * SIZE);
		}

		// Reset the color!
		Graphics.batch.setColor(1, 1, 1, 1);
	}

	/**
	 * Places a tile
	 *
	 * @param entity Item owner
	 * @return Should the item be removed from inventory
	 */
	@Override
	protected boolean onUse(Entity entity) {
		CameraComponent camera = CameraSystem.instance.get("main").getComponent(CameraComponent.class);
		Vector3 mouse = camera.camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));

		this.place((short) (mouse.x / SIZE), (short) (mouse.y / SIZE));

		return true;
	}

	/**
	 * Places self on given cords
	 *
	 * @param x In World X
	 * @param y In World Y
	 */
	protected void place(short x, short y) {

	}

	/**
	 * Returns amount of tileable neighbors
	 *
	 * @param x Tile X
	 * @param y Tile Y
	 * @return Packed binary
	 */
	public int getNeighbors(short x, short y) {
		String id = this.getComponent(IdComponent.class).id;

		boolean top = this.shouldTile(this.getNeighbor(x, (short) (y + 1)), id);
		boolean right = this.shouldTile(this.getNeighbor((short) (x + 1), y), id);
		boolean bottom = this.shouldTile(this.getNeighbor(x, (short) (y - 1)), id);
		boolean left = this.shouldTile(this.getNeighbor((short) (x - 1), y), id);

		return BinaryPacker.pack(top, right, bottom, left);
	}

	/**
	 * Returns a neighbor at that pos
	 *
	 * @param x Neighbor X
	 * @param y Neighbor Y
	 * @return It's ID
	 */
	protected String getNeighbor(short x, short y) {
		return null;
	}

	/**
	 * Returns wall pattern
	 *
	 * @param data Info about the wall
	 * @return The pattern
	 */
	public int getVariant(int data) {
		return this.getHelper().getVariant(data);
	}

	/**
	 * Returns tile pattern
	 *
	 * @param data Info about the tile
	 * @return The health
	 */
	public int getHealth(int data) {
		return this.getHelper().getBlockHealth(data);
	}

	/**
	 * Returns, if tile should tile to given tile
	 *
	 * @param neighbor Wall
	 * @param self     Self
	 * @return Should tile
	 */
	protected boolean shouldTile(String neighbor, String self) {
		return Objects.equals(neighbor, self);
	}
}