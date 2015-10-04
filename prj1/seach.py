from collections import deque
import heapq
import math

class node:
	def __init__(self, v):
		self.v = v #index into coordinates
		self.parent = None
		self.depth = -1
		self.heur = -1
		self.neighbour = []
	# return neighbours
	def successor(self):
		return self.neighbour
	def addNeighbours(self, v):
		self.neighbour.append(v)
	#return path to root
	def traceback(self, coordinates):
		if self.parent==None:
			return ["Solution Path:\n vertex "+str(self.v)+" "+ '({0}, {1})'.format(coordinates[self.v].y, coordinates[self.v].x)+"\n", 0, [self.v]]
		parentPath = self.parent.traceback(coordinates)
		tmp = parentPath[2]
		tmp.append(self.v)
		return [parentPath[0]+" vertex "+str(self.v)+" "+ '({0}, {1})'.format(coordinates[self.v].y, coordinates[self.v].x)+"\n",parentPath[1]+1, tmp]


class point:
	def __init__(self,xy):
		self.x = int(xy[1])
		self.y = int(xy[0])

#use adjacent list to store the graph
#we need to use a array to store each vertice (point), for each vertex, we store the adjecent nodes as a list, this adjacent list just store index
class Graph:
	def __init__(self,name):
		with open(name) as f:
			#first get coordinates, then use this coordinate to build graph
			self.coordinates = [None]*int(f.readline().strip().split()[1])
			self.graph_nodes = [None]*len(self.coordinates)
			for i in range(0,len(self.coordinates)):
				line = f.readline().strip().split()
				self.coordinates[i] = point(line[1:])
				self.graph_nodes[i] = node(i)
				if i != int(line[0]):
					print i
					print line[0]
					raise Exception("wrong vertex index")
			
			#use adjacent list to represent graph
			#self.graph = [None]*len(self.coordinates)
			f.readline()
			for line in f:
				v1,v2 = line.split()[1:]
				v1,v2 = int(v1), int(v2)
				self.graph_nodes[v1].addNeighbours(v2)
				self.graph_nodes[v2].addNeighbours(v1)
				#if self.graph[v1]==None:
				#	self.graph[v1] = []
				#if self.graph[v2] == None:
				#	self.graph[v2] = []
				#self.graph[v1].append(v2)
				#self.graph[v2].append(v1)
			self.summary = "search algorithm = {0}\ntotal iterations = {1}\nmax frontier size= {2}\nvertices visited = {3}/275\npath length      ={4}"

			
			
			
			
	def find_index(self,y,x):
		for i in range(0, len(self.coordinates)):
			if self.coordinates[i].x == x and self.coordinates[i].y == y:
				return i
		return -1

	def clearGraph(self):
		for node in self.graph_nodes:
			node.parent==None


	def bfs_search(self, start, end):
		self.clearGraph()

		iterations = 0
		maxFrontierSize = 1
		vertixVisitedCount = 1
		if start == end:
			path = self.graph_nodes[start].traceback(self.coordinates)
			return path[0]+self.summary.format("BFS",iterations,maxFrontierSize,vertixVisitedCount,path[1]), path[2]
		#queue used to keep track of the frontier
		frontier = deque()
		frontier.append(start)
		# set to keep track of the visited node
		visited = set()
		
		while len(frontier)!=0:
			maxFrontierSize = max(maxFrontierSize,len(frontier))
			iterations += 1
			tmp = frontier.popleft()
			if tmp == end:
				#reach goal state
				path = self.graph_nodes[tmp].traceback(self.coordinates)
				return path[0]+self.summary.format("BFS",iterations,maxFrontierSize,vertixVisitedCount,path[1]), path[2]
			visited.add(tmp)
			#for each node examine the child
			for next in self.graph_nodes[tmp].successor():
				if next not in visited and next not in frontier:
					frontier.append(next)
					self.graph_nodes[next].parent = self.graph_nodes[tmp]
					vertixVisitedCount+=1
		
	def dfs_search(self, start, end):
		self.clearGraph()

		iterations = 0
		maxFrontierSize = 1
		vertixVisitedCount = 1
		if start == end:
			path = self.graph_nodes[start].traceback(self.coordinates)
			return path[0]+self.summary.format("DFS",iterations,maxFrontierSize,vertixVisitedCount,path[1]), path[2]
		#queue used to keep track of the frontier
		frontier = []
		frontier.append(start)
		# set to keep track of the visited node
		visited = set()
		
		while len(frontier)!=0:
			maxFrontierSize = max(maxFrontierSize,len(frontier))
			iterations += 1
			tmp = frontier.pop()
			#print self.coordinates[tmp].y, self.coordinates[tmp].x
			if tmp == end:
				#reach goal state
				path = self.graph_nodes[tmp].traceback(self.coordinates)
				return path[0]+self.summary.format("DFS",iterations,maxFrontierSize,vertixVisitedCount,path[1]), path[2]
			visited.add(tmp)
			#for each node examine the child
			for next in self.graph_nodes[tmp].successor():
				if next not in visited and next not in frontier:
					frontier.append(next)
					self.graph_nodes[next].parent = self.graph_nodes[tmp]
					vertixVisitedCount+=1
	def EuclinDist(self, start, end):
		return math.sqrt((self.coordinates[start].y-self.coordinates[end].y)**2+(self.coordinates[start].x-self.coordinates[end].x)**2)


	def prior_search(self, start, end):
		self.clearGraph()
		iterations = 0
		maxFrontierSize = 1
		vertixVisitedCount = 1
		if start == end:
			path = self.graph_nodes[start].traceback(self.coordinates)
			return path[0]+self.summary.format("DFS",iterations,maxFrontierSize,vertixVisitedCount,path[1]), path[2]
		#queue used to keep track of the frontier
		frontier = []

		frontierInt = deque()
		frontierInt.append(start)		

		heapq.heappush(frontier,(self.EuclinDist(start, end),start))
		frontierInt.append(start)
		# set to keep track of the visited node
		visited = set()
		ii = 10
		while len(frontier)!=0:
			maxFrontierSize = max(maxFrontierSize,len(frontier))
			iterations += 1
			tmp = heapq.heappop(frontier)
			frontierInt.popleft()
			#if ii>=0:
			#	print "tmp is: "+str(tmp)
			#	print self.coordinates[tmp[1]].y, self.coordinates[tmp[1]].x
			#	print self.coordinates[end].y, self.coordinates[end].x
			#	ii -= 1
			
			tmp = tmp[1]
			#print self.coordinates[tmp].y, self.coordinates[tmp].x
			if tmp == end:
				#reach goal state
				path = self.graph_nodes[tmp].traceback(self.coordinates)
				return path[0]+self.summary.format("Greedy BFS",iterations,maxFrontierSize,vertixVisitedCount,path[1]), path[2]
			visited.add(tmp)
			#for each node examine the child
			for next in self.graph_nodes[tmp].successor():
				if next not in visited and next not in frontierInt:
					heapq.heappush(frontier,(self.EuclinDist(next, end), next))
					frontierInt.append(next)
					self.graph_nodes[next].parent = self.graph_nodes[tmp]
					#if ii>=0:
					#	print "node:"
					#	print next
					#	print self.graph_nodes[next].parent.v
					vertixVisitedCount+=1

