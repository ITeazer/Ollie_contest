// Wsl_F@ITeazer

#include <iostream>
#include <fstream>
#include <sstream>
#include <cmath>
#include <math.h>
#include <iomanip>
#include <algorithm>
#include <functional>
#include <cstring>
#include <cstdio>
#include <cstdlib>
#include <stdlib.h>
#include <stdio.h>
#include <string>
#include <string.h>
#include <vector>
#include <map>
#include <set>
#include <stack>
#include <list>
#include <queue>
#include <deque>
#include <bitset>
#include <numeric>
#include <cassert>
#include <time.h>
#include <ctime>
#include <memory.h>
#include <complex>
#include <utility>
#include <climits>
#include <cctype>


using namespace std;
#pragma comment(linker, "/STACK:1024000000,1024000000")


typedef long long LL;
typedef unsigned long long uLL;
typedef double dbl;
typedef vector<int> vi;
typedef vector<LL> vL;
typedef vector<string> vs;
typedef pair<int, int> pii;
typedef pair<LL, LL> pLL;

#define mp(x,y)  make_pair((x),(y))
#define pb(x)  push_back(x)
#define sqr(x) ((x)*(x))

// maximum size (height & width) of maze
const int MaxN = 200;
// impossibly high distance
const int infDist = 1000*1000*1000;
// number of lines in current maze
int rowsNumber = 0;
// number of rows in current maze
int columnsNum = 0;

// current maze. Do NOT modify it!!!
//  0    - road
// -1    - droid start position
// -2    - finish
// -3    - walls (non road)
int mazeInput[MaxN][MaxN];

int distanceToWalls[MaxN][MaxN];

const int ROAD = 0;
const int START = -1;
const int FINISH = -2;
const int WALL = -3;

// copy of mazeInput for editing
int maze[MaxN][MaxN];
// height of Ollie in cm
int droidH = 8;
// width of Ollie in cm
int droidW = 12;



// read map from file to mazeInput & maze arrays
void read(string inputName)
{
    ifstream in(inputName.c_str());
    string s;
    vector<string> lines;
    int maxL = 0;
    while (getline(in,s))
    {
        maxL = max(maxL, (int) s.length());
        lines.pb(s);
    }
    in.close();

    columnsNum = maxL;
    rowsNumber = lines.size();

    for (int i = 0; i < MaxN; i++)
        for (int j = 0; j < MaxN; j++)
        {
            maze[i][j] = WALL;
            mazeInput[i][j] = WALL;
        }

    for (int i = 0; i < rowsNumber; i++)
    {
        for (int j = 0; j < lines[i].size(); j++)
        {
            if (lines[i][j] == ' ') maze[i+1][j+1] = ROAD; // 0
            else if (lines[i][j] == 'd') maze[i+1][j+1] = START; // -1
            else if (lines[i][j] == 'f') maze[i+1][j+1] = FINISH; // -2
            else if (lines[i][j] == '*') maze[i+1][j+1] = WALL; // -3

            mazeInput[i+1][j+1] = maze[i+1][j+1];
        }
    }

    cout<<"read successfully!"<<endl;
}

void getCopyOfMaze(int mazeCopy[MaxN][MaxN])
{
    for (int i = 0; i < MaxN; i++)
        for (int j = 0; j < MaxN; j++)
            mazeCopy[i][j] = mazeInput[i][j];
}

// find first North West occurence of number num in the maze
pii findFirstOccurrence(int num)
{
    for (int i = 1; i <= rowsNumber; i++)
        for (int j = 1; j <= columnsNum; j++)
            if (maze[i][j] == num)
                return mp(i,j);

    return mp(0,0);
}

// find center of square that encoded by number num
pii findCenter(int num)
{
    pii start = findFirstOccurrence(num);
    if (start.first == 0)
        return start;

    int i = start.first;
    int j = start.second;
    int w= 1;
    while (w <= columnsNum && maze[i][j+w] == num) w++;
    int h= 1;
    while (h <= rowsNumber && maze[i+h][j] == num) h++;
    return mp(i+h/2,j+w/2);
}

void calcDistanceToWalls()
{
    getCopyOfMaze(distanceToWalls);

    queue<pii> q;

    for (int i = 0; i < MaxN; i++)
        for (int j = 0; j < MaxN; j++)
            if (distanceToWalls[i][j] == WALL)
            {
                distanceToWalls[i][j] = 0;
                q.push(mp(i,j));
            }
            else
            {
                distanceToWalls[i][j] = infDist;
            }

    while (!q.empty())
    {
        pii cur = q.front();
        q.pop();
        int curRow = cur.first;
        int curColumn = cur.second;
        int nextVal = distanceToWalls[curRow][curColumn] + 1;

        if (curRow>0)
            if (distanceToWalls[curRow-1][curColumn] > nextVal)
            {
                distanceToWalls[curRow-1][curColumn] = nextVal;
                q.push(mp(curRow-1,curColumn));
            }
        if (curRow<MaxN-1)
            if (distanceToWalls[curRow+1][curColumn] > nextVal)
            {
                distanceToWalls[curRow+1][curColumn] = nextVal;
                q.push(mp(curRow+1,curColumn));
            }
        if (curColumn>0)
            if (distanceToWalls[curRow][curColumn-1] > nextVal)
                {
                    distanceToWalls[curRow][curColumn-1] = nextVal;
                    q.push(mp(curRow,curColumn-1));
                }
        if (curColumn<MaxN-1)
            if (distanceToWalls[curRow][curColumn+1] > nextVal)
                {
                    distanceToWalls[curRow][curColumn+1] = nextVal;
                    q.push(mp(curRow,curColumn+1));
                }
    }

}

