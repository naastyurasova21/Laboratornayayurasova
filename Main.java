import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        System.out.println("Запуск системы управления лифтами");
        System.out.println("Количество лифтов: 3");
        System.out.println("Макс. пассажиров в лифте: 5");
        System.out.println("Этажей: 10");

        ElevatorController controller = new ElevatorController(3, 5);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        controller.addPriorityFloor(1);
        controller.addPriorityFloor(10);

        System.out.println("\nПервый тестовый запрос");
        controller.submitCompleteRequest(new ElevatorRequest(1, 8));

        controller.printStatus();

        try {
            Thread.sleep(3000);

            System.out.println("Второй тестовый запрос");
            controller.submitCompleteRequest(new ElevatorRequest(3, 1));

            Thread.sleep(3000);

            System.out.println("Третий тестовый запрос");
            controller.submitCompleteRequest(new ElevatorRequest(5, 10));

            Thread.sleep(3000);

            System.out.println("Четвертый тестовый запрос");
            controller.submitCompleteRequest(new ElevatorRequest(2, 7));

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        for (int i = 0; i < 6; i++) {
            try {
                Thread.sleep(5000);
                controller.printStatus();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        controller.shutdown();
        System.out.println("Процесс завершен");
    }
}