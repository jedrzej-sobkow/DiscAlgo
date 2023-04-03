package Main;

import DiscAlgorithms.*;
import MyObjects.*;

import java.util.Random;

public class Main {

    private static final int REQUEST_LIFETIME = 1;
    private static final int PLATTER_CHANGE_TIME = 1;
    private static final int BLOCK_CHANGE_TIME = 3;
    private static final int CYLINDER_CHANGE_TIME = 10;
    private static final int BLOCKS_PER_CYLINDER = 100;
    private static final int CYLINDERS_PER_PLATTER = 100;
    private static final int NUMBER_OF_PLATTERS = 10;
    private static final int NUMBER_OF_REQUESTS = 150;
    private static Disc disc;

    private static final boolean ARE_REQUESTS_COMING_SIMULTANEOUSLY = false;
    private static final boolean IS_DEADLINE_IMPORTANT = true;

    public static void main(String[] args) {

        createDisk();
        generateRequests();

        FCFS fcfs = new FCFS(disc, CYLINDER_CHANGE_TIME, BLOCK_CHANGE_TIME, PLATTER_CHANGE_TIME, REQUEST_LIFETIME);
        SSTF sstf = new SSTF(disc, CYLINDER_CHANGE_TIME, BLOCK_CHANGE_TIME, PLATTER_CHANGE_TIME, REQUEST_LIFETIME);
        SCAN scan = new SCAN(disc, CYLINDER_CHANGE_TIME, BLOCK_CHANGE_TIME, PLATTER_CHANGE_TIME, REQUEST_LIFETIME);
        C_SCAN c_scan = new C_SCAN(disc, CYLINDER_CHANGE_TIME, BLOCK_CHANGE_TIME, PLATTER_CHANGE_TIME, REQUEST_LIFETIME);
        EDF edf = new EDF(disc, CYLINDER_CHANGE_TIME, BLOCK_CHANGE_TIME, PLATTER_CHANGE_TIME, REQUEST_LIFETIME);
        FD_SCAN fd_scan = new FD_SCAN(disc, CYLINDER_CHANGE_TIME, BLOCK_CHANGE_TIME, PLATTER_CHANGE_TIME, REQUEST_LIFETIME);
    }

    private static void createDisk () {
        disc = new Disc(new Request[CYLINDERS_PER_PLATTER][BLOCKS_PER_CYLINDER][NUMBER_OF_PLATTERS]);
    }

    //TODO można pomyśleć nad innym sposobem generowania procesów (konkretniej wybierania adresów)
    private static void generateRequests () {

        Random rng = new Random(42);

        int numberOfSegments = NUMBER_OF_PLATTERS * BLOCKS_PER_CYLINDER * CYLINDERS_PER_PLATTER;
        int address = rng.nextInt(numberOfSegments-1);
        for (int rID = 0; rID < NUMBER_OF_REQUESTS; rID++) {

            while (disc.getRequest(address) != null) {
                address = rng.nextInt(numberOfSegments-1);
            }

            int momentOfNotification = rng.nextInt(numberOfSegments - numberOfSegments/3);
            double deadline = momentOfNotification + rng.nextInt(numberOfSegments);
            if (!IS_DEADLINE_IMPORTANT) {
                deadline = Double.POSITIVE_INFINITY;
            }

            int[] position = disc.getCylinderBlockAndPlatterOfGivenAddress(address);

            Request newRequest = new Request(
                                        position[0],
                                        position[1],
                                        position[2],
                                        momentOfNotification,
                                        deadline
                                    );
            disc.addRequest(address, newRequest);
        }
    }

    public static void printDisc () {

    }
}