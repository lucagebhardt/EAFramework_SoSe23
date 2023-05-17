import random
from tkinter import Tk, Canvas
import neat
import math
import visualize

# Kantenlänge des Labyrinths
MAP_SIZE = 25
STEPS = 100
GENERATIONS = 100


class MapGenerator:
    """
        MapGenerator kümmert sich um die Erzeugung und die Anzeige der
        Labyrinth-Karten.
    """

    def __init__(self, size, start, end):
        self.size = size
        self.start = start
        self.end = end
        self.map = [[0 for _ in range(size)] for _ in range(size)]
        self.tilesize = 20
    
    def generate(self):
        """
           Erzeugt eine Zufallskarte und gibt sie als 2D-Array zurück.
           Eine 0 im Array steht für ein betretbares Feld, 1 für ein
           Hindernis, S für Start und E für Ende.
        """
        
        # Sicherstellen, dass es mindestens einen PFad vom Start bis zum Ende gibt
        while True:
            self.map = [[random.randint(0, 1) for _ in range(self.size)] for _ in range(self.size)]
            if self._is_valid():
                break

        # Start und Ende Punkte setzen
        self.map[self.start[0]][self.start[1]] = 'S'
        self.map[self.end[0]][self.end[1]] = 'E'

    def _is_valid(self):
        start, end = self.start, self.end
        queue = [start]
        visited = set(queue)
        while queue:
            row, col = queue.pop(0)
            if (row, col) == end:
                return True
            neighbors = self._get_neighbors(row, col)
            for neighbor in neighbors:
                if neighbor not in visited and self.map[neighbor[0]][neighbor[1]] == 0:
                    visited.add(neighbor)
                    queue.append(neighbor)
        return False

    def _get_neighbors(self, row, col):
        neighbors = []
        if row > 0:
            neighbors.append((row-1, col))
        if row < self.size-1:
            neighbors.append((row+1, col))
        if col > 0:
            neighbors.append((row, col-1))
        if col < self.size-1:
            neighbors.append((row, col+1))
        return neighbors

    def draw_map(self, agent):
        """
            Erstellt und zeigt die GUI an.
        """

        ts = self.tilesize
        root = Tk()
        root.title('NEAT Maze')
        canvas = Canvas(root, width=self.size*ts, height=self.size*ts)
        canvas.pack()
        for row in range(self.size):
            for col in range(self.size):
                if self.map[col][row] == 'S':
                    canvas.create_rectangle(col*ts, row*ts, (col+1)*ts, (row+1)*ts, fill='red')
                elif self.map[col][row] == 'E':
                    canvas.create_rectangle(col*ts, row*ts, (col+1)*ts, (row+1)*ts, fill='blue')
                else:
                    color = 'white' if self.map[col][row] == 0 else 'gray'
                    canvas.create_rectangle(col*ts, row*ts, (col+1)*ts, (row+1)*ts, fill=color)
        # draw the agent's path
        canvas.create_oval(
                    agent.pos_x*ts+ts*0.25, 
                    agent.pos_y*ts+ts*0.25, 
                    (agent.pos_x+1)*ts-ts*0.25, 
                    (agent.pos_y+1)*ts-ts*0.25, 
                    fill='orange')

        for i in range(0,MAP_SIZE*5):
            inputs = agent._get_map_env()
            output = agent.activate_net(inputs)
            if agent.move(output):
                canvas.create_oval(
                    agent.pos_x*ts+ts*0.25, 
                    agent.pos_y*ts+ts*0.25, 
                    (agent.pos_x+1)*ts-ts*0.25, 
                    (agent.pos_y+1)*ts-ts*0.25, 
                    fill='orange')
                
            if agent.pos_x == 0 and agent.pos_y == 0:
                    break

        root.mainloop()

