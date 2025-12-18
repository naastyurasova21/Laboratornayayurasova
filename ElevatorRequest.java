public class ElevatorRequest {
    private final int callFloor;
    private final Direction direction;
    private final int destinationFloor;

    public enum Direction {
        UP, DOWN
    }

    public ElevatorRequest(int callFloor, Direction direction) {
        this.callFloor = callFloor;
        this.direction = direction;
        this.destinationFloor = -1;
    }

    public ElevatorRequest(int callFloor, int destinationFloor) {
        this.callFloor = callFloor;
        this.destinationFloor = destinationFloor;
        this.direction = destinationFloor > callFloor ? Direction.UP : Direction.DOWN;
    }

    public ElevatorRequest(int destinationFloor) {
        this.callFloor = -1;
        this.destinationFloor = destinationFloor;
        this.direction = null;
    }

    public int getCallFloor() { return callFloor; }
    public Direction getDirection() { return direction; }
    public int getDestinationFloor() { return destinationFloor; }
    public boolean isExternalCall() { return callFloor != -1 && destinationFloor == -1; }
    public boolean isInternalCall() { return callFloor == -1 && destinationFloor != -1; }
    public boolean isCompleteRequest() { return callFloor != -1 && destinationFloor != -1; }
}