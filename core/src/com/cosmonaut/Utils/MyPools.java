package com.cosmonaut.Utils;

import com.badlogic.gdx.utils.Pool;
import com.cosmonaut.MyGdxGame;
import com.cosmonaut.Bodies.Exit;
import com.cosmonaut.Bodies.ItemSwitch;
import com.cosmonaut.Bodies.Leak;
import com.cosmonaut.Bodies.ObstacleDoor;
import com.cosmonaut.Bodies.ObstacleLight;
import com.cosmonaut.Bodies.ObstacleLightning;
import com.cosmonaut.Bodies.ObstacleMoving;
import com.cosmonaut.Bodies.ObstaclePiston;
import com.cosmonaut.Bodies.ObstacleRevolving;
import com.cosmonaut.Bodies.Polygone;
import com.cosmonaut.Bodies.Wall;
import com.cosmonaut.Items.FuelRefill;
import com.cosmonaut.Items.Gyrophare;
import com.cosmonaut.Items.OxygenRefill;
import com.cosmonaut.Items.Upgrade;

public class MyPools {
	
	final MyGdxGame game;

	
	public MyPools(final MyGdxGame game){
		this.game = game;
	}
	
	/*
	 * Walls
	 */
	public Pool<Wall> wallPool = new Pool<Wall>(0, 200){
		@Override
		protected Wall newObject() {
			return new Wall(game);
		}	
	};
	
	public Wall obtainWall(){
		return this.wallPool.obtain();
	}
	
	public void free(Wall wall){
		this.wallPool.free(wall);
	}
	
	/*
	 * Doors
	 */	
	public Pool<ObstacleDoor> doorPool = new Pool<ObstacleDoor>(){
		@Override
		protected ObstacleDoor newObject() {
			return new ObstacleDoor(game);
		}	
	};
	
	public ObstacleDoor obtainDoor(){
		return this.doorPool.obtain();
	}
	
	public void free(ObstacleDoor door){
		this.doorPool.free(door);
	}
	
	/*
	 * Light obstacles
	 */
	public Pool<ObstacleLight> lightObstaclePool = new Pool<ObstacleLight>(){
		@Override
		protected ObstacleLight newObject() {
			return new ObstacleLight(game);
		}	
	};
	
	public ObstacleLight obtainObstacleLight(){
		return this.lightObstaclePool.obtain();
	}
	
	public void free(ObstacleLight obstacle){
		this.lightObstaclePool.free(obstacle);
	}
	
	/*
	 * Revolving obstacles
	 */
	public Pool<ObstacleRevolving> revolvingObstaclePool = new Pool<ObstacleRevolving>(){
		@Override
		protected ObstacleRevolving newObject() {
			return new ObstacleRevolving(game);
		}	
	};
	
	public ObstacleRevolving obtainObstacleRevolving(){
		return this.revolvingObstaclePool.obtain();
	}
	
	public void free(ObstacleRevolving obstacle){
		this.revolvingObstaclePool.free(obstacle);
	}
	
	/*
	 * Polygones
	 */
	public Pool<Polygone> polygonePool = new Pool<Polygone>(){
		@Override
		protected Polygone newObject() {
			return new Polygone(game);
		}	
	};
	
	public Polygone obtainPolygone(){
		return this.polygonePool.obtain();
	}
	
	public void free(Polygone obstacle){
		this.polygonePool.free(obstacle);
	}

	/*
	 * Leaks
	 */
	public Pool<Leak> leakPool = new Pool<Leak>(){
		@Override
		protected Leak newObject() {
			return new Leak(game);
		}	
	};
	
	public Leak obtainLeak(){
		return this.leakPool.obtain();
	}
	
	public void free(Leak leak){
		this.leakPool.free(leak);
	}
	
	/*
	 * Moving obstacles
	 */
	public Pool<ObstacleMoving> movingObstaclePool = new Pool<ObstacleMoving>(){
		@Override
		protected ObstacleMoving newObject() {
			return new ObstacleMoving(game);
		}	
	};
	
	public ObstacleMoving obtainObstacleMoving(){
		return this.movingObstaclePool.obtain();
	}
	
	public void free(ObstacleMoving moving){
		this.movingObstaclePool.free(moving);
	}
	
	/*
	 * Moving obstacles
	 */
	public Pool<Exit> exitPool = new Pool<Exit>(){
		@Override
		protected Exit newObject() {
			return new Exit(game);
		}	
	};
	
	public Exit obtainExit(){
		return this.exitPool.obtain();
	}
	
	public void free(Exit exit){
		this.exitPool.free(exit);
	}
	
	/*
	 * Switchs
	 */
	public Pool<ItemSwitch> switchPool = new Pool<ItemSwitch>(){
		@Override
		protected ItemSwitch newObject() {
			return new ItemSwitch(game);
		}	
	};
	
