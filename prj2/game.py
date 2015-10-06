import random
from collections import deque
import heapq

class node:
    def __init__(self, block_num, stack_num):
        self.block_num = block_num
        self.stack_num = stack_num
        self.parent = None
        self.depth = -1
        self.state = []
        for i in range(0,stack_num):
            self.state.append([])

    def init(self, state):
        self.stack_num = len(state)
        self.block_num = 0
        for stack in state:
            self.block_num+=len(stack)
        self.parent = None
        self.depth = -1
        self.state =[]
        for stack in state:
            self.state.append(list(stack))

    def shuffle(self, iteration):
        for i in range(0,self.block_num):
            self.state[0].append(chr(ord('A')+i))
        #shuffle
        while iteration:
            stack_from = random.randint(0, self.stack_num-1)
            stack_to = random.randint(0, self.stack_num-1)

            if len(self.state[stack_from])!=0:
                self.state[stack_to].append(self.state[stack_from][-1])
                del self.state[stack_from][-1]
            iteration-=1

    def successor(self):
        result = []
        for i in range(0,self.stack_num):
            if len(self.state[i])!=0:
                for j in range(1, self.stack_num):
                    next = (i+j)%self.stack_num
                    result.append(self.child(i, next))
        #print "result length: ",len(result)
        #print self.stack_num
        return result

    def child(self, fromInx, toInx):
        #print 'index: ',fromInx, toInx
        #print self.block_num,self.stack_num
        childNode = node(self.block_num, self.stack_num);
        for i in range(0,len(self.state)):
            childNode.state[i] = (list(self.state[i]))
        #print "*****"
        #print self.state
        #print "**************"
        #print childNode.state
        childNode.state[toInx].append(childNode.state[fromInx][-1])
        del childNode.state[fromInx][-1]
        return childNode


    def traceback(self):
        if not self.parent:
            return str(self)
        return self.parent.traceback()+"\n"+str(self)+"\n"

    def __eq__(self, other):
        return self.state==other.state
    def __str__(self):
        result = ""
        for i in range(0,len(self.state)):
            result += str(i+1)+" | "
            for ele in self.state[i]:
                result += " "+ele
            result +='\n'
        return result

    #def __hash__(self):
    #    return 0
    def printSelf(self):
        print "|--------------------------------->"
        print 'state: '+str(self.state)
        print 'parent: '+str(self.parent)
        print 'depth: '+str(self.depth)
        print "stack num: "+str(self.stack_num)
        print "block num: "+str(self.block_num)
        print "<---------------------------------|"
