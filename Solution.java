
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import javafx.util.Pair;

/**
 *
 * @author WslF@ITeazer
 */
public class Solution {

    // maximum size (height & width) of maze
    final static int MaxN = 200;
// impossibly high distance
    final static int infDist = 1000 * 1000 * 1000;
// number of lines in current maze
    int rowsNumber = 0;
// number of rows in current maze
    int columnsNum = 0;

// current maze. Do NOT modify it!!!
//  0    - road
// -1    - droid start position
// -2    - finish
// -3    - walls (non road)
    int[][] mazeInput = new int[MaxN][MaxN];

    int[][] distanceToWalls = new int[MaxN][MaxN];

    final static int ROAD = 0;
    final static int START = -1;
    final static int FINISH = -2;
    final static int WALL = -3;

// copy of mazeInput for editing
    int[][] maze = new int[MaxN][MaxN];
// height of Ollie in cm
    final static int droidH = 8;
// width of Ollie in cm
    final static int droidW = 12;

// read map from file to mazeInput & maze arrays
    private void read(String inputName) {
        List<String> lines = new ArrayList<>();

        try {
            Path filePath = Paths.get(inputName);
            if (Files.exists(filePath)) {
                lines = Files.readAllLines(filePath);
            }
        } catch (Exception ex) {
            System.err.println("Couldn't read map");
            return;
        }

        rowsNumber = lines.size();
        columnsNum = lines.get(0).length();

        for (int i = 0; i < MaxN; i++) {
            for (int j = 0; j < MaxN; j++) {
                maze[i][j] = WALL;
                mazeInput[i][j] = WALL;
            }
        }

        for (int i = 0; i < rowsNumber; i++) {
            for (int j = 0; j < lines.get(i).length(); j++) {
                if (lines.get(i).charAt(j) == ' ') {
                    maze[i + 1][j + 1] = ROAD; // 0
                } else if (lines.get(i).charAt(j) == 'd') {
                    maze[i + 1][j + 1] = START; // -1
                } else if (lines.get(i).charAt(j) == 'f') {
                    maze[i + 1][j + 1] = FINISH; // -2
                } else if (lines.get(i).charAt(j) == '*') {
                    maze[i + 1][j + 1] = WALL; // -3
                }
                mazeInput[i + 1][j + 1] = maze[i + 1][j + 1];
            }
        }

        System.out.println("read successfully!");
    }

    private void getCopyOfMaze(int[][] mazeCopy) {
        for (int i = 0; i < MaxN; i++) {
            for (int j = 0; j < MaxN; j++) {
                mazeCopy[i][j] = mazeInput[i][j];
            }
        }
    }

// find first North West occurence of number num in the maze
    int[] findFirstOccurrence(int num) {
        for (int i = 1; i <= rowsNumber; i++) {
            for (int j = 1; j <= columnsNum; j++) {
                if (maze[i][j] == num) {
                    return new int[]{i, j};
                }
            }
        }

        return new int[]{0, 0};
    }

// find center of square that encoded by number num
    int[] findCenter(int num) {
        int[] start = findFirstOccurrence(num);
        if (start[0] == 0) {
            return start;
        }

        int i = start[0];
        int j = start[1];
        int w = 1;
        while (w <= columnsNum && maze[i][j + w] == num) {
            w++;
        }
        int h = 1;
        while (h <= rowsNumber && maze[i + h][j] == num) {
            h++;
        }
        return new int[]{i + h / 2, j + w / 2};
    }

    void calcDistanceToWalls() {
        getCopyOfMaze(distanceToWalls);

        Queue<int[]> q = new LinkedList<int[]>();

        for (int i = 0; i < MaxN; i++) {
            for (int j = 0; j < MaxN; j++) {
                if (distanceToWalls[i][j] == WALL) {
                    distanceToWalls[i][j] = 0;
                    q.add(new int[]{i, j});
                } else {
                    distanceToWalls[i][j] = infDist;
                }
            }
        }

        while (!q.isEmpty()) {
            int[] cur = q.poll();
            int curRow = cur[0];
            int curColumn = cur[1];
            int nextVal = distanceToWalls[curRow][curColumn] + 1;

            if (curRow > 0) {
                if (distanceToWalls[curRow - 1][curColumn] > nextVal) {
                    distanceToWalls[curRow - 1][curColumn] = nextVal;
                    q.add(new int[]{curRow - 1, curColumn});
                }
            }
            if (curRow < MaxN - 1) {
                if (distanceToWalls[curRow + 1][curColumn] > nextVal) {
                    distanceToWalls[curRow + 1][curColumn] = nextVal;
                    q.add(new int[]{curRow + 1, curColumn});
                }
            }
            if (curColumn > 0) {
                if (distanceToWalls[curRow][curColumn - 1] > nextVal) {
                    distanceToWalls[curRow][curColumn - 1] = nextVal;
                    q.add(new int[]{curRow, curColumn - 1});
                }
            }
            if (curColumn < MaxN - 1) {
                if (distanceToWalls[curRow][curColumn + 1] > nextVal) {
                    distanceToWalls[curRow][curColumn + 1] = nextVal;
                    q.add(new int[]{curRow, curColumn + 1});
                }
            }
        }

    }

// set wall value for cell in maze if distance from it to real wall <= num
    private void extendWalls(int num) {
        calcDistanceToWalls();

        for (int i = 0; i < MaxN; i++) {
            for (int j = 0; j < MaxN; j++) {
                if (distanceToWalls[i][j] <= num) {
                    maze[i][j] = WALL;
                }
            }
        }

        System.out.println("extended walls by " + num + " cells");
    }