	public ItemSwitch obtainSwitch(){
		return this.switchPool.obtain();
	}
	
	public void free(ItemSwitch itemSwitch){
		this.switchPool.free(itemSwitch);
	}
	
	/*
	 * Lightnings
	 */
	public Pool<ObstacleLightning> lightningPool = new Pool<ObstacleLightning>(){
		@Override
		protected ObstacleLightning newObject() {
			return new ObstacleLightning(game);
		}	
	};
	
	public ObstacleLightning obtainLightning(){
		return this.lightningPool.obtain();
	}
	
	public void free(ObstacleLightning lightning){
		this.lightningPool.free(lightning);
	}
	
	/*
	 * FuelRefill
	 */
	public Pool<FuelRefill> fuelPool = new Pool<FuelRefill>(){
		@Override
		protected FuelRefill newObject() {
			return new FuelRefill(game);
		}	
	};
	
	public FuelRefill obtainFuelRefill(){
		return this.fuelPool.obtain();
	}
	
	public void free(FuelRefill lightning){
		this.fuelPool.free(lightning);
	}
	
	/*
	 * OxygenRefill
	 */
	public Pool<OxygenRefill> oxygenPool = new Pool<OxygenRefill>(){
		@Override
		protected OxygenRefill newObject() {
			return new OxygenRefill(game);
		}	
	};
	
	public OxygenRefill obtainOxygenRefill(){
		return this.oxygenPool.obtain();
	}
	
	public void free(OxygenRefill oxygen){
		this.oxygenPool.free(oxygen);
	}
	
	/*
	 * Upgrade
	 */
	public Pool<Upgrade> upgradePool = new Pool<Upgrade>(){
		@Override
		protected Upgrade newObject() {
			return new Upgrade(game);
		}	
	};
	
	public Upgrade obtainUpgrade(){
		return this.upgradePool.obtain();
	}
	
	public void free(Upgrade upgrade){
		this.upgradePool.free(upgrade);
	}
	
	/*
	 * Gyrophares
	 */
	public Pool<Gyrophare> gyropharePool = new Pool<Gyrophare>(){
		@Override
		protected Gyrophare newObject() {
			return new Gyrophare(game);
		}	
	};
	
	public Gyrophare obtainGyrophare(){
		return this.gyropharePool.obtain();
	}
	
	public void free(Gyrophare gyrophare){
		this.gyropharePool.free(gyrophare);
	}
	
	/*
	 * Piston
	 */
	public Pool<ObstaclePiston> pistonPool = new Pool<ObstaclePiston>(){
		@Override
		protected ObstaclePiston newObject() {
			return new ObstaclePiston(game);
		}	
	};
	
	public ObstaclePiston obtainPiston(){
		return this.pistonPool.obtain();
	}
	
	public void free(ObstaclePiston piston){
		this.pistonPool.free(piston);
	}
	
	public void write(){
		System.out.println("*****************************Pool Status*****************************");
		System.out.println(	"Walls : " + wallPool.getFree() + "|" + wallPool.peak + "|" + wallPool.max + "\n" + 
							"Doors : " + doorPool.getFree() + "|" + doorPool.peak + "\n" + 
							"Light Obstacles : " + lightObstaclePool.getFree() + "|" + lightObstaclePool.peak + "\n" + 
							"Revolving Obstacles : " + revolvingObstaclePool.getFree() + "|" + revolvingObstaclePool.peak + "\n" + 
							"Polygones : " + polygonePool.getFree() + "|" + polygonePool.peak + "\n" + 
							"Leaks : " + leakPool.getFree() + "|" + leakPool.peak + "\n" + 
							"Moving Obstacles : " + movingObstaclePool.getFree() + "|" + movingObstaclePool.peak + "\n" + 
							"Exit : " + exitPool.getFree() + "|" + exitPool.peak + "\n" + 
							"Switch : " + switchPool.getFree() + "|" + switchPool.peak + "\n" +
							"Lightning : " + lightningPool.getFree() + "|" + lightningPool.peak + "\n" +
							"Fuel Refill : " + fuelPool.getFree() + "|" + fuelPool.peak + "\n" + 
							"Oxygen Refill : " + oxygenPool.getFree() + "|" + oxygenPool.peak + "\n" + 
							"Upgrade : " + upgradePool.getFree() + "|" + upgradePool.peak + "\n" + 
							"Gyrophare : " + gyropharePool.getFree() + "|" + gyropharePool.peak + "\n" + 
							"Piston : " + pistonPool.getFree() + "|" + pistonPool.peak);
		System.out.println("*********************************************************************");
	}
}
