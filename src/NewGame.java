package src;
// PacMan.java
// Simple PacMan implementation


import ch.aplu.jgamegrid.Actor;
import ch.aplu.jgamegrid.GGBackground;
import ch.aplu.jgamegrid.GameGrid;
import ch.aplu.jgamegrid.Location;
import src.utility.GameCallback;
import src.utility.NewGameCallback;

import java.awt.*;
import java.util.ArrayList;
import java.util.Properties;

public class NewGame extends GameGrid
{
  private final static int nbHorzCells = 20;
  private final static int nbVertCells = 11;
  protected NewPacManGameGrid grid = null;

  protected NewPacActor pacActor = new NewPacActor(this);
  private NewMonster troll = new NewMonster(this, MonsterType.Troll);
  private NewMonster tx5 = new NewMonster(this, MonsterType.TX5);


  private ArrayList<Location> pillAndItemLocations = new ArrayList<Location>();
  private ArrayList<Actor> iceCubes = new ArrayList<Actor>();
  private ArrayList<Actor> goldPieces = new ArrayList<Actor>();
  private ArrayList<Actor> whitePortal = new ArrayList<Actor>();
  private ArrayList<Actor> yellowPortal = new ArrayList<Actor>();
  private ArrayList<Actor> darkPortal = new ArrayList<Actor>();
  private ArrayList<Actor> grayPortal = new ArrayList<Actor>();
  private NewGameCallback gameCallback;
  private Properties properties;
  private int seed = 30006;
  private ArrayList<Location> propertyPillLocations = new ArrayList<>();
  private ArrayList<Location> propertyGoldLocations = new ArrayList<>();

  public NewGame(NewGameCallback gameCallback, Properties properties, int mapNum)
  {
    //Setup game
    super(nbHorzCells, nbVertCells, 20, false);
    grid = new NewPacManGameGrid(nbHorzCells, nbVertCells, mapNum);
    this.gameCallback = gameCallback;
    this.properties = properties;
    setSimulationPeriod(100);
    setTitle("[PacMan in the Multiverse]");

    //Setup for auto test
    pacActor.setPropertyMoves(properties.getProperty("PacMan.move"));
    pacActor.setAuto(Boolean.parseBoolean(properties.getProperty("PacMan.isAuto")));
//    loadPillAndItemsLocations();

    GGBackground bg = getBg();
    drawGrid(bg);

    //Setup Random seeds
    seed = Integer.parseInt(properties.getProperty("seed"));
    pacActor.setSeed(seed);
    troll.setSeed(seed);
    tx5.setSeed(seed);
    addKeyRepeatListener(pacActor);
    setKeyRepeatPeriod(150);
    troll.setSlowDown(3);
    tx5.setSlowDown(3);
    pacActor.setSlowDown(3);
    tx5.stopMoving(5);
    setupActorLocations();



    //Run the game
    doRun();
    show();
    // Loop to look for collision in the application thread
    // This makes it improbable that we miss a hit
    boolean hasPacmanBeenHit;
    boolean hasPacmanEatAllPills;
    setupPillAndItemsLocations();
    int maxPillsAndItems = countPillsAndItems();

    do {
      hasPacmanBeenHit = troll.getLocation().equals(pacActor.getLocation()) ||
              tx5.getLocation().equals(pacActor.getLocation());
      hasPacmanEatAllPills = pacActor.getNbPills() >= maxPillsAndItems;
      delay(10);
    } while(!hasPacmanBeenHit && !hasPacmanEatAllPills);
    delay(120);

    Location loc = pacActor.getLocation();
    troll.setStopMoving(true);
    tx5.setStopMoving(true);
    pacActor.removeSelf();

    String title = "";
    if (hasPacmanBeenHit) {
      bg.setPaintColor(Color.red);
      title = "GAME OVER";
      addActor(new Actor("sprites/explosion3.gif"), loc);
    } else if (hasPacmanEatAllPills) {
      bg.setPaintColor(Color.yellow);
      title = "YOU WIN";
      if (mapNum!=2) {
        new NewGame(gameCallback, properties, 2);
      }
    }
    setTitle(title);
    gameCallback.endOfGame(title);

    doPause();
  }

  public NewGameCallback getGameCallback() {
    return gameCallback;
  }

