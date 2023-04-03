package DiscAlgorithms;

import Comparators.SortByDeadline;
import Comparators.SortByMomentOfNotification;
import MyObjects.Disc;
import MyObjects.Request;
import Useful.DistanceCalculator;
import Useful.TableManager;

import java.util.ArrayList;

public class FD_SCAN {

    private final ArrayList<Request> queueOfRequests;
    private ArrayList<Request> listOfDeadRequests = new ArrayList<>();
    private Request lastlyExecutedRequest = null;
    private final int cylinderChangeTime;
    private final int blockChangeTime;
    private final int platterChangeTime;
    private final int requestLifetime;
    private int time = 0;

    public FD_SCAN (Disc disc, int cylChangeTime, int blkChangeTime, int pltChangeTime, int reqLifetime) {
        queueOfRequests = TableManager.convert3DRequestTableTo1DArrayList(disc.getSelfClone().getDisc());
        queueOfRequests.sort(new SortByMomentOfNotification());
        blockChangeTime = blkChangeTime;
        cylinderChangeTime = cylChangeTime;
        platterChangeTime = pltChangeTime;
        requestLifetime = reqLifetime;
        System.out.println();
        carryOutTheSimulation();
    }

    private void carryOutTheSimulation() {

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

    private Request findNextRequest() {

        if (queueOfRequests.size() == 0)
            return null;

        if (lastlyExecutedRequest == null)
            return queueOfRequests.remove(0);

        ArrayList<Request> consideredRequests = new ArrayList<>();
        consideredRequests.add(queueOfRequests.get(0));

        int consideredSize = 1;

        int maxNotificationTime = Math.max(consideredRequests.get(0).getMomentOfNotification(), time);

        while (consideredSize < queueOfRequests.size() && queueOfRequests.get(consideredSize).getMomentOfNotification() <= maxNotificationTime) {
            consideredRequests.add(queueOfRequests.get(consideredSize));
            consideredSize++;
        }

        consideredRequests.sort(new SortByDeadline());
        for (Request request: consideredRequests) {
            int timeAfterArrivalToRequest = time + DistanceCalculator.getDifferenceInTimeBetweenTwoRequests(lastlyExecutedRequest, request, platterChangeTime, cylinderChangeTime, blockChangeTime);
            if (request.getDeadline() == Double.POSITIVE_INFINITY) {
                return queueOfRequests.remove(0);
            }
            if (timeAfterArrivalToRequest <= request.getDeadline()) {
                queueOfRequests.remove(request);
                return request;
            }
        }
        return queueOfRequests.remove(0);

    }

}
