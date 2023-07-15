package src;
// PacGrid.java

import ch.aplu.jgamegrid.Location;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import java.io.File;
import java.util.List;


public class NewPacManGameGrid {
    private int nbHorzCells;
    private int nbVertCells;
    private int[][] mazeArray;
    public static int[][] qmazeArray;

    public NewPacManGameGrid(int nbHorzCells, int nbVertCells, int mapNum) {
        this.nbHorzCells = nbHorzCells;
        this.nbVertCells = nbVertCells;
        mazeArray = new int[nbVertCells][nbHorzCells];
        qmazeArray = new int[nbVertCells][nbHorzCells];
        String maze =
                "xxxxxxxxxxxxxxxxxxxx" + // 0
                        "x....x....g...x....x" + // 1
                        "xgxx.x.xxxxxx.x.xx.x" + // 2
                        "x.x.......i.g....x.x" + // 3
                        "x.x.xx.xx  xx.xx.x.x" + // 4
                        "x......x    x......x" + // 5
                        "x.x.xx.xxxxxx.xx.x.x" + // 6
                        "x.x......gi......x.x" + // 7
                        "xixx.x.xxxxxx.x.xx.x" + // 8
                        "x...gx....g...x....x" + // 9
                        "xxxxxxxxxxxxxxxxxxxx";// 10

        Element level = new Element("level");
        Document document;
        File selectedFile = new File(System.getProperty("user.dir") + "/game/map"+ mapNum+".xml");
        SAXBuilder builder = new SAXBuilder();
        try {
            if (selectedFile.canRead() && selectedFile.exists()) {
                document = (Document) builder.build(selectedFile);

                Element rootNode = document.getRootElement();

                List sizeList = rootNode.getChildren("size");
                Element sizeElem = (Element) sizeList.get(0);
                int height = Integer.parseInt(sizeElem
                        .getChildText("height"));
                int width = Integer
                        .parseInt(sizeElem.getChildText("width"));

                boolean hasPill = false;
                List rows = rootNode.getChildren("row");
                for (int y = 0; y < rows.size(); y++) {
                    Element cellsElem = (Element) rows.get(y);
                    List cells = cellsElem.getChildren("cell");

                    for (int x = 0; x < cells.size(); x++) {
                        Element cell = (Element) cells.get(x);
                        String cellValue = cell.getText();

                        char tileNr = ' ';
                        if (cellValue.equals("PathTile"))
                            tileNr = ' ';
                        else if (cellValue.equals("WallTile"))
                            tileNr = 'x';
                        else if (cellValue.equals("PillTile"))
                        { tileNr = '.';hasPill=true;}
                        else if (cellValue.equals("GoldTile"))
                            tileNr = 'g';
                        else if (cellValue.equals("IceTile"))
                            tileNr = 'i';//e
                        else if (cellValue.equals("PacTile"))
                            tileNr = ' ';
                        else if (cellValue.equals("TrollTile"))
                            tileNr = ' ';//g
                        else if (cellValue.equals("TX5Tile"))
                            tileNr = ' ';
                        else if (cellValue.equals("PortalWhiteTile"))
                            tileNr = 'w';//i
                        else if (cellValue.equals("PortalYellowTile"))
                            tileNr = 'y';
                        else if (cellValue.equals("PortalDarkGoldTile"))
                            tileNr = 'd';
                        else if (cellValue.equals("PortalDarkGrayTile"))
                            tileNr = 'r';
                        else
                            tileNr = ' ';
                        int value = toInt(tileNr);
                        mazeArray[y][x] = value;
                        if (value<=0) {
                            qmazeArray[y][x] = 1;
                        } else {
                            qmazeArray[y][x] = 0;
                        }


                    }


                }

                //check level here
                if (!hasPill) {
                    System.out.println("no pill in game");
                }
            } else {
                //check document level

                String errInfo = "[Level" + mapNum + "map" + mapNum + ".xml - bad document";
                throw new RuntimeException(" map file not exsit");
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }



    public int getCell(Location location) {
        return mazeArray[location.y][location.x];
    }

    public int[][] getMaze() {
        return qmazeArray;
    }

    private int toInt(char c) {
        if (c == 'x')
            return 0;
        if (c == '.')
            return 1;
        if (c == ' ')
            return 2;
        if (c == 'g')
            return 3;
        if (c == 'i')
            return 4;
        if (c == 'w')
            return 5;
        if (c == 'y')
            return 6;
        if (c == 'd')
            return 7;
        if (c == 'r')
            return 8;
        return -1;
    }
}