  private void setupActorLocations() {
    String[] trollLocations = this.properties.getProperty("Troll.location").split(",");
    String[] tx5Locations = this.properties.getProperty("TX5.location").split(",");
    String[] pacManLocations = this.properties.getProperty("PacMan.location").split(",");
    int trollX = Integer.parseInt(trollLocations[0]);
    int trollY = Integer.parseInt(trollLocations[1]);

    int tx5X = Integer.parseInt(tx5Locations[0]);
    int tx5Y = Integer.parseInt(tx5Locations[1]);

    int pacManX = Integer.parseInt(pacManLocations[0]);
    int pacManY = Integer.parseInt(pacManLocations[1]);

    addActor(troll, new Location(trollX, trollY), Location.NORTH);
    addActor(pacActor, new Location(pacManX, pacManY));
    addActor(tx5, new Location(tx5X, tx5Y), Location.NORTH);
  }

  private int countPillsAndItems() {
    int pillsAndItemsCount = 0;
    for (int y = 0; y < nbVertCells; y++)
    {
      for (int x = 0; x < nbHorzCells; x++)
      {
        Location location = new Location(x, y);
        int a = grid.getCell(location);
        if (a == 1 && propertyPillLocations.size() == 0) { // Pill
          pillsAndItemsCount++;
        } else if (a == 3 && propertyGoldLocations.size() == 0) { // Gold
          pillsAndItemsCount++;
        }
      }
    }
    if (propertyPillLocations.size() != 0) {
      pillsAndItemsCount += propertyPillLocations.size();
    }

    if (propertyGoldLocations.size() != 0) {
      pillsAndItemsCount += propertyGoldLocations.size();
    }

    return pillsAndItemsCount;
  }

  public ArrayList<Location> getPillAndItemLocations() {
    return pillAndItemLocations;
  }


  private void loadPillAndItemsLocations() {
    String pillsLocationString = properties.getProperty("Pills.location");
    if (pillsLocationString != null) {
      String[] singlePillLocationStrings = pillsLocationString.split(";");
      for (String singlePillLocationString: singlePillLocationStrings) {
        String[] locationStrings = singlePillLocationString.split(",");
        propertyPillLocations.add(new Location(Integer.parseInt(locationStrings[0]), Integer.parseInt(locationStrings[1])));
      }
    }

    String goldLocationString = properties.getProperty("Gold.location");
    if (goldLocationString != null) {
      String[] singleGoldLocationStrings = goldLocationString.split(";");
      for (String singleGoldLocationString: singleGoldLocationStrings) {
        String[] locationStrings = singleGoldLocationString.split(",");
        propertyGoldLocations.add(new Location(Integer.parseInt(locationStrings[0]), Integer.parseInt(locationStrings[1])));
      }
    }
  }
  private void setupPillAndItemsLocations() {
    for (int y = 0; y < nbVertCells; y++)
    {
      for (int x = 0; x < nbHorzCells; x++)
      {
        Location location = new Location(x, y);
        int a = grid.getCell(location);
        if (a == 1 && propertyPillLocations.size() == 0) {
          pillAndItemLocations.add(location);
        }
        if (a == 3 &&  propertyGoldLocations.size() == 0) {
          pillAndItemLocations.add(location);
        }
        if (a == 4) {
          pillAndItemLocations.add(location);
        }
      }
    }


    if (propertyPillLocations.size() > 0) {
      for (Location location : propertyPillLocations) {
        pillAndItemLocations.add(location);
      }
    }
    if (propertyGoldLocations.size() > 0) {
      for (Location location : propertyGoldLocations) {
        pillAndItemLocations.add(location);
      }
    }
  }

  private void drawGrid(GGBackground bg)
  {
    bg.clear(Color.gray);
    bg.setPaintColor(Color.white);
    for (int y = 0; y < nbVertCells; y++)
    {
      for (int x = 0; x < nbHorzCells; x++)
      {
        bg.setPaintColor(Color.white);
        Location location = new Location(x, y);
        int a = grid.getCell(location);
        if (a > 0)
          bg.fillCell(location, Color.lightGray);
        if (a == 1 && propertyPillLocations.size() == 0) { // Pill
          putPill(bg, location);
        } else if (a == 3 && propertyGoldLocations.size() == 0) { // Gold
          putGold(bg, location);
        } else if (a == 4) {
          putIce(bg, location);
        } else if (a == 5) {
          putWhiteTile(bg, location);
        }
        else if (a == 6) {
          putYellowTile(bg, location);
        }
        else if (a == 7) {
          putDarkTile(bg, location);
        }
        else if (a == 8) {
          putGrayTile(bg, location);
        }
      }
    }

    for (Location location : propertyPillLocations) {
      putPill(bg, location);
    }

    for (Location location : propertyGoldLocations) {
      putGold(bg, location);
    }
  }

