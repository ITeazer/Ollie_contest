# Wsl_F@ITeazer
from Queue import Queue


class OllieInMaze:
    def __init__(self):
        # maximum size (height & width) of maze
        self.MaxN = 200

        #  impossibly high distance
        self.infDist = 1000 * 1000 * 1000

        # codes for cells in the maze
        self.ROAD = 0
        self.START = -1
        self.FINISH = -2
        self.WALL = -3

        #  height of Ollie in cm
        self.droidH = 8
        #  width of Ollie in cm
        self.droidW = 12
        #  number of lines in current maze
        self.rowsNumber = 0
        #  number of columns in current maze
        self.columnsNum = 0

        #  current maze. Do NOT modify it!!!
        #   0    - road
        #  -1    - droid start position
        #  -2    - finish
        #  -3    - walls (non road)
        self.mazeInput = [[0 for x in range(self.MaxN)] for y in range(self.MaxN)]

        self.distanceToWalls = [[0 for x in range(self.MaxN)] for y in range(self.MaxN)]

        #  copy of mazeInput for editing
        self.maze = [[0 for x in range(self.MaxN)] for y in range(self.MaxN)]

        for i in range(0, self.MaxN):
            for j in range(0, self.MaxN):
                self.maze[i][j] = self.WALL
                self.mazeInput[i][j] = self.WALL

    # read map from file to mazeInput & maze arrays
    def read(self, input_name):
        f = open(input_name)
        lines = f.readlines()

        self.rowsNumber = len(lines)
        self.columnsNum = len(lines[0])
        print "maze size: " + repr(self.rowsNumber) + "x" + repr(self.columnsNum)

        i = -1
        for line in lines:
            i += 1
            for j in range(0, self.columnsNum):
                if line[j] == ' ':
                    self.maze[i][j] = self.ROAD  # 0
                elif line[j] == 'd':
                    self.maze[i][j] = self.START  # -1
                elif line[j] == 'f':
                    self.maze[i][j] = self.FINISH  # -2
                else:
                    self.maze[i][j] = self.WALL  # -3

                self.mazeInput[i][j] = self.maze[i][j]
        print "read successfully!"

    # printing maze to console
    def print_maze(self):
        for i in range(0, self.rowsNumber):
            for j in range(0, self.columnsNum):
                print self.maze[i][j],
            print ""

    # makes copy of mazeInput to array
    def get_copy_of_maze(self, array):
        for i in range(0, self.MaxN):
            for j in range(0, self.MaxN):
                array[i][j] = self.mazeInput[i][j]

    # find first North West occurrence of number num in the maze
    def find_first_occurrence(self, num):
        for i in range(0, self.rowsNumber):
            for j in range(0, self.columnsNum):
                if self.maze[i][j] == num:
                    return (i, j)
        return (0, 0)

    # find center of square that encoded by number num
    def find_center(self, num):
        start = self.find_first_occurrence(num)
        if start[0] == 0:
            return (0, 0)

        i = start[0]
        j = start[1]
        w = 1
        while w < self.columnsNum and self.maze[i][j + w] == num:
            w += 1

        h = 1
        while h < self.rowsNumber and self.maze[i + h][j] == num:
            h += 1

        return (i + h / 2, j + w / 2)

    # calculates distance to closet wall for each cell
    def calc_distance_to_walls(self):
        self.get_copy_of_maze(self.distanceToWalls)
        q = Queue()

        for i in range(0, self.MaxN):
            for j in range(0, self.MaxN):
                if self.distanceToWalls[i][j] == self.WALL:
                    self.distanceToWalls[i][j] = 0
                    q.put((i, j))
                else:
                    self.distanceToWalls[i][j] = self.infDist

        while not q.empty():
            cur = q.get()
            cur_row = cur[0]
            cur_column = cur[1]
            next_val = self.distanceToWalls[cur_row][cur_column] + 1

            if cur_row > 0:
                if self.distanceToWalls[cur_row - 1][cur_column] > next_val:
                    self.distanceToWalls[cur_row - 1][cur_column] = next_val
                    q.put((cur_row - 1, cur_column))

            if cur_row < self.MaxN - 1:
                if self.distanceToWalls[cur_row + 1][cur_column] > next_val:
                    self.distanceToWalls[cur_row + 1][cur_column] = next_val
                    q.put((cur_row + 1, cur_column))

            if cur_column > 0:
                if self.distanceToWalls[cur_row][cur_column - 1] > next_val:
                    self.distanceToWalls[cur_row][cur_column - 1] = next_val
                    q.put((cur_row, cur_column - 1))

            if cur_column < self.MaxN - 1:
                if self.distanceToWalls[cur_row][cur_column + 1] > next_val:
                    self.distanceToWalls[cur_row][cur_column + 1] = next_val
                    q.put((cur_row, cur_column + 1))

        print "distance to walls calculated"

    #  set wall value for cell in maze if distance from it to real wall <= num
    def extend_walls(self, num):
        self.calc_distance_to_walls()

        for i in range(0, self.MaxN):
            for j in range(0, self.MaxN):
                if self.distanceToWalls[i][j] <= num:
                    self.maze[i][j] = self.WALL

        print "extended walls by " + repr(num) + " cells"

    # calculate distance from start to all (not wall) cells in maze
    def calc_dist(self, start_row, start_column):
        for i in range(0, self.rowsNumber):
            for j in range(0, self.columnsNum):
                if self.maze[i][j] != self.WALL:
                    self.maze[i][j] = self.infDist

        self.maze[start_row][start_column] = 0

        q = Queue()
        q.put((start_row, start_column))

        while not q.empty():
            cur = q.get()
            cur_row = cur[0]
            cur_column = cur[1]

            if self.maze[cur_row][cur_column] == self.WALL:
                continue  # shouldn't happen:)

            next_val = self.maze[cur_row][cur_column] + 1

            if self.maze[cur_row - 1][cur_column] > next_val:
                self.maze[cur_row - 1][cur_column] = next_val
                q.put((cur_row - 1, cur_column))

            if self.maze[cur_row + 1][cur_column] > next_val:
                self.maze[cur_row + 1][cur_column] = next_val
                q.put((cur_row + 1, cur_column))

            if self.maze[cur_row][cur_column - 1] > next_val:
                self.maze[cur_row][cur_column - 1] = next_val
                q.put((cur_row, cur_column - 1))

            if self.maze[cur_row][cur_column + 1] > next_val:
                self.maze[cur_row][cur_column + 1] = next_val
                q.put((cur_row, cur_column + 1))

        print "distance from " + repr(start_row) + " " + repr(start_column) + " to all (not wall) cells calculated"

    # returns list of tuples < direction, length( in cm) >
    # directions: {N, E, S, W}
    def find_way(self):
        '''
                 ^
                 N
            <- W   E ->
                 S
                 v
        '''
        droid = self.find_center(self.START)
        print "Ollie start location: " + repr(droid[0]) + " " + repr(droid[1])
        finish = self.find_center(self.FINISH)
        print "finish location: " + repr(finish[0]) + " " + repr(finish[1])

        # check size of smallest entrance, int should be at least twice greater than tah value
        self.extend_walls(5)
        self.calc_dist(droid[0], droid[1])

        way = []
        cur_row = finish[0]
        cur_column = finish[1]

        while cur_row != droid[0] or cur_column != droid[1]:
            next_val = self.maze[cur_row][cur_column] - 1
            if self.maze[cur_row - 1][cur_column] == next_val:
                cur_row -= 1
                way.append(('S', 1))
            elif self.maze[cur_row + 1][cur_column] == next_val:
                cur_row += 1
                way.append(('N', 1))
            elif self.maze[cur_row][cur_column - 1] == next_val:
                cur_column -= 1
                way.append(('E', 1))
            else:
                cur_column += 1
                way.append(('W', 1))

        way.reverse()
        return OllieInMaze.unite_ans(way)

    @staticmethod
    def unite_ans(t_ans):
        ans = []

        t_ans.append(('#', 0))
        length_t_ans = len(t_ans)
        for i in range(1, length_t_ans):
            if t_ans[i][0] == t_ans[i - 1][0]:
                t_ans[i] = (t_ans[i][0], t_ans[i - 1][1] + 1)
            else:
                ans.append(t_ans[i - 1])

        return ans


if __name__ == '__main__':
    ollie = OllieInMaze()

    ollie.read("round1.map")
    way = ollie.find_way()

    length = len(way)
    for i in range(0, length):
        print repr(way[i])
