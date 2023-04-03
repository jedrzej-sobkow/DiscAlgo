package DiscAlgorithms;

import MyObjects.Disc;
import MyObjects.Request;
import Useful.DistanceCalculator;
import Useful.StatsManager;

import java.util.ArrayList;

public class C_SCAN {

    private final Disc disc;
    private final int cylinderChangeTime;
    private final int blockChangeTime;
    private final int platterChangeTime;
    private final int requestLifetime;
    private int time = 0;
    private Request lastlyExecutedRequest = null;
    private ArrayList<Request> listOfDeadRequests = new ArrayList<>();

    private int cylinderChangingNumberOfMoves = 0;
    private int platterChangingNumberOfMoves = 0;
    private int blockChangingNumberOfMoves = 0;

    public C_SCAN(Disc disc, int cylChangeTime, int blkChangeTime, int pltChangeTime, int reqLifetime) {
        this.disc = disc.getSelfClone();
        cylinderChangeTime = cylChangeTime;
        blockChangeTime = blkChangeTime;
        platterChangeTime = pltChangeTime;
        requestLifetime = reqLifetime;
        System.out.println();
        carryOutTheSimulation();
        System.out.println("C-SCAN");
        StatsManager.getStats(listOfDeadRequests, time, cylinderChangingNumberOfMoves, blockChangingNumberOfMoves, platterChangingNumberOfMoves);

    }

    private void carryOutTheSimulation () {

        Request nextRequest = findNextRequest();

        while (nextRequest != null) {

            time += DistanceCalculator.getDifferenceInTimeBetweenTwoRequests(lastlyExecutedRequest, nextRequest, platterChangeTime, cylinderChangeTime, blockChangeTime);
            if (lastlyExecutedRequest != null) {
                cylinderChangingNumberOfMoves += Math.abs(lastlyExecutedRequest.getCylinderID() - nextRequest.getCylinderID());
                platterChangingNumberOfMoves += Math.abs(lastlyExecutedRequest.getPlatterID() - nextRequest.getPlatterID());
                blockChangingNumberOfMoves += Math.abs(lastlyExecutedRequest.getBlockID() - nextRequest.getBlockID());
            }
            else {
                cylinderChangingNumberOfMoves += nextRequest.getCylinderID();
                platterChangingNumberOfMoves += nextRequest.getPlatterID();
                blockChangingNumberOfMoves += nextRequest.getBlockID();
            }

            nextRequest.setWaitingTime(time-nextRequest.getMomentOfNotification());
            time += requestLifetime;

            lastlyExecutedRequest = nextRequest;
            listOfDeadRequests.add(nextRequest);

            nextRequest = findNextRequest();
        }
    }

    private Request findNextRequest () {

        int tempTime = time;

        int previousAddress = disc.getAddress(lastlyExecutedRequest);
        if (previousAddress == -1) {
            previousAddress = 0;
        }

        int lastServicedRequestAddress = previousAddress;
        int potentialAddress = previousAddress;
        Request potentialRequest;

        boolean isAnyAlive = false;
        int numberOfChecksForTheSameRequest = 0;

        while (isAnyAlive || !(numberOfChecksForTheSameRequest == 2)) {

            potentialAddress += 1;
            if (potentialAddress > disc.getLastAddress()) {
                potentialAddress = 0;
            }


            if (lastServicedRequestAddress == potentialAddress) {
                numberOfChecksForTheSameRequest++;
            }

            potentialRequest = disc.getRequest(potentialAddress);
            tempTime += DistanceCalculator.getDifferenceInTimeBetweenTwoSegments(previousAddress, potentialAddress,
                    disc, platterChangeTime,
                    cylinderChangeTime, blockChangeTime);
//            cylinderChangingNumberOfMoves += Math.abs(disc.getCylinderID(previousAddress) - disc.getCylinderID(potentialAddress));
//            platterChangingNumberOfMoves += Math.abs(disc.getPlatterID(previousAddress) - disc.getPlatterID(potentialAddress));
//            blockChangingNumberOfMoves += Math.abs(disc.getBlockID(previousAddress) - disc.getBlockID(potentialAddress));

            if (potentialRequest != null) {
                isAnyAlive = true;
                if(potentialRequest.getMomentOfNotification() <= tempTime) {
//                    this.time = tempTime;
                    return disc.removeRequest(potentialAddress);
                }
            }


            previousAddress = potentialAddress;
        }

        return null;
    }

}