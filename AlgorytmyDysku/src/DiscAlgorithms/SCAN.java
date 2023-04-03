package DiscAlgorithms;

import Main.Main;
import MyObjects.Disc;
import MyObjects.Request;
import Useful.DistanceCalculator;

import java.util.ArrayList;

public class SCAN {

    private final Disc disc;
    private final int cylinderChangeTime;
    private final int blockChangeTime;
    private final int platterChangeTime;
    private final int requestLifetime;
    private int time = 0;
    private boolean headFlag = true;
    private Request lastlyExecutedRequest = null;
    private ArrayList<Request> listOfDeadRequests = new ArrayList<>();

    public SCAN (Disc disc, int cylChangeTime, int blkChangeTime, int pltChangeTime, int reqLifetime) {
        this.disc = disc;
        cylinderChangeTime = cylChangeTime;
        blockChangeTime = blkChangeTime;
        platterChangeTime = pltChangeTime;
        requestLifetime = reqLifetime;
        System.out.println();
        carryOutTheSimulation();
    }

    private void carryOutTheSimulation () {

        Request nextRequest = findNextRequest();

        while (nextRequest != null) {

            nextRequest.setWaitingTime(time-nextRequest.getMomentOfNotification());

            lastlyExecutedRequest = nextRequest;
            listOfDeadRequests.add(nextRequest);

            nextRequest = findNextRequest();
        }
    }

    //TODO Naprawić zliczanie czasu.
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

            if(headFlag) {
                potentialAddress += 1;
                if (potentialAddress == disc.getLastAddress()) {
                    headFlag = false;
                }
            }
            else {
                potentialAddress -= 1;
                if (potentialAddress == 0) {
                    headFlag = true;
                }
            }

            if (lastServicedRequestAddress == potentialAddress) {
                numberOfChecksForTheSameRequest++;
            }

            potentialRequest = disc.getRequest(potentialAddress);
            tempTime += DistanceCalculator.getDifferenceInTimeBetweenTwoSegments(previousAddress, potentialAddress,
                    disc, platterChangeTime,
                    cylinderChangeTime, blockChangeTime);

            if (potentialRequest != null) {
                isAnyAlive = true;
                if(potentialRequest.getMomentOfNotification() <= tempTime) {
                    this.time = tempTime;
                    return disc.removeRequest(potentialAddress);
                }
            }


            previousAddress = potentialAddress;
        }

        return null;

//        if (headFlag) {
//            while (++actualAddress <= disc.getLastAddress()) {
//                int potentialAddress = actualAddress++;
//                Request potentialRequest = disc.getRequest(potentialAddress);
//                if (potentialRequest != null && potentialRequest.getMomentOfNotification() < time) {
//                    time += DistanceCalculator.getDifferenceInTimeBetweenTwoSegments
//                            (actualAddress, potentialAddress,disc,platterChangeTime,cylinderChangeTime,blockChangeTime);
//                    disc.addRequest(potentialAddress, null);
//                    return potentialRequest;
//                }
//                time += DistanceCalculator.getDifferenceInTimeBetweenTwoSegments
//                        (actualAddress, potentialAddress,disc,platterChangeTime,cylinderChangeTime,blockChangeTime);
//                //lastlyExecutedRequest.setCylinderID();
//                //moveHeadToRightEdge();
//            }
//            headFlag = false;
//        }
//        else {
//            while (actualAddress-- >= 0) {
//                int potentialAddress = actualAddress--;
//                Request potentialRequest = disc.getRequest(potentialAddress);
//                if (potentialRequest != null && potentialRequest.getMomentOfNotification() < time) {
//                    time += DistanceCalculator.getDifferenceInTimeBetweenTwoSegments
//                            (actualAddress, potentialAddress,disc,platterChangeTime,cylinderChangeTime,blockChangeTime);
//                    disc.addRequest(potentialAddress, null);
//                    return potentialRequest;
//                }
//                time += DistanceCalculator.getDifferenceInTimeBetweenTwoSegments
//                        (actualAddress, potentialAddress,disc,platterChangeTime,cylinderChangeTime,blockChangeTime);
//                //moveHeadToLeftEdge();
//            }
//            headFlag = true;
//        }
//        return null;
    }

}