import sys

if __name__=="__main__":
	argv = sys.argv
	#print argv
	if len(argv) == 6:
		graph_name = argv[1]
		start_y = int(argv[2])
		start_x = int(argv[3])
		end_y = int(argv[4])
		end_x = int(argv[5])
		# then find the index of start and end node
		tamu = [Graph(graph_name),Graph(graph_name), Graph(graph_name)] 
		start_index = tamu[0].find_index(start_y, start_x)
		end_index = tamu[0].find_index(end_y, end_x)
		#print start_index, end_index
		#then search tamu graph
		trace1 =  tamu[0].bfs_search(start_index, end_index)
		print trace1[0]
		print "====================================="
		trace2 = tamu[1].dfs_search(start_index, end_index)
		print trace2[0]
		print "====================================="
		trace3 = tamu[2].prior_search(start_index, end_index)
		print trace3[0]
		with open('trace.txt','w') as f:
			f.write("======BFS trace======\n")
			for ele in trace1[1]:
				f.write("{0} {1}\n".format(tamu[0].coordinates[ele].y, tamu[0].coordinates[ele].x))
			f.write('\n')
			f.write('=======DFS trace==========\n')
			for ele in trace2[1]:
				f.write("{0} {1}\n".format(tamu[1].coordinates[ele].y, tamu[1].coordinates[ele].x))
			f.write('\n')
			f.write('====== Greedy BFS==========\n')
			for ele in trace3[1]:
				f.write("{0} {1}\n".format(tamu[2].coordinates[ele].y, tamu[2].coordinates[ele].x))
			f.write('\n')
			
	else:
		print "Wrong num of argv"
		print "Example: BFS_nav ATM.graph 1 20 20 20"
	
