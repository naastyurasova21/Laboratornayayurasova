import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class Elevator {
    private final int id;
    private int currentFloor;
    private Direction direction;
    private Status status;
    private final ConcurrentSkipListSet<Integer> targetFloors;
    private final Lock lock;
    private final Condition condition;
    private volatile boolean running;
    private int passengerCount;
    private final int maxPassengers;
    private final Set<Integer> priorityFloors;

    public enum Direction {
        UP, DOWN, IDLE
    }

    public enum Status {
        STOPPED,
        MOVING,
        DOORS_OPEN,
        DOORS_CLOSING
    }

    public Elevator(int id, int maxPassengers) {
        this.id = id;
        this.currentFloor = 1;
        this.direction = Direction.IDLE;
        this.status = Status.STOPPED;
        this.targetFloors = new ConcurrentSkipListSet<>();
        this.lock = new ReentrantLock();
        this.condition = lock.newCondition();
        this.running = true;
        this.passengerCount = 0;
        this.maxPassengers = maxPassengers;
        this.priorityFloors = ConcurrentHashMap.newKeySet();
    }

    public void addTargetFloor(int floor, boolean isPriority) {
        lock.lock();
        try {
            targetFloors.add(floor);
            if (isPriority) {
                priorityFloors.add(floor);
            }
            updateDirection();
            condition.signal();
        } finally {
            lock.unlock();
        }
    }

    public boolean addPassenger() {
        lock.lock();
        try {
            if (passengerCount < maxPassengers) {
                passengerCount++;
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    public void removePassenger() {
        lock.lock();
        try {
            if (passengerCount > 0) {
                passengerCount--;
            }
        } finally {
            lock.unlock();
        }
    }

    public void move() throws InterruptedException {
        while (running) {
            lock.lock();
            try {
                if (targetFloors.isEmpty()) {
                    status = Status.STOPPED;
                    direction = Direction.IDLE;
                    condition.await(500, TimeUnit.MILLISECONDS);
                    continue;
                }

                status = Status.MOVING;
                Integer nextFloor = getNextFloor();

                if (nextFloor != null) {
                    moveToFloor(nextFloor);
                }

            } finally {
                lock.unlock();
            }
        }
        System.out.println("Лифт " + id + " завершил работу");
    }

    private Integer getNextFloor() {
        if (targetFloors.isEmpty()) return null;

        if (direction == Direction.UP) {
            for (int floor = currentFloor + 1; floor <= targetFloors.last(); floor++) {
                if (priorityFloors.contains(floor)) {
                    return floor;
                }
            }
        } else if (direction == Direction.DOWN) {
            for (int floor = currentFloor - 1; floor >= targetFloors.first(); floor--) {
                if (priorityFloors.contains(floor)) {
                    return floor;
                }
            }
        }

        if (direction == Direction.UP) {
            Integer next = targetFloors.ceiling(currentFloor + 1);
            if (next == null) {
                direction = Direction.DOWN;
                return targetFloors.floor(currentFloor - 1);
            }
            return next;
        } else if (direction == Direction.DOWN) {
            Integer next = targetFloors.floor(currentFloor - 1);
            if (next == null) {
                direction = Direction.UP;
                return targetFloors.ceiling(currentFloor + 1);
            }
            return next;
        } else {
            Integer higher = targetFloors.ceiling(currentFloor + 1);
            Integer lower = targetFloors.floor(currentFloor - 1);

            if (higher != null && lower != null) {
                direction = Math.abs(higher - currentFloor) <= Math.abs(lower - currentFloor)
                        ? Direction.UP : Direction.DOWN;
                return direction == Direction.UP ? higher : lower;
            } else if (higher != null) {
                direction = Direction.UP;
                return higher;
            } else if (lower != null) {
                direction = Direction.DOWN;
                return lower;
            }
        }
        return null;
    }

    private void moveToFloor(int targetFloor) throws InterruptedException {
        int steps = Math.abs(targetFloor - currentFloor);

        for (int i = 0; i < steps; i++) {
            if (!running) return;

            Thread.sleep(500);

            if (targetFloor > currentFloor) {
                currentFloor++;
            } else {
                currentFloor--;
            }

            System.out.println("Лифт " + id + " на этаже " + currentFloor +
                    ", статус: " + status + ", направление: " + direction);

            boolean shouldStop = targetFloors.contains(currentFloor) ||
                    priorityFloors.contains(currentFloor);

            if (shouldStop) {
                stopAtFloor(currentFloor);
                break;
            }
        }
    }

    private void stopAtFloor(int floor) throws InterruptedException {
        status = Status.DOORS_OPEN;
        targetFloors.remove(floor);
        priorityFloors.remove(floor);

        System.out.println("Лифт " + id + " прибыл на этаж " + floor);
        System.out.println("Лифт " + id + " открытие дверей");

        Thread.sleep(1500);

        status = Status.DOORS_CLOSING;
        System.out.println("Лифт " + id + " закрытие дверей");
        Thread.sleep(500);

        status = Status.MOVING;
    }

    private void updateDirection() {
        if (!targetFloors.isEmpty() && direction == Direction.IDLE) {
            Integer first = targetFloors.first();
            direction = first > currentFloor ? Direction.UP : Direction.DOWN;
        }
    }

    public void stop() {
        running = false;
        lock.lock();
        try {
            condition.signal();
        } finally {
            lock.unlock();
        }
    }

    public int getId() { return id; }
    public int getCurrentFloor() { return currentFloor; }
    public Direction getDirection() { return direction; }
    public Status getStatus() { return status; }
    public boolean hasTargets() { return !targetFloors.isEmpty(); }
    public Set<Integer> getTargetFloors() { return new HashSet<>(targetFloors); }
    public int getPassengerCount() { return passengerCount; }
    public int getMaxPassengers() { return maxPassengers; }
    public boolean isFull() { return passengerCount >= maxPassengers; }
    public boolean acceptsPassengers() { return passengerCount < maxPassengers && status == Status.DOORS_OPEN; }
}