class Agent:
    """
        Repräsentiert einen Agenten, der sich durch das Labyrinth bewegt.
    """
    
    def __init__(self, net):
        self.net = net
        self.pos_x = None
        self.pos_y = None
        self.goal_x = None
        self.goal_y = None
        self.map = None
        self.visited = set()
        self.fitness = 0.0
        self.moves_history = list()

    def set_map(self, map):
        self.map = map

    def set_start(self, x, y):
        self.pos_x = x
        self.pos_y = y
        self.visited.add((x, y))

    def set_goal(self, x, y):
        self.goal_x = x
        self.goal_y = y

    def activate_net(self, inputs):
        output = self.net.activate(inputs)
        return output.index(max(output))

    def move(self, direction):
        """
            Bewegt den Agenten auf dem Labyrinth. Es muss sichergestellt werden,
            dass der Agent nur zulässige Bewegungen mehr. Die Methode gibt
            True zurück, wenn die Bewegung erfolgreich war, andernfalls False.
        """
        
        def valid_move(x, y):
            return x >= 0 and x < len(self.map) and 0 <= y and y < len(self.map[0]) and self.map[x][y] != 1 and (x, y) not in self.visited and (self.map[x][y] == 0 or self.map[x][y] == 'S')
        
        # 0=up, 1=right, 2=down, 3=left
        success = False
        match direction:
            case 0:
                new_y = self.pos_y-1
                if valid_move(self.pos_x,new_y):
                    self.pos_y = new_y
                    success = True
                    self.moves_history.append(True)
                else:
                    self.moves_history.append(False)
            case 1:
                new_x = self.pos_x+1
                if valid_move(new_x, self.pos_y):
                    self.pos_x = new_x
                    success = True
                    self.moves_history.append(True)
                else:
                    self.moves_history.append(False)
            case 2:
                new_y = self.pos_y+1
                if valid_move(self.pos_x,new_y):
                    self.pos_y = new_y
                    success = True
                    self.moves_history.append(True)
                else:
                    self.moves_history.append(False)

            case 3:
                new_x = self.pos_x-1
                if valid_move(new_x, self.pos_y):
                    self.pos_x = new_x
                    success = True
                    self.moves_history.append(True)
                else:
                    self.moves_history.append(False)
            case _: self.moves_history.append(False)
        return success

    def _get_distance(self):
        return math.sqrt((self.goal_x - self.pos_x)**2 + (self.goal_y - self.pos_y)**2)

    def _get_map_env(self):
        env = []
        def get_value(x, y):
            if x < 0 or y < 0 or x >= len(self.map) or y >= len(self.map[0]):
                return 1 # value for out-of-bounds indices
            elif self.map[x][y] == 'E' or self.map[x][y] == 'S':
                return 0
            else:
                return self.map[x][y]
        env.append(get_value(self.pos_x - 1, self.pos_y + 1)) # top left
        env.append(get_value(self.pos_x, self.pos_y + 1)) # top middle
        env.append(get_value(self.pos_x + 1, self.pos_y + 1)) # top right
        env.append(get_value(self.pos_x - 1, self.pos_y)) # middle left
        env.append(get_value(self.pos_x + 1, self.pos_y)) # middle right
        env.append(get_value(self.pos_x - 1, self.pos_y - 1)) # bottom left
        env.append(get_value(self.pos_x, self.pos_y - 1)) # bottom middle
        env.append(get_value(self.pos_x + 1, self.pos_y - 1)) # bottom right
        return env


    def run(self):
        """
            Führt eine Maximalanzahl von Schritten für den Agenten aus.
            Die Richtung wird vom "Gehirn", dem neuronalen Netz festgelegt.
        """
        
        # TODO
        for i in range(STEPS):
            inputs = self._get_map_env()
            output = self.activate_net(inputs)
            self.move(output)

            if self.pos_x == 0 and self.pos_y == 0:
                self.fitness = 1.0
                return
        self.fitness = self.euclidean_distance_norm()
        return
    def euclidean_distance_norm(self):
        return self.euclidian_distance(self.pos_x, self.pos_y, MAP_SIZE, MAP_SIZE) / self.euclidian_distance(MAP_SIZE,MAP_SIZE)

    def norm_number_of_valid_moves(self):
        value = float(self.moves_history.count(True)) / float(STEPS)
        if value == 1.0:
            value = 0.99
        return value


    def euclidian_distance(self,x1,y1,x2=0,y2=0):
        return math.sqrt((x2-x1) ** 2 + (y2-y1) ** 2)

# Creates agents with the given net and tests it on the given map
def eval_genomes(genomes, config):
    """
        Testet jedes Genom mit einem Agenten.
    """
    
    map = config.map
    for genome_id, genome in genomes:
        net = neat.nn.FeedForwardNetwork.create(genome, config)
        agent = Agent(net)
        agent.set_map(map)
        agent.set_start(MAP_SIZE-1, MAP_SIZE-1)
        agent.set_goal(0, 0)
        agent.run()
        genome.fitness = agent.fitness

    return

# Erzeugen einer Zufallskarte der Größe 20x20
generator = MapGenerator(MAP_SIZE, (0, 0), (MAP_SIZE-1, MAP_SIZE-1))
generator.generate()

# Laden einer geeigneten NEAT-Konfiguration aus der Datei 'neat-config'
config = neat.Config(neat.DefaultGenome, neat.DefaultReproduction,
                     neat.DefaultSpeciesSet, neat.DefaultStagnation,
                     'neat-config')
config.map = generator.map
#config.fitness_criterion = neat.config.Minimize
# Erzeugen einer Population
p = neat.Population(config)

# Ein Listener, der den Status auf der Konsole loggt
p.add_reporter(neat.StdOutReporter(False))

# Statistik-Logger für die Visualisierung des Netzes
stats = neat.StatisticsReporter()
p.add_reporter(stats)

# Run until a solution is found.
winner = p.run(eval_genomes, GENERATIONS) # up to X generations

visualize.draw_net(config, winner, True)
visualize.draw_net(config, winner, True, prune_unused=False)
visualize.plot_stats(stats, ylog=False, view=True)
visualize.plot_species(stats, view=True)

net = neat.nn.FeedForwardNetwork.create(winner, config)
agent = Agent(net)
agent.set_map(config.map)
agent.set_goal(0,0)
agent.set_start(MAP_SIZE-1, MAP_SIZE-1)
generator.draw_map(agent)