    private ArrayList<Pair<Character, Integer>> uniteAns(ArrayList<Pair<Character, Integer>> tAns) {
        ArrayList<Pair<Character, Integer>> ans = new ArrayList<>();
        tAns.add(new Pair('#', 0));
        for (int i = 1; i < tAns.size(); i++) {
            if (tAns.get(i).getKey().equals(tAns.get(i - 1).getKey())) {
                tAns.set(i, new Pair(tAns.get(i).getKey(), tAns.get(i-1).getValue() + 1));
            } else {
                ans.add(tAns.get(i - 1));
            }
        }

        return ans;
    }

    void calcDist(int startRow, int startColumn) {
        for (int i = 1; i <= rowsNumber; i++) {
            for (int j = 1; j <= columnsNum; j++) {
                if (maze[i][j] != WALL) {
                    maze[i][j] = infDist;
                }
            }
        }

        maze[startRow][startColumn] = 0;

        Queue<int[]> q = new LinkedList<>();
        q.add(new int[]{startRow, startColumn});

        while (!q.isEmpty()) {
            int curRow = q.peek()[0];
            int curColumn = q.peek()[1];
            q.poll();

            if (maze[curRow][curColumn] == WALL) {
                continue; // shouldn't happens:)
            }
            int nextVal = maze[curRow][curColumn] + 1;

            if (maze[curRow - 1][curColumn] > nextVal) {
                maze[curRow - 1][curColumn] = nextVal;
                q.add(new int[]{curRow - 1, curColumn});
            }
            if (maze[curRow + 1][curColumn] > nextVal) {
                maze[curRow + 1][curColumn] = nextVal;
                q.add(new int[]{curRow + 1, curColumn});
            }
            if (maze[curRow][curColumn - 1] > nextVal) {
                maze[curRow][curColumn - 1] = nextVal;
                q.add(new int[]{curRow, curColumn - 1});
            }
            if (maze[curRow][curColumn + 1] > nextVal) {
                maze[curRow][curColumn + 1] = nextVal;
                q.add(new int[]{curRow, curColumn + 1});
            }
        }

        System.out.println("distance from " + startRow + " " + startColumn + " to all (not wall) cells calculated");
    }

    /* returns vector of pairs <dirrction, length (in cm)>
 directions : {N, E, S, W}

      ^
      N
 <- W   E ->
      S
      v
     */
    private ArrayList<Pair<Character, Integer>> findWay() {
        int[] p = findCenter(START);
        int droidX = p[0];
        int droidY = p[1];
        //cout<<"droid: "<<droidX<<" "<<droidY<<endl;

        p = findCenter(FINISH);
        int finishX = p[0];
        int finishY = p[1];
        //cout<<"finish: "<<finishX<<" "<<finishY<<endl;

        extendWalls(5); // check size of smallest enterence, int should be at least twice greater than tah value
        calcDist(droidX, droidY);

        ArrayList<Pair<Character, Integer>> way = new ArrayList<>();

        int curX = finishX;
        int curY = finishY;
        while (curX != droidX || curY != droidY) {
            int nextVal = maze[curX][curY] - 1;
            if (maze[curX - 1][curY] == nextVal) {
                curX--;
                way.add(new Pair('S', 1));
                continue;
            } else if (maze[curX + 1][curY] == nextVal) {
                curX++;
                way.add(new Pair('N', 1));
                continue;
            } else if (maze[curX][curY - 1] == nextVal) {
                curY--;
                way.add(new Pair('E', 1));
                continue;
            } else if (maze[curX][curY + 1] == nextVal) {
                curY++;
                way.add(new Pair('W', 1));
                continue;
            }

        }

        Collections.reverse(way);
        return uniteAns(way);
    }

    int solve() {

        read("round1.map");
        ArrayList<Pair<Character, Integer>> way = findWay();
        System.out.println("way:");
        for (int i = 0; i < way.size(); i++) {
            System.out.println(way.get(i).getKey() + " " + way.get(i).getValue());
        }

        return 0;
    }

    public static void main(String[] args) {
        Solution solution = new Solution();
        solution.solve();
    }
}
