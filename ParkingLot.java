import java.util.*;

class ParkingSpot {
    String plate;
    long entryTime;
    String status;
}

public class ParkingLot {

    private ParkingSpot[] table;
    private int capacity;
    private int size;
    private int totalProbes;

    public ParkingLot(int capacity) {
        this.capacity = capacity;
        this.table = new ParkingSpot[capacity];
        for (int i = 0; i < capacity; i++) {
            table[i] = new ParkingSpot();
            table[i].status = "EMPTY";
        }
    }

    private int hash(String plate) {
        return Math.abs(plate.hashCode()) % capacity;
    }

    public String parkVehicle(String plate) {
        int index = hash(plate);
        int probes = 0;

        while (!table[index].status.equals("EMPTY") && !table[index].status.equals("DELETED")) {
            index = (index + 1) % capacity;
            probes++;
        }

        table[index].plate = plate;
        table[index].entryTime = System.currentTimeMillis();
        table[index].status = "OCCUPIED";

        size++;
        totalProbes += probes;

        return "Assigned spot #" + index + " (" + probes + " probes)";
    }

    public String exitVehicle(String plate) {
        int index = hash(plate);

        while (!table[index].status.equals("EMPTY")) {
            if (table[index].status.equals("OCCUPIED") && table[index].plate.equals(plate)) {
                long duration = System.currentTimeMillis() - table[index].entryTime;
                table[index].status = "DELETED";
                size--;

                double hours = duration / 3600000.0;
                double fee = Math.ceil(hours) * 5;

                return "Spot #" + index + " freed, Duration: " + String.format("%.2f", hours) + "h, Fee: $" + fee;
            }
            index = (index + 1) % capacity;
        }

        return "Vehicle not found";
    }

    public String getStatistics() {
        double occupancy = (size * 100.0) / capacity;
        double avgProbes = size == 0 ? 0 : (double) totalProbes / size;
        return "Occupancy: " + String.format("%.2f", occupancy) + "%, Avg Probes: " + String.format("%.2f", avgProbes);
    }

    public static void main(String[] args) {
        ParkingLot lot = new ParkingLot(500);

        System.out.println(lot.parkVehicle("ABC-1234"));
        System.out.println(lot.parkVehicle("ABC-1235"));
        System.out.println(lot.parkVehicle("XYZ-9999"));

        System.out.println(lot.exitVehicle("ABC-1234"));

        System.out.println(lot.getStatistics());
    }
}