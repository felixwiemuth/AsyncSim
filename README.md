AsyncSim
========

Copyright (C) 2017 Felix Wiemuth

License
-------

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

About
-----
AsyncSim is a Java simulator for distributed systems in an asynchronous model. It can simulate tasks with arbitrary Java code and provides a simple model of a network with asynchronous messaging. Random durations of executing tasks and sending messages can be simulated.

Project status
--------------
A first basic version of the simulator is currently being developed.

Computation model
-----------------
### General model
In the abstract model, a distributed system consists of a set of nodes that is connected by communication links. On each node, a task is running, which consists of local variables and a set of *commands*. Each command has a *guard* and an *action*, the guard specifying a condition and the action code to be run when the condition is satisfied. Whenever at least one guard of a task is satisfied, the action of one randomly chosen command with a satisfied guard is executed. While an action is executed, a tasks is blocked in the sense that it will not check whether guards are satisfied. Thus only one action can be executed at a time. As soon as an action has finished, a next action is run if a guard is satisfied. A task can perform any computation and in addition send and receive messages. To this end, it has access to a *message queue* and an interface *send(dest, msg)*. The message queue can be checked to contain new messages (which can be a condition of a guard) and messages can be retrieved. A message includes the sender id and payload data. To send a message, a task calls the send interface with a destination id and a payload. Sending of messages and execution of actions takes a finite but unknown amount of time.

### Concrete model
There is a global virtual time which starts at 0 and continuously increases. Activities that can consume virtual time in this model are the transportation of messages through the network and the execution of actions in tasks. Each link and each action is associated a duration, which can either be a fixed amount of time or a probability distribution over an amount of time. When a message is sent over a link, it is not immediately delivered to the destination node but kept by the network for a randomly picked duration from the given distribution. The effect of actions will not be visible until their randomly picked duration is over, i.e. all messages sent by an action will be sent at the time the action finishes (to simulate an action that sends and then continues with some computation, an action can be split into multiple actions). While a task can have only one action running at a time, it can still receive messages while executing an action. However, these messages won't be seen by an action that is already running.

Implementation
--------------
### Concepts
The approach is an event-based simulator. Events in the sense of the simulator are those which consume virtual time, i.e., have a duration. Whenever a message should be sent or an action should be executed, instead of executing the corresponding Java code directly, the code is posted to an event queue together with its virtual due time. The due time is calculated from the current time and the given duration. The event queue is a priority queue where the events are ordered by due time. The simulator is run by repeatedly executing the event which is at the head of the queue, updating the current time to the event's due time. The execution of the event may produce more events that are inserted into the queue. With this approach, the simulator does not need any concurrency which makes the implementation much simpler.

### Special aspects
- The effect of messages being held back by the network for their duration of transportation is achieved simply by the fact that the code which adds the message to a node is executed at a later virtual time
- The simulation of the duration of executing an action is a bit more tricky: the state of a task might change while the virtual time passes. However, as only one action can be executed at a time, the only way the state of a task can change is that it receives new messages. As specified, an action does not see messages which are received while it is running. Therefore, message sent to a task while is has a running action are posted to a temporary message queue and passed to the actual message queue after the action has finished.


Examples
--------
### FloodingTask
FloodingTask is an interesting example which demonstrates the features of the simulator (not including random durations).

#### Scenario
 It creates a network of n nodes, where each node i is connected to every other node j by a link with cost i*j. Each node runs the same task with only one command: whenever it receives a message, it appends its id to the message and sends it over two random links. The execution time for this action in task i is i.

#### Execution
When exectued, the messages sent in the network will increase exponentially over time. Looking at the first messages sent, one notices that the lower ids will be more present than the higher. This is due to the lower link cost to and from lower ids, as well as their lower computation time. However, eventually each message will be reproduced twice (with an appended id) and be sent over the network, just later than others as messages queue up for being processed.
