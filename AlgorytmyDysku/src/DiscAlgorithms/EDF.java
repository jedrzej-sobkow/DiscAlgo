package DiscAlgorithms;

import Comparators.SortByMomentOfNotification;
import MyObjects.Disc;
import MyObjects.Request;
import Useful.DistanceCalculator;
import Useful.TableManager;

import java.util.ArrayList;

public class EDF {

    private final ArrayList<Request> queueOfRequests;
    private ArrayList<Request> listOfDeadRequests = new ArrayList<>();
    private Request lastlyExecutedRequest = null;
    private final int cylinderChangeTime;
    private final int blockChangeTime;
    private final int platterChangeTime;
    private final int requestLifetime;
    private int time = 0;

    public EDF (Disc disc, int cylChangeTime, int blkChangeTime, int pltChangeTime, int reqLifetime) {
        queueOfRequests = TableManager.convert3DRequestTableTo1DArrayList(disc.getSelfClone().getDisc());
        queueOfRequests.sort(new SortByMomentOfNotification());
        blockChangeTime = blkChangeTime;
        cylinderChangeTime = cylChangeTime;
        platterChangeTime = pltChangeTime;
        requestLifetime = reqLifetime;
        System.out.println();
        carryOutTheSimulation();
    }

    private void carryOutTheSimulation () {

        Request nextRequest = findNextRequest();

        while (nextRequest != null) {

            if (nextRequest.getMomentOfNotification() > time)
                time = nextRequest.getMomentOfNotification();

            if (lastlyExecutedRequest != null)
                time += DistanceCalculator.getDifferenceInTimeBetweenTwoRequests
                        (lastlyExecutedRequest,nextRequest,platterChangeTime,cylinderChangeTime,blockChangeTime);

            nextRequest.setWaitingTime(time-nextRequest.getMomentOfNotification());
            time += requestLifetime;

            lastlyExecutedRequest = nextRequest;
            listOfDeadRequests.add(nextRequest);

            nextRequest = findNextRequest();
        }
    }

    private Request findNextRequest () {

        if (queueOfRequests.size() == 0)
            return null;

        if (lastlyExecutedRequest == null)
            return queueOfRequests.remove(0);

        Request bestNextRequest = queueOfRequests.get(0);
        double theMostUrgentDeadline = bestNextRequest.getDeadline();

        int numberOfProcessesComingBeforeActualTime = 1;

        while (numberOfProcessesComingBeforeActualTime < queueOfRequests.size() &&
                queueOfRequests.get(numberOfProcessesComingBeforeActualTime).getMomentOfNotification() <=
                        Math.max(bestNextRequest.getMomentOfNotification(), time)) {

            Request potentialRequest = queueOfRequests.get(numberOfProcessesComingBeforeActualTime);


            if (potentialRequest.getDeadline() < theMostUrgentDeadline) {
                bestNextRequest = potentialRequest;
                theMostUrgentDeadline = potentialRequest.getDeadline();
            }

            numberOfProcessesComingBeforeActualTime++;
        }
        queueOfRequests.remove(bestNextRequest);
        return bestNextRequest;
    }
}