  private void putPill(GGBackground bg, Location location){
    bg.fillCircle(toPoint(location), 5);
  }

  private void putGold(GGBackground bg, Location location){
    bg.setPaintColor(Color.yellow);
    bg.fillCircle(toPoint(location), 5);
    Actor gold = new Actor("sprites/gold.png");
    this.goldPieces.add(gold);
    addActor(gold, location);
  }

  private void putIce(GGBackground bg, Location location){
    bg.setPaintColor(Color.blue);
    bg.fillCircle(toPoint(location), 5);
    Actor ice = new Actor("sprites/ice.png");
    this.iceCubes.add(ice);
    addActor(ice, location);
  }

  private void putWhiteTile(GGBackground bg, Location location){
    bg.setPaintColor(Color.white);
    bg.fillCircle(toPoint(location), 5);
    Actor whitePortal = new Actor("sprites/i_portalwhitetile.png");
    this.whitePortal.add(whitePortal);
    addActor(whitePortal, location);
  }

  private void putYellowTile(GGBackground bg, Location location){
    bg.setPaintColor(Color.white);
    bg.fillCircle(toPoint(location), 5);
    Actor yellowPortals = new Actor("sprites/j_portalyellowtile.png");
    this.yellowPortal.add(yellowPortals);
    addActor(yellowPortals, location);
  }

  private void putDarkTile(GGBackground bg, Location location){
    bg.setPaintColor(Color.white);
    bg.fillCircle(toPoint(location), 5);
    Actor darkPortals = new Actor("sprites/k_portaldarkgoldtile.png");
    this.darkPortal.add(darkPortals);
    addActor(darkPortals, location);
  }

  private void putGrayTile(GGBackground bg, Location location){
    bg.setPaintColor(Color.white);
    bg.fillCircle(toPoint(location), 5);
    Actor grayPortals = new Actor("sprites/l_portaldarkgraytile.png");
    this.grayPortal.add(grayPortals);
    addActor(grayPortals, location);
  }

  public void removeItem(String type,Location location){
    if(type.equals("gold")){
      for (Actor item : this.goldPieces){
        if (location.getX() == item.getLocation().getX() && location.getY() == item.getLocation().getY()) {
          item.hide();
        }
      }
    }else if(type.equals("ice")){
      for (Actor item : this.iceCubes){
        if (location.getX() == item.getLocation().getX() && location.getY() == item.getLocation().getY()) {
          item.hide();
        }
      }
    }
  }

  public Location jumpItem(Location location){
    boolean isPortal = false;
    for (Actor item : this.whitePortal){
        if (location.getX() == item.getLocation().getX() && location.getY() == item.getLocation().getY()) {
          isPortal = true;
        }
    }
    if (isPortal) {
      for (Actor item : this.whitePortal) {
        if (location.getX() != item.getLocation().getX() || location.getY() != item.getLocation().getY()) {
          location.x = item.getLocation().getX();
          location.y = item.getLocation().getY();
          break;
        }
      }
        return location;
      }

      for (Actor item : this.grayPortal){
        if (location.getX() == item.getLocation().getX() && location.getY() == item.getLocation().getY()) {
          isPortal = true;
        }
      }
      if (isPortal) {
        for (Actor item : this.grayPortal) {
          if (location.getX() != item.getLocation().getX() || location.getY() != item.getLocation().getY()) {
            location.x = item.getLocation().getX();
            location.y = item.getLocation().getY();
            break;
          }
        }
        return location;
      }

        for (Actor item : this.yellowPortal){
          if (location.getX() == item.getLocation().getX() && location.getY() == item.getLocation().getY()) {
            isPortal = true;
          }
        }
        if (isPortal) {
          for (Actor item : this.yellowPortal) {
            if (location.getX() != item.getLocation().getX() || location.getY() != item.getLocation().getY()) {
              location.x = item.getLocation().getX();
              location.y = item.getLocation().getY();
              break;
            }
          }
          return location;
        }

          for (Actor item : this.darkPortal){
            if (location.getX() == item.getLocation().getX() && location.getY() == item.getLocation().getY()) {
              isPortal = true;
            }
          }
          if (isPortal) {
            for (Actor item : this.darkPortal) {
              if (location.getX() != item.getLocation().getX() || location.getY() != item.getLocation().getY()) {
                location.x = item.getLocation().getX();
                location.y = item.getLocation().getY();
                break;
              }

            }
            return location;
          }


    return location;

  }

  public int getNumHorzCells(){
    return this.nbHorzCells;
  }
  public int getNumVertCells(){
    return this.nbVertCells;
  }
}