// set wall value for cell in maze if distance from it to real wall <= num
void extendWalls(int num)
{
    calcDistanceToWalls();

    for (int i = 0; i < MaxN; i++)
        for (int j = 0; j < MaxN; j++)
        {
            if (distanceToWalls[i][j] <= num)
                maze[i][j] = WALL;
        }

    cout<<"extended walls by " << num << " cells" << endl;
    return;
}

vector<pair<char, int> >  uniteAns(vector<pair<char, int> >  tAns)
{
    vector<pair<char, int> > ans;
    tAns.pb(mp('#',0));
    for (int i = 1; i < tAns.size(); i++)
    {
        if (tAns[i].first == tAns[i-1].first) tAns[i].second = tAns[i-1].second + 1;
        else ans.pb(tAns[i-1]);
    }

    return ans;
}

void calcDist(int startRow, int startColumn)
{
    for (int i = 1; i <= rowsNumber; i++)
        for (int j = 1; j <= columnsNum; j++)
            if (maze[i][j] != WALL)
                maze[i][j] = infDist;

    maze[startRow][startColumn] = 0;

    queue<pii> q;
    q.push(mp(startRow, startColumn));

    while (!q.empty())
    {
        int curRow = q.front().first;
        int curColumn = q.front().second;
        q.pop();

        if (maze[curRow][curColumn] == WALL) continue; // shouldn't happens:)
        int nextVal = maze[curRow][curColumn] + 1;

        if (maze[curRow-1][curColumn] > nextVal)
        {
            maze[curRow-1][curColumn] = nextVal;
            q.push(mp(curRow-1,curColumn));
        }
        if (maze[curRow+1][curColumn] > nextVal)
        {
            maze[curRow+1][curColumn] = nextVal;
            q.push(mp(curRow+1,curColumn));
        }
        if (maze[curRow][curColumn-1] > nextVal)
        {
            maze[curRow][curColumn-1] = nextVal;
            q.push(mp(curRow,curColumn-1));
        }
        if (maze[curRow][curColumn+1] > nextVal)
        {
            maze[curRow][curColumn+1] = nextVal;
            q.push(mp(curRow,curColumn+1));
        }
    }

    cout<<"distance from " << startRow<<" "<< startColumn << " to all (not wall) cells calculated"<<endl;
}

/* returns vector of pairs <dirrction, length (in cm)>
 directions : {N, E, S, W}

      ^
      N
 <- W   E ->
      S
      v
*/
vector<pair<char, int> > findWay()
{
    pii p = findCenter(START);
    int droidX = p.first;
    int droidY = p.second;
    cout<<"droid: "<<droidX<<" "<<droidY<<endl;

    p = findCenter(FINISH);
    int finishX = p.first;
    int finishY = p.second;
    cout<<"finish: "<<finishX<<" "<<finishY<<endl;

    extendWalls(5); // check size of smallest enterence, int should be at least twice greater than tah value
    calcDist(droidX, droidY);

    vector<pair<char, int> > way;

    int curX = finishX;
    int curY = finishY;
    while (curX != droidX || curY != droidY)
    {
        int nextVal = maze[curX][curY] - 1;
        if (maze[curX-1][curY] == nextVal)
        {
            curX--;
            way.pb(mp('S',1));
            continue;
        }
        else if (maze[curX+1][curY] == nextVal)
        {
            curX++;
            way.pb(mp('N',1));
            continue;
        }
        else if (maze[curX][curY-1] == nextVal)
        {
            curY--;
            way.pb(mp('E',1));
            continue;
        }
        else if (maze[curX][curY+1] == nextVal)
        {
            curY++;
            way.pb(mp('W',1));
            continue;
        }

    }

    reverse(way.begin(), way.end());
    return uniteAns(way);
}

int main()
{
    ios_base::sync_with_stdio(0);
    cin.tie(0);

    read("round1.map");
    vector<pair<char, int> > way = findWay();

    cout<<"way:"<<endl;
    for (int i = 0; i <  way.size(); i++)
        cout<<way[i].first<<" "<<way[i].second<<endl;


    return 0;
}