class stacking_blocks:
    def __init__(self, block_num, stack_num, runBFS):
        self.block_num = block_num
        self.stack_num = stack_num
        #goal node
        self.goal_node = node(block_num, stack_num)
        self.goal_node.shuffle(0)
        #empty node
        self.start_node = node(self.block_num, self.stack_num)
        #intitialize the node to be random state as the start node
        self.start_node.shuffle(5000)
        if runBFS:
            #self.start_node.printSelf()
            self.bfs_search(self.start_node, self.goal_node)

        #a* search
        self.start_node.depth=-1
        #self.start_node.printSelf()
        #self.goal_node.printSelf()
        self.a_star_search(self.start_node,self.goal_node)

        #default heuristic a* search
        self.start_node.depth=-1
        #self.start_node.printSelf()
        #self.goal_node.printSelf()
        self.default_a_star_search(self.start_node,self.goal_node)


    def bfs_search(self, start, end):
        print "BFS"
        print start
        frontier = deque()
        frontier.append(start)
        start.depth = 0
        total_goal_tests = 0
        max_queue_size = 1
        visited =[]
        iteration =0
        while len(frontier)!=0:
            iteration+=1
            max_queue_size = max(max_queue_size,len(frontier))
            curr = frontier.popleft()
            #print curr
            visited.append(curr)
            total_goal_tests+=1
            if curr==end:
                print "goal reached, total iteration {0}".format(iteration)
                self.print_result(curr,total_goal_tests,max_queue_size)
                return
            children = curr.successor()
            if len(children)==0:
                print "No Children"
            for child in children:
                #print "----------"
                #print child
                #print "--------"
                #print child not in visited
                #print child not in frontier

                if child not in visited and child not in frontier:
                    child.depth = curr.depth+1
                    child.parent = curr
                    frontier.append(child)

        print "frontier is 0"
        print frontier

    #calculate the f(n) value of the node
    def calc_heuristic(self,curr,goal):
        distance = 0
        #print curr
        #print goal
        for i in range(0,len(curr)):
            curr_len = len(curr[i])
            goal_len = len(goal[i])
            if  curr_len ==0:
                distance+=goal_len
            elif goal_len==0:
                distance+=curr_len
            else:
                #compare one by one
                allSame = True
                for j in range(0,min(curr_len,goal_len)):
                    if curr[i][j] != goal[i][j]:
                        distance += curr_len-j
                        distance += goal_len-j
                        allSame = False
                if allSame:
                    distance += max(curr_len,goal_len) - min(curr_len,goal_len)
        return distance


    def default_heuristic(self,curr,goal):
        #num of stacks out of place
        curr_length = len(curr[0])
        same = 0
        for i in range(0,curr_length):
            if curr[0][i]==goal[0][i]:
                same +=1
        return len(goal[0])-same

    def default_f_value(self,node,goal):
        g=node.depth
        h = self.default_heuristic(node.state, goal.state)
        return  g+h

    def f_value(self,node, goal):
        #g = node.depth
        g=node.depth
        h = self.calc_heuristic(node.state, goal.state)
        return  g+h

    def default_a_star_search(self, start, end):
        print "simple A* search"
        #print start
        frontier = []
        heapq.heappush(frontier,(self.default_f_value(start,end),start))
        frontier_track = []
        frontier_track.append(start)

        start.depth = 0
        total_goal_tests = 0
        max_queue_size = 1
        visited =[]
        iteration = 0
        while len(frontier)!=0:
            iteration+=1
            max_queue_size = max(max_queue_size,len(frontier))
            curr = heapq.heappop(frontier)[1]
            frontier_track.remove(curr)

            #print curr
            visited.append(curr)
            total_goal_tests+=1
            if curr==end:
                print "goal reached, total iteration {0}".format(iteration)
                self.print_result(curr,total_goal_tests,max_queue_size)
                return
            children = curr.successor()
            if len(children)==0:
                print "No Children"
            for child in children:
                #print "----------"
                #print child
                #print "--------"
                #print child not in visited
                #print child not in frontier
                if child in visited or child in frontier_track:
                    if child in frontier_track or child.depth<=curr.depth+1:
                        continue
                child.depth = curr.depth+1
                child.parent = curr
                heapq.heappush(frontier,(self.default_f_value(child,end),child))
                frontier_track.append(child)
        print "Error frontier is 0"
        print frontier

    def a_star_search(self, start, end):
        print "A* search"
        #print start
        frontier = []
        heapq.heappush(frontier,(self.f_value(start,end),start))

        frontier_track = []
        frontier_track.append(start)

        start.depth = 0
        total_goal_tests = 0
        max_queue_size = 1
        visited =[]
        iteration = 0
        while len(frontier)!=0:
            iteration +=1
            if iteration>=10000:
                print "iteration more than 10000",iteration
                break
            max_queue_size = max(max_queue_size,len(frontier))
            curr = heapq.heappop(frontier)[1]
            frontier_track.remove(curr)
            #print curr
            visited.append(curr)
            total_goal_tests+=1
            if curr==end:
                print "goal reached, total iteration {0}".format(iteration)
                self.print_result(curr,total_goal_tests,max_queue_size)
                return
            children = curr.successor()
            if len(children)==0:
                print "No Children"
            for child in children:
                #print "----------"
                #print child
                #print "--------"
                #print child not in visited
                #print child not in frontier
                if child in visited or child in frontier_track:
                    if child in frontier_track or child.depth<=curr.depth+1:
                        continue
                child.depth = curr.depth+1
                child.parent = curr
                heapq.heappush(frontier,(self.f_value(child,end),child))
                frontier_track.append(child)

        print "Error frontier is 0"
        print frontier

    def print_result(self, node,total_goal_tests,max_queue_size):
        print "success! depth={0},total_goal_tests={1}," \
              "max_queue_size={2}\n".format(node.depth,total_goal_tests,max_queue_size)
        print node.traceback()

import sys
if __name__=="__main__":
    argv = sys.argv
    if len(argv) == 4:
        block_num = int(argv[1])
        stack_num = int(argv[2])
        if argv[3] == 'true':
            runBFS = True
        else:
            runBFS = False
        print block_num, stack_num, runBFS
        mygame = stacking_blocks(block_num,stack_num,runBFS)

    else:
        print "Wrong num of argv"
        print "Example: play <num of blocks> <num of stacks>"
