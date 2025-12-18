import java.util.*;
import java.util.concurrent.*;

public class ElevatorController {
    private final List<Elevator> elevators;
    private final BlockingQueue<ElevatorRequest> requestQueue;
    private final ExecutorService executor;
    private volatile boolean running;
    private final Map<Integer, Boolean> priorityFloors;

    public ElevatorController(int numElevators, int maxPassengers) {
        this.elevators = new ArrayList<>();
        this.requestQueue = new LinkedBlockingQueue<>();
        this.executor = Executors.newFixedThreadPool(numElevators + 1);
        this.running = true;
        this.priorityFloors = new ConcurrentHashMap<>();

        for (int i = 0; i < numElevators; i++) {
            Elevator elevator = new Elevator(i + 1, maxPassengers);
            elevators.add(elevator);

            executor.execute(() -> {
                try {
                    elevator.move();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        executor.execute(this::processRequests);
    }

    private void processRequests() {
        while (running) {
            try {
                ElevatorRequest request = requestQueue.poll(100, TimeUnit.MILLISECONDS);
                if (request != null) {
                    handleRequest(request);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void handleRequest(ElevatorRequest request) {
        if (request.isExternalCall()) {
            handleExternalRequest(request);
        } else if (request.isCompleteRequest()) {
            handleCompleteRequest(request);
        } else if (request.isInternalCall()) {
            handleInternalRequest(request);
        }
    }

    private void handleExternalRequest(ElevatorRequest request) {
        Elevator bestElevator = findBestElevatorForRequest(request);

        if (bestElevator != null) {
            System.out.println("Внешний вызов: этаж " + request.getCallFloor() +
                    " (" + request.getDirection() + ")" +
                    " -> Лифт " + bestElevator.getId());

            boolean isPriority = priorityFloors.getOrDefault(request.getCallFloor(), false);
            bestElevator.addTargetFloor(request.getCallFloor(), isPriority);
        } else {
            System.out.println("Нет доступных лифтов для вызова с этажа " +
                    request.getCallFloor() + ". Возврат в очередь.");
            requestQueue.offer(request);
        }
    }

    private void handleCompleteRequest(ElevatorRequest request) {
        Elevator bestElevator = findBestElevatorForRequest(request);

        if (bestElevator != null) {
            System.out.println("Полный запрос: с " + request.getCallFloor() +
                    " на " + request.getDestinationFloor() +
                    " -> Лифт " + bestElevator.getId());

            boolean isCallPriority = priorityFloors.getOrDefault(request.getCallFloor(), false);
            boolean isDestPriority = priorityFloors.getOrDefault(request.getDestinationFloor(), false);

            bestElevator.addTargetFloor(request.getCallFloor(), isCallPriority);
            bestElevator.addTargetFloor(request.getDestinationFloor(), isDestPriority);

            if (bestElevator.addPassenger()) {
                System.out.println("Пассажир вошел в лифт " + bestElevator.getId());
            } else {
                System.out.println("Лифт " + bestElevator.getId() + " переполнен!");
            }
        } else {
            System.out.println("Нет доступного лифта для полного запроса.");
        }
    }

    private void handleInternalRequest(ElevatorRequest request) {
        for (Elevator elevator : elevators) {
            if (elevator.getStatus() == Elevator.Status.DOORS_OPEN) {
                System.out.println("Внутренний запрос: этаж " + request.getDestinationFloor() +
                        " -> Лифт " + elevator.getId());

                boolean isPriority = priorityFloors.getOrDefault(request.getDestinationFloor(), false);
                elevator.addTargetFloor(request.getDestinationFloor(), isPriority);
                return;
            }
        }

        System.out.println("Нет лифтов с открытыми дверями для внутреннего запроса.");
    }

    private Elevator findBestElevatorForRequest(ElevatorRequest request) {
        Elevator bestElevator = null;
        int minScore = Integer.MAX_VALUE;

        for (Elevator elevator : elevators) {
            if (elevator.isFull()) {
                continue;
            }

            int score = calculateElevatorScore(elevator, request);

            if (score < minScore) {
                minScore = score;
                bestElevator = elevator;
            }
        }

        return bestElevator;
    }

    private int calculateElevatorScore(Elevator elevator, ElevatorRequest request) {
        int score = 0;
        int floorDiff = Math.abs(elevator.getCurrentFloor() - request.getCallFloor());

        score = floorDiff * 10;

        Elevator.Direction elevatorDir = elevator.getDirection();
        ElevatorRequest.Direction requestDir = request.getDirection();

        if (elevatorDir != Elevator.Direction.IDLE) {
            boolean isSameDirection = false;

            if (elevatorDir == Elevator.Direction.UP &&
                    request.getCallFloor() >= elevator.getCurrentFloor() &&
                    requestDir == ElevatorRequest.Direction.UP) {
                isSameDirection = true;
            } else if (elevatorDir == Elevator.Direction.DOWN &&
                    request.getCallFloor() <= elevator.getCurrentFloor() &&
                    requestDir == ElevatorRequest.Direction.DOWN) {
                isSameDirection = true;
            }

            if (!isSameDirection) {
                score += 30;
            }
        }

        score += elevator.getPassengerCount() * 3;
        score += elevator.getTargetFloors().size() * 15;

        if (elevatorDir == Elevator.Direction.IDLE) {
            score -= 40;
        }

        if (elevator.getPassengerCount() == 0) {
            score -= 20;
        }

        if (priorityFloors.getOrDefault(request.getCallFloor(), false)) {
            score -= 30;
        }

        if (elevator.getStatus() == Elevator.Status.STOPPED) {
            score -= 25;
        }

        return Math.max(0, score);
    }

    public void submitRequest(ElevatorRequest request) {
        requestQueue.add(request);

        if (request.isExternalCall()) {
            System.out.println("Поступил внешний вызов: этаж " + request.getCallFloor() +
                    ", направление: " + request.getDirection());
        } else if (request.isCompleteRequest()) {
            System.out.println("Поступил полный запрос: с " + request.getCallFloor() +
                    " на " + request.getDestinationFloor());
        } else if (request.isInternalCall()) {
            System.out.println("Поступил внутренний запрос: этаж " +
                    request.getDestinationFloor());
        }
    }

    public void submitExternalRequest(ElevatorRequest request) {
        submitRequest(request);
    }

    public void submitInternalRequest(ElevatorRequest request) {
        submitRequest(request);
    }

    public void submitCompleteRequest(ElevatorRequest request) {
        submitRequest(request);
    }

    public void addPriorityFloor(int floor) {
        if (!priorityFloors.containsKey(floor)) {
            priorityFloors.put(floor, true);
            System.out.println("Этаж " + floor + " установлен как приоритетный");
        }
    }

    public void removePriorityFloor(int floor) {
        priorityFloors.remove(floor);
        System.out.println("Этаж " + floor + " удален из приоритетных");
    }

    public void shutdown() {
        System.out.println("Завершение работы контроллера...");
        running = false;

        for (Elevator elevator : elevators) {
            elevator.stop();
        }

        executor.shutdown();

        try {
            if (!executor.awaitTermination(3, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        System.out.println("Контроллер завершил работу.");
    }

    public void printStatus() {
        System.out.println("Запросов в очереди: " + requestQueue.size());
        System.out.println("Приоритетные этажи: " +
                priorityFloors.keySet().stream()
                        .filter(k -> priorityFloors.get(k))
                        .sorted()
                        .toList());

        for (Elevator elevator : elevators) {
            String statusStr = "";
            switch (elevator.getStatus()) {
                case STOPPED: statusStr = "СТОИТ"; break;
                case MOVING: statusStr = "ДВИЖЕТСЯ"; break;
                case DOORS_OPEN: statusStr = "ДВЕРИ ОТКРЫТЫ"; break;
                case DOORS_CLOSING: statusStr = "ДВЕРИ ЗАКРЫВАЮТСЯ"; break;
            }

            String directionStr = "";
            switch (elevator.getDirection()) {
                case UP: directionStr = "ВВЕРХ"; break;
                case DOWN: directionStr = "ВНИЗ"; break;
                case IDLE: directionStr = "БЕЗ НАПРАВЛЕНИЯ"; break;
            }

            System.out.printf("Лифт %d: этаж %2d | %-18s | %-15s | пассажиры: %d/%d | цели: %s%n",
                    elevator.getId(),
                    elevator.getCurrentFloor(),
                    statusStr,
                    directionStr,
                    elevator.getPassengerCount(),
                    elevator.getMaxPassengers(),
                    elevator.getTargetFloors());
        }
    }

    public List<Elevator> getElevators() {
        return new ArrayList<>(elevators);
    }

    public int getQueueSize() {
        return requestQueue.size();
    